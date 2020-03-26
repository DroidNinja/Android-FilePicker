package droidninja.filepicker.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PhotoDirectory(var bucketId: String? = null,
                private var coverPath: Uri? = null,
                var name: String? = null,
                var dateAdded: Long = 0,
                val medias: MutableList<Media> = mutableListOf()
) : Parcelable {

    fun getCoverPath(): Uri? {
        return when {
            medias.size > 0 -> medias[0].path
            coverPath != null -> coverPath
            else -> null
        };
    }

    fun setCoverPath(coverPath: Uri?) {
        this.coverPath = coverPath
    }

    fun addPhoto(imageId: Long, fileName: String, path: Uri, mediaType: Int) {
        medias.add(Media(imageId, fileName, path, mediaType))
    }
}