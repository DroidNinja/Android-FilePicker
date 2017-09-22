package droidninja.filepicker;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import droidninja.filepicker.utils.Orientation;

/**
 * Created by droidNinja on 22/07/17.
 */

public abstract class BaseFilePickerActivity extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState, @LayoutRes int layout) {
        super.onCreate(savedInstanceState);
        setTheme(PickerManager.getInstance().getTheme());
        setContentView(layout);

        //set orientation
        Orientation orientation = PickerManager.getInstance().getOrientation();
        if(orientation==Orientation.PORTRAIT_ONLY)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if(orientation==Orientation.LANDSCAPE_ONLY)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initView();
    }

    protected abstract void initView();
}
