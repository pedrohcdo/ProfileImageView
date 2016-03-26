The Profile Image View can be used to display profile picture or photo with frame, it is an advanced, flexible and optimized.

Maven

Gradle

This library is available in maven and jcenter, add this code in Android Studio and Build:
---------------
    
    repositories {
        maven()
        jcenter()
    }
    
    dependencies {
        compile ''org.pedrohc.profileimageview:profileimv:2.0.0'
    }

Examples Screenshot (Included in project)
---------------

Gallery Example:
  
Others Example:

 

About
---------------
This view regards most contracts of a View, is a list of supported features

- **Attributes:**
  - **android:background**: A View is not restricted to this attribute, then this attribute can be used in parallel without interfering with the operation of the View.
  - **android:padding**: Attribute working properly
- **Bitmap and Optimizations:**
  - This view does not create any scaled Bitmap and is well optimized avoiding redundant codes and slow methods.
  - If any image is assigned a resource in the xml or code, the view automatically release the memory image after being removed from the window thus avoiding unnecessary use of memory.
- **Visual Studio Design:** This view is displayed correctly on Android Studio preview.
Tutorial

Tutorial:
---------------
Eg. of usage in .xml:

  <material.teste.teste.photo.ProfileImageView
      android:layout_width="150dp"
      android:layout_height="150dp"
      app:mode="selectable"
      app:shadow="true"
      app:border="true"
      app:borderRadius="4dp"
      app:borderColor="#FFFFFF"
      app:shadowColor="#AA000000"
      app:image="@drawable/image_test"
      app:imageScaleMode="fit"
      app:frame="square" />

To use the View you will have a list of optional attributes:

- **app:frame**: Shapes used to frame the image, among them are:
  - circle
  - square
  - pentagon
  - hexagon
  - heptagon
  - octagon
  - enneagon
  - decagon
  - diamond
  - star
  - triangle
- **app:shadow**: To enable shadow use "true" and to disable use "false".
- **app:border**: To enable border use "true" and to disable use "false".
- **app:image**: Reference to image. eg: "@drawable/image"
- **app:mode**: The view mode of operation, there are 3:
  - portrait -> Normal operation, without any animation or interaction feature.
  - feature -> This mode is used for animations such as buttons addition profile picture, it also includes animation interaction. This attribute works in conjunction with all attributes that include "feature" in its name.
  - selectable -> This mode is used for selecting and includes animation interaction. This attribute works in conjunction with all attributes that include "selectable" in its name.
- **app:featureIcon**: Set an icon for the mode "feature" that will be displayed over the image.
- **app:featureText**: Set an text for the mode "feature" that will be displayed over the image.
- **app:featureColor**: Set an color for the mode "feature", this color is used to color the icon and text.
- **app:selectableColor**: Set an color for the mode "feature", This color is used to color the edge when the view receiving an interaction.
- **app:imageScale**: Image scale.
- **app:imageScrollX**: If the image scale is larger than 1.0, it can be adjusted using this attribute, use values between -1.0 and 1.0 which are the ends and to the center use 0.0.
- **app:imageScrollY**: If the image scale is larger than 1.0, it can be adjusted using this attribute, use values between -1.0 and 1.0 which are the ends and to the center use 0.0.
- **app:borderRadius**: Border Radius in dp.
- **app:borderColor**: Border Color.
- **app:backgroundColor**: Used to color the background when an image has a transparent background or is adjusted to maintain the appearance.
- **app:shadowColor**: Shadow Color.
- **app:imageScaleMode**: Standard image scale, among them are:
  - center: Adjust the image across Frame
  - fit: Adjust the image across View

Eg. of usage in .java:

    float density = context.getResources().getDisplayMetrics().density;
    Bitmap image = ..
    Bitmap featureIcon = ..

    ProfileImageView.Theme theme = new ProfileImageView.Theme();
    theme.imageScale = 1;
    theme.imageScrollX = 0;
    theme.imageScrollY = 0;
    theme.borderRadius = 2 * density;
    theme.borderColor = Color.WHITE;
    theme.backgroundColor = Color.WHITE;
    theme.shadowColor = Color.BLACK;
    theme.featureColor = 0xFF58B094;
    theme.selectableColor = 0xFF58B094;

    ProfileImageView riv = new ProfileImageView(context);
    riv.setMode(ProfileImageView.Mode.FEATURE);
    riv.setScaleMode(ProfileImageView.Mode.FIT);
    riv.setImage(image);
    riv.setTheme(theme);
    riv.setFeatureIcon(featureIcon);
    riv.setFrame(ProfileImageView.Frame.CIRCLE);
    riv.setBorder(true);
    riv.setShadow(true);

Bests Pratices
---------------
- For many galleries or view use bitmaps with the "setImage" or "setFeatureImage" and always recycle when not in use.
- Do not use large images, this can cause an overload on the heap.

Changelog
---------------
2.0.0
First release.

Licence
---------------
Copyright - Pedro H. Chaves

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Created by Pedro on 18/03/2016.
