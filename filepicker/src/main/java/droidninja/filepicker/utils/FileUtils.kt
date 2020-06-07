package droidninja.filepicker.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import androidx.annotation.Nullable
import droidninja.filepicker.FilePickerConst
import java.io.File

/**
 * Created by droidNinja on 08/03/17.
 */

object FileUtils {

    @Nullable
    fun getFilePath(context: Context, selectedImage: Uri?): String? {
        return if (selectedImage == null) null
        else FileUtils.getPathFromUri(context, selectedImage)
    }

    fun getFileType(path: String): FilePickerConst.FILE_TYPE {
        val fileExtension = FilePickerUtils.getFileExtension(File(path))
        if (TextUtils.isEmpty(fileExtension))
            return FilePickerConst.FILE_TYPE.UNKNOWN

        if (isExcelFile(path))
            return FilePickerConst.FILE_TYPE.EXCEL
        if (isDocFile(path))
            return FilePickerConst.FILE_TYPE.WORD
        if (isPPTFile(path))
            return FilePickerConst.FILE_TYPE.PPT
        if (isPDFFile(path))
            return FilePickerConst.FILE_TYPE.PDF
        return if (isTxtFile(path))
            FilePickerConst.FILE_TYPE.TXT
        else
            FilePickerConst.FILE_TYPE.UNKNOWN
    }

    fun isExcelFile(path: String): Boolean {
        val types = arrayOf("xls", "xlsx")
        return FilePickerUtils.contains(types, path)
    }

    fun isDocFile(path: String): Boolean {
        val types = arrayOf("doc", "docx", "dot", "dotx")
        return FilePickerUtils.contains(types, path)
    }

    fun isPPTFile(path: String): Boolean {
        val types = arrayOf("ppt", "pptx")
        return FilePickerUtils.contains(types, path)
    }

    fun isPDFFile(path: String): Boolean {
        val types = arrayOf("pdf")
        return FilePickerUtils.contains(types, path)
    }

    fun isTxtFile(path: String): Boolean {
        val types = arrayOf("txt")
        return FilePickerUtils.contains(types, path)
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val uriString: String = uri.toString()
        val myFile = File(uriString)
        if (uriString.startsWith("content://")) {
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri, null, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor
                            .getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        } else if (uriString.startsWith("file://")) {
            return myFile.name
        }
        return uriString
    }

    fun getPathFromUri(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, uri)
        ) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                        split[1]
                )
                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                    context,
                    uri,
                    null,
                    null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun getDataColumn(
            context: Context, uri: Uri, selection: String?,
            selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
                column
        )
        try {
            cursor = context.contentResolver
                    .query(
                            uri, projection, selection, selectionArgs,
                            null
                    )
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


}
