package droidninja.filepicker

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.annotation.IntegerRes
import droidninja.filepicker.models.FileType
import droidninja.filepicker.models.sort.SortingTypes
import java.util.ArrayList

/**
 * Created by droidNinja on 29/07/16.
 */
class FilePickerBuilder {

    private val mPickerOptionsBundle: Bundle = Bundle()

    fun setMaxCount(maxCount: Int): FilePickerBuilder {
        PickerManager.setMaxCount(maxCount)
        return this
    }

    fun setActivityTheme(theme: Int): FilePickerBuilder {
        PickerManager.theme = theme
        return this
    }

    fun setActivityTitle(title: String): FilePickerBuilder {
        PickerManager.title = title
        return this
    }

    fun setSelectedFiles(selectedPhotos: ArrayList<String>): FilePickerBuilder {
        mPickerOptionsBundle.putStringArrayList(FilePickerConst.KEY_SELECTED_MEDIA, selectedPhotos)
        return this
    }

    fun enableVideoPicker(status: Boolean): FilePickerBuilder {
        PickerManager.setShowVideos(status)
        return this
    }

    fun enableImagePicker(status: Boolean): FilePickerBuilder {
        PickerManager.setShowImages(status)
        return this
    }

    fun enableSelectAll(status: Boolean): FilePickerBuilder {
        PickerManager.enableSelectAll(status)
        return this
    }

    fun setCameraPlaceholder(@DrawableRes drawable: Int): FilePickerBuilder {
        PickerManager.cameraDrawable = drawable
        return this
    }

    fun showGifs(status: Boolean): FilePickerBuilder {
        PickerManager.isShowGif = status
        return this
    }

    fun showFolderView(status: Boolean): FilePickerBuilder {
        PickerManager.isShowFolderView = status
        return this
    }

    fun enableDocSupport(status: Boolean): FilePickerBuilder {
        PickerManager.isDocSupport = status
        return this
    }

    fun enableCameraSupport(status: Boolean): FilePickerBuilder {
        PickerManager.isEnableCamera = status
        return this
    }


    fun withOrientation(@IntegerRes orientation:  Int): FilePickerBuilder {
        PickerManager.orientation = orientation
        return this
    }

    fun addFileSupport(title: String, extensions: Array<String>,
                       @DrawableRes drawable: Int): FilePickerBuilder {
        PickerManager.addFileType(FileType(title, extensions, drawable))
        return this
    }

    fun addFileSupport(title: String, extensions: Array<String>): FilePickerBuilder {
        PickerManager.addFileType(FileType(title, extensions, 0))
        return this
    }

    fun sortDocumentsBy(type: SortingTypes): FilePickerBuilder {
        PickerManager.sortingType = type
        return this
    }

    fun pickPhoto(context: Activity) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_PHOTO)
    }

    fun pickPhoto(context: Fragment) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_PHOTO)
    }

    fun pickFile(context: Activity) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_DOC)
    }

    fun pickFile(context: Fragment) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_DOC)
    }

    fun pickPhoto(context: Activity, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, requestCode)
    }

    fun pickPhoto(context: Fragment, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, requestCode)
    }

    fun pickFile(context: Activity, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER)
        start(context, requestCode)
    }

    fun pickFile(context: Fragment, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.DOC_PICKER)
        start(context, requestCode)
    }

    private fun start(context: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context,
                        context.resources.getString(R.string.permission_filepicker_rationale),
                        Toast.LENGTH_SHORT).show()
                return
            }
        }

        PickerManager.providerAuthorities = context.applicationContext.packageName + ".droidninja.filepicker.provider"

        val intent = Intent(context, FilePickerActivity::class.java)
        intent.putExtras(mPickerOptionsBundle)

        context.startActivityForResult(intent, requestCode)
    }

    private fun start(fragment: Fragment, requestCode: Int) {
        fragment.context?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(it,
                                FilePickerConst.PERMISSIONS_FILE_PICKER) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(fragment.context, it
                            .resources
                            .getString(R.string.permission_filepicker_rationale), Toast.LENGTH_SHORT).show()
                    return
                }
            }

            PickerManager.providerAuthorities = it.applicationContext?.packageName + ".droidninja.filepicker.provider"

            val intent = Intent(fragment.activity, FilePickerActivity::class.java)
            intent.putExtras(mPickerOptionsBundle)

            fragment.startActivityForResult(intent, requestCode)
        }
    }

    companion object {
        @JvmStatic
        val instance: FilePickerBuilder
            get() = FilePickerBuilder()
    }
}
