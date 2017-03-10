package droidninja.filepicker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.models.Media;
import droidninja.filepicker.utils.AndroidLifecycleUtils;
import droidninja.filepicker.views.SmoothCheckBox;

public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder, Media>{

  private final Context context;
  private final RequestManager glide;
  private final boolean showCamera;
  private int imageSize;

  public final static int ITEM_TYPE_CAMERA = 100;
  public final static int ITEM_TYPE_PHOTO  = 101;
  private View.OnClickListener cameraOnClickListener;

  public PhotoGridAdapter(Context context, RequestManager requestManager, ArrayList<Media> medias, ArrayList<String> selectedPaths, boolean showCamera)
  {
    super(medias, selectedPaths);
    this.context = context;
    this.glide = requestManager;
    this.showCamera = showCamera;
    setColumnNumber(context,3);
  }

  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_layout, parent, false);

    return new PhotoViewHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    if(showCamera)
      return (position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
    else
      return ITEM_TYPE_PHOTO;
  }

  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, int position) {
    if(getItemViewType(position) == ITEM_TYPE_PHOTO) {

      final Media media = getItems().get(showCamera?position-1:position);

      if(AndroidLifecycleUtils.canLoadImage(holder.imageView.getContext())) {
        glide.load(new File(media.getPath()))
                .centerCrop()
                .dontAnimate()
                .thumbnail(0.5f)
                .override(imageSize, imageSize)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.imageView);
      }


      if(media.getMediaType()==FilePickerConst.MEDIA_TYPE_VIDEO)
        holder.videoIcon.setVisibility(View.VISIBLE);
      else
        holder.videoIcon.setVisibility(View.GONE);

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(PickerManager.getInstance().getMaxCount()==1)
            PickerManager.getInstance().add(media.getPath(), FilePickerConst.FILE_TYPE_MEDIA);
          else
            if (holder.checkBox.isChecked() || PickerManager.getInstance().shouldAdd()) {
            holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
          }
        }
      });

      //in some cases, it will prevent unwanted situations
      holder.checkBox.setVisibility(View.GONE);
      holder.checkBox.setOnCheckedChangeListener(null);
      holder.checkBox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if(holder.checkBox.isChecked() || PickerManager.getInstance().shouldAdd()) {
            holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
          }
        }
      });

      //if true, your checkbox will be selected, else unselected
      holder.checkBox.setChecked(isSelected(media));

      holder.selectBg.setVisibility(isSelected(media) ? View.VISIBLE : View.GONE);
      holder.checkBox.setVisibility(isSelected(media) ? View.VISIBLE : View.GONE);

      holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
          toggleSelection(media);
          holder.selectBg.setVisibility(isChecked ? View.VISIBLE : View.GONE);

          if (isChecked)
          {
            holder.checkBox.setVisibility(View.VISIBLE);
            PickerManager.getInstance().add(media.getPath(), FilePickerConst.FILE_TYPE_MEDIA);
          }
          else
          {
            holder.checkBox.setVisibility(View.GONE);
            PickerManager.getInstance().remove(media.getPath(),FilePickerConst.FILE_TYPE_MEDIA);
          }
        }
      });

    }
    else
    {
      holder.imageView.setImageResource(R.drawable.ic_camera);
      holder.checkBox.setVisibility(View.GONE);
      holder.itemView.setOnClickListener(cameraOnClickListener);
      holder.videoIcon.setVisibility(View.GONE);
    }
  }

  private void setColumnNumber(Context context, int columnNum) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(metrics);
    int widthPixels = metrics.widthPixels;
    imageSize = widthPixels / columnNum;
  }

  @Override
  public int getItemCount() {
    if(showCamera)
      return getItems().size()+1;
    return getItems().size();
  }

  public void setCameraListener(View.OnClickListener onClickListener)
  {
    this.cameraOnClickListener = onClickListener;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {

      SmoothCheckBox checkBox;

      ImageView imageView;

      ImageView videoIcon;

      View selectBg;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      checkBox = (SmoothCheckBox) itemView.findViewById(R.id.checkbox);
      imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
      videoIcon = (ImageView) itemView.findViewById(R.id.video_icon);
      selectBg = itemView.findViewById(R.id.transparent_bg);
    }
  }
}
