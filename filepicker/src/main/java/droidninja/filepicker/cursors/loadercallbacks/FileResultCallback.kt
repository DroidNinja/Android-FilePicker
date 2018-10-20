package droidninja.filepicker.cursors.loadercallbacks

interface FileResultCallback<T> {
    fun onResultCallback(files: List<T>)
}