package droidninja.filepicker.fragments

interface PhotoPickerFragmentListener {
    fun onItemSelected()
    fun setToolbarTitle(count: Int)
}