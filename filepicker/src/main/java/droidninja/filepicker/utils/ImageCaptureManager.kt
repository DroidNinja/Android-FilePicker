package droidninja.filepicker.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.content.FileProvider
import android.text.TextUtils
import android.util.Log
import androidx.annotation.WorkerThread

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import droidninja.filepicker.PickerManager

class ImageCaptureManager(private val mContext: Context) {

    var currentPhotoPath: Uri? = null

    @Throws(IOException::class)
    private fun createImageFile(): Uri? {
        val imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg"
        val resolver = mContext.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        currentPhotoPath = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return currentPhotoPath
    }


    @WorkerThread
    @Throws(IOException::class)
    fun dispatchTakePictureIntent(): Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
            // Create the File where the photo should go
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val photoURI = createImageFile()
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, createImageFile())
            }
            return takePictureIntent
        }
        return null
    }


    fun deleteContentUri(path: Uri?) {
        if(path != null){
            mContext.contentResolver.delete(path, null , null)
        }
    }

    companion object {

        private val CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath"
        val REQUEST_TAKE_PHOTO = 0x101
    }

}
