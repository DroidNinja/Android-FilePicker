package droidninja.filepicker.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PhotoDirectory(
                var id: Long = 0,
                var bucketId: String? = null,
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

    override fun equals(other: Any?): Boolean {
        return this.bucketId == (other as? PhotoDirectory)?.bucketId
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (bucketId?.hashCode() ?: 0)
        result = 31 * result + (coverPath?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + medias.hashCode()
        return result
    }
}