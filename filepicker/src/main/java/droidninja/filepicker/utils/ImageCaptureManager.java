package droidninja.filepicker.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import droidninja.filepicker.PickerManager;

public class ImageCaptureManager {

  private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
  public static final int REQUEST_TAKE_PHOTO = 0x101;

  private String mCurrentPhotoPath;
  private Context mContext;

  public ImageCaptureManager(Context mContext) {
    this.mContext = mContext;
  }

  private File createImageFile() throws IOException {
    // Create an image file name
//    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    if (!storageDir.exists()) {
      if (!storageDir.mkdir()) {
        Log.e("TAG", "Throwing Errors....");
        throw new IOException();
      }
    }

    File image = new File(storageDir, imageFileName);
    //                File.createTempFile(
    //                imageFileName,  /* prefix */
    //                ".jpg",         /* suffix */
    //                storageDir      /* directory */
    //        );

    // Save a file: path for use with ACTION_VIEW intents
    mCurrentPhotoPath = image.getAbsolutePath();
    return image;
  }


  public Intent dispatchTakePictureIntent(Context context) throws IOException {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// Ensure that there's a camera activity to handle the intent
    if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
// Create the File where the photo should go
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        File newFile = createImageFile();
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri photoURI = FileProvider.getUriForFile(context, PickerManager.getInstance().getProviderAuthorities(), newFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
      } else {
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(createImageFile()));
      }
      return takePictureIntent;
    }
    return null;
  }


  public void galleryAddPic() {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

    if (TextUtils.isEmpty(mCurrentPhotoPath)) {
      return;
    }

    File f = new File(mCurrentPhotoPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    mContext.sendBroadcast(mediaScanIntent);

//    notifyMediaStoreScanner(context, f);
  }

  public final void notifyMediaStoreScanner(Context mContext, final File file) {
    try {
      MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
              file.getAbsolutePath(), file.getName(), null);
      mContext.sendBroadcast(new Intent(
              Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }


  public String getCurrentPhotoPath() {
    return mCurrentPhotoPath;
  }


  public void onSaveInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null && mCurrentPhotoPath != null) {
      savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
      mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
    }
  }

}
