package droidninja.filepicker.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList

import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.models.Document
import droidninja.filepicker.views.SmoothCheckBox

/**
 * Created by droidNinja on 29/07/16.
 */
class FileListAdapter(private val context: Context, private var mFilteredList: List<Document>, selectedPaths: List<String>,
                      private val mListener: FileAdapterListener?) : SelectableAdapter<FileListAdapter.FileViewHolder, Document>(mFilteredList, selectedPaths), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_doc_layout, parent, false)

        return FileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val document = mFilteredList[position]

        val drawable = document.fileType.getDrawable()
        holder.imageView.setImageResource(drawable)
        if (drawable == R.drawable.icon_file_unknown || drawable == R.drawable.icon_file_pdf) {
            holder.fileTypeTv.visibility = View.VISIBLE
            holder.fileTypeTv.text = document.fileType.title
        } else {
            holder.fileTypeTv.visibility = View.GONE
        }

        holder.fileNameTextView.text = document.title
        holder.fileSizeTextView.text = Formatter.formatShortFileSize(context, java.lang.Long.parseLong(document.size))

        holder.itemView.setOnClickListener { onItemClicked(document, holder) }

        //in some cases, it will prevent unwanted situations
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnClickListener { onItemClicked(document, holder) }

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.isChecked = isSelected(document)

        holder.itemView.setBackgroundResource(
                if (isSelected(document)) R.color.bg_gray else android.R.color.white)
        holder.checkBox.visibility = if (isSelected(document)) View.VISIBLE else View.GONE

        holder.checkBox.setOnCheckedChangeListener(object : SmoothCheckBox.OnCheckedChangeListener {
            override fun onCheckedChanged(checkBox: SmoothCheckBox, isChecked: Boolean) {
                toggleSelection(document)
                holder.itemView.setBackgroundResource(if (isChecked) R.color.bg_gray else android.R.color.white)
            }
        })
    }

    private fun onItemClicked(document: Document, holder: FileViewHolder) {
        if (PickerManager.getMaxCount() == 1) {
            PickerManager.add(document.path, FilePickerConst.FILE_TYPE_DOCUMENT)
        } else {
            if (holder.checkBox.isChecked) {
                PickerManager.remove(document.path, FilePickerConst.FILE_TYPE_DOCUMENT)
                holder.checkBox.setChecked(!holder.checkBox.isChecked, true)
                holder.checkBox.visibility = View.GONE
            } else if (PickerManager.shouldAdd()) {
                PickerManager.add(document.path, FilePickerConst.FILE_TYPE_DOCUMENT)
                holder.checkBox.setChecked(!holder.checkBox.isChecked, true)
                holder.checkBox.visibility = View.VISIBLE
            }
        }

        mListener?.onItemSelected()
    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {

                    mFilteredList = items
                } else {

                    val filteredList = ArrayList<Document>()

                    for (document in items) {

                        if (document.title.toLowerCase().contains(charString)) {

                            filteredList.add(document)
                        }
                    }

                    mFilteredList = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                    mFilteredList = filterResults.values as List<Document>
                    notifyDataSetChanged()
            }
        }
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var fileTypeTv: TextView

        internal var checkBox: SmoothCheckBox

        internal var imageView: ImageView

        internal var fileNameTextView: TextView

        internal var fileSizeTextView: TextView

        init {
            checkBox = itemView.findViewById(R.id.checkbox)
            imageView = itemView.findViewById(R.id.file_iv)
            fileNameTextView = itemView.findViewById(R.id.file_name_tv)
            fileTypeTv = itemView.findViewById(R.id.file_type_tv)
            fileSizeTextView = itemView.findViewById(R.id.file_size_tv)
        }
    }
}
