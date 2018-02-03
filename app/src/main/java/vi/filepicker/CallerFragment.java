package vi.filepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import butterknife.BindView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.fragments.BaseFragment;
import java.util.ArrayList;
/**
 * A simple {@link Fragment} subclass.
 */
public class CallerFragment extends BaseFragment {
  private int MAX_ATTACHMENT_COUNT = 10;
  private ArrayList<String> photoPaths = new ArrayList<>();
  private ArrayList<String> docPaths = new ArrayList<>();
  private ArrayList<String> folderPaths = new ArrayList<>();

  public CallerFragment() {
    // Required empty public constructor
  }

  @BindView(R.id.open_fragment) Button openFragmentBtn;

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.activity_main, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    initView();
  }

  private void initView() {
    openFragmentBtn.setVisibility(View.GONE);
  }

    @OnClick(R.id.pick_photo)
    public void pickPhotoClicked(View view) {
        onPickPhoto();
    }

    @OnClick(R.id.pick_doc)
    public void pickDocClicked(View view) {
        onPickDoc();
    }

    @OnClick(R.id.pick_folder)
    public void pickFolderClicked(View view) {
      onPickFolder();
    }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case FilePickerConst.REQUEST_CODE_PHOTO:
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

      case FilePickerConst.REQUEST_CODE_FOLDER:
        if (resultCode == Activity.RESULT_OK && data != null) {
          folderPaths = new ArrayList<>();
          folderPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_FOLDER));
        }
        break;
    }

    addThemToView(photoPaths,docPaths, folderPaths);
  }

  private void addThemToView(ArrayList<String> imagePaths, ArrayList<String> docPaths, ArrayList<String> folderPaths) {
    ArrayList<String> filePaths = new ArrayList<>();
    if(imagePaths!=null)
      filePaths.addAll(imagePaths);

    if(docPaths!=null)
      filePaths.addAll(docPaths);

    if(folderPaths!=null)
      filePaths.addAll(folderPaths);

    RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview);
    if (recyclerView != null) {
      StaggeredGridLayoutManager layoutManager =
          new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
      layoutManager.setGapStrategy(
          StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
      recyclerView.setLayoutManager(layoutManager);

      ImageAdapter imageAdapter = new ImageAdapter(getActivity(), filePaths);

      recyclerView.setAdapter(imageAdapter);
      recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    Toast.makeText(getActivity(), "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT)
        .show();
  }

    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT-docPaths.size();
        if((docPaths.size()+photoPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(getActivity(), "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .pickPhoto(this);
    }

    public void onPickDoc() {
        int maxCount = MAX_ATTACHMENT_COUNT-photoPaths.size();
        if((docPaths.size()+photoPaths.size())==MAX_ATTACHMENT_COUNT)
            Toast.makeText(getActivity(), "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
        else
            FilePickerBuilder.getInstance().setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .pickFile(this);
    }

    public void onPickFolder() {
      int maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size();
      if ((docPaths.size() + photoPaths.size()) == MAX_ATTACHMENT_COUNT)
        Toast.makeText(getActivity(), "Cannot select more than " + MAX_ATTACHMENT_COUNT + " items", Toast.LENGTH_SHORT).show();
      else
        FilePickerBuilder.getInstance().setMaxCount(maxCount)
                .setSelectedFiles(folderPaths)
                .setMaxCount(3)
                .setActivityTheme(R.style.FilePickerTheme)
                .pickFolder(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
