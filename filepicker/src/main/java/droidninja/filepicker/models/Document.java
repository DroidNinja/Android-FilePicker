package droidninja.filepicker.models;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.R;
import droidninja.filepicker.utils.Utils;

/**
 * Created by droidNinja on 29/07/16.
 */
public class Document extends BaseFile {
    private String mimeType;
    private String size;

    public Document(int id, String title, String path) {
        super(id,title,path);
    }

    public Document() {
        super(0,null,null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        return id == document.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTitle() {
        return new File(this.path).getName();
    }

    public void setTitle(String title) {
        this.name = title;
    }

    public int getTypeDrawable()
    {
        if(getFileType()== FilePickerConst.FILE_TYPE.EXCEL)
            return R.drawable.ic_xls;
        if(getFileType()== FilePickerConst.FILE_TYPE.WORD)
            return R.drawable.ic_doc;
        if(getFileType()== FilePickerConst.FILE_TYPE.PPT)
            return R.drawable.icon_ppt;
        if(getFileType()== FilePickerConst.FILE_TYPE.PDF)
            return R.drawable.ic_pdf;
        if(getFileType()== FilePickerConst.FILE_TYPE.TXT)
            return R.drawable.ic_txt;
        else
            return R.drawable.ic_doc;
    }

    public boolean isThisType(FilePickerConst.FILE_TYPE type)
    {
        if(getFileType() == type)
            return true;

        return false;
    }

    public FilePickerConst.FILE_TYPE getFileType()
    {
        String fileExtension = Utils.getFileExtension(new File(this.path));
        if(TextUtils.isEmpty(fileExtension))
            return FilePickerConst.FILE_TYPE.UNKNOWN;

        if(isExcelFile())
            return FilePickerConst.FILE_TYPE.EXCEL;
        if(isDocFile())
            return FilePickerConst.FILE_TYPE.WORD;
        if(isPPTFile())
            return FilePickerConst.FILE_TYPE.PPT;
        if(isPDFFile())
            return FilePickerConst.FILE_TYPE.PDF;
        if(isTxtFile())
            return FilePickerConst.FILE_TYPE.TXT;
        else
            return FilePickerConst.FILE_TYPE.UNKNOWN;
    }

    public boolean isExcelFile()
    {
        String[] types = {"xls","xlsx"};
        return Utils.contains(types, this.path);
    }

    public boolean isDocFile()
    {
        String[] types = {"doc","docx", "dot","dotx"};
        return Utils.contains(types, this.path);
    }

    public boolean isPPTFile()
    {
        String[] types = {"ppt","pptx"};
        return Utils.contains(types, this.path);
    }

    public boolean isPDFFile()
    {
        String[] types = {"pdf"};
        return Utils.contains(types, this.path);
    }

    public boolean isTxtFile()
    {
        String[] types = {"txt"};
        return Utils.contains(types, this.path);
    }

}
