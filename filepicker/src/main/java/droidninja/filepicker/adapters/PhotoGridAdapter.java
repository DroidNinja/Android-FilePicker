package droidninja.filepicker.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import java.io.File;
import java.util.ArrayList;

import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.models.Photo;
import droidninja.filepicker.utils.image.FrescoFactory;
import droidninja.filepicker.views.SmoothCheckBox;

public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder, Photo>{

  private final Context context;
  private int imageSize;

  public final static int ITEM_TYPE_CAMERA = 100;
  public final static int ITEM_TYPE_PHOTO  = 101;
  private View.OnClickListener cameraOnClickListener;

  public PhotoGridAdapter(Context context, ArrayList<Photo> photos, ArrayList<String> selectedPaths)
  {
    super(photos, selectedPaths);
    this.context = context;
    setColumnNumber(context,3);
  }

  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_layout, parent, false);

    return new PhotoViewHolder(itemView);
  }

  @Override
  public int getItemViewType(int position) {
    return (position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
  }

  @Override
  public void onBindViewHolder(final PhotoViewHolder holder, int position) {
    if(getItemViewType(position) == ITEM_TYPE_PHOTO) {

      final Photo photo = getItems().get(position-1);

      FrescoFactory.getLoader().showImage(holder.imageView, Uri.fromFile(new File(photo.getPath())), FrescoFactory.newOption(imageSize, imageSize));

      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (holder.checkBox.isChecked() || PickerManager.getInstance().shouldAdd()) {
            holder.checkBox.setChecked(!holder.checkBox.isChecked(), true);
          }
        }
      });

      //in some cases, it will prevent unwanted situations
      holder.checkBox.setVisibility(View.VISIBLE);
      holder.checkBox.setOnCheckedChangeListener(null);
      holder.checkBox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!holder.checkBox.isChecked() && !PickerManager.getInstance().shouldAdd()) {
            return;
          }
        }
      });

      //if true, your checkbox will be selected, else unselected
      holder.checkBox.setChecked(isSelected(photo));

      holder.selectBg.setVisibility(isSelected(photo) ? View.VISIBLE : View.GONE);

      holder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
          toggleSelection(photo);
          holder.selectBg.setVisibility(isChecked ? View.VISIBLE : View.GONE);

          if (isChecked)
            PickerManager.getInstance().add(photo);
          else
            PickerManager.getInstance().remove(photo);
        }
      });
    }
    else
    {
      FrescoFactory.getLoader().showImage(holder.imageView,R.drawable.ic_camera,null);
      holder.checkBox.setVisibility(View.GONE);
      holder.itemView.setOnClickListener(cameraOnClickListener);
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
    return getItems().size()+1;
  }

  public void setCameraListener(View.OnClickListener onClickListener)
  {
    this.cameraOnClickListener = onClickListener;
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {

      SmoothCheckBox checkBox;

      SimpleDraweeView imageView;

      View selectBg;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      checkBox = (SmoothCheckBox) itemView.findViewById(R.id.checkbox);
      imageView = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
      selectBg = itemView.findViewById(R.id.transparent_bg);
    }
  }
}
