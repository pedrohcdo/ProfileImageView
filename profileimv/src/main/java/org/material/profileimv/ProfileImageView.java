package org.material.profileimv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
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
public class ProfileImageView extends View {

    // Conts
    final private static float DEFAULT_BORDER_RADIUS = 4.0f;
    final private static int DEFAULT_BORDER_COLOR = Color.WHITE;
    final private static int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    final private static int DEFAULT_SHADOW_COLOR = Color.rgb(0, 0, 0);
    final private static int DEFAULT_FEATURE_COLOR = 0xFF58B094;
    final private static int DEFAULT_SELECTABLE_COLOR = 0xFF58B094;
    final private static int DEFAULT_FEATURE_MULTIPLY_SHADOW_COLOR = 140;
    final private static int DEFAULT_SELECTABLE_MULTIPLY_SHADOW_COLOR = 50;
    final private static int DEFAULT_FEATURE_SHADOW_COLOR = Color.argb(35, 0, 0, 0);
    final private static float DEFAULT_MAX_CLICK_DISTANCE = 10;


    // Do not change
    final private static float COS45 = (float) Math.cos(Math.PI / 4.0f);
    final private static int SIMULATE_SHADOW_COLOR = Color.argb(50, 0, 0, 0);
    final private static int FEATURE_ANIMATION_START = 0;
    final private static int FEATURE_ANIMATION_END = 1;
    final private static int FEATURE_ANIMATION_NULL = 2;
    final private static int FEATURE_ANIMATION_DURATION = 100; /** ms */

    // Private Variables
    private boolean mBorder;
    private boolean mShadow;
    private Mode mMode = Mode.PORTRAIT;
    private ScaleMode mScaleMode = ScaleMode.FIT;
    private Theme mTheme;
    private Bitmap mImage;
    private Bitmap mFeatureIcon;
    private List<Object> mSingleReferences = new ArrayList<>();
    private String mFeatureText;
    private OnClickListener mOnClickListener;
    private Frame mFrame = Frame.SHAPE_PENTAGON;

    // Private Variables (Uses)
    private float mDensity;
    private Paint mBackgroundPaint;
    private Paint mBorderPaint;
    private Paint mPhotoPaint;
    private Paint mFeaturePaint;
    private Rect[] mFeatureRect = {new Rect(), new Rect()};
    private boolean mPressed;
    private float[] mPressedPosition = new float[2];
    private int mFeatureAnimationMode = FEATURE_ANIMATION_NULL;
    private long mFeatureAnimationStartTime = 0;
    private Handler mHandler;
    private int mLastImageRes;
    private int mLastFeatureIconRes;

