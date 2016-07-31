package droidninja.filepicker.adapters;

/**
 * Created by donglua on 15/6/30.
 */
public interface Selectable<T> {


  /**
   * Indicates if the item at position position is selected
   *
   * @param item to check
   * @return true if the item is selected, false otherwise
   */
  boolean isSelected(T item);

  /**
   * Toggle the selection status of the item at a given position
   *
   * @param item to toggle the selection status for
   */
  void toggleSelection(T item);

  /**
   * Clear the selection status for all items
   */
  void clearSelection();

  /**
   * Count the selected items
   *
   * @return Selected items count
   */
  int getSelectedItemCount();

}
