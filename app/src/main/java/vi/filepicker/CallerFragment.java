package vi.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.fragments.BaseFragment;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.utils.ContentUriUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static vi.filepicker.MainActivity.RC_FILE_PICKER_PERM;
import static vi.filepicker.MainActivity.RC_PHOTO_PICKER_PERM;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallerFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
  private int MAX_ATTACHMENT_COUNT = 10;
  private ArrayList<Uri> photoPaths = new ArrayList<>();
  private ArrayList<Uri> docPaths = new ArrayList<>();

  public CallerFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.activity_main, container, false);
    Button openFragmentBtn = view.findViewById(R.id.open_fragment);
    openFragmentBtn.setVisibility(View.GONE);
    view.findViewById(R.id.pick_photo).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pickPhoto();
      }
    });
    view.findViewById(R.id.pick_doc).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pickDoc();
      }
    });
    return view;
  }

  @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
  public void pickPhoto() {
    if (EasyPermissions.hasPermissions(getContext(), FilePickerConst.PERMISSIONS_FILE_PICKER)) {
      onPickPhoto();
    } else {
      // Ask for one permission
      EasyPermissions.requestPermissions(
          this,
          getString(R.string.rationale_photo_picker),
          RC_PHOTO_PICKER_PERM,
              FilePickerConst.PERMISSIONS_FILE_PICKER);
    }
  }

  @AfterPermissionGranted(RC_FILE_PICKER_PERM)
  public void pickDoc() {
    if (EasyPermissions.hasPermissions(getContext(), FilePickerConst.PERMISSIONS_FILE_PICKER)) {
      onPickDoc();
    } else {
      // Ask for one permission
      EasyPermissions.requestPermissions(
          this,
          getString(R.string.rationale_doc_picker),
          RC_FILE_PICKER_PERM,
              FilePickerConst.PERMISSIONS_FILE_PICKER);
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case FilePickerConst.REQUEST_CODE_PHOTO:
        if (resultCode == Activity.RESULT_OK && data != null) {
          ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
          if(dataList != null) {
            photoPaths = new ArrayList<Uri>();
            photoPaths.addAll(dataList);
          }
        }
        break;

      case FilePickerConst.REQUEST_CODE_DOC:
        if (resultCode == Activity.RESULT_OK && data != null) {
          ArrayList<Uri> dataList = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
          if(dataList != null) {
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

    final RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
    if (recyclerView != null) {
      StaggeredGridLayoutManager layoutManager =
          new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
      layoutManager.setGapStrategy(
          StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
      recyclerView.setLayoutManager(layoutManager);

      ImageAdapter imageAdapter = new ImageAdapter(getActivity(), filePaths, new ImageAdapter.ImageAdapterListener() {
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

    Toast.makeText(getActivity(), "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT)
        .show();
  }

  public void onPickPhoto() {
    int maxCount = MAX_ATTACHMENT_COUNT - docPaths.size();
    if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
      Toast.makeText(getActivity(), "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
          Toast.LENGTH_SHORT).show();
    } else {
      FilePickerBuilder.Companion.getInstance()
          .setMaxCount(maxCount)
          .setSelectedFiles(photoPaths)
          .setActivityTheme(R.style.FilePickerTheme)
          .pickPhoto(this);
    }
  }

  public void onPickDoc() {
    int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
    if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT) {
      Toast.makeText(getActivity(), "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items",
          Toast.LENGTH_SHORT).show();
    } else {
      FilePickerBuilder.Companion.getInstance()
          .setMaxCount(maxCount)
          .setSelectedFiles(docPaths)
          .enableDocSupport(true)
          .setActivityTheme(R.style.FilePickerTheme)
          .pickFile(this);
    }
  }

  @Override public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
  }

  @Override public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
      new AppSettingsDialog.Builder(this).build().show();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }
}
