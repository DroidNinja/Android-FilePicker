package droidninja.filepicker.adapters

/**
 * Created by donglua on 15/6/30.
 */
interface Selectable<T> {

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    val selectedItemCount: Int


    /**
     * Indicates if the item at position position is selected
     *
     * @param item to check
     * @return true if the item is selected, false otherwise
     */
    fun isSelected(item: T): Boolean

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param item to toggle the selection status for
     */
    fun toggleSelection(item: T)

    /**
     * Clear the selection status for all items
     */
    fun clearSelection()

}
