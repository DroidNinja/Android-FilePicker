package droidninja.filepicker.cursors

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
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
import java.util.function.Predicate

/**
 * Created by droidNinja on 01/08/16.
 */
class DocScannerTask(val contentResolver: ContentResolver, private val fileTypes: List<FileType>, private val comparator: Comparator<Document>?,
                     private val resultCallback: FileMapResultCallback?) : AsyncTask<Void, Void, Map<FileType, List<Document>>>() {

    private val DOC_PROJECTION = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE, MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.TITLE)

    private fun createDocumentType(documents: ArrayList<Document>): HashMap<FileType, List<Document>> {
        val documentMap = HashMap<FileType, List<Document>>()

        for (fileType in fileTypes) {
            val documentListFilteredByType = documents.filter { document -> document.isThisType(fileType.extensions) }

            comparator?.let {
                documentListFilteredByType.sortedWith(comparator)
            }

            documentMap[fileType] = documentListFilteredByType
        }

        return documentMap
    }

    override fun doInBackground(vararg voids: Void): Map<FileType, List<Document>> {
        var documents = ArrayList<Document>()

        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE
                + "!="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " AND "
                + MediaStore.Files.FileColumns.MEDIA_TYPE
                + "!="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

        val cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), DOC_PROJECTION, selection, null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC")

        if (cursor != null) {
            documents = getDocumentFromCursor(cursor)
            cursor.close()
        }

        return createDocumentType(documents)
    }

    override fun onPostExecute(documents: Map<FileType, List<Document>>) {
        resultCallback?.onResultCallback(documents)
    }

    private fun getDocumentFromCursor(data: Cursor): ArrayList<Document> {
        val documents = ArrayList<Document>()
        while (data.moveToNext()) {

            val imageId = data.getInt(data.getColumnIndexOrThrow(_ID))
            val path = data.getString(data.getColumnIndexOrThrow(DATA))
            val title = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))

            if (path != null) {

                val fileType = getFileType(PickerManager.getFileTypes(), path)
                val file = File(path)
                if (fileType != null && !file.isDirectory && file.exists()) {

                    val document = Document(imageId, title, path)
                    document.fileType = fileType

                    val mimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
                    if (mimeType != null && !TextUtils.isEmpty(mimeType)) {
                        document.mimeType = mimeType
                    } else {
                        document.mimeType = ""
                    }

                    document.size = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))

                    if (!documents.contains(document)) documents.add(document)
                }
            }
        }

        return documents
    }

    private fun getFileType(types: ArrayList<FileType>, path: String): FileType? {
        for (index in types.indices) {
            for (string in types[index].extensions) {
                if (path.endsWith(string)) return types[index]
            }
        }
        return null
    }
}
