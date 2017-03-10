package droidninja.filepicker.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.MediaDetailsActivity;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.FolderGridAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.utils.GridSpacingItemDecoration;
import droidninja.filepicker.utils.ImageCaptureManager;
import droidninja.filepicker.utils.MediaStoreHelper;


public class MediaFolderPickerFragment extends BaseFragment implements FolderGridAdapter.FolderGridAdapterListener{

    private static final String TAG = MediaFolderPickerFragment.class.getSimpleName();
    private static final int SCROLL_THRESHOLD = 30;
    RecyclerView recyclerView;

    TextView emptyView;

    private PhotoPickerFragmentListener mListener;
    private FolderGridAdapter photoGridAdapter;
    private ImageCaptureManager imageCaptureManager;
    private RequestManager mGlideRequestManager;
    private int fileType;

    public MediaFolderPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_folder_picker, container, false);
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

    public static MediaFolderPickerFragment newInstance(int fileType) {
        MediaFolderPickerFragment photoPickerFragment = new MediaFolderPickerFragment();
        Bundle bun = new Bundle();
        bun.putInt(FILE_TYPE, fileType);
        photoPickerFragment.setArguments(bun);
        return  photoPickerFragment;
    }

    public interface PhotoPickerFragmentListener {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlideRequestManager = Glide.with(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        fileType = getArguments().getInt(FILE_TYPE);

        imageCaptureManager = new ImageCaptureManager(getActivity());
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        int spanCount = 2; // 2 columns
        int spacing = 5; // 5px
        boolean includeEdge = false;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.d(">>> Picker >>>", "dy = " + dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    mGlideRequestManager.pauseRequests();
                } else {
                    resumeRequestsIfNotDestroyed();
                }
            }
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeRequestsIfNotDestroyed();
                }
            }
        });
        
        getDataFromMedia();
    }

    private void getDataFromMedia() {
        Bundle mediaStoreArgs = new Bundle();
        mediaStoreArgs.putBoolean(FilePickerConst.EXTRA_SHOW_GIF, PickerManager.getInstance().isShowGif());
        mediaStoreArgs.putInt(FilePickerConst.EXTRA_FILE_TYPE, fileType);

        if(fileType==FilePickerConst.MEDIA_TYPE_IMAGE) {
            MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
                    new FileResultCallback<PhotoDirectory>() {
                        @Override
                        public void onResultCallback(List<PhotoDirectory> dirs) {
                            updateList(dirs);
                        }
                    });
        }
        else if(fileType==FilePickerConst.MEDIA_TYPE_VIDEO)
        {
            MediaStoreHelper.getVideoDirs(getActivity(), mediaStoreArgs,
                    new FileResultCallback<PhotoDirectory>() {
                        @Override
                        public void onResultCallback(List<PhotoDirectory> dirs) {
                            updateList(dirs);
                        }
                    });
        }
    }

    private void updateList(List<PhotoDirectory> dirs) {
        Log.i("updateList",""+dirs.size());
        if(dirs.size()>0) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        PhotoDirectory photoDirectory = new PhotoDirectory();
        photoDirectory.setBucketId(FilePickerConst.ALL_PHOTOS_BUCKET_ID);

        if(fileType==FilePickerConst.MEDIA_TYPE_VIDEO)
        {
            photoDirectory.setName(getString(R.string.all_videos));
        }
        else if(fileType==FilePickerConst.MEDIA_TYPE_IMAGE)
        {
            photoDirectory.setName(getString(R.string.all_photos));
        }
        else
            photoDirectory.setName(getString(R.string.all_files));

        if(dirs.size()>0 && dirs.get(0).getMedias().size()>0)
        {
            photoDirectory.setDateAdded(dirs.get(0).getDateAdded());
            photoDirectory.setCoverPath(dirs.get(0).getMedias().get(0).getPath());
        }

        for (int i = 0; i < dirs.size(); i++) {
            photoDirectory.addPhotos(dirs.get(i).getMedias());
        }

        dirs.add(0,photoDirectory);

            if(photoGridAdapter!=null)
            {
                photoGridAdapter.setData(dirs);
                photoGridAdapter.notifyDataSetChanged();
            }
            else
            {
                photoGridAdapter = new FolderGridAdapter(getActivity(), mGlideRequestManager, (ArrayList<PhotoDirectory>) dirs, null, (fileType==FilePickerConst.MEDIA_TYPE_IMAGE) && PickerManager.getInstance().isEnableCamera());
                recyclerView.setAdapter(photoGridAdapter);

                photoGridAdapter.setFolderGridAdapterListener(this);
            }

    }

    @Override
    public void onCameraClicked() {
        try {
            Intent intent = imageCaptureManager.dispatchTakePictureIntent(getActivity());
            if(intent!=null)
                startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
            else
                Toast.makeText(getActivity(), R.string.no_camera_exists, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFolderClicked(PhotoDirectory photoDirectory) {
        Intent intent = new Intent(getActivity(), MediaDetailsActivity.class);
        intent.putExtra(PhotoDirectory.class.getSimpleName(),photoDirectory);
        intent.putExtra(FilePickerConst.EXTRA_FILE_TYPE,fileType);
        getActivity().startActivityForResult(intent, FilePickerConst.REQUEST_CODE_MEDIA_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                if(resultCode== Activity.RESULT_OK)
                {
                    imageCaptureManager.galleryAddPic();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getDataFromMedia();

                        }
                    },1000);
                }
                break;
        }
    }

    private void resumeRequestsIfNotDestroyed() {
        if (!AndroidLifecycleUtils.canLoadImage(this)) {
            return;
        }

        mGlideRequestManager.resumeRequests();
    }
}
