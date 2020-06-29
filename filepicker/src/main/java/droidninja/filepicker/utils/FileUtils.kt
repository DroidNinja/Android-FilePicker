package droidninja.filepicker.utils

import android.text.TextUtils

import java.io.File

import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.R

/**
 * Created by droidNinja on 08/03/17.
 */

object FileUtils {

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

}
