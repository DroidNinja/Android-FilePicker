package droidninja.filepicker.cursors

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils

import java.io.File
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.HashMap

import droidninja.filepicker.PickerManager
import droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback
import droidninja.filepicker.models.Document
import droidninja.filepicker.models.FileType
import droidninja.filepicker.utils.FilePickerUtils

import android.provider.BaseColumns._ID
import android.provider.MediaStore.MediaColumns.DATA
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback
import droidninja.filepicker.models.PhotoDirectory
import java.util.function.Predicate

/**
 * Created by droidNinja on 01/08/16.
 */
class PhotoScannerTask(val contentResolver: ContentResolver, private val args:Bundle,
                       private val resultCallback: FileResultCallback<PhotoDirectory>?) : AsyncTask<Void, Void, MutableList<PhotoDirectory>>() {

    override fun doInBackground(vararg voids: Void): MutableList<PhotoDirectory> {
        val bucketId = args.getString(FilePickerConst.EXTRA_BUCKET_ID, null)
        val mediaType = args.getInt(FilePickerConst.EXTRA_FILE_TYPE, FilePickerConst.MEDIA_TYPE_IMAGE)

        val projection = null
        val uri = MediaStore.Files.getContentUri("external")
        val sortOrder = MediaStore.Images.Media._ID + " DESC"

        var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

        if (mediaType == FilePickerConst.MEDIA_TYPE_VIDEO) {
            selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        }

        if (bucketId != null)
            selection += " AND " + MediaStore.Images.Media.BUCKET_ID + "='" + bucketId + "'"

        val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)

        if (cursor != null) {
            val data = getPhotoDirectories(cursor)
            cursor.close()
            return data
        }

        return mutableListOf()
    }

    override fun onPostExecute(result: MutableList<PhotoDirectory>?) {
        super.onPostExecute(result)
        result?.let {
            resultCallback?.onResultCallback(it.toList())
        }
    }

    private fun getPhotoDirectories(data: Cursor): MutableList<PhotoDirectory> {
        val directories = ArrayList<PhotoDirectory>()

        while (data.moveToNext()) {

            val imageId = data.getInt(data.getColumnIndexOrThrow(_ID))
            val bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))
            val name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            val path = data.getString(data.getColumnIndexOrThrow(DATA))
            val fileName = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
            val mediaType = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))

            val photoDirectory = PhotoDirectory()
            photoDirectory.bucketId = bucketId
            photoDirectory.name = name

            if (!directories.contains(photoDirectory)) {
                if (path != null && path.toLowerCase().endsWith("gif")) {
                    if (PickerManager.isShowGif) {
                        photoDirectory.addPhoto(imageId, fileName, path, mediaType)
                    }
                } else {
                    photoDirectory.addPhoto(imageId, fileName, path, mediaType)
                }

                photoDirectory.dateAdded = data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                directories.add(photoDirectory)
            } else {
                directories[directories.indexOf(photoDirectory)]
                        .addPhoto(imageId, fileName, path, mediaType)
            }
        }

        return directories
    }
}
