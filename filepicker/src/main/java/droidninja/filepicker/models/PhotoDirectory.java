package droidninja.filepicker.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerConst;

public class PhotoDirectory extends BaseFile implements Parcelable{

  private String bucketId;
  private String coverPath;
  private String name;
  private long   dateAdded;
  private List<Media> medias = new ArrayList<>();

  public PhotoDirectory()
  {
    super();
  }

  public PhotoDirectory(int id, String name, String path) {
    super(id, name, path);
  }

  protected PhotoDirectory(Parcel in) {
    bucketId = in.readString();
    coverPath = in.readString();
    name = in.readString();
    dateAdded = in.readLong();
  }

  public static final Creator<PhotoDirectory> CREATOR = new Creator<PhotoDirectory>() {
    @Override
    public PhotoDirectory createFromParcel(Parcel in) {
      return new PhotoDirectory(in);
    }

    @Override
    public PhotoDirectory[] newArray(int size) {
      return new PhotoDirectory[size];
    }
  };

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PhotoDirectory)) return false;

    PhotoDirectory directory = (PhotoDirectory) o;

    boolean hasId = !TextUtils.isEmpty(bucketId);
    boolean otherHasId = !TextUtils.isEmpty(directory.bucketId);

    if (hasId && otherHasId) {
      if (!TextUtils.equals(bucketId, directory.bucketId)) {
        return false;
      }

      return TextUtils.equals(name, directory.name);
    }

    return false;
  }

  @Override
  public int hashCode() {
    if (TextUtils.isEmpty(bucketId)) {
      if (TextUtils.isEmpty(name)) {
        return 0;
      }

      return name.hashCode();
    }

    int result = bucketId.hashCode();

    if (TextUtils.isEmpty(name)) {
      return result;
    }

    result = 31 * result + name.hashCode();
    return result;
  }

  public String getCoverPath() {
    return coverPath;
  }

  public void setCoverPath(String coverPath) {
    this.coverPath = coverPath;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getDateAdded() {
    return dateAdded;
  }

  public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  public List<Media> getMedias() {
    return medias;
  }

  public void setMedias(List<Media> medias) {
    this.medias = medias;
  }

  public List<String> getPhotoPaths() {
    List<String> paths = new ArrayList<>(medias.size());
    for (Media media : medias) {
      paths.add(media.getPath());
    }
    return paths;
  }

  public void addPhoto(int id, String name, String path, int mediaType) {
    medias.add(new Media(id, name, path, mediaType));
  }

  public void addPhoto(Media media) {
    medias.add(media);
  }

  public void addPhotos(List<Media> photosList) {
    medias.addAll(photosList);
  }

  public String getBucketId() {
    if(bucketId.equals(FilePickerConst.ALL_PHOTOS_BUCKET_ID))
      return null;
    return bucketId;
  }

  public void setBucketId(String bucketId) {
    this.bucketId = bucketId;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(bucketId);
    parcel.writeString(coverPath);
    parcel.writeString(name);
    parcel.writeLong(dateAdded);
  }
}