    /**
     * Constructor
     *
     * @param context
     */
    public ProfileImageView(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public ProfileImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    /**
     * Initializes paint objects and sets desired attributes.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    private void init(Context context, AttributeSet attrs, int defStyle) {
        Log.d("LogTest", "Created");
        // Uses
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ProfileImageView, defStyle, 0);
        mDensity = context.getResources().getDisplayMetrics().density;

        // Get functions
        boolean border = attributes.getBoolean(R.styleable.ProfileImageView_border, false);
        boolean shadow = attributes.getBoolean(R.styleable.ProfileImageView_shadow, false);
        Mode mode = Mode.fromValue(attributes.getInt(R.styleable.ProfileImageView_mode, Mode.PORTRAIT.ordinal()));
        ScaleMode scaleMode = ScaleMode.fromValue(attributes.getInt(R.styleable.ProfileImageView_imageScaleMode, ScaleMode.FIT.ordinal()));
        Frame frame = Frame.getDefaultFromEnum(attributes.getInt(R.styleable.ProfileImageView_frame, 1));

        // Create paints (This order is important)
        mBackgroundPaint = new Paint();
        mBorderPaint = new Paint();
        mPhotoPaint = new Paint();
        mFeaturePaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBorderPaint.setAntiAlias(true);
        mPhotoPaint.setAntiAlias(true);
        mFeaturePaint.setAntiAlias(false);
        mFeaturePaint.setFilterBitmap(false);
        mFeaturePaint.setDither(false);

        // Set Photo and Feature Compose
        final int photoRes = attributes.getResourceId(R.styleable.ProfileImageView_image, -1);
        final int featureIconRes = attributes.getResourceId(R.styleable.ProfileImageView_featureIcon, -1);
        final String featureText = attributes.getString(R.styleable.ProfileImageView_featureText);
        setImageFromResource(photoRes);
        setFeatureIconFromResource(featureIconRes);
        setFeatureText(featureText);

        //
        setBorder(border);
        setShadow(shadow);
        setMode(mode);
        setScaleMode(scaleMode);
        setFrame(frame);

        // Make Theme
        final Theme theme = new Theme();
        theme.imageScale = attributes.getFloat(R.styleable.ProfileImageView_imageScale, 1.0f);
        theme.imageScrollX = attributes.getFloat(R.styleable.ProfileImageView_imageScrollX, 0.0f);
        theme.imageScrollY = attributes.getFloat(R.styleable.ProfileImageView_imageScrollY, 0.0f);
        theme.borderRadius = attributes.getDimension(R.styleable.ProfileImageView_borderRadius, DEFAULT_BORDER_RADIUS * mDensity);
        theme.borderColor = attributes.getColor(R.styleable.ProfileImageView_borderColor, DEFAULT_BORDER_COLOR);
        theme.backgroundColor = attributes.getColor(R.styleable.ProfileImageView_backgroundColor, DEFAULT_BACKGROUND_COLOR);
        theme.shadowColor = attributes.getColor(R.styleable.ProfileImageView_shadowColor, DEFAULT_SHADOW_COLOR);
        theme.featureColor = attributes.getColor(R.styleable.ProfileImageView_featureColor, DEFAULT_FEATURE_COLOR);
        theme.selectableColor = attributes.getColor(R.styleable.ProfileImageView_selectableColor, DEFAULT_SELECTABLE_COLOR);

        // Set theme
        setTheme(theme);

        // We no longer need our attributes TypedArray, give it back to cache
        attributes.recycle();

        //
        mHandler = new FeatureAnimationHandler();
    }

    /**
     * Set Feature Text
     *
     * @param text
     */
    public void setFeatureText(final String text) {
        if((mFeatureText == null && text == null) ||
                mFeatureText != null && mFeatureText.equals(text) ||
                text != null && text.equals(mFeatureText))
            return;
        mFeatureText = text;
        invalidate();
    }

    /**
     * Set Image from Resource
     *
     * @param resId -1 to null
     */
    public  void setImageFromResource(final int resId) {
        if(resId < -1)
            throw new ProfileImageViewException("Not permitted value, use -1 to null or above -1 to the desired resource.");
        // Recycle
        recycleImage();
        // Resolve Resource
        if(resId == -1)
            mImage = null;
        else
            mImage = ProfileImageViewUtils.decodeFromResource(getResources(), resId);
        // Set Shader
        if (mImage != null)
            mPhotoPaint.setShader(new BitmapShader(mImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        else
            mPhotoPaint.setShader(null);
        // Add to recyclable objects
        addRecyclableObject(mImage);
        //
        mLastImageRes = mImage == null ? -1 : resId;
        // Refresh layout and draw
        requestLayout();
        invalidate();
    }

    /**
     * Set Image
     *
     * @param bitmap To remove image use null
     */
    public void setImage(final Bitmap bitmap) {
        // Recycle
        recycleImage();
        // Set Image
        mImage = bitmap;
        // Set Shader
        if (mImage != null)
            mPhotoPaint.setShader(new BitmapShader(mImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        else
            mPhotoPaint.setShader(null);
        //
        mLastImageRes = -1;
        // Refresh layout and draw
        requestLayout();
        invalidate();
    }

    /**
     * Set Feature Icon from Resource
     *
     * @param resId -1 to null
     */
    public void setFeatureIconFromResource(final int resId) {
        if(resId < -1)
            throw new ProfileImageViewException("Not permitted value, use -1 to null or above -1 to the desired resource.");
        // Recycle
        recycleFeatureIcon();
        // Resolve resource
        if(resId == -1)
            mFeatureIcon = null;
        else
            mFeatureIcon = ProfileImageViewUtils.decodeFromResource(getResources(), resId);
        // Add to recyclable objects
        addRecyclableObject(mFeatureIcon);
        //
        mLastFeatureIconRes = mFeatureIcon == null ? -1 : resId;
        // Draw
        invalidate();
    }

    /**
     * Set Feature Icon
     *
     * @param icon To remove icon use null
     */
    public void setFeatureIcon(final Bitmap icon) {
        // Recycle
        recycleFeatureIcon();
        // Set icon
        mFeatureIcon = icon;
        //
        mLastFeatureIconRes = -1;
        // Draw
        invalidate();
    }

    /**
     * Recycle Image
     */
    private void recycleImage() {
        if(mImage != null && mLastImageRes != -1) {
            if(removeRecyclableObject(mImage) && !mImage.isRecycled())
                mImage.recycle();
            mImage = null;
        }
    }

    /**
     * Recycle Feature Icon
     */
    private void recycleFeatureIcon() {
        if(mFeatureIcon != null && mLastImageRes != -1) {
            if(removeRecyclableObject(mFeatureIcon) && !mFeatureIcon.isRecycled())
                mFeatureIcon.recycle();
            mFeatureIcon = null;
        }
    }

    /**
     * Unrecycle Image
     */
    private void unrecycleImage() {
        if(mLastImageRes != -1)
            setImageFromResource(mLastImageRes);
    }

    /**
     * Unrecycle Feature Icon
     */
    private void unrecycleFeatureIcon() {
        if(mLastFeatureIconRes != -1)
            setFeatureIconFromResource(mLastFeatureIconRes);
    }

    /**
     * Add recyclable object
     * @param object
     * @return
     */
    private void addRecyclableObject(final Object object) {
        if(object == null)
            return;
        mSingleReferences.add(object);
    }

    /**
     * Remove recyclable object
     */
    private boolean removeRecyclableObject(final Object object) {
        if(object == null)
            return false;
        final Iterator<Object> itr = mSingleReferences.iterator();
        boolean result = false;
        while(itr.hasNext()) {
            final Object itrObject = itr.next();
            if(itrObject == object) {
                itr.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Set Frame
     *
     * @param frame
     */
    public void setFrame(final Frame frame) {
        if(frame == null)
            throw new ProfileImageViewException("Set a valid way.");
        mFrame = frame.clone();
        requestLayout();
        invalidate();
    }

    /**
     * Get Frame
     * @return
     */
    public Frame getFrame() {
        return mFrame.clone();
    }

    /**
     * Set border
     *
     * @param border
     */
    public void setBorder(final boolean border) {
        if(mBorder == border)
            return;
        mBorder = border;
        requestLayout();
        invalidate();
    }

    /**
     * Has Border
     *
     * @return
     */
    public boolean hasBorder() {
        return mBorder;
    }

    /**
     * Set shadow
     *
     * @param shadow
     */
    public void setShadow(final boolean shadow) {
        if(mShadow == shadow)
            return;
        mShadow = shadow;
        if(mShadow)
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        else
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        requestLayout();
        invalidate();
    }

    /**
     * Has Shadow
     *
     * @return
     */
    public boolean hasShadow() {
        return mShadow;
    }

    /**
     * Set Mode
     *
     * @param mode
     */
    public void setMode(final Mode mode) {
        if(mMode == mode)
            return;
        if(mode == null)
            throw new ProfileImageViewException("Set a valid mode.");
        mMode = mode;
        invalidate();
    }

    /**
     * Get Mode
     *
     * @return
     */
    public Mode getMode() {
        return mMode;
    }

    /**
     * Set Scale Mode
     *
     * @param mode
     */
    public void setScaleMode(final ScaleMode mode) {
        if(mScaleMode == mode)
            return;
        if(mode == null)
            throw new ProfileImageViewException("Set a valid mode.");
        mScaleMode = mode;
        requestLayout();
        invalidate();
    }

    /**
     * Get Scale Mode
     *
     * @return
     */
    public ScaleMode getScaleMode() {
        return mScaleMode;
    }

    /**
     * Set Theme
     *
     * @param theme
     */
    public void setTheme(final Theme theme) {
        if(theme == null)
            throw new ProfileImageViewException("Set a valid theme.");
        mTheme = theme.clone();
        refreshTheme();
        invalidate();
    }


    /**
     * Get Theme
     *
     * @return
     */
    public Theme getTheme() {
        return mTheme.clone();
    }

    /**
     * Refresh Theme
     */
    private void refreshTheme() {
        mPhotoPaint.setColor(Color.RED);
        mFeaturePaint.setTextSize(mDensity * 22.0f);
        mFeaturePaint.setTypeface(Typeface.SANS_SERIF);
        requestLayout();
        invalidate();
    }

    /**
     * On Measure
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        if(mImage != null) {
            width = resolveSizeAndState(mImage.getWidth(), widthMeasureSpec, 0);
            height = resolveSizeAndState(mImage.getHeight(), heightMeasureSpec, 0);
        } else {
            width = resolveSizeAndState(getSuggestedMinimumWidth(), widthMeasureSpec, 0);
            height = resolveSizeAndState(getSuggestedMinimumHeight(), heightMeasureSpec, 0);
        }
        setMeasuredDimension(width, height);
    }

    /**
     * Helper - Get border Radius
     * @return
     */
    private float helperGetBorderRadius() {
        if(!mBorder)
            return 0;
        return mTheme.borderRadius;
    }

    /**
     * Helper - Get border Radius
     * @return
     */
    private float helperGetShadowRadius() {
        if(!mShadow)
            return 0.0f;
        return 2.0f * mDensity;
    }

    /**
     * On Layout
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //
        if(mImage != null) {
            Shader shader = mPhotoPaint.getShader();
            if (shader != null) {
                final float ms1 = helperGetBorderRadius() + helperGetShadowRadius();
                final float ms2 = ms1 * 2;
                final float width = (right - left) - ms2 - getPaddingLeft() - getPaddingRight();
                final float height = (bottom - top) - ms2 - getPaddingBottom() - getPaddingTop();
                final float size = Math.min(width, height) - helperGetShadowRadius() * 2;
                final float scaleImage = (size * 1.0f) / (mImage.getWidth()-2);
                float scaleContent = mTheme.imageScale;
                //
                Matrix shaderMatrix = new Matrix();
                switch(mScaleMode) {
                    case CENTER:
                        scaleContent *= mFrame.getCenterSquareScale();
                    case FIT:
                        final float finalScale = scaleImage * scaleContent;
                        float c = Math.abs((size * scaleContent - size) / 2);//
                        if(scaleContent < 1.0f)
                            c = 0;
                        final float imageSize = size * scaleContent;
                        final float imageScrollX = Math.max(Math.min(mTheme.imageScrollX * c, c), -c) + (width - imageSize) / 2 + getPaddingLeft() - finalScale;
                        final float imageScrollY = Math.max(Math.min(mTheme.imageScrollY * c, c), -c) + (height - imageSize) / 2 + getPaddingTop() - helperGetShadowRadius() - finalScale;
                        shaderMatrix.preTranslate(imageScrollX + ms1, imageScrollY + ms1);
                        shaderMatrix.preScale(finalScale, finalScale);
                }
                shader.setLocalMatrix(shaderMatrix);
            }
        }
    }

    /**
     * On Touch
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float ex;
        float ey;
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPressed = true;
                mPressedPosition[0] = event.getX();
                mPressedPosition[1] = event.getY();
                startFeatureAnimation(FEATURE_ANIMATION_START);
                return true;
            case MotionEvent.ACTION_MOVE:
                if(mPressed) {
                    ex = event.getX();
                    ey = event.getY();
                    if (ex < getPaddingLeft() || ey < getPaddingRight() ||
                            ex >= (getMeasuredWidth() - getPaddingRight()) || ey >= (getMeasuredHeight() - getPaddingBottom())) {
                        mPressed = false;
                        startFeatureAnimation(FEATURE_ANIMATION_END);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mPressed) {
                    mPressed = false;
                    ex = event.getX();
                    ey = event.getY();
                    float dx = ex - mPressedPosition[0];
                    float dy = ey - mPressedPosition[1];
                    float d = (float)Math.hypot(dy, dx);
                    if(d <= DEFAULT_MAX_CLICK_DISTANCE * mDensity) {
                        if(mOnClickListener != null)
                            mOnClickListener.onClick(this);
                    }
                    startFeatureAnimation(FEATURE_ANIMATION_END);
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if(mPressed) {
                    startFeatureAnimation(FEATURE_ANIMATION_END);
                    mPressed = false;
                    return true;
                }

        }
        return super.onTouchEvent(event);
    }

    /**
     * Set On Click Listener
     * @param l
     */
    @Override
    public void setOnClickListener(OnClickListener l) {
        mOnClickListener = l;
    }

    /**
     * Start Feature Animation
     *
     * @param mode
     */
    private void startFeatureAnimation(final int mode) {
        if(mFeatureAnimationMode == mode)
            return;
        mFeatureAnimationMode = mode;
        switch (mode) {
            case FEATURE_ANIMATION_START:
            case FEATURE_ANIMATION_END:
                mFeatureAnimationStartTime = System.currentTimeMillis();
                updateFeatureAnimation();
                break;
            case FEATURE_ANIMATION_NULL:
                mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Get Animation Frame
     *
     * @return
     */
    private float getAnimationFrame(final float from, final float to) {
        final float frame = Math.min(((System.currentTimeMillis() - mFeatureAnimationStartTime) * 1.0f) / FEATURE_ANIMATION_DURATION, 1.0f);
        switch (mFeatureAnimationMode) {
            case FEATURE_ANIMATION_START:
                return (1.0f - frame) * from + frame * to;
            default:
            case FEATURE_ANIMATION_END:
                return (1.0f - frame) * to + frame * from;
        }
    }

    /**
     * Return true if has animation
     *
     * @return
     */
    private boolean hasAnimation() {
        return (mFeatureAnimationMode == FEATURE_ANIMATION_START ||
                mFeatureAnimationMode == FEATURE_ANIMATION_END);
    }

    /**
     * Update Feature Animation
     */
    private void updateFeatureAnimation() {
        final long time = System.currentTimeMillis() - mFeatureAnimationStartTime;
        if(time <= FEATURE_ANIMATION_DURATION) {
            mHandler.sendEmptyMessage(0);
        }
        invalidate();
    }

    /**
     * On Attached to Window
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        unrecycleImage();
        unrecycleFeatureIcon();
    }

    /**
     * On Detach From Window
     */
    @Override
    protected void onDetachedFromWindow() {
        recycleImage();
        recycleFeatureIcon();
        startFeatureAnimation(FEATURE_ANIMATION_NULL);
        super.onDetachedFromWindow();
    }

    /**
     * On Draw
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Uses
        final float shadowSize = helperGetShadowRadius() * 2;
        final float width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        final float height = getMeasuredHeight() - getPaddingBottom() - getPaddingTop();
        final float cx = width / 2 + getPaddingLeft();
        final float cy = height / 2 + getPaddingTop() - shadowSize / 2;
        final float radius = Math.min(width, height) / 2.0f;
        // Emulate shadow to Android Studio GUI
        if(isInEditMode() && mShadow) {
            mBorderPaint.setColor(SIMULATE_SHADOW_COLOR);
            drawFrame(canvas, cx, cy + 2.0f * mDensity, radius, mBorderPaint);
        }
        // Border
        if(!isInEditMode() && mShadow)
             mBorderPaint.setShadowLayer(4.0f * mDensity, 0, 2.0f * mDensity, mTheme.shadowColor);
        mBorderPaint.setColor(mTheme.borderColor);
        drawFrame(canvas, cx, cy, radius - shadowSize, mBorderPaint);
        if(!isInEditMode())
            mBorderPaint.setShadowLayer(0, 0, 0, 0);
        // Feature or Selectable border
        if(mMode == Mode.FEATURE) {
            final int grayAnimation = (int) getAnimationFrame(0, 20);
            final int shadow = Math.min(Math.max(DEFAULT_FEATURE_MULTIPLY_SHADOW_COLOR, 0), 255);
            final int red = Math.max(shadow - grayAnimation, 0);
            final int green = Math.max(shadow - grayAnimation, 0);
            final int blue = Math.max(shadow - grayAnimation, 0);
            mPhotoPaint.setColorFilter(new PorterDuffColorFilter(Color.argb(255, red, green, blue), PorterDuff.Mode.MULTIPLY));
        } else if(mMode == Mode.SELECTABLE) {
            final int shadow = Math.min(Math.max(DEFAULT_SELECTABLE_MULTIPLY_SHADOW_COLOR, 0), 255);
            final int grayAnimation = (int) getAnimationFrame(0, shadow);
            mPhotoPaint.setColorFilter(new PorterDuffColorFilter(Color.argb(255, 255-grayAnimation, 255-grayAnimation, 255-grayAnimation), PorterDuff.Mode.MULTIPLY));
            mBorderPaint.setColor(Color.argb((int) getAnimationFrame(0, 255), Color.red(mTheme.selectableColor), Color.green(mTheme.selectableColor), Color.blue(mTheme.selectableColor)));
            drawFrame(canvas, cx, cy, radius - shadowSize, mBorderPaint);
        } else {
            mPhotoPaint.setColorFilter(null);
        }
        // Image
        mPhotoPaint.setColor(Color.WHITE);
        final float photoRadius = radius - helperGetBorderRadius() - shadowSize;
        if(mTheme.backgroundColor != mTheme.borderColor) {
            mBorderPaint.setColor(mTheme.backgroundColor);
            drawFrame(canvas, cx, cy, photoRadius, mBorderPaint);
        }
        drawFrame(canvas, cx, cy, photoRadius, mPhotoPaint);
        mPhotoPaint.setColorFilter(null);
        // If feature mode
        if(mMode == Mode.FEATURE) {
            final float frameWidth = mFrame.getWidth();
            final float frameHeight = mFrame.getHeight();
            final Frame.FrameVertex center = mFrame.getCenter();
            float size = radius * 0.45f * Math.min(frameWidth / 2.0f, frameHeight / 2.0f);
            final float featureCX = (cx + center.x * photoRadius);
            final float featureCY = (cy + center.y * photoRadius);
            final float featureShadowY = 2.0f * mDensity;
            // Draw Feature Icon
            if(mFeatureIcon != null) {
                final float animatedSize = size * getAnimationFrame(1.0f, 0.8f);
                final Rect srcRect = mFeatureRect[0];
                final Rect dstRect = mFeatureRect[1];
                srcRect.set(0, 0, mFeatureIcon.getWidth(), mFeatureIcon.getHeight());
                dstRect.set((int) Math.floor(featureCX - animatedSize), (int) Math.floor(featureCY - animatedSize), (int) Math.ceil(featureCX + animatedSize), (int) Math.floor(featureCY + animatedSize));
                mFeaturePaint.setColorFilter(new PorterDuffColorFilter(DEFAULT_FEATURE_SHADOW_COLOR, PorterDuff.Mode.MULTIPLY));
                dstRect.top += featureShadowY;
                dstRect.bottom += featureShadowY;
                canvas.drawBitmap(mFeatureIcon, srcRect, dstRect, mFeaturePaint);
                dstRect.top -= featureShadowY;
                dstRect.bottom -= featureShadowY;
                mFeaturePaint.setColorFilter(new PorterDuffColorFilter(mTheme.featureColor, PorterDuff.Mode.MULTIPLY));
                canvas.drawBitmap(mFeatureIcon, srcRect, dstRect, mFeaturePaint);
            } else
                size = 0;
            // Draw Feature Text
            if(mFeatureText != null) {
                // Normalize Feature Text
                mFeaturePaint.setTextSize(0.25f * radius * Math.min(frameWidth / 2.0f, frameHeight / 2.0f));
                final Rect featureRect = mFeatureRect[0];
                final int featureTextY = (int)(Math.floor(featureCY + size) + mFeaturePaint.getTextSize() - 0.1f * radius);
                final float approximateAnglePercent = (featureTextY - featureCY) / (radius - helperGetBorderRadius() - shadowSize);
                final float approximateMaxTextSize = Math.max((float)(Math.cos((Math.PI / 2) * (approximateAnglePercent+0.05f/** Spacing */)) * radius * 2), 0);
                String finalText = "";
                int totalFinalTextSize = 0;
                for(int i=0; i<mFeatureText.length(); i++) {
                    final String c = "" + mFeatureText.charAt(i);
                    mFeaturePaint.getTextBounds(c, 0, c.length(), featureRect);
                    if((totalFinalTextSize + featureRect.width()) >= approximateMaxTextSize) {
                        finalText += "..";
                        break;
                    }
                    finalText += c;
                    totalFinalTextSize += featureRect.width();
                }
                // Draw Feature Text
                mFeaturePaint.setColorFilter(null);
                mFeaturePaint.getTextBounds(finalText, 0, finalText.length(), featureRect);
                final int featureTextX = (int)(featureCX - featureRect.width() / 2);
                mFeaturePaint.setColor(DEFAULT_FEATURE_SHADOW_COLOR);
                canvas.drawText(finalText, featureTextX, featureTextY + featureShadowY, mFeaturePaint);
                mFeaturePaint.setColor(mTheme.featureColor);
                canvas.drawText(finalText, featureTextX, featureTextY, mFeaturePaint);
            }
        }
    }

    /**
     * Draw frame
     *
     * @param cx
     * @param cy
     * @param radius
     * @param paint
     */
    private void drawFrame(final Canvas canvas, final float cx, final float cy, final float radius, final Paint paint) {
        if(mFrame.mOptimizedFrame == Frame.OPTIMIZED_FRAME_CIRCLE)
            canvas.drawCircle(cx, cy, radius, paint);
        else if(mFrame.mOptimizedFrame == Frame.OPTIMIZED_FRAME_SQUARE) {
            final float left = cx - radius;
            final float top = cy - radius;
            canvas.drawRect(left, top, left + radius * 2, top + radius * 2, paint);
        } else {
            final Path framePath = mFrame.makePath(radius, cx, cy);
            canvas.drawPath(framePath, paint);
        }
    }


    /**
     * Mode
     */
    public enum Mode {

        /** Portrait mode */
        PORTRAIT,
        /** Selectable mode */
        SELECTABLE,
        /** Feature mode */
        FEATURE;

        /**
         * From value
         *
         * @param mode
         * @return
         */
        private static Mode fromValue(final int mode) {
            final Mode[] modeValues = values();
            if(mode < 0 || mode >= modeValues.length)
                throw new ProfileImageViewException("This mode was not recognized.");
            return values()[mode];
        }
    }

    /**
     * Scale Mode
     */
    public enum ScaleMode {

        /** Fit mode */
        FIT,
        /** Center mode */
        CENTER;

        /**
         * From value
         *
         * @param mode
         * @return
         */
        private static ScaleMode fromValue(final int mode) {
            final ScaleMode[] modeValues = values();
            if(mode < 0 || mode >= modeValues.length)
                throw new ProfileImageViewException("This mode was not recognized.");
            return values()[mode];
        }
    }

    /**
     * Frame
     */
    final public static class Frame implements Cloneable {

        /**
         * Frame Vertex
         */
        final public static class FrameVertex implements Cloneable {

            // Variables
            float x;
            float y;

            /**
             * Constructor
             *
             * @param x
             * @param y
             */
            public FrameVertex(final float x, final float y) {
                this.x = x;
                this.y = y;
            }

            /**
             * Clone
             * @return FrameVertex
             */
            public FrameVertex clone() {
                return new FrameVertex(this.x, this.y);
            }
        }

        // Consts
        final private static int OPTIMIZED_FRAME_DISABLED = -1;
        final private static int OPTIMIZED_FRAME_CIRCLE = 0;
        final private static int OPTIMIZED_FRAME_SQUARE = 1;

        // Defaults Shapes
        final public static Frame SHAPE_SQUARE = new Frame();
        final public static Frame SHAPE_CIRCLE = new Frame();
        final public static Frame SHAPE_TRIANGLE = new Frame();
        final public static Frame SHAPE_PENTAGON = new Frame();
        final public static Frame SHAPE_HEXAGON = new Frame();
        final public static Frame SHAPE_HEPTAGON = new Frame();
        final public static Frame SHAPE_OCTAGON = new Frame();
        final public static Frame SHAPE_ENNEAGON = new Frame();
        final public static Frame SHAPE_DECAGON = new Frame();
        final public static Frame SHAPE_STAR = new Frame();
        final public static Frame SHAPE_DIAMOND = new Frame();

        // Initialize defaults
        static {
            // Optimized Circle
            SHAPE_CIRCLE.mOptimizedFrame = OPTIMIZED_FRAME_CIRCLE;
            SHAPE_CIRCLE.setCenterSquareScale(COS45);
            SHAPE_CIRCLE.mNativeFrame = true;
            // Optimized Square
            SHAPE_SQUARE.mOptimizedFrame = OPTIMIZED_FRAME_SQUARE;
            SHAPE_SQUARE.setCenterSquareScale(1.0f);
            SHAPE_SQUARE.mNativeFrame = true;
            // Triangle
            SHAPE_TRIANGLE.addVertex(0, -1);
            SHAPE_TRIANGLE.addVertex(1f, 0.666f);
            SHAPE_TRIANGLE.addVertex(-1f, 0.666f);
            SHAPE_TRIANGLE.setCenterSquareScale(0.39267996f);
            SHAPE_TRIANGLE.mNativeFrame = true;
            // Pentagon
            int c=0;
            double add = -Math.PI / 2.0f;
            for(double i=0; i<Math.PI*2; i+= Math.PI*0.4f) {
                SHAPE_PENTAGON.addVertex((float) Math.cos(i + add), (float) Math.sin(i + add));
                // silly idea :p
                c++;
                if(c == 5)
                    break;
            }
            SHAPE_PENTAGON.setCenterSquareScale(0.5720635f);
            SHAPE_PENTAGON.mNativeFrame = true;
            // Hexagon
            c=0;
            for(double i=0; i<Math.PI*2; i+= Math.PI*0.333333f) {
                SHAPE_HEXAGON.addVertex((float) Math.cos(i + add), (float) Math.sin(i + add));
                // silly idea :p
                c++;
                if(c == 6)
                    break;
            }
            SHAPE_HEXAGON.setCenterSquareScale(0.6123717f);
            SHAPE_HEXAGON.mNativeFrame = true;
            // Heptagon
            c=0;
            for(double i=0; i<Math.PI*2; i+= Math.PI*0.285714f) {
                SHAPE_HEPTAGON.addVertex((float) Math.cos(i + add), (float) Math.sin(i + add));
                // silly idea :p
                c++;
                if(c == 7)
                    break;
            }
            SHAPE_HEPTAGON.setCenterSquareScale(0.6370865f);
            SHAPE_HEPTAGON.mNativeFrame = true;
            // Octagon
            c=0;
            for(double i=0; i<Math.PI*2; i+= Math.PI*0.25f) {
                SHAPE_OCTAGON.addVertex((float) Math.cos(i + add), (float) Math.sin(i + add));
                // silly idea :p
                c++;
                if(c == 8)
                    break;
            }
            SHAPE_OCTAGON.setCenterSquareScale(COS45);
            SHAPE_OCTAGON.mNativeFrame = true;
            // Enneagon
            c=0;
            for(double i=0; i<Math.PI*2; i+= Math.PI*0.222222f) {
                SHAPE_ENNEAGON.addVertex((float) Math.cos(i + add), (float) Math.sin(i + add));
                // silly idea :p
                c++;
                if(c == 9)
                    break;
            }
            SHAPE_ENNEAGON.setCenterSquareScale(0.664464f);
            SHAPE_ENNEAGON.mNativeFrame = true;
            // Decagon
            c=0;
            for(double i=0; i<Math.PI*2; i+= Math.PI*0.2f) {
                SHAPE_DECAGON.addVertex((float) Math.cos(i + add), (float) Math.sin(i + add));
                // silly idea :p
                c++;
                if(c == 10)
                    break;
            }
            SHAPE_DECAGON.setCenterSquareScale(0.6724988f);
            SHAPE_DECAGON.mNativeFrame = true;
            // Star
            SHAPE_STAR.addVertex(0f, -1f);
            SHAPE_STAR.addVertex(0.3f, -0.34f);
            SHAPE_STAR.addVertex(1f, -0.24f);
            SHAPE_STAR.addVertex(0.5f, 0.28f);
            SHAPE_STAR.addVertex(0.6f, 1f);
            SHAPE_STAR.addVertex(0f, 0.64f);
            SHAPE_STAR.addVertex(-0.62f, 1f);
            SHAPE_STAR.addVertex(-0.5f, 0.26f);
            SHAPE_STAR.addVertex(-1f, -0.18f);
            SHAPE_STAR.addVertex(-0.32f, -0.34f);
            SHAPE_STAR.setCenterSquareScale(0.4f);
            SHAPE_STAR.mNativeFrame = true;
            // Diamond
            SHAPE_DIAMOND.addVertex(0, -1);
            SHAPE_DIAMOND.addVertex(1, 0);
            SHAPE_DIAMOND.addVertex(0, 1);
            SHAPE_DIAMOND.addVertex(-1, 0);
            SHAPE_DIAMOND.setCenterSquareScale(0.5f);
            SHAPE_DIAMOND.mNativeFrame = true;
        }


        // Final Private Variables
        final private List<FrameVertex> mVertices = new ArrayList<>();

        // Private Variables
        private float mCenterSquareScale = 1.0f;
        private int mOptimizedFrame = OPTIMIZED_FRAME_DISABLED;
        private boolean mNativeFrame = false;

        /**
         * Add vertex
         *
         * @param x The value will be fixed between [-1, 1]
         * @param y The value will be fixed between [-1, 1]
         */
        final public void addVertex(float x, float y) {
            if(mNativeFrame)
                throw new ProfileImageViewException("You can not change native forms.");
            mVertices.add(new FrameVertex(Math.max(Math.min(x, 1.0f), -1.0f), Math.max(Math.min(y, 1.0f), -1.0f)));
        }

        /**
         * Clear Vertexs
         */
        final public void clearVertices() {
            if(mNativeFrame)
                throw new ProfileImageViewException("You can not change native forms.");
            mVertices.clear();
        }

        /**
         * Get Vertices
         *
         * @return
         */
        final public List<FrameVertex> getVertices() {
            final List<FrameVertex> vertices = new ArrayList<>();
            for(final FrameVertex vertex : mVertices)
                vertices.add(vertex.clone());
            return vertices;
        }

        /**
         * Set center square scale.<br>
         *     Used to multiply the square photo to limit into circle.<br>
         *     <b>eg for circle:</b> Squared limited to circle <b>-</b> Math.cos(45º) / 1 = 0.707~
         * @param centerSquareScale
         */
        final public void setCenterSquareScale(final float centerSquareScale) {
            if(mNativeFrame)
                throw new ProfileImageViewException("You can not change native forms.");
            mCenterSquareScale = centerSquareScale;
        }

        /**
         * Get center square scale<br>
         *     Used to multiply the square photo to limit into circle
         * @return
         */
        final public float getCenterSquareScale() {
            return mCenterSquareScale;
        }

        /**
         * Return true if is native frame
         * @return
         */
        final public boolean isNative() {
            return mNativeFrame;
        }

        /**
         * Make Path
         *
         * @param radius
         * @return
         */
        final private Path makePath(final float radius, final float cx, final float cy) {
            final Path path = new Path();
            if(mVertices.size() <= 2)
                return path;
            final FrameVertex firstVertex = mVertices.get(0);
            path.moveTo(cx + firstVertex.x * radius, cy + firstVertex.y * radius);
            for(int i=1; i<mVertices.size(); i++) {
                final FrameVertex vertex = mVertices.get(i);
                path.lineTo(cx + vertex.x * radius, cy + vertex.y * radius);
            }
            path.lineTo(cx + firstVertex.x * radius, cy + firstVertex.y * radius);
            return path;
        }

        /**
         * Get Rect
         * @return
         */
        final private FrameVertex getCenter() {
            if(mOptimizedFrame != OPTIMIZED_FRAME_DISABLED)
                return new FrameVertex(0, 0);
            return ProfileImageViewUtils.calculateCenter(mVertices);
        }

        /**
         * Get Width
         * @return
         */
        final private float getWidth() {
            if(mOptimizedFrame != OPTIMIZED_FRAME_DISABLED)
                return 2.0f;
            return ProfileImageViewUtils.calculateWidth(mVertices);
        }

        /**
         * Get Height
         * @return
         */
        final private float getHeight() {
            if(mOptimizedFrame != OPTIMIZED_FRAME_DISABLED)
                return 2.0f;
            return ProfileImageViewUtils.calculateHeight(mVertices);
        }

        /**
         * Get DefaultFrom Enum
         * @param ordinal
         * @return
         */
        final static private Frame getDefaultFromEnum(final int ordinal) {
            switch(ordinal) {
                case 0:
                    return SHAPE_TRIANGLE;
                case 1:
                    return SHAPE_SQUARE;
                default:
                case 2:
                    return SHAPE_CIRCLE;
                case 3:
                    return SHAPE_PENTAGON;
                case 4:
                    return SHAPE_HEXAGON;
                case 5:
                    return SHAPE_HEPTAGON;
                case 6:
                    return SHAPE_OCTAGON;
                case 7:
                    return SHAPE_ENNEAGON;
                case 8:
                    return SHAPE_DECAGON;
                case 9:
                    return SHAPE_STAR;
                case 10:
                    return SHAPE_DIAMOND;
            }
        }

        /**
         * Clone
         *
         * @return
         */
        final public Frame clone() {
            final Frame frame = new Frame();
            for(final FrameVertex vertex : mVertices)
                frame.mVertices.add(new FrameVertex(vertex.x, vertex.y));
            frame.mOptimizedFrame = mOptimizedFrame;
            frame.mCenterSquareScale = mCenterSquareScale;
            frame.mNativeFrame = mNativeFrame;
            return frame;
        }
    }


    /**
     * Circular Photo Theme
     */
    final public static class Theme implements Cloneable {

        //
        public float imageScale;
        public float imageScrollX;
        public float imageScrollY;
        public float borderRadius;
        public int borderColor;
        public int backgroundColor;
        public int shadowColor;
        public int featureColor;
        public int selectableColor;

        /**
         * Clone
         * @return
         */
        public Theme clone() {
            final Theme theme = new Theme();
            theme.imageScale = imageScale;
            theme.imageScrollX = imageScrollX;
            theme.imageScrollY = imageScrollY;
            theme.borderRadius = borderRadius;
            theme.borderColor = borderColor;
            theme.backgroundColor = backgroundColor;
            theme.shadowColor = shadowColor;
            theme.featureColor = featureColor;
            theme.selectableColor = selectableColor;
            return theme;
        }
    }

    /**
     * Feature Animation Handler
     */
    final public class FeatureAnimationHandler extends Handler {

        /**
         * Handle Message
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            updateFeatureAnimation();
        }
    }
}
