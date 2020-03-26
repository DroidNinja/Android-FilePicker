package droidninja.filepicker.viewmodels

import android.app.Application
import android.content.ContentUris
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.PickerManager
import droidninja.filepicker.models.PhotoDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class VMMediaPicker(application: Application) : BaseViewModel(application) {
    private val _lvMediaData = MutableLiveData<List<PhotoDirectory>>()
    val lvMediaData: LiveData<List<PhotoDirectory>>
        get() = _lvMediaData


    fun getMedia(bucketId: String? = null, mediaType: Int = FilePickerConst.MEDIA_TYPE_IMAGE) {
        launchDataLoad {
            val dirs = queryImages(bucketId, mediaType)
            _lvMediaData.postValue(dirs)
        }
    }

    @WorkerThread
    suspend fun queryImages(bucketId: String?, mediaType: Int): MutableList<PhotoDirectory> {
        var data = mutableListOf<PhotoDirectory>()
        withContext(Dispatchers.IO) {
            val projection = null
            val uri = MediaStore.Files.getContentUri("external")
            val sortOrder = MediaStore.Images.Media._ID + " DESC"

            var selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

            if (mediaType == FilePickerConst.MEDIA_TYPE_VIDEO) {
                selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            }

            if (!PickerManager.isShowGif) {
                selection += " AND " + MediaStore.Images.Media.MIME_TYPE + "!='" + MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif") + "'"
            }

            if (bucketId != null)
                selection += " AND " + MediaStore.Images.Media.BUCKET_ID + "='" + bucketId + "'"


            val cursor = getApplication<Application>().contentResolver.query(uri, projection, selection, null, sortOrder)

            if (cursor != null) {
                data = getPhotoDirectories(cursor)
                cursor.close()
            }
        }
        return data
    }

    @WorkerThread
    private fun getPhotoDirectories(data: Cursor): MutableList<PhotoDirectory> {
        val directories = mutableListOf<PhotoDirectory>()

        while (data.moveToNext()) {

            val imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))
            val name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            val fileName = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
            val mediaType = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))

            val photoDirectory = PhotoDirectory()
            photoDirectory.bucketId = bucketId
            photoDirectory.name = name
            val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
            )
            if (!directories.contains(photoDirectory)) {

                photoDirectory.addPhoto(imageId, fileName, contentUri, mediaType)
                photoDirectory.dateAdded = data.getLong(data.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                directories.add(photoDirectory)
            } else {
                directories[directories.indexOf(photoDirectory)]
                        .addPhoto(imageId, fileName, contentUri, mediaType)
            }
        }

        return directories
    }
}