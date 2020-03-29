package droidninja.filepicker.viewmodels

import android.app.Application
import android.content.ContentUris
import android.database.ContentObserver
import android.database.Cursor
import android.provider.BaseColumns
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.models.Media
import droidninja.filepicker.models.PhotoDirectory
import droidninja.filepicker.utils.ImageCaptureManager
import droidninja.filepicker.utils.registerObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class VMMediaPicker(application: Application) : BaseViewModel(application) {

    private val _lvMediaData = MutableLiveData<List<Media>>()
    val lvMediaData: LiveData<List<Media>>
        get() = _lvMediaData

    private val _lvPhotoDirsData = MutableLiveData<List<PhotoDirectory>>()
    val lvPhotoDirsData: LiveData<List<PhotoDirectory>>
        get() = _lvPhotoDirsData

    private val _lvDataChanged = MutableLiveData<Boolean>()
    val lvDataChanged: LiveData<Boolean>
        get() = _lvDataChanged

    private var contentObserver: ContentObserver? = null

    private fun registerContentObserver(){
        if (contentObserver == null) {
            contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ) {
                _lvDataChanged.value = true
            }
        }
    }

    fun getMedia(bucketId: String? = null, mediaType: Int = FilePickerConst.MEDIA_TYPE_IMAGE) {
        launchDataLoad {
            val medias = mutableListOf<Media>()
            queryImages(bucketId, mediaType).map { dir->
                medias.addAll(dir.medias)
            }
            medias.sortWith(Comparator { a, b -> (b.id - a.id).toInt() })

            _lvMediaData.postValue(medias)
            registerContentObserver()
        }
    }

    fun getPhotoDirs(bucketId: String? = null, mediaType: Int = FilePickerConst.MEDIA_TYPE_IMAGE) {
        launchDataLoad {
            val dirs = queryImages(bucketId, mediaType)
            val photoDirectory = PhotoDirectory()
            photoDirectory.bucketId = null

            when (mediaType) {
                FilePickerConst.MEDIA_TYPE_VIDEO -> {
                    photoDirectory.name = getApplication<Application>().applicationContext.getString(R.string.all_videos)
                }
                FilePickerConst.MEDIA_TYPE_IMAGE -> {
                    photoDirectory.name = getApplication<Application>().applicationContext.getString(R.string.all_photos)
                }
                else -> {
                    photoDirectory.name = getApplication<Application>().applicationContext.getString(R.string.all_files)
                }
            }

            if (dirs.isNotEmpty() && dirs[0].medias.size > 0) {
                photoDirectory.dateAdded = dirs[0].dateAdded
                photoDirectory.setCoverPath(dirs[0].medias[0].path)
            }

            for (i in dirs.indices) {
                photoDirectory.medias.addAll(dirs[i].medias)
            }

            dirs.add(0, photoDirectory)
            _lvPhotoDirsData.postValue(dirs)
            registerContentObserver()
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
                data = getPhotoDirectories(mediaType, cursor)
                cursor.close()
            }
        }
        return data
    }

    @WorkerThread
    private fun getPhotoDirectories(fileType: Int, data: Cursor): MutableList<PhotoDirectory> {
        val directories = mutableListOf<PhotoDirectory>()

        while (data.moveToNext()) {

            val imageId = data.getLong(data.getColumnIndexOrThrow(BaseColumns._ID))
            val bucketId = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID))
            val name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            val fileName = data.getString(data.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
            val mediaType = data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))

            val photoDirectory = PhotoDirectory()
            photoDirectory.id = imageId
            photoDirectory.bucketId = bucketId
            photoDirectory.name = name

            var contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId
            )
            if (fileType == FilePickerConst.MEDIA_TYPE_VIDEO) {
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        imageId
                )
            }
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

    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }
}