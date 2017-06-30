package droidninja.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import droidninja.filepicker.fragments.DocFragment;
import droidninja.filepicker.fragments.DocPickerFragment;
import droidninja.filepicker.fragments.MediaDetailPickerFragment;
import droidninja.filepicker.fragments.MediaFolderPickerFragment;
import droidninja.filepicker.fragments.MediaPickerFragment;
import droidninja.filepicker.utils.FragmentUtil;

public class FilePickerActivity extends AppCompatActivity implements
        MediaDetailPickerFragment.PhotoPickerFragmentListener,
        DocFragment.DocFragmentListener,
        DocPickerFragment.DocPickerFragmentListener,
        MediaFolderPickerFragment.PhotoPickerFragmentListener,
        MediaPickerFragment.MediaPickerFragmentListener {

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(PickerManager.getInstance().getTheme());
        setContentView(R.layout.activity_file_picker);
        setOrientation();
        initView();
    }

    private void setOrientation() {
        switch (PickerManager.getInstance().getOrientation()) {
            case ONLY_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //default
                break;
            case ONLY_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case PORTRAIT_AND_LASCAPE:
                //do nothing
                break;
        }
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ArrayList<String> selectedPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            type = intent.getIntExtra(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER);

            if (selectedPaths != null) {
                if (type == FilePickerConst.MEDIA_PICKER) {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_MEDIA);
                } else {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_DOCUMENT);
                }
            }

            setToolbarTitle(PickerManager.getInstance().getCurrentCount());
            openSpecificFragment(type, selectedPaths);
        }
    }

    private void openSpecificFragment(int type, @Nullable ArrayList<String> selectedPaths) {
        if (selectedPaths == null) {
            selectedPaths = new ArrayList<>();
        }

        if (PickerManager.getInstance().getMaxCount() == 1) {
            selectedPaths.clear();
        }

        if (type == FilePickerConst.MEDIA_PICKER) {
            MediaPickerFragment photoFragment = MediaPickerFragment.newInstance();
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        } else {
            if (PickerManager.getInstance().isDocSupport())
                PickerManager.getInstance().addDocTypes();

            DocPickerFragment photoFragment = DocPickerFragment.newInstance(selectedPaths);
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        }
    }

    private void setToolbarTitle(int count) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (PickerManager.getInstance().getMaxCount() > 1)
                actionBar.setTitle(String.format(getString(R.string.attachments_title_text), count, PickerManager.getInstance().getMaxCount()));
            else {
                if (type == FilePickerConst.MEDIA_PICKER)
                    actionBar.setTitle(R.string.select_photo_text);
                else
                    actionBar.setTitle(R.string.select_doc_text);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_done) {
            if (type == FilePickerConst.MEDIA_PICKER)
                returnData(PickerManager.getInstance().getSelectedPhotos());
            else
                returnData(PickerManager.getInstance().getSelectedFiles());

            return true;
        } else if (i == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_MEDIA_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    if (type == FilePickerConst.MEDIA_PICKER)
                        returnData(PickerManager.getInstance().getSelectedPhotos());
                    else
                        returnData(PickerManager.getInstance().getSelectedFiles());
                } else {
                    setToolbarTitle(PickerManager.getInstance().getCurrentCount());
                }
                break;
        }
    }

    private void returnData(ArrayList<String> paths) {
        Intent intent = new Intent();
        if (type == FilePickerConst.MEDIA_PICKER)
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, paths);
        else
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemSelected() {
        setToolbarTitle(PickerManager.getInstance().getCurrentCount());

        if (PickerManager.getInstance().getMaxCount() == 1)
            returnData(type == FilePickerConst.FILE_TYPE_MEDIA ? PickerManager.getInstance().getSelectedPhotos() : PickerManager.getInstance().getSelectedFiles());
    }
}
