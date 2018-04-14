# Android-FilePicker
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--FilePicker-green.svg?style=true)](https://android-arsenal.com/details/1/4044)
 [ ![Latest Version](https://api.bintray.com/packages/droidninja/maven/com.droidninja.filepicker/images/download.svg) ](https://bintray.com/droidninja/maven/com.droidninja.filepicker/_latestVersion)
 
A filepicker which allows to select images and videos with flexibility. It also supports selection of files by specifying its file type. Check out app module for example.

  ![demo](https://image.ibb.co/iRpztv/device_2017_03_10_164003.png)
  ![demo](https://image.ibb.co/m75uRF/device_2017_03_10_163900.png)
  ![demo](https://image.ibb.co/ct4A0a/device_2017_03_10_163835.png)

# Installation

* As of now, It is only available in jCenter(), So just put this in your app dependencies:
```gradle
    compile 'com.droidninja:filepicker:2.1.5'
```

# Note
This Filepicker is based on the MediaStore api provided by android. It checks MediaStore database for a file entry. If your file is not showing in the picker, it means that it is not inserted into MediaStore database yet. There is method in `FilePickerUtils` class named `notifyMediaStore(Context context, String path)` through which you can notify the MediaStore database.
Don't forget to add WRITE_EXTERNAL_STORAGE permission.
  
# Usage
  
  Just include this in your onclick function:
  * For **photopicker**:
 ```java
 FilePickerBuilder.getInstance().setMaxCount(5)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.LibAppTheme)
                .pickPhoto(this);
 ```
If you want to use custom request code, you just have to like this:
  ```java
  FilePickerBuilder.getInstance().setMaxCount(5)
                 .setSelectedFiles(filePaths)
                 .setActivityTheme(R.style.LibAppTheme)
                 .pickPhoto(this, CUSTOM_REQUEST_CODE);
  ```
 
  * For **document picker**:
 ```java
  FilePickerBuilder.getInstance().setMaxCount(10)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.LibAppTheme)
                .pickFile(this);
 ```
 
 If you want to use custom request code, you just have to like this:
   ```java
   FilePickerBuilder.getInstance().setMaxCount(5)
                  .setSelectedFiles(filePaths)
                  .setActivityTheme(R.style.LibAppTheme)
                  .pickFile(this, CUSTOM_REQUEST_CODE);
   ```
  
 
 After this, you will get list of file paths in activity result:
 ```java
 @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode)
            {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if(resultCode== Activity.RESULT_OK && data!=null)
                    {
                        photoPaths = new ArrayList<>();
                        photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    }
                    break;
                case FilePickerConst.REQUEST_CODE_DOC:
                    if(resultCode== Activity.RESULT_OK && data!=null)
                    {
                        docPaths = new ArrayList<>();
                        docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                    }
                    break;
            }
            addThemToView(photoPaths,docPaths);
        }
 ```

 # Builder Methods

**Android FilePicker** now has more flexibility. Supported builder methods are:

Method     | Use
-------- | ---
setMaxCount(int maxCount) | used to specify maximum count of media picks (dont use if you want no limit)
setActivityTheme(int theme)    | used to set theme for toolbar (must be an non-actionbar theme or use LibAppTheme)
setActivityTitle(String title)    | used to set title for toolbar
setSelectedFiles(ArrayList<String> selectedPhotos)     | to show already selected items
enableVideoPicker(boolean status)    | added video picker alongside images
enableImagePicker(boolean status)    | added option to disable image picker
enableSelectAll(boolean status)    | added option to enable/disable select all feature(it will only work with no limit option)
setCameraPlaceholder(int drawable)    | set custom camera drawable
withOrientation(Orientation type)  | In case, if you want to set orientation. (*default=Orientation.UNSPECIFIED*)
showGifs(boolean status)    | to show gifs images in the picker
showFolderView(boolean status)    | if you want to show folder type pick view, enable this. (*Enabled by default*)
enableDocSupport(boolean status)    | If you want to enable/disable default document picker, use this method. (*Enabled by default*)
enableCameraSupport(boolean status)    | to show camera in the picker (*Enabled by default*)
addFileSupport(String title, String[] extensions, @DrawableRes int drawable)    | If you want to specify custom file type, use this method. (*example below*)

If you want to add custom file type picker, use *addFileSupport()* method like this ( for zip support):

 ```java
String zipTypes = {".zip",".rar"};
    addFileSupport("ZIP",zipTypes, R.drawable.ic_zip_icon);
```

#Styling
Just override these styles in your main module to change colors and themes
```xml
<style name="LibAppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="android:colorBackground">@android:color/background_light</item>
        <item name="android:windowBackground">@android:color/white</item>
    </style>

    <style name="PickerTabLayout" parent="Widget.Design.TabLayout">
        <item name="tabBackground">@color/colorPrimary</item>
        <item name="tabGravity">fill</item>
        <item name="tabMaxWidth">0dp</item>
    </style>
    
    <style name="SmoothCheckBoxStyle">
        <item name="color_checked">@color/checkbox_color</item>
        <item name="color_unchecked">@android:color/white</item>
        <item name="color_unchecked_stroke">@color/checkbox_unchecked_color</item>
        <item name="color_tick">@android:color/white</item>
    </style>
```

# Proguard
```
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
```

# Donate

You guys are doing great job by filing bugs and sending pull requests. I am doing everything to maintain this project. If you want to support, your donation is highly appreciated (and I love food, coffee and beer). Thank you!

**PayPal**

* **[Donate $5](https://www.paypal.me/droidninja/5)**: Thank's for creating this project, here's a coffee (or some beer) for you!
* **[Donate $10](https://www.paypal.me/droidninja/10)**: Wow, I am stunned. Let me take you to the movies!
* **[Donate $15](https://www.paypal.me/droidninja/15)**: I really appreciate your work, let's grab some lunch!
* **[Donate $25](https://www.paypal.me/droidninja/25)**: That's some awesome stuff you did right there, dinner is on me!
* **[Donate $50](https://www.paypal.me/droidninja/50)**: I really really want to support this project, great job!
* **[Donate $100](https://www.paypal.me/droidninja/100)**: You are the man! This project saved me hours (if not days) of struggle and hard work, simply awesome!
* **[Donate $2799](https://www.paypal.me/droidninja/2799)**: Go buddy, buy Macbook Pro for yourself!
Of course, you can also choose what you want to donate, all donations are awesome!



 # Credits
  
  Inspired by [PhotoPicker](https://github.com/donglua/PhotoPicker)
  
  [SmoothCheckbox](https://github.com/andyxialm/SmoothCheckBox)
  
# Youtube Demo

  [![Demo](https://img.youtube.com/vi/r3u2uKjN4Ks/0.jpg)](https://www.youtube.com/watch?v=r3u2uKjN4Ks)

# License
```license
Copyright 2016 Arun Sharma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
