package droidninja.filepicker.utils;

import android.text.TextUtils;

import java.io.File;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.R;

/**
 * Created by droidNinja on 08/03/17.
 */

public class FileUtils {

    public static int getTypeDrawable(String path)
    {
        if(getFileType(path)== FilePickerConst.FILE_TYPE.EXCEL)
            return R.drawable.ic_excel;
        if(getFileType(path)== FilePickerConst.FILE_TYPE.WORD)
            return R.drawable.ic_word;
        if(getFileType(path)== FilePickerConst.FILE_TYPE.PPT)
            return R.drawable.ic_ppt;
        if(getFileType(path)== FilePickerConst.FILE_TYPE.PDF)
            return R.drawable.ic_pdf;
        if(getFileType(path)== FilePickerConst.FILE_TYPE.TXT)
            return R.drawable.ic_txt;
        else
            return R.drawable.ic_txt;
    }

    public static FilePickerConst.FILE_TYPE getFileType(String path)
    {
        String fileExtension = Utils.getFileExtension(new File(path));
        if(TextUtils.isEmpty(fileExtension))
            return FilePickerConst.FILE_TYPE.UNKNOWN;

        if(isExcelFile(path))
            return FilePickerConst.FILE_TYPE.EXCEL;
        if(isDocFile(path))
            return FilePickerConst.FILE_TYPE.WORD;
        if(isPPTFile(path))
            return FilePickerConst.FILE_TYPE.PPT;
        if(isPDFFile(path))
            return FilePickerConst.FILE_TYPE.PDF;
        if(isTxtFile(path))
            return FilePickerConst.FILE_TYPE.TXT;
        else
            return FilePickerConst.FILE_TYPE.UNKNOWN;
    }

    public static boolean isExcelFile(String path)
    {
        String[] types = {"xls","xlsx"};
        return Utils.contains(types, path);
    }

    public static boolean isDocFile(String path)
    {
        String[] types = {"doc","docx", "dot","dotx"};
        return Utils.contains(types, path);
    }

    public static boolean isPPTFile(String path)
    {
        String[] types = {"ppt","pptx"};
        return Utils.contains(types, path);
    }

    public static boolean isPDFFile(String path)
    {
        String[] types = {"pdf"};
        return Utils.contains(types, path);
    }

    public static boolean isTxtFile(String path)
    {
        String[] types = {"txt"};
        return Utils.contains(types, path);
    }

}
