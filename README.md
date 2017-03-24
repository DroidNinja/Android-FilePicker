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
    compile 'com.droidninja:filepicker:2.0.3'
```
  
 # Usage
  
  Just include this in your onclick function:
  * For **photopicker**:
 ```java
 FilePickerBuilder.getInstance().setMaxCount(5)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .pickPhoto(this);
 ```
 
  * For **document picker**:
 ```java
  FilePickerBuilder.getInstance().setMaxCount(10)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .pickFile(this);
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
                        photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
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
setMaxCount(int maxCount) | used to specify maximum count of media picks
setActivityTheme(int theme)    | used to set theme for toolbar (must be an actionbar theme)
setSelectedFiles(ArrayList<String> selectedPhotos)     | to show already selected items
addVideoPicker()    | added video picker alongside images
enableOrientation(boolean status)  | In case, if you want to disable orientation (*disabled by default*)
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
