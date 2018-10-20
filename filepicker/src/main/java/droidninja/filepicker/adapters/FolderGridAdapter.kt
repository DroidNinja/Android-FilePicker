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

import java.io.File
import java.util.ArrayList

import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.models.PhotoDirectory
import droidninja.filepicker.utils.AndroidLifecycleUtils

class FolderGridAdapter(private val context: Context, private val glide: RequestManager, photos: MutableList<PhotoDirectory>, selectedPaths: MutableList<String>, private val showCamera: Boolean) : SelectableAdapter<FolderGridAdapter.PhotoViewHolder, PhotoDirectory>(photos, selectedPaths) {
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
                glide.load(File(photoDirectory.coverPath))
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
            holder.imageView.setImageResource(PickerManager.cameraDrawable)
            holder.itemView.setOnClickListener {
                folderGridAdapterListener?.onCameraClicked()
            }
            holder.bottomOverlay.visibility = View.GONE
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

    fun setFolderGridAdapterListener(onClickListener: FolderGridAdapterListener) {
        this.folderGridAdapterListener = onClickListener
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView
        var folderTitle: TextView
        var folderCount: TextView
        var bottomOverlay: View
        var selectBg: View

        init {
            imageView = itemView.findViewById<View>(R.id.iv_photo) as ImageView
            folderTitle = itemView.findViewById<View>(R.id.folder_title) as TextView
            folderCount = itemView.findViewById<View>(R.id.folder_count) as TextView
            bottomOverlay = itemView.findViewById(R.id.bottomOverlay)
            selectBg = itemView.findViewById(R.id.transparent_bg)
        }
    }

    companion object {

        val ITEM_TYPE_CAMERA = 100
        val ITEM_TYPE_PHOTO = 101
    }
}
