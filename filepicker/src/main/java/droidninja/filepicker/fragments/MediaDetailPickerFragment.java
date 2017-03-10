package droidninja.filepicker.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.PhotoGridAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.utils.ImageCaptureManager;
import droidninja.filepicker.utils.MediaStoreHelper;


public class MediaDetailPickerFragment extends BaseFragment{

    private static final String TAG = MediaDetailPickerFragment.class.getSimpleName();
    private static final int SCROLL_THRESHOLD = 30;
    RecyclerView recyclerView;

    TextView emptyView;

    private PhotoPickerFragmentListener mListener;
    private PhotoGridAdapter photoGridAdapter;
    private ImageCaptureManager imageCaptureManager;
    private RequestManager mGlideRequestManager;
    private int fileType;

    public MediaDetailPickerFragment() {
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

    public static MediaDetailPickerFragment newInstance(int fileType) {
        MediaDetailPickerFragment mediaDetailPickerFragment = new MediaDetailPickerFragment();
        Bundle bun = new Bundle();
        bun.putInt(FILE_TYPE, fileType);
        mediaDetailPickerFragment.setArguments(bun);
        return mediaDetailPickerFragment;
    }

    public interface PhotoPickerFragmentListener {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlideRequestManager = Glide.with(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
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
        ArrayList<Media> medias = new ArrayList<>();
        for (int i = 0; i < dirs.size(); i++) {
            medias.addAll(dirs.get(i).getMedias());
        }

        Collections.sort(medias,new Comparator<Media>() {
            @Override
            public int compare(Media a, Media b) {
                return b.getId() - a.getId();
            }
        });

        if(medias.size()>0) {
            emptyView.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.VISIBLE);
        }

            if(photoGridAdapter!=null)
            {
                photoGridAdapter.setData(medias);
                photoGridAdapter.notifyDataSetChanged();
            }
            else
            {
                photoGridAdapter = new PhotoGridAdapter(getActivity(), mGlideRequestManager, (ArrayList<Media>) medias, PickerManager.getInstance().getSelectedPhotos(),(fileType==FilePickerConst.MEDIA_TYPE_IMAGE) && PickerManager.getInstance().isEnableCamera());
                recyclerView.setAdapter(photoGridAdapter);
                photoGridAdapter.setCameraListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
