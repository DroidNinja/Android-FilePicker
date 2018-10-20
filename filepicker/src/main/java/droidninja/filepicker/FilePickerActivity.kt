package droidninja.filepicker

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import droidninja.filepicker.fragments.DocFragment
import droidninja.filepicker.fragments.DocPickerFragment
import droidninja.filepicker.fragments.MediaPickerFragment
import droidninja.filepicker.fragments.PhotoPickerFragmentListener
import droidninja.filepicker.utils.FragmentUtil
import java.util.ArrayList

class FilePickerActivity : BaseFilePickerActivity(), PhotoPickerFragmentListener, DocFragment.DocFragmentListener, DocPickerFragment.DocPickerFragmentListener, MediaPickerFragment.MediaPickerFragmentListener {
    private var type: Int = 0

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_file_picker)
    }

    override fun initView() {
        val intent = intent
        if (intent != null) {
            var selectedPaths: ArrayList<String>? = intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
            type = intent.getIntExtra(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)

            if (selectedPaths != null) {

                if (PickerManager.getMaxCount() == 1) {
                    selectedPaths.clear()
                }

                PickerManager.clearSelections()
                if (type == FilePickerConst.MEDIA_PICKER) {
                    PickerManager.add(selectedPaths, FilePickerConst.FILE_TYPE_MEDIA)
                } else {
                    PickerManager.add(selectedPaths, FilePickerConst.FILE_TYPE_DOCUMENT)
                }
            }

            setToolbarTitle(PickerManager.currentCount)
            openSpecificFragment(type)
        }
    }

    private fun setToolbarTitle(count: Int) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            val maxCount = PickerManager.getMaxCount()
            if (maxCount == -1 && count > 0) {
                actionBar.title = String.format(getString(R.string.attachments_num), count)
            } else if (maxCount > 0 && count > 0) {
                actionBar.title = String.format(getString(R.string.attachments_title_text), count, maxCount)
            } else if (!TextUtils.isEmpty(PickerManager.title)) {
                actionBar.title = PickerManager.title
            } else {
                if (type == FilePickerConst.MEDIA_PICKER) {
                    actionBar.setTitle(R.string.select_photo_text)
                } else {
                    actionBar.setTitle(R.string.select_doc_text)
                }
            }
        }
    }

    private fun openSpecificFragment(type: Int) {
        if (type == FilePickerConst.MEDIA_PICKER) {
            val photoFragment = MediaPickerFragment.newInstance()
            FragmentUtil.replaceFragment(this, R.id.container, photoFragment)
        } else {
            if (PickerManager.isDocSupport) PickerManager.addDocTypes()

            val photoFragment = DocPickerFragment.newInstance()
            FragmentUtil.replaceFragment(this, R.id.container, photoFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.picker_menu, menu)
        val menuItem = menu.findItem(R.id.action_done)
        if (menuItem != null) {
            menuItem.isVisible = PickerManager.getMaxCount() != 1
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == R.id.action_done) {
            if (type == FilePickerConst.MEDIA_PICKER) {
                returnData(PickerManager.selectedPhotos)
            } else {
                returnData(PickerManager.selectedFiles)
            }

            return true
        } else if (i == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        PickerManager.reset()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FilePickerConst.REQUEST_CODE_MEDIA_DETAIL -> if (resultCode == Activity.RESULT_OK) {
                if (type == FilePickerConst.MEDIA_PICKER) {
                    returnData(PickerManager.selectedPhotos)
                } else {
                    returnData(PickerManager.selectedFiles)
                }
            } else {
                setToolbarTitle(PickerManager.currentCount)
            }
        }
    }

    private fun returnData(paths: ArrayList<String>) {
        val intent = Intent()
        if (type == FilePickerConst.MEDIA_PICKER) {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, paths)
        } else {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths)
        }

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onItemSelected() {
        val currentCount = PickerManager.currentCount
        setToolbarTitle(currentCount)

        if (PickerManager.getMaxCount() == 1 && currentCount == 1) {
            returnData(
                    if (type == FilePickerConst.MEDIA_PICKER)
                        PickerManager.selectedPhotos
                    else
                        PickerManager.selectedFiles)
        }
    }

    companion object {

        private val TAG = FilePickerActivity::class.java.simpleName
    }
}
