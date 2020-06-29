package droidninja.filepicker.models

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

@Parcelize
class Media @JvmOverloads constructor(override var id: Long = 0,
                                      override var name: String,
                                      override var path: Uri,
                                      var mediaType: Int = 0) : BaseFile(id, name, path)





