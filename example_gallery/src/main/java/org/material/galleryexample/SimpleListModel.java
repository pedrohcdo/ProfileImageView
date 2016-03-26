package org.material.galleryexample;

import android.graphics.Bitmap;

import org.material.profileimv.ProfileImageView;

/**
 * Created by Pedro on 23/03/2016.
 */
public class SimpleListModel {

    Bitmap image;
    Bitmap featureIcon;
    String featureText;
    ProfileImageView.Frame frame;
    ProfileImageView.Mode mode;

    public  SimpleListModel(Bitmap image, ProfileImageView.Frame frame, ProfileImageView.Mode mode) {
        this.image = image;
        this.frame = frame;
        this.mode = mode;
    }

    public  SimpleListModel(Bitmap image, Bitmap featureIcon, String featureText, ProfileImageView.Frame frame, ProfileImageView.Mode mode) {
        this.image = image;
        this.featureIcon = featureIcon;
        this.featureText = featureText;
        this.frame = frame;
        this.mode = mode;
    }
}
