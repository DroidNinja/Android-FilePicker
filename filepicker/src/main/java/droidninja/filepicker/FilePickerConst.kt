package droidninja.filepicker

import android.Manifest

/**
 * Created by droidNinja on 28/07/16.
 */
object FilePickerConst {
    const val REQUEST_CODE_PHOTO = 233
    const val REQUEST_CODE_DOC = 234

    const val REQUEST_CODE_MEDIA_DETAIL = 235
    const val REQUEST_CODE_PERMISSION = 988

    const val DEFAULT_MAX_COUNT = -1
    const val DEFAULT_COLUMN_NUMBER = 3

    const val MEDIA_PICKER = 0x11
    const val DOC_PICKER = 0x12

    const val KEY_SELECTED_MEDIA = "SELECTED_PHOTOS"
    const val KEY_SELECTED_DOCS = "SELECTED_DOCS"

    const val EXTRA_PICKER_TYPE = "EXTRA_PICKER_TYPE"
    const val EXTRA_SHOW_GIF = "SHOW_GIF"
    const val EXTRA_FILE_TYPE = "EXTRA_FILE_TYPE"
    const val EXTRA_BUCKET_ID = "EXTRA_BUCKET_ID"
    const val ALL_PHOTOS_BUCKET_ID = "ALL_PHOTOS_BUCKET_ID"
    const val PPT_MIME_TYPE = "application/mspowerpoint"

    const val FILE_TYPE_MEDIA = 1
    const val FILE_TYPE_DOCUMENT = 2

    const val MEDIA_TYPE_IMAGE = 1
    const val MEDIA_TYPE_VIDEO = 3

    const val PERMISSIONS_FILE_PICKER = Manifest.permission.WRITE_EXTERNAL_STORAGE

    val docExtensions = arrayOf("ppt", "pptx", "xls", "xlsx", "doc", "docx", "dot", "dotx")

    const val PDF = "PDF"
    const val PPT = "PPT"
    const val DOC = "DOC"
    const val XLS = "XLS"
    const val TXT = "TXT"

    enum class FILE_TYPE {
        PDF, WORD, EXCEL, PPT, TXT, UNKNOWN
    }
}
