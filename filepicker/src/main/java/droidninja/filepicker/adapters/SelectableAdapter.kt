package droidninja.filepicker.adapters

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView

import java.util.ArrayList

import droidninja.filepicker.PickerManager
import droidninja.filepicker.models.BaseFile

abstract class SelectableAdapter<VH : RecyclerView.ViewHolder, T : BaseFile>(items: List<T>, selectedPaths: List<Uri>) : RecyclerView.Adapter<VH>(), Selectable<T> {
    var items: List<T>
        private set

    protected var selectedPhotos: MutableList<T>

    override val selectedItemCount: Int
        get() = selectedPhotos.size

    val selectedPaths: ArrayList<Uri>
        get() {
            val paths = ArrayList<Uri>()
            for (index in selectedPhotos.indices) {
                paths.add(selectedPhotos[index].path)
            }
            return paths
        }

    init {
        this.items = items
        selectedPhotos = ArrayList()

        addPathsToSelections(selectedPaths)
    }

    private fun addPathsToSelections(selectedPaths: List<Uri>?) {
        if (selectedPaths == null) return

        for (index in items.indices) {
            for (jindex in selectedPaths.indices) {
                if (items[index].path == selectedPaths[jindex]) {
                    selectedPhotos.add(items[index])
                }
            }
        }
    }

    /**
     * Indicates if the item at position where is selected
     *
     * @param photo Media of the item to check
     * @return true if the item is selected, false otherwise
     */
    override fun isSelected(item: T): Boolean {
        return selectedPhotos.contains(item)
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Media of the item to toggle the selection status for
     */
    override fun toggleSelection(item: T) {
        if (selectedPhotos.contains(item)) {
            selectedPhotos.remove(item)
        } else {
            selectedPhotos.add(item)
        }
    }

    /**
     * Clear the selection status for all items
     */
    override fun clearSelection() {
        selectedPhotos.clear()
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedPhotos.clear()
        selectedPhotos.addAll(items as Iterable<T>)
        notifyDataSetChanged()
    }

    fun setData(items: List<T>) {
        this.items = items
    }

    companion object {

        private val TAG = "SelectableAdapter"
    }
}