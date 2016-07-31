package droidninja.filepicker;

import android.app.Application;

import droidninja.filepicker.utils.image.FrescoManager;

/**
 * Created by droidNinja on 14/06/16.
 */
public class FilePickerDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FrescoManager.init(this);
    }
}
