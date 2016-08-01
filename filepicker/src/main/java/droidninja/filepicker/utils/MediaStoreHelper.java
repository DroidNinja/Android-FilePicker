package droidninja.filepicker.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import droidninja.filepicker.cursors.DocScannerTask;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.cursors.loadercallbacks.PhotoDirLoaderCallbacks;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.PhotoDirectory;

public class MediaStoreHelper {

  public static void getPhotoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    activity.getSupportLoaderManager()
        .initLoader(0, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getDocs(FragmentActivity activity, FileResultCallback<Document> fileResultCallback)
  {
    new DocScannerTask(activity,fileResultCallback).execute();
  }
}