package droidninja.filepicker.models;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.FilePickerUtils;
import java.io.File;

/**
 * Created by droidNinja on 29/07/16.
 */
public class Document extends BaseFile {
    private String mimeType;
    private String size;
    private FileType fileType;

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

    public boolean isThisType(String[] types)
    {
        return FilePickerUtils.INSTANCE.contains(types, this.path);
    }

    public FileType getFileType()
    {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
