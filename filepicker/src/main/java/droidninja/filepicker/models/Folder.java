package droidninja.filepicker.models;

/**
 * Modeling a folder
 * Created by brianhoang on 1/30/18.
 */

public class Folder extends BaseFile {
    public Folder(String name, String path) {
        super(-1, name, path);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return null != path && path.equals(o.toString());
    }

    @Override
    public String toString() {
        return path;
    }
}
