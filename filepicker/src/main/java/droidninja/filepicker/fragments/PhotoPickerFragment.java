package droidninja.filepicker.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.PhotoGridAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Photo;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.ImageCaptureManager;
import droidninja.filepicker.utils.MediaStoreHelper;


public class PhotoPickerFragment extends BaseFragment {

    private static final String TAG = PhotoPickerFragment.class.getSimpleName();
    RecyclerView recyclerView;

    TextView emptyView;

    private PhotoPickerFragmentListener mListener;
    private PhotoGridAdapter photoGridAdapter;
    private ArrayList<String> selectedPaths;
    private ImageCaptureManager imageCaptureManager;

    public PhotoPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_picker, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PhotoPickerFragmentListener) {
            mListener = (PhotoPickerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PhotoPickerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static PhotoPickerFragment newInstance(ArrayList<String> selectedPaths) {
        PhotoPickerFragment photoPickerFragment = new PhotoPickerFragment();
        photoPickerFragment.selectedPaths = selectedPaths;
        return  photoPickerFragment;
    }

    public interface PhotoPickerFragmentListener {
        // TODO: Update argument type and name

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews(view);
        initView();
    }

    private void setViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
    }

    private void initView() {
        imageCaptureManager = new ImageCaptureManager(getActivity());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getDataFromMedia();
    }

    private void getDataFromMedia() {
        Bundle mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(FilePickerConst.EXTRA_SHOW_GIF, false);

        MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
                new FileResultCallback<PhotoDirectory>() {
                    @Override
                    public void onResultCallback(List<PhotoDirectory> dirs) {
                        updateList(dirs);
                    }
                });
    }

    private void updateList(List<PhotoDirectory> dirs) {
        ArrayList<Photo> photos = new ArrayList<>();
        for (int i = 0; i < dirs.size(); i++) {
            photos.addAll(dirs.get(i).getPhotos());
        }

        if(photos.size()>0) {
            emptyView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.VISIBLE);
        }

            if(photoGridAdapter!=null)
            {
                photoGridAdapter.setData(photos);
                photoGridAdapter.notifyDataSetChanged();
            }
            else
            {
                photoGridAdapter = new PhotoGridAdapter(getActivity(), photos,selectedPaths);
                recyclerView.setAdapter(photoGridAdapter);
                photoGridAdapter.setCameraListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = imageCaptureManager.dispatchTakePictureIntent();
                            if(intent!=null)
                                startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                            else
                                Toast.makeText(getActivity(), "No Application exists for camera!", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                if(resultCode== Activity.RESULT_OK)
                {
                    imageCaptureManager.galleryAddPic(getActivity());
                    getDataFromMedia();
                }
                break;
        }
    }
}
