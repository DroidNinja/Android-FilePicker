package droidninja.filepicker

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import droidninja.filepicker.models.FileType
import droidninja.filepicker.models.sort.SortingTypes
import java.util.*

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

    fun setSelectedFiles(selectedPhotos: ArrayList<Uri>): FilePickerBuilder {
        mPickerOptionsBundle.putParcelableArrayList(FilePickerConst.KEY_SELECTED_MEDIA, selectedPhotos)
        return this
    }

    fun enableVideoPicker(status: Boolean) = apply {
        PickerManager.setShowVideos(status)
    }

    fun enableImagePicker(status: Boolean) = apply {
        PickerManager.setShowImages(status)
    }

    fun enableSelectAll(status: Boolean) = apply {
        PickerManager.enableSelectAll(status)
    }

    fun setCameraImagePlaceholder(@DrawableRes drawable: Int) = apply {
        PickerManager.cameraImageDrawable = drawable
    }

    fun setCameraVideoPlaceholder(@DrawableRes drawable: Int) = apply {
        PickerManager.cameraVideoDrawable = drawable
    }

    fun showGifs(status: Boolean) = apply {
        PickerManager.isShowGif = status
    }

    fun showFolderView(status: Boolean) = apply {
        PickerManager.isShowFolderView = status
    }

    fun enableDocSupport(status: Boolean) = apply {
        PickerManager.isDocSupport = status
    }

    fun enableCameraSupport(status: Boolean) = apply {
        PickerManager.isEnableCamera = status
    }


    fun withOrientation(@IntegerRes orientation: Int) = apply {
        PickerManager.orientation = orientation
    }

    @JvmOverloads
    fun addFileSupport(title: String,
                       extensions: Array<String>,
                       @DrawableRes drawable: Int = R.drawable.icon_file_unknown) = apply {
        PickerManager.addFileType(FileType(title, extensions, drawable))
    }

    fun sortDocumentsBy(type: SortingTypes) = apply {
        PickerManager.sortingType = type
    }

    fun pickMedia(context: Activity) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, FilePickerConst.REQUEST_CODE_PHOTO)
    }

    fun pickMedia(context: Fragment) {
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

    fun pickMedia(context: Activity, requestCode: Int) {
        mPickerOptionsBundle.putInt(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER)
        start(context, requestCode)
    }

    fun pickMedia(context: Fragment, requestCode: Int) {
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
