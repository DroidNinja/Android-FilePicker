package droidninja.filepicker.cursors;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.content.CursorLoader;

import droidninja.filepicker.FilePickerConst;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

public class PhotoDirectoryLoader extends CursorLoader {

  final String[] IMAGE_PROJECTION = {
      Media._ID,
      Media.DATA,
      Media.BUCKET_ID,
      Media.BUCKET_DISPLAY_NAME,
      Media.DATE_ADDED,
          Media.TITLE
  };

  public PhotoDirectoryLoader(Context context, Bundle args) {
    super(context);
    String bucketId = args.getString(FilePickerConst.EXTRA_BUCKET_ID, null);
    int mediaType = args.getInt(FilePickerConst.EXTRA_FILE_TYPE, FilePickerConst.MEDIA_TYPE_IMAGE);

    setProjection(null);
    setUri(MediaStore.Files.getContentUri("external"));
    setSortOrder(Media._ID + " DESC");

    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

    if(mediaType==FilePickerConst.MEDIA_TYPE_VIDEO)
    {
        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
              + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    }

    if(bucketId!=null)
      selection += " AND " + Media.BUCKET_ID + "='" + bucketId + "'";

    setSelection(selection);

  }


  private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
    super(context, uri, projection, selection, selectionArgs, sortOrder);
  }


}