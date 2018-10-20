package droidninja.filepicker.utils

import android.content.ContentResolver
import android.os.Bundle

import java.util.Comparator

import droidninja.filepicker.cursors.DocScannerTask
import droidninja.filepicker.cursors.PhotoScannerTask
import droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback
import droidninja.filepicker.models.Document
import droidninja.filepicker.models.FileType
import droidninja.filepicker.models.PhotoDirectory

object MediaStoreHelper {

    fun getDirs(contentResolver: ContentResolver, args: Bundle, resultCallback: FileResultCallback<PhotoDirectory>) {
        PhotoScannerTask(contentResolver,args,resultCallback).execute()
    }

    fun getDocs(contentResolver: ContentResolver,
                fileTypes: List<FileType>,
                comparator: Comparator<Document>,
                fileResultCallback: FileMapResultCallback) {
        DocScannerTask(contentResolver, fileTypes, comparator, fileResultCallback).execute()
    }
}