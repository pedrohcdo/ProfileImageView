package org.pedrohc.profileimageview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 *  Copyright - Pedro H. Chaves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by Pedro on 18/03/2016.
 */
final public class ProfileImageViewUtils {

    /** Private Constructor */
    private ProfileImageViewUtils() {}

    /**
     * Decode from Resource
     *
     * @param resId
     * @return
     */
    public static Bitmap decodeFromResource(final Resources resources, final int resId) {
        // Decode
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, options);
        // If not decoded
        if(bitmap == null)
            return null;
        // Correct Aspect and CLAMP
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int size = Math.max(width, height);
        final Bitmap corrected = Bitmap.createBitmap(size+2, size+2, Bitmap.Config.ARGB_8888);
        final Canvas correctedCanvas = new Canvas(corrected);
        correctedCanvas.drawBitmap(bitmap, (size - width) / 2 + 1, (size - height) / 2 + 1, null);
        bitmap.recycle();
        bitmap = corrected;
        //
        return bitmap;
    }

    /**
     * Calculate Center Scale
     * @param vertices
     * @return
     */
    public static float calculateCenterScale(final List<ProfileImageView.Frame.FrameVertex> vertices) {
        final ProfileImageView.Frame.FrameVertex centroid = calculateFrameCentroid(vertices);
        float cx = centroid.x * 100;
        float cy = centroid.y * 100;
        float minusCenterDistance = 100.0f;
        float centerScale = 100.0f;
        for(int i=0; i<vertices.size(); i++) {
            ProfileImageView.Frame.FrameVertex a;
            ProfileImageView.Frame.FrameVertex b;
            if(i == vertices.size() - 1) {
                a = vertices.get(i);
                b = vertices.get(0);
            } else {
                a = vertices.get(i);
                b = vertices.get(i + 1);
            }
            float ax = a.x * 100;
            float ay = a.y * 100;
            float bx = b.x * 100;
            float by = b.y * 100;
            float dx = bx - ax;
            float dy = by - ay;
            float d = (float)Math.hypot(dy, dx);
            for(float j=0; j<d; j++) {
                float x = ax + (j / d) * dx;
                float y = ay + (j / d) * dy;
                float dcx = x - cx;
                float dcy = y - cy;
                float dc = (float)Math.hypot(dcy, dcx);
                if(dc < minusCenterDistance) {
                    minusCenterDistance = dc;
                    centerScale = Math.min(Math.abs(dcx), Math.abs(dcy));
                }
            }
        }
        return Math.abs((minusCenterDistance / 100.0f) * (float)Math.cos(Math.PI / 4));
    }

    /**
     * Calc Triangle Area
     * @param v1
     * @param v2
     * @param v3
     * @return
     */
    private static float calculateTriangleArea(ProfileImageView.Frame.FrameVertex v1, ProfileImageView.Frame.FrameVertex v2, ProfileImageView.Frame.FrameVertex v3 ) {
        float d1 = v1.x * v2.y + v1.y * v3.x + v2.x * v3.y;
        float d2 = v1.x * v3.y + v1.y * v2.x + v2.y * v3.x;
        return (float)Math.abs( ( d1 - d2 ) / 2 );
    }

    /**
     * Calculate frame centroid
     * @param vertices
     */
    public static ProfileImageView.Frame.FrameVertex calculateFrameCentroid(final List<ProfileImageView.Frame.FrameVertex> vertices) {
        if(vertices.size() == 0)
            return new ProfileImageView.Frame.FrameVertex(0, 0);
        if(vertices.size() == 1)
            return vertices.get(0).clone();
        if(vertices.size() == 2) {
            final ProfileImageView.Frame.FrameVertex a = vertices.get(0);
            final ProfileImageView.Frame.FrameVertex b = vertices.get(1);
            return new ProfileImageView.Frame.FrameVertex((a.x + b.x) / 2, (a.y + b.y) / 2);
        }
        if(vertices.size() == 3) {
            final ProfileImageView.Frame.FrameVertex a = vertices.get(0);
            final ProfileImageView.Frame.FrameVertex b = vertices.get(1);
            final ProfileImageView.Frame.FrameVertex c = vertices.get(2);
            return new ProfileImageView.Frame.FrameVertex((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3);
        }
        final List<ProfileImageView.Frame.FrameVertex> centroids = new ArrayList<>();
        float smxSum = 0;
        float smySum = 0;
        float areaSum = 0;
        for(int i=1; i<vertices.size()-1; i+=1) {
            ProfileImageView.Frame.FrameVertex a = vertices.get(0);
            ProfileImageView.Frame.FrameVertex b = vertices.get(i);
            ProfileImageView.Frame.FrameVertex c = vertices.get(i+1);
            float area = calculateTriangleArea(a, b, c);
            float cx = (a.x + b.x + c.x) / 3;
            float cy = (a.y + b.y + c.y) / 3;
            smxSum  += cx * area;
            smySum  += cy * area;
            areaSum += area;
        }
        return new ProfileImageView.Frame.FrameVertex((float)(smxSum/areaSum), (float)(smySum/areaSum));
    }

    /**
     * Calculate Center
     * @param vertices
     * @return
     */
    public static ProfileImageView.Frame.FrameVertex calculateCenter(final List<ProfileImageView.Frame.FrameVertex> vertices) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        for(final ProfileImageView.Frame.FrameVertex vertex : vertices) {
            minX = Math.min(minX, vertex.x);
            maxX = Math.max(maxX, vertex.x);
            minY = Math.min(minY, vertex.y);
            maxY = Math.max(maxY, vertex.y);
        }
        return new ProfileImageView.Frame.FrameVertex((maxX + minX) / 2, (maxY + minY) / 2);
    }

    /**
     * Get Width
     * @return
     */
    public static float calculateWidth(final List<ProfileImageView.Frame.FrameVertex> vertices) {
        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        for(final ProfileImageView.Frame.FrameVertex vertex : vertices) {
            minX = Math.min(minX, vertex.x);
            maxX = Math.max(maxX, vertex.x);
        }
        return maxX - minX;
    }

    /**
     * Get Height
     * @return
     */
    public static float calculateHeight(final List<ProfileImageView.Frame.FrameVertex> vertices) {
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        for(final ProfileImageView.Frame.FrameVertex vertex : vertices) {
            minY = Math.min(minY, vertex.y);
            maxY = Math.max(maxY, vertex.y);
        }
        return maxY - minY;
    }
}
