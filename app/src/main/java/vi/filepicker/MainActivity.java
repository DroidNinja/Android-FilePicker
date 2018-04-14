package vi.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.Orientation;
import java.util.List;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

  public static final int RC_PHOTO_PICKER_PERM = 123;
  public static final int RC_FILE_PICKER_PERM = 321;
  private static final int CUSTOM_REQUEST_CODE = 532;
  private int MAX_ATTACHMENT_COUNT = 10;
  private ArrayList<String> photoPaths = new ArrayList<>();
  private ArrayList<String> docPaths = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @AfterPermissionGranted(RC_PHOTO_PICKER_PERM) @OnClick(R.id.pick_photo)
  public void pickPhotoClicked() {
    if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
      onPickPhoto();
    } else {
      // Ask for one permission
      EasyPermissions.requestPermissions(this, getString(R.string.rationale_photo_picker),
          RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
    }
  }

  @AfterPermissionGranted(RC_FILE_PICKER_PERM) @OnClick(R.id.pick_doc)
  public void pickDocClicked() {
    if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
      onPickDoc();
    } else {
      // Ask for one permission
      EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
          RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
    }
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case CUSTOM_REQUEST_CODE:
        if (resultCode == Activity.RESULT_OK && data != null) {
          photoPaths = new ArrayList<>();
          photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
        }
        break;

      case FilePickerConst.REQUEST_CODE_DOC:
        if (resultCode == Activity.RESULT_OK && data != null) {
          docPaths = new ArrayList<>();
          docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
        }
        break;
    }

    addThemToView(photoPaths, docPaths);
  }

  private void addThemToView(ArrayList<String> imagePaths, ArrayList<String> docPaths) {
    ArrayList<String> filePaths = new ArrayList<>();
    if (imagePaths != null) filePaths.addAll(imagePaths);

    if (docPaths != null) filePaths.addAll(docPaths);

    RecyclerView recyclerView = findViewById(R.id.recyclerview);
    if (recyclerView != null) {
      StaggeredGridLayoutManager layoutManager =
          new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
      layoutManager.setGapStrategy(
          StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
      recyclerView.setLayoutManager(layoutManager);

      ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);

      recyclerView.setAdapter(imageAdapter);
      recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    Toast.makeText(this, "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT).show();
  }

  public void onPickPhoto() {
    int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size();
    if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
      Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
          Toast.LENGTH_SHORT).show();
    } else {
      FilePickerBuilder.getInstance()
          .setMaxCount(maxCount)
          .setSelectedFiles(photoPaths)
          .setActivityTheme(R.style.FilePickerTheme)
          .setActivityTitle("Please select media")
          .enableVideoPicker(false)
          .enableCameraSupport(true)
          .showGifs(false)
          .showFolderView(false)
          .enableSelectAll(true)
          .enableImagePicker(true)
          .setCameraPlaceholder(R.drawable.custom_camera)
          .withOrientation(Orientation.UNSPECIFIED)
          .pickPhoto(this, CUSTOM_REQUEST_CODE);
    }
  }

  public void onPickDoc() {
    String[] zips = { ".zip", ".rar" };
    String[] pdfs = { ".pdf" };
    int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
    if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
      Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
          Toast.LENGTH_SHORT).show();
    } else {
      FilePickerBuilder.getInstance()
          .setMaxCount(maxCount)
          .setSelectedFiles(docPaths)
          .setActivityTheme(R.style.FilePickerTheme)
          .setActivityTitle("Please select doc")
          .addFileSupport("ZIP", zips)
          .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)
          .enableDocSupport(false)
          .enableSelectAll(true)
          .sortDocumentsBy(SortingTypes.name)
          .withOrientation(Orientation.UNSPECIFIED)
          .pickFile(this);
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  public void onOpenFragmentClicked(View view) {
    Intent intent = new Intent(this, FragmentActivity.class);
    startActivity(intent);
  }

  @Override public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
  }

  @Override public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      new AppSettingsDialog.Builder(this).build().show();
    }
  }
}
