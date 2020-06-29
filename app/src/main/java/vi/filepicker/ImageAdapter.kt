package vi.filepicker

import android.content.Context
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

/**
 * Created by droidNinja on 29/07/16.
 */
class ImageAdapter(private val context: Context, private val paths: ArrayList<Uri>) : RecyclerView.Adapter<ImageAdapter.FileViewHolder>() {
    private var imageSize = 0
    private fun setColumnNumber(context: Context, columnNum: Int) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metrics)
        val widthPixels = metrics.widthPixels
        imageSize = widthPixels / columnNum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val path = paths[position]
        Glide.with(context)
                .load(path)
                .apply(RequestOptions.centerCropTransform()
                        .dontAnimate()
                        .override(imageSize, imageSize)
                        .placeholder(droidninja.filepicker.R.drawable.image_placeholder))
                .thumbnail(0.5f)
                .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return paths.size
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: AppCompatImageView = itemView.findViewById(R.id.iv_photo)
    }

    init {
        setColumnNumber(context, 3)
    }
}