package droidninja.filepicker.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File

/**
 * Created by droidNinja on 29/07/16.
 */
object FilePickerUtils {

    fun getFileExtension(file: File): String {
        val name = file.name
        try {
            return name.substring(name.lastIndexOf(".") + 1)
        } catch (e: Exception) {
            return ""
        }

    }

    fun contains(types: Array<String>, mimeType: String?): Boolean {
        for (type in types) {
            if(MimeTypeMap.getSingleton().getMimeTypeFromExtension(type) == mimeType){
                return true
            }
        }
        return false
    }
}

fun ContentResolver.registerObserver(
        uri: Uri,
        observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}
