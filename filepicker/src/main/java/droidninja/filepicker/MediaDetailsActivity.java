package droidninja.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import droidninja.filepicker.adapters.FileAdapterListener;
import droidninja.filepicker.adapters.PhotoGridAdapter;
import droidninja.filepicker.cursors.loadercallbacks.FileResultCallback;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.models.PhotoDirectory;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.utils.MediaStoreHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaDetailsActivity extends BaseFilePickerActivity implements FileAdapterListener {

  private static final int SCROLL_THRESHOLD = 30;
  private RecyclerView recyclerView;
  private TextView emptyView;
  private RequestManager mGlideRequestManager;
  private PhotoGridAdapter photoGridAdapter;
  private int fileType;
  private MenuItem selectAllItem;
  private PhotoDirectory photoDirectory;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState, R.layout.activity_media_details);
  }

  @Override protected void initView() {
    mGlideRequestManager = Glide.with(this);
    Intent intent = getIntent();
    if (intent != null) {

      fileType =
          intent.getIntExtra(FilePickerConst.EXTRA_FILE_TYPE, FilePickerConst.MEDIA_TYPE_IMAGE);
      photoDirectory = intent.getParcelableExtra(PhotoDirectory.class.getSimpleName());
      if (photoDirectory != null) {

        setUpView();
        setTitle(0);
      }
    }
  }

  public void setTitle(int count) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      int maxCount = PickerManager.getInstance().getMaxCount();
      if (maxCount == -1 && count > 0) {
        actionBar.setTitle(String.format(getString(R.string.attachments_num), count));
      } else if (maxCount > 0 && count > 0) {
        actionBar.setTitle(
            String.format(getString(R.string.attachments_title_text), count, maxCount));
      } else {
        actionBar.setTitle(photoDirectory.getName());
      }
    }
  }

  private void setUpView() {
    recyclerView = findViewById(R.id.recyclerview);
    emptyView = findViewById(R.id.empty_view);

    StaggeredGridLayoutManager layoutManager =
        new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
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
  }

  @Override protected void onResume() {
    super.onResume();
    getDataFromMedia(photoDirectory.getBucketId());
  }

  private void getDataFromMedia(String bucketId) {
    Bundle mediaStoreArgs = new Bundle();
    mediaStoreArgs.putBoolean(FilePickerConst.EXTRA_SHOW_GIF, false);
    mediaStoreArgs.putString(FilePickerConst.EXTRA_BUCKET_ID, bucketId);

    mediaStoreArgs.putInt(FilePickerConst.EXTRA_FILE_TYPE, fileType);

    if (fileType == FilePickerConst.MEDIA_TYPE_IMAGE) {
      MediaStoreHelper.getPhotoDirs(this, mediaStoreArgs, new FileResultCallback<PhotoDirectory>() {
        @Override public void onResultCallback(List<PhotoDirectory> dirs) {
          updateList(dirs);
        }
      });
    } else if (fileType == FilePickerConst.MEDIA_TYPE_VIDEO) {
      MediaStoreHelper.getVideoDirs(this, mediaStoreArgs, new FileResultCallback<PhotoDirectory>() {
        @Override public void onResultCallback(List<PhotoDirectory> dirs) {
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

    Collections.sort(medias, new Comparator<Media>() {
      @Override public int compare(Media a, Media b) {
        return b.getId() - a.getId();
      }
    });

    if (medias.size() > 0) {
      emptyView.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    } else {
      emptyView.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
      return;
    }

    if (photoGridAdapter != null) {
      photoGridAdapter.setData(medias);
      photoGridAdapter.notifyDataSetChanged();
    } else {
      photoGridAdapter = new PhotoGridAdapter(this, mGlideRequestManager, (ArrayList<Media>) medias,
          PickerManager.getInstance().getSelectedPhotos(), false, this);
      recyclerView.setAdapter(photoGridAdapter);
    }

    if (PickerManager.getInstance().getMaxCount() == -1) {
      if (photoGridAdapter != null && selectAllItem != null) {
        if (photoGridAdapter.getItemCount() == photoGridAdapter.getSelectedItemCount()) {
          selectAllItem.setIcon(R.drawable.ic_select_all);
          selectAllItem.setChecked(true);
        }
      }
      setTitle(PickerManager.getInstance().getCurrentCount());
    }
  }

  private void resumeRequestsIfNotDestroyed() {
    if (!AndroidLifecycleUtils.canLoadImage(this)) {
      return;
    }

    mGlideRequestManager.resumeRequests();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if(PickerManager.getInstance().getMaxCount()>1) {
      getMenuInflater().inflate(R.menu.media_detail_menu, menu);
      selectAllItem = menu.findItem(R.id.action_select);
      selectAllItem.setVisible(PickerManager.getInstance().hasSelectAll());
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.action_done) {
      setResult(RESULT_OK, null);
      finish();

      return true;
    } else if (itemId == R.id.action_select) {
      if (photoGridAdapter != null && selectAllItem != null) {
        if (selectAllItem.isChecked()) {
          PickerManager.getInstance().deleteMedia(photoGridAdapter.getSelectedPaths());
          photoGridAdapter.clearSelection();

          selectAllItem.setIcon(R.drawable.ic_deselect_all);
        } else {
          photoGridAdapter.selectAll();
          PickerManager.getInstance()
              .add(photoGridAdapter.getSelectedPaths(), FilePickerConst.FILE_TYPE_MEDIA);
          selectAllItem.setIcon(R.drawable.ic_select_all);
        }
        selectAllItem.setChecked(!selectAllItem.isChecked());
        setTitle(PickerManager.getInstance().getCurrentCount());
      }
      return true;
    } else if (itemId == android.R.id.home) {
      onBackPressed();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public void onItemSelected() {
    int maxCount = PickerManager.getInstance().getMaxCount();
    if (maxCount == 1) {
      setResult(RESULT_OK, null);
      finish();
    }
    setTitle(PickerManager.getInstance().getCurrentCount());
  }

  @Override public void onBackPressed() {
    setResult(RESULT_CANCELED, null);
    finish();
  }
}
