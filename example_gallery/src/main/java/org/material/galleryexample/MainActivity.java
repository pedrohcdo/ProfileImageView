package org.material.galleryexample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.material.profileimv.ProfileImageView;
import org.material.profileimv.ProfileImageViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Example
 */
public class MainActivity extends AppCompatActivity {

    List<Bitmap> mBitmaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBitmaps.add(ProfileImageViewUtils.decodeFromResource(getResources(), R.drawable.g));
        mBitmaps.add(ProfileImageViewUtils.decodeFromResource(getResources(), R.drawable.g2));
        mBitmaps.add(ProfileImageViewUtils.decodeFromResource(getResources(), R.drawable.set));

        List<SimpleListModel> models1 = new ArrayList<>();
        models1.add(new SimpleListModel(mBitmaps.get(0), ProfileImageView.Frame.SHAPE_SQUARE, ProfileImageView.Mode.SELECTABLE));
        models1.add(new SimpleListModel(mBitmaps.get(1), ProfileImageView.Frame.SHAPE_CIRCLE, ProfileImageView.Mode.SELECTABLE));
        models1.add(new SimpleListModel(mBitmaps.get(0), ProfileImageView.Frame.SHAPE_DIAMOND, ProfileImageView.Mode.SELECTABLE));
        models1.add(new SimpleListModel(mBitmaps.get(1), ProfileImageView.Frame.SHAPE_PENTAGON, ProfileImageView.Mode.SELECTABLE));
        models1.add(new SimpleListModel(mBitmaps.get(0), ProfileImageView.Frame.SHAPE_HEXAGON, ProfileImageView.Mode.SELECTABLE));

        RecyclerView list1 = (RecyclerView) findViewById(R.id.list_1);
        list1.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        list1.setAdapter(new SimpleListAdapter(this, models1));

        List<SimpleListModel> models2 = new ArrayList<>();
        models2.add(new SimpleListModel(mBitmaps.get(0), mBitmaps.get(2), "Set", ProfileImageView.Frame.SHAPE_SQUARE, ProfileImageView.Mode.FEATURE));
        models2.add(new SimpleListModel(mBitmaps.get(1), mBitmaps.get(2), "Set", ProfileImageView.Frame.SHAPE_CIRCLE, ProfileImageView.Mode.FEATURE));
        models2.add(new SimpleListModel(mBitmaps.get(0), mBitmaps.get(2), "Set", ProfileImageView.Frame.SHAPE_DIAMOND, ProfileImageView.Mode.FEATURE));
        models2.add(new SimpleListModel(mBitmaps.get(1), mBitmaps.get(2), "Set", ProfileImageView.Frame.SHAPE_PENTAGON, ProfileImageView.Mode.FEATURE));
        models2.add(new SimpleListModel(mBitmaps.get(0), mBitmaps.get(2), "Set", ProfileImageView.Frame.SHAPE_HEXAGON, ProfileImageView.Mode.FEATURE));

        RecyclerView list2 = (RecyclerView) findViewById(R.id.list_2);
        list2.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        list2.setAdapter(new SimpleListAdapter(this, models2));

        List<SimpleListModel> models3 = new ArrayList<>();
        models3.add(new SimpleListModel(mBitmaps.get(0), mBitmaps.get(1), "Set", ProfileImageView.Frame.SHAPE_SQUARE, ProfileImageView.Mode.PORTRAIT));
        models3.add(new SimpleListModel(mBitmaps.get(1), mBitmaps.get(1), "Set", ProfileImageView.Frame.SHAPE_CIRCLE, ProfileImageView.Mode.PORTRAIT));
        models3.add(new SimpleListModel(mBitmaps.get(0), mBitmaps.get(1), "Set", ProfileImageView.Frame.SHAPE_DIAMOND, ProfileImageView.Mode.PORTRAIT));
        models3.add(new SimpleListModel(mBitmaps.get(1), mBitmaps.get(1), "Set", ProfileImageView.Frame.SHAPE_PENTAGON, ProfileImageView.Mode.PORTRAIT));
        models3.add(new SimpleListModel(mBitmaps.get(0), mBitmaps.get(1), "Set", ProfileImageView.Frame.SHAPE_HEXAGON, ProfileImageView.Mode.PORTRAIT));

        RecyclerView list3 = (RecyclerView) findViewById(R.id.list_3);
        list3.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
        list3.setAdapter(new SimpleListAdapter(this, models3));
    }

    @Override
    protected void onDestroy() {
        for(Bitmap bitmap : mBitmaps)
            bitmap.recycle();
        super.onDestroy();
    }
}
