package droidninja.filepicker.cursors;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.android.internal.util.Predicate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.PickerManager;
import droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import droidninja.filepicker.utils.FilePickerUtils;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.MediaColumns.DATA;

/**
 * Created by droidNinja on 01/08/16.
 */
public class DocScannerTask extends AsyncTask<Void, Void, Map<FileType, List<Document>>> {

  final String[] DOC_PROJECTION = {
      MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DATA,
      MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns.SIZE,
      MediaStore.Files.FileColumns.DATE_ADDED, MediaStore.Files.FileColumns.TITLE
  };
  private final FileMapResultCallback resultCallback;
  private final Comparator<Document> comparator;
  private final List<FileType> fileTypes;

  private final ContentResolver contentResolver;

  public DocScannerTask(Context context, List<FileType> fileTypes, Comparator<Document> comparator,
      FileMapResultCallback fileResultCallback) {
    this.contentResolver = context.getContentResolver();
    this.fileTypes = fileTypes;
    this.comparator = comparator;
    this.resultCallback = fileResultCallback;
  }

  private HashMap<FileType, List<Document>> createDocumentType(ArrayList<Document> documents) {
    HashMap<FileType, List<Document>> documentMap = new HashMap<>();

    for (final FileType fileType : fileTypes) {
      Predicate<Document> docContainsTypeExtension = new Predicate<Document>() {
        public boolean apply(Document document) {
          return document.isThisType(fileType.extensions);
        }
      };
      ArrayList<Document> documentListFilteredByType =
          (ArrayList<Document>) FilePickerUtils.filter(documents, docContainsTypeExtension);

      if (comparator != null) Collections.sort(documentListFilteredByType, comparator);

      documentMap.put(fileType, documentListFilteredByType);
    }

    return documentMap;
  }

  @Override protected Map<FileType, List<Document>> doInBackground(Void... voids) {
    ArrayList<Document> documents = new ArrayList<>();

    String selection = MediaStore.Files.FileColumns.MEDIA_TYPE
        + "!="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        + " AND "
        + MediaStore.Files.FileColumns.MEDIA_TYPE
        + "!="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

    Cursor cursor =
        contentResolver.query(MediaStore.Files.getContentUri("external"), DOC_PROJECTION, selection,
            null, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

    if (cursor != null) {
      documents = getDocumentFromCursor(cursor);
      cursor.close();
    }

    return createDocumentType(documents);
  }

  @Override protected void onPostExecute(Map<FileType, List<Document>> documents) {
    if (resultCallback != null) {
      resultCallback.onResultCallback(documents);
    }
  }

  private ArrayList<Document> getDocumentFromCursor(Cursor data) {
    ArrayList<Document> documents = new ArrayList<>();
    while (data.moveToNext()) {

      int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
      String path = data.getString(data.getColumnIndexOrThrow(DATA));
      String title = data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));

      if (path != null) {

        FileType fileType = getFileType(PickerManager.getInstance().getFileTypes(), path);
        File file = new File(path);
        if (fileType != null && !file.isDirectory() && file.exists()) {

          Document document = new Document(imageId, title, path);
          document.setFileType(fileType);

          String mimeType =
              data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE));
          if (mimeType != null && !TextUtils.isEmpty(mimeType)) {
            document.setMimeType(mimeType);
          } else {
            document.setMimeType("");
          }

          document.setSize(
              data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)));

          if (!documents.contains(document)) documents.add(document);
        }
      }
    }

    return documents;
  }

  private FileType getFileType(ArrayList<FileType> types, String path) {
    for (int index = 0; index < types.size(); index++) {
      for (String string : types.get(index).extensions) {
        if (path.endsWith(string)) return types.get(index);
      }
    }
    return null;
  }
}
