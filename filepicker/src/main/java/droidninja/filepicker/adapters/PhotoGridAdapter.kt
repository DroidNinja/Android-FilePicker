package droidninja.filepicker.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView

import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions

import java.io.File
import java.util.ArrayList

import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.models.Media
import droidninja.filepicker.utils.AndroidLifecycleUtils
import droidninja.filepicker.views.SmoothCheckBox

class PhotoGridAdapter(private val context: Context,
                       private val glide: RequestManager,
                       medias: ArrayList<Media>,
                       selectedPaths: ArrayList<String>,
                       private val showCamera: Boolean,
                       private val mListener: FileAdapterListener?) : SelectableAdapter<PhotoGridAdapter.PhotoViewHolder, Media>(medias, selectedPaths) {
    private var imageSize: Int = 0
    private var cameraOnClickListener: View.OnClickListener? = null

    init {
        setColumnNumber(context, 3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_photo_layout, parent, false)

        return PhotoViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (showCamera)
            if (position == 0) ITEM_TYPE_CAMERA else ITEM_TYPE_PHOTO
        else
            ITEM_TYPE_PHOTO
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

            val media = items[if (showCamera) position - 1 else position]

            if (AndroidLifecycleUtils.canLoadImage(holder.imageView.context)) {
                glide.load(File(media.path))
                        .apply(RequestOptions
                                .centerCropTransform()
                                .override(imageSize, imageSize)
                                .placeholder(R.drawable.image_placeholder))
                        .thumbnail(0.5f)
                        .into(holder.imageView)
            }


            if (media.getMediaType() == FilePickerConst.MEDIA_TYPE_VIDEO)
                holder.videoIcon.visibility = View.VISIBLE
            else
                holder.videoIcon.visibility = View.GONE

            holder.itemView.setOnClickListener { onItemClicked(holder, media) }

            //in some cases, it will prevent unwanted situations
            holder.checkBox.visibility = View.GONE
            holder.checkBox.setOnCheckedChangeListener(null)
            holder.checkBox.setOnClickListener { onItemClicked(holder, media) }

            //if true, your checkbox will be selected, else unselected
            holder.checkBox.isChecked = isSelected(media)

            holder.selectBg.visibility = if (isSelected(media)) View.VISIBLE else View.GONE
            holder.checkBox.visibility = if (isSelected(media)) View.VISIBLE else View.GONE

            holder.checkBox.setOnCheckedChangeListener(object : SmoothCheckBox.OnCheckedChangeListener {
                override fun onCheckedChanged(checkBox: SmoothCheckBox, isChecked: Boolean) {
                    toggleSelection(media)
                    holder.selectBg.visibility = if (isChecked) View.VISIBLE else View.GONE

                    if (isChecked) {
                        holder.checkBox.visibility = View.VISIBLE
                        PickerManager.add(media.path, FilePickerConst.FILE_TYPE_MEDIA)
                    } else {
                        holder.checkBox.visibility = View.GONE
                        PickerManager.remove(media.path, FilePickerConst.FILE_TYPE_MEDIA)
                    }

                    mListener?.onItemSelected()
                }
            })

        } else {
            holder.imageView.setImageResource(PickerManager.cameraDrawable)
            holder.checkBox.visibility = View.GONE
            holder.itemView.setOnClickListener(cameraOnClickListener)
            holder.videoIcon.visibility = View.GONE
        }
    }

    private fun onItemClicked(holder: PhotoViewHolder, media: Media) {
        if (PickerManager.getMaxCount() == 1) {
            PickerManager.add(media.path, FilePickerConst.FILE_TYPE_MEDIA)
            mListener?.onItemSelected()
        } else if (holder.checkBox.isChecked || PickerManager.shouldAdd()) {
            holder.checkBox.setChecked(!holder.checkBox.isChecked, true)
        }
    }

    private fun setColumnNumber(context: Context, columnNum: Int) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        val widthPixels = metrics.widthPixels
        imageSize = widthPixels / columnNum
    }

    override fun getItemCount(): Int {
        return if (showCamera) items.size + 1 else items.size
    }

    fun setCameraListener(onClickListener: View.OnClickListener) {
        this.cameraOnClickListener = onClickListener
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var checkBox: SmoothCheckBox

        var imageView: ImageView

        var videoIcon: ImageView

        var selectBg: View

        init {
            checkBox = itemView.findViewById<View>(R.id.checkbox) as SmoothCheckBox
            imageView = itemView.findViewById<View>(R.id.iv_photo) as ImageView
            videoIcon = itemView.findViewById<View>(R.id.video_icon) as ImageView
            selectBg = itemView.findViewById(R.id.transparent_bg)
        }
    }

    companion object {

        val ITEM_TYPE_CAMERA = 100
        val ITEM_TYPE_PHOTO = 101
    }
}
