package droidninja.filepicker.cursors.loadercallbacks

import droidninja.filepicker.models.Document
import droidninja.filepicker.models.FileType

/**
 * Created by gabriel on 10/2/17.
 */

interface FileMapResultCallback {
    fun onResultCallback(files: Map<FileType, List<Document>>)
}

