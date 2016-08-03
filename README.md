#Android-FilePicker

A photopicker to select and click photos and document picker to select different types of documents.

  ![demo](http://i.imgur.com/WxWuJKn.png)
  ![demo](http://i.imgur.com/TDIBxLY.png)
  ![demo](http://i.imgur.com/HrmUEvS.png)

***Installation***

* As of now, It is only available in jCenter(), So just put this in your app dependencies:
```gradle
    compile 'com.droidninja:filepicker:1.0.0'
```

* If you are using Application class in the manifest file (android:name), you need to include **tools:replace="android:name"** in the <application> tag. e.g
```xml
<application
        android:name=".ApplicationClass"
        android:icon="@drawable/ic_launcher"
        tools:replace="android:name">
        ......
        </application>
```
  
  ***Usage***
  
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE:
                if(resultCode==RESULT_OK && data!=null)
                {
                    filePaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS);
                    //use them anywhere
                }
        }
    }
 ```
  
  **Credits**
  
  Inspired by [PhotoPicker](https://github.com/donglua/PhotoPicker)
  
  [SmoothCheckbox](https://github.com/andyxialm/SmoothCheckBox)
  
  **Youtube Demo**

  [![Demo](https://img.youtube.com/vi/r3u2uKjN4Ks/0.jpg)](https://www.youtube.com/watch?v=r3u2uKjN4Ks)


