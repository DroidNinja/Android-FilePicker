package droidninja.filepicker.models;

import android.webkit.MimeTypeMap;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.R;

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
        return name;
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
        if(this.mimeType==null)
            return FilePickerConst.FILE_TYPE.UNKNOWN;

        if(this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls")) ||
                this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx")))
            return FilePickerConst.FILE_TYPE.EXCEL;
        if(this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc")) ||
                this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx")) ||
                this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("dot")) ||
                this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("dotx")))
            return FilePickerConst.FILE_TYPE.WORD;
        if(this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt")) ||
                this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"))||
                this.mimeType.equals(FilePickerConst.PPT_MIME_TYPE))
            return FilePickerConst.FILE_TYPE.PPT;
        if(this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")))
            return FilePickerConst.FILE_TYPE.PDF;
        if(this.mimeType.equals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt")))
            return FilePickerConst.FILE_TYPE.TXT;
        else
            return FilePickerConst.FILE_TYPE.UNKNOWN;
    }

}
