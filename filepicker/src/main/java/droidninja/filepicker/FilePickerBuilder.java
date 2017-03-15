package droidninja.filepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import droidninja.filepicker.models.FileType;

/**
 * Created by droidNinja on 29/07/16.
 */
public class FilePickerBuilder {

    private final Bundle mPickerOptionsBundle;
    private final Context context;

    public FilePickerBuilder(Context context)
    {
        mPickerOptionsBundle = new Bundle();
        this.context = context;
    }

    public static FilePickerBuilder getInstance(Context context)
    {
        return new FilePickerBuilder(context);
    }

    public FilePickerBuilder setMaxCount(int maxCount)
    {
        PickerManager.getInstance(context).setMaxCount(maxCount);
        return this;
    }

    public FilePickerBuilder setActivityTheme(int theme)
    {
        PickerManager.getInstance(context).setTheme(theme);
        return this;
    }

    public FilePickerBuilder setSelectedFiles(ArrayList<String> selectedPhotos)
    {
        mPickerOptionsBundle.putStringArrayList(FilePickerConst.KEY_SELECTED_MEDIA, selectedPhotos);
        return this;
    }

    public FilePickerBuilder addVideoPicker()
    {
        PickerManager.getInstance(context).setShowVideos(true);
        return this;
    }

    public FilePickerBuilder showGifs(boolean status)
    {
        PickerManager.getInstance(context).setShowGif(status);
        return this;
    }

    public FilePickerBuilder showFolderView(boolean status)
    {
        PickerManager.getInstance(context).setShowFolderView(status);
        return this;
    }

    public FilePickerBuilder enableDocSupport(boolean status)
    {
        PickerManager.getInstance(context).setDocSupport(status);
        return this;
    }

    public FilePickerBuilder enableCameraSupport(boolean status)
    {
        PickerManager.getInstance(context).setEnableCamera(status);
        return this;
    }

    public FilePickerBuilder enableOrientation(boolean status)
    {
        PickerManager.getInstance(context).setEnableOrientation(status);
        return this;
    }

    public FilePickerBuilder addFileSupport(String title, String[] extensions, @DrawableRes int drawable)
    {
        PickerManager.getInstance(context).addFileType(new FileType(title,extensions,drawable));
        return this;
    }

    public FilePickerBuilder addFileSupport(String title, String[] extensions)
    {
        PickerManager.getInstance(context).addFileType(new FileType(title,extensions,0));
        return this;
    }

    public void pickPhoto(Activity context)
    {
       mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE,FilePickerConst.MEDIA_PICKER);
        start(context,FilePickerConst.MEDIA_PICKER);
    }

    public void pickPhoto(Fragment context)
    {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE,FilePickerConst.MEDIA_PICKER);
        start(context,FilePickerConst.MEDIA_PICKER);
    }

    public void pickFile(Activity context)
    {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE,FilePickerConst.DOC_PICKER);
        start(context,FilePickerConst.DOC_PICKER);
    }

    public void pickFile(Fragment context)
    {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE,FilePickerConst.DOC_PICKER);
        start(context,FilePickerConst.DOC_PICKER);
    }

    private void start(Activity context, int pickerType)
    {
        Intent intent = new Intent(context, FilePickerActivity.class);
        intent.putExtras(mPickerOptionsBundle);

        if(pickerType==FilePickerConst.MEDIA_PICKER)
            context.startActivityForResult(intent,FilePickerConst.REQUEST_CODE_PHOTO);
        else
            context.startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

    private void start(Fragment fragment, int pickerType)
    {
        Intent intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
        intent.putExtras(mPickerOptionsBundle);
        if(pickerType==FilePickerConst.MEDIA_PICKER)
            fragment.startActivityForResult(intent,FilePickerConst.REQUEST_CODE_PHOTO);
        else
            fragment.startActivityForResult(intent,FilePickerConst.REQUEST_CODE_DOC);
    }

}
