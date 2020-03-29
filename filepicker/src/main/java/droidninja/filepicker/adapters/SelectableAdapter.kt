package droidninja.filepicker.adapters

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView

import java.util.ArrayList

import droidninja.filepicker.PickerManager
import droidninja.filepicker.models.BaseFile

abstract class SelectableAdapter<VH : RecyclerView.ViewHolder, T : BaseFile>(var items: List<T>, var selectedPaths: MutableList<Uri> = mutableListOf()) : RecyclerView.Adapter<VH>(), Selectable<T> {

    override val selectedItemCount: Int
        get() = selectedPaths.size
    /**
     * Indicates if the item at position where is selected
     *
     * @param photo Media of the item to check
     * @return true if the item is selected, false otherwise
     */
    override fun isSelected(item: T): Boolean {
        return selectedPaths.contains(item.path)
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param photo Media of the item to toggle the selection status for
     */
    override fun toggleSelection(item: T) {
        if (selectedPaths.contains(item.path)) {
            selectedPaths.remove(item.path)
        } else {
            selectedPaths.add(item.path)
        }
    }

    /**
     * Clear the selection status for all items
     */
    override fun clearSelection() {
        selectedPaths.clear()
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedPaths.clear()
        selectedPaths.addAll(items.map {
            it.path
        })
        notifyDataSetChanged()
    }

    fun setData(items: List<T>, selectedPaths: MutableList<Uri>) {
        this.items = items
        this.selectedPaths = selectedPaths
        notifyDataSetChanged()
    }

    companion object {

        private val TAG = "SelectableAdapter"
    }
}