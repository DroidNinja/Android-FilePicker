package droidninja.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import droidninja.filepicker.fragments.DocFragment;
import droidninja.filepicker.fragments.DocPickerFragment;
import droidninja.filepicker.fragments.FolderPickerFragment;
import droidninja.filepicker.fragments.MediaPickerFragment;
import droidninja.filepicker.fragments.PhotoPickerFragmentListener;
import droidninja.filepicker.utils.FragmentUtil;

import java.util.ArrayList;

public class FilePickerActivity extends BaseFilePickerActivity implements
        PhotoPickerFragmentListener,
        DocFragment.DocFragmentListener,
        DocPickerFragment.DocPickerFragmentListener,
        MediaPickerFragment.MediaPickerFragmentListener,
        FolderPickerFragment.FolderPickerFragmentListener {

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState,R.layout.activity_file_picker);
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            if(getSupportActionBar()!=null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            ArrayList<String> selectedPaths = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            type = intent.getIntExtra(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER);

            if(selectedPaths!=null) {

                if (PickerManager.getInstance().getMaxCount() == 1) {
                    selectedPaths.clear();
                }

                PickerManager.getInstance().clearSelections();
                if (type == FilePickerConst.MEDIA_PICKER) {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_MEDIA);
                } else if (type == FilePickerConst.DOC_PICKER) {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_DOCUMENT);
                } else {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_FOLDER);
                }
            }
            else
                selectedPaths = new ArrayList<>();

            setToolbarTitle(PickerManager.getInstance().getCurrentCount());
            openSpecificFragment(type, selectedPaths);
        }
    }

    private void setToolbarTitle(int count) {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            int maxCount = PickerManager.getInstance().getMaxCount();
            if(maxCount == -1 && count>0)
                actionBar.setTitle(String.format(getString(R.string.attachments_num), count));
            else if (maxCount > 0 && count>0)
                actionBar.setTitle(String.format(getString(R.string.attachments_title_text), count, maxCount));
            else {
                if (type == FilePickerConst.MEDIA_PICKER)
                    actionBar.setTitle(R.string.select_photo_text);
                else if (type == FilePickerConst.DOC_PICKER)
                    actionBar.setTitle(R.string.select_doc_text);
                else
                    actionBar.setTitle(R.string.select_folder);
            }
        }

    }

    private void openSpecificFragment(int type, @Nullable ArrayList<String> selectedPaths) {
        if (type == FilePickerConst.MEDIA_PICKER) {
            MediaPickerFragment photoFragment = MediaPickerFragment.newInstance();
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        } else if (type == FilePickerConst.DOC_PICKER) {
            if (PickerManager.getInstance().isDocSupport())
                PickerManager.getInstance().addDocTypes();

            DocPickerFragment photoFragment = DocPickerFragment.newInstance();
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        } else {
            FolderPickerFragment folderPickerFragment = new FolderPickerFragment();
            folderPickerFragment.setArguments(new Bundle());
            FragmentUtil.addFragment(this, R.id.container, folderPickerFragment);
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
            if (type == FilePickerConst.MEDIA_PICKER) {
                returnData(PickerManager.getInstance().getSelectedPhotos());
            } else if (type == FilePickerConst.DOC_PICKER) {
                returnData(PickerManager.getInstance().getSelectedFiles());
            } else {
                returnData(PickerManager.getInstance().getSelectedFolder());
            }

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
        PickerManager.getInstance().reset();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE_MEDIA_DETAIL:
                if(resultCode== Activity.RESULT_OK)
                {
                    if (type == FilePickerConst.MEDIA_PICKER)
                        returnData(PickerManager.getInstance().getSelectedPhotos());
                    else
                        returnData(PickerManager.getInstance().getSelectedFiles());
                }
                else
                {
                    setToolbarTitle(PickerManager.getInstance().getCurrentCount());
                }
                break;
        }
    }

    private void returnData(ArrayList<String> paths) {
        Intent intent = new Intent();
        if (type == FilePickerConst.MEDIA_PICKER) {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, paths);
        } else if (type == FilePickerConst.DOC_PICKER) {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths);
        } else {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_FOLDER, paths);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemSelected() {
        int currentCount = PickerManager.getInstance().getCurrentCount();
        setToolbarTitle(currentCount);

        if (PickerManager.getInstance().getMaxCount() == 1 && currentCount == 1)
            returnData(type == FilePickerConst.MEDIA_PICKER ? PickerManager.getInstance().getSelectedPhotos()
                    : type == FilePickerConst.DOC_PICKER ? PickerManager.getInstance().getSelectedFiles()
                    : PickerManager.getInstance().getSelectedFolder());
    }
}
