package vi.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.models.sort.SortingTypes;
import droidninja.filepicker.utils.ContentUriUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final int RC_PHOTO_PICKER_PERM = 123;
    public static final int RC_FILE_PICKER_PERM = 321;
    private static final int CUSTOM_REQUEST_CODE = 532;
    private int MAX_ATTACHMENT_COUNT = 10;
    private ArrayList<Uri> photoPaths = new ArrayList<>();
    private ArrayList<Uri> docPaths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.pick_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPhotoClicked();
            }
        });
        findViewById(R.id.pick_doc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDocClicked();
            }
        });
    }

    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    public void pickPhotoClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickPhoto();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    @AfterPermissionGranted(RC_FILE_PICKER_PERM)
    public void pickDocClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                    if (dataList != null) {
                        photoPaths = new ArrayList<Uri>();
                        photoPaths.addAll(dataList);
                    }
                }
                break;

            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
                    if (dataList != null) {
                        docPaths = new ArrayList<>();
                        docPaths.addAll(dataList);
                    }
                }
                break;
        }

        addThemToView(photoPaths, docPaths);
    }

    private void addThemToView(ArrayList<Uri> imagePaths, ArrayList<Uri> docPaths) {
        ArrayList<Uri> filePaths = new ArrayList<>();
        if (imagePaths != null) filePaths.addAll(imagePaths);

        if (docPaths != null) filePaths.addAll(docPaths);

        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths, new ImageAdapter.ImageAdapterListener() {
                @Override
                public void onItemClick(Uri uri) {
                    try {
                        //make sure to use this getFilePath method from worker thread
                        String path = ContentUriUtils.INSTANCE.getFilePath(recyclerView.getContext(), uri);
                        if (path != null) {
                            Toast.makeText(recyclerView.getContext(), path, Toast.LENGTH_SHORT).show();
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            });

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
                    .setMaxCount(10)
                    .setSelectedFiles(photoPaths) //this is optional
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select media")
                    .setImageSizeLimit(5)
                    .setVideoSizeLimit(10)
                    .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                    .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                    .enableVideoPicker(true)
                    .enableCameraSupport(true)
                    .showGifs(true)
                    .showFolderView(false)
                    .enableSelectAll(true)
                    .enableImagePicker(true)
                    .setCameraPlaceholder(R.drawable.custom_camera)
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickPhoto(this, CUSTOM_REQUEST_CODE);
        }
    }

    public void onPickDoc() {
        String[] zips = {"zip", "rar"};
        String[] pdfs = {"aac"};
        int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
        if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
                    Toast.LENGTH_SHORT).show();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(1)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select doc")
                    .setImageSizeLimit(5) //Provide Size in MB
                    .setVideoSizeLimit(20)
//                    .addFileSupport("ZIP", zips)
//                    .addFileSupport("AAC", pdfs, R.drawable.pdf_blue)
                    .enableDocSupport(true)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.NAME)
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickFile(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onOpenFragmentClicked(View view) {
        Intent intent = new Intent(this, FragmentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
