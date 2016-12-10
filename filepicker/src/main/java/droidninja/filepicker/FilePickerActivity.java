package droidninja.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import droidninja.filepicker.fragments.DocFragment;
import droidninja.filepicker.fragments.DocPickerFragment;
import droidninja.filepicker.fragments.PhotoPickerFragment;
import droidninja.filepicker.models.Photo;
import droidninja.filepicker.utils.FragmentUtil;
import droidninja.filepicker.utils.image.FrescoManager;

public class FilePickerActivity extends AppCompatActivity implements PhotoPickerFragment.PhotoPickerFragmentListener, DocFragment.PhotoPickerFragmentListener,
        PickerManagerListener{

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PickerManager.getInstance().getTheme());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        if (savedInstanceState == null) {
            FrescoManager.init(this);
            initView();
        }
    }

    private void initView() {
        Intent intent = getIntent();
        if(intent!=null)
        {
            setUpToolbar();

            ArrayList<String> selectedPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS);
            type = intent.getIntExtra(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.PHOTO_PICKER);

            setToolbarTitle(0);

            PickerManager.getInstance().setPickerManagerListener(this);
            openSpecificFragment(type,selectedPaths);
        }
    }

    private void setUpToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void openSpecificFragment(int type, ArrayList<String> selectedPaths)
    {
        if(PickerManager.getInstance().getMaxCount()==1)
            selectedPaths.clear();

        if(type==FilePickerConst.PHOTO_PICKER)
        {
            PhotoPickerFragment photoFragment = PhotoPickerFragment.newInstance(selectedPaths);
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        }
        else {
            DocPickerFragment photoFragment = DocPickerFragment.newInstance(selectedPaths);
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        }
    }

    private void setToolbarTitle(int count) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            if(PickerManager.getInstance().getMaxCount()>1)
                actionBar.setTitle(String.format(getString(R.string.attachments_title_text),count,PickerManager.getInstance().getMaxCount()));
            else
            {
                if(type==FilePickerConst.PHOTO_PICKER)
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
            Intent intent = new Intent();

            if(type==FilePickerConst.PHOTO_PICKER)
                intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS, PickerManager.getInstance().getSelectedPhotos());
            else
                intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, PickerManager.getInstance().getSelectedFiles());
            setResult(RESULT_OK, intent);
            finish();

            return true;
        }
        else if(i == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(int currentCount) {
        setToolbarTitle(currentCount);
    }

    @Override
    public void onSingleItemSelected(ArrayList<String> paths) {
        Intent intent = new Intent();
        if(type==FilePickerConst.PHOTO_PICKER)
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS, paths);
        else
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}
