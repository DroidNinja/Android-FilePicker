package droidninja.filepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;
import java.util.ArrayList;

/**
 * Created by droidNinja on 29/07/16.
 */
public class FilePickerBuilder {

  private final Bundle mPickerOptionsBundle;

  public FilePickerBuilder() {
    mPickerOptionsBundle = new Bundle();
  }

  public static FilePickerBuilder getInstance() {
    return new FilePickerBuilder();
  }

  public FilePickerBuilder setMaxCount(int maxCount) {
    PickerManager.getInstance().setMaxCount(maxCount);
    return this;
  }

  public FilePickerBuilder setActivityTheme(int theme) {
    PickerManager.getInstance().setTheme(theme);
    return this;
  }

  public FilePickerBuilder setActivityTitle(String title) {
    PickerManager.getInstance().setTitle(title);
    return this;
  }

  public FilePickerBuilder setSelectedFiles(ArrayList<String> selectedPhotos) {
    mPickerOptionsBundle.putStringArrayList(FilePickerConst.KEY_SELECTED_MEDIA, selectedPhotos);
    return this;
  }

  public FilePickerBuilder enableVideoPicker(boolean status) {
    PickerManager.getInstance().setShowVideos(status);
    return this;
  }

  public FilePickerBuilder enableImagePicker(boolean status) {
    PickerManager.getInstance().setShowImages(status);
    return this;
  }

  public FilePickerBuilder enableSelectAll(boolean status) {
    PickerManager.getInstance().enableSelectAll(status);
    return this;
  }

  public FilePickerBuilder setCameraPlaceholder(@DrawableRes int drawable) {
    PickerManager.getInstance().setCameraDrawable(drawable);
    return this;
  }

  public FilePickerBuilder showGifs(boolean status) {
    PickerManager.getInstance().setShowGif(status);
    return this;
  }

  public FilePickerBuilder showFolderView(boolean status) {
    PickerManager.getInstance().setShowFolderView(status);
    return this;
  }

  public FilePickerBuilder enableDocSupport(boolean status) {
    PickerManager.getInstance().setDocSupport(status);
    return this;
  }

  public FilePickerBuilder enableCameraSupport(boolean status) {
    PickerManager.getInstance().setEnableCamera(status);
    return this;
  }

  public FilePickerBuilder withOrientation(Orientation orientation) {
    PickerManager.getInstance().setOrientation(orientation);
    return this;
  }

  public FilePickerBuilder addFileSupport(String title, String[] extensions,
      @DrawableRes int drawable) {
    PickerManager.getInstance().addFileType(new FileType(title, extensions, drawable));
    return this;
  }

  public FilePickerBuilder addFileSupport(String title, String[] extensions) {
    PickerManager.getInstance().addFileType(new FileType(title, extensions, 0));
    return this;
  }

  public FilePickerBuilder sortDocumentsBy(SortingTypes type) {
    PickerManager.getInstance().setSortingType(type);
    return this;
  }

  public void pickPhoto(Activity context) {
    mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER);
    start(context, FilePickerConst.MEDIA_PICKER);
  }

  public void pickPhoto(Fragment context) {
    mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER);
    start(context, FilePickerConst.MEDIA_PICKER);
  }

  public void pickFile(Activity context) {
    mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER);
    start(context, FilePickerConst.DOC_PICKER);
  }

  public void pickFile(Fragment context) {
    mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER);
    start(context, FilePickerConst.DOC_PICKER);
  }

  private void start(Activity context, int pickerType) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(context, FilePickerConst.PERMISSIONS_FILE_PICKER)
          != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(context, context.getResources().getString(R.string.permission_filepicker_rationale),
            Toast.LENGTH_SHORT).show();
        return;
      }
    }

    PickerManager.getInstance()
        .setProviderAuthorities(
            context.getApplicationContext().getPackageName() + ".droidninja.filepicker.provider");

    Intent intent = new Intent(context, FilePickerActivity.class);
    intent.putExtras(mPickerOptionsBundle);

    if (pickerType == FilePickerConst.MEDIA_PICKER) {
      context.startActivityForResult(intent, FilePickerConst.REQUEST_CODE_PHOTO);
    } else {
      context.startActivityForResult(intent, FilePickerConst.REQUEST_CODE_DOC);
    }
  }

  private void start(Fragment fragment, int pickerType) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (ContextCompat.checkSelfPermission(fragment.getContext(), FilePickerConst.PERMISSIONS_FILE_PICKER)
          != PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(fragment.getContext(), fragment.getContext().getResources().getString(R.string.permission_filepicker_rationale),
            Toast.LENGTH_SHORT).show();
        return;
      }
    }

    PickerManager.getInstance()
        .setProviderAuthorities(fragment.getContext().getApplicationContext().getPackageName()
            + ".droidninja.filepicker.provider");

    Intent intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
    intent.putExtras(mPickerOptionsBundle);
    if (pickerType == FilePickerConst.MEDIA_PICKER) {
      fragment.startActivityForResult(intent, FilePickerConst.REQUEST_CODE_PHOTO);
    } else {
      fragment.startActivityForResult(intent, FilePickerConst.REQUEST_CODE_DOC);
    }
  }
}
