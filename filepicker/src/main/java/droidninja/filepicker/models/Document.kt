package droidninja.filepicker.models

import android.net.Uri
import kotlinx.android.parcel.Parcelize

@Parcelize
class Document @JvmOverloads constructor(override var id: Long = 0,
                                         override var name: String,
                                         override var path: Uri,
                                         var mimeType: String? = null,
                                         var size: String? = null,
                                         var fileType: FileType? = null
) : BaseFile(id, name, path)