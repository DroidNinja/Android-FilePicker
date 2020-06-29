package droidninja.filepicker.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import droidninja.filepicker.FilePickerConst

import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.models.PhotoDirectory
import droidninja.filepicker.utils.AndroidLifecycleUtils

class FolderGridAdapter(private val context: Context,
                        private val glide: RequestManager,
                        var items: List<PhotoDirectory>,
                        private val showCamera: Boolean,
                        private val fileType: Int) : RecyclerView.Adapter<FolderGridAdapter.PhotoViewHolder>() {
    private var imageSize: Int = 0
    private var folderGridAdapterListener: FolderGridAdapterListener? = null

    interface FolderGridAdapterListener {
        fun onCameraClicked()
        fun onFolderClicked(photoDirectory: PhotoDirectory)
    }

    init {
        setColumnNumber(context, 3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_folder_layout, parent, false)

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

            val photoDirectory = items[if (showCamera) position - 1 else position]

            if (AndroidLifecycleUtils.canLoadImage(holder.imageView.context)) {
                glide.load(photoDirectory.getCoverPath())
                        .apply(RequestOptions
                                .centerCropTransform()
                                .override(imageSize, imageSize)
                                .placeholder(R.drawable.image_placeholder))
                        .thumbnail(0.5f)
                        .into(holder.imageView)
            }

            holder.folderTitle.text = photoDirectory.name
            holder.folderCount.text = photoDirectory.medias.size.toString()

            holder.itemView.setOnClickListener {
                    folderGridAdapterListener?.onFolderClicked(photoDirectory)
            }
            holder.bottomOverlay.visibility = View.VISIBLE
        } else {
            if (fileType == FilePickerConst.MEDIA_TYPE_IMAGE) {
                holder.imageView.setImageResource(PickerManager.cameraImageDrawable)
            } else {
                holder.imageView.setImageResource(PickerManager.cameraVideoDrawable)
            }
            holder.itemView.setOnClickListener {
                folderGridAdapterListener?.onCameraClicked()
            }
            holder.bottomOverlay.visibility = View.GONE
        }
    }

    fun setData(newItems: List<PhotoDirectory>) {
        this.items = newItems
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

    fun setFolderGridAdapterListener(onClickListener: FolderGridAdapterListener) {
        this.folderGridAdapterListener = onClickListener
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView = itemView.findViewById<View>(R.id.iv_photo) as ImageView
        var folderTitle: TextView = itemView.findViewById<View>(R.id.folder_title) as TextView
        var folderCount: TextView = itemView.findViewById<View>(R.id.folder_count) as TextView
        var bottomOverlay: View = itemView.findViewById(R.id.bottomOverlay)
        var selectBg: View = itemView.findViewById(R.id.transparent_bg)

    }

    companion object {

        const val ITEM_TYPE_CAMERA = 100
        const val ITEM_TYPE_PHOTO = 101
    }
}
