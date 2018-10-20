package droidninja.filepicker.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
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

    fun contains(types: Array<String>, path: String): Boolean {
        for (string in types) {
            if (path.toLowerCase().endsWith(string)) return true
        }
        return false
    }

    fun <T> contains2(array: Array<T>, v: T?): Boolean {
        if (v == null) {
            for (e in array)
                if (e == null)
                    return true
        } else {
            for (e in array)
                if (e === v || v == e)
                    return true
        }

        return false
    }

    fun notifyMediaStore(context: Context, path: String?) {
        if (path != null && !TextUtils.isEmpty(path)) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(path)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            context.sendBroadcast(mediaScanIntent)
        }
    }
}
