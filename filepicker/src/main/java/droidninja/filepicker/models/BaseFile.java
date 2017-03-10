package droidninja.filepicker.models;

import android.os.Parcel;
import android.os.Parcelable;

import droidninja.filepicker.utils.Utils;

/**
 * Created by droidNinja on 29/07/16.
 */
public class BaseFile implements Parcelable{
    protected int id;
    protected String name;
    protected String path;

    public BaseFile()
    {

    }

    public BaseFile(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    protected BaseFile(Parcel in) {
        id = in.readInt();
        name = in.readString();
        path = in.readString();
    }

    public static final Creator<BaseFile> CREATOR = new Creator<BaseFile>() {
        @Override
        public BaseFile createFromParcel(Parcel in) {
            return new BaseFile(in);
        }

        @Override
        public BaseFile[] newArray(int size) {
            return new BaseFile[size];
        }
    };

    public boolean isImage()
    {
        String[] types = {"jpg","jpeg","png","gif"};
        return Utils.contains(types, this.path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseFile)) return false;

        BaseFile baseFile = (BaseFile) o;

        return id == baseFile.id;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(path);
    }
}
