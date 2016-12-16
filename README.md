# Android-FilePicker
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Android--FilePicker-green.svg?style=true)](https://android-arsenal.com/details/1/4044)
 [ ![Latest Version](https://api.bintray.com/packages/droidninja/maven/com.droidninja.filepicker/images/download.svg) ](https://bintray.com/droidninja/maven/com.droidninja.filepicker/_latestVersion)
 
A photopicker to select and click photos and document picker to select different types of documents.

  ![demo](http://i.imgur.com/WxWuJKn.png)
  ![demo](http://i.imgur.com/TDIBxLY.png)
  ![demo](http://i.imgur.com/HrmUEvS.png)

# Installation

* As of now, It is only available in jCenter(), So just put this in your app dependencies:
```gradle
    compile 'com.droidninja:filepicker:1.0.8'
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
                .pickDocument(this);
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
