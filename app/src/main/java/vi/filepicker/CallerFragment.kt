package vi.filepicker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import droidninja.filepicker.FilePickerBuilder.Companion.instance
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.fragments.BaseFragment
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import vi.filepicker.MainActivity.Companion.RC_FILE_PICKER_PERM
import vi.filepicker.MainActivity.Companion.RC_PHOTO_PICKER_PERM
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CallerFragment : BaseFragment(), PermissionCallbacks {
    private val MAX_ATTACHMENT_COUNT = 10
    private var photoPaths = ArrayList<Uri>()
    private var docPaths = ArrayList<Uri>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_main, container, false)
        val openFragmentBtn = view.findViewById<Button>(R.id.open_fragment)
        openFragmentBtn.visibility = View.GONE
        view.findViewById<View>(R.id.pick_photo).setOnClickListener { pickPhoto() }
        view.findViewById<View>(R.id.pick_doc).setOnClickListener { pickDoc() }
        return view
    }

    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    fun pickPhoto() {
        if (EasyPermissions.hasPermissions(context!!, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickPhoto()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM,
                    FilePickerConst.PERMISSIONS_FILE_PICKER)
        }
    }

    @AfterPermissionGranted(RC_FILE_PICKER_PERM)
    fun pickDoc() {
        if (EasyPermissions.hasPermissions(context!!, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM,
                    FilePickerConst.PERMISSIONS_FILE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FilePickerConst.REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null) {
                val dataList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null) {
                    photoPaths = ArrayList()
                    photoPaths.addAll(dataList)
                }
            }
            FilePickerConst.REQUEST_CODE_DOC -> if (resultCode == Activity.RESULT_OK && data != null) {
                val dataList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null) {
                    docPaths = ArrayList()
                    docPaths.addAll(dataList)
                }
            }
        }
        addThemToView(photoPaths, docPaths)
    }

    private fun addThemToView(imagePaths: ArrayList<Uri>?, docPaths: ArrayList<Uri>?) {
        val filePaths = ArrayList<Uri>()
        if (imagePaths != null) filePaths.addAll(imagePaths)
        if (docPaths != null) filePaths.addAll(docPaths)
        val recyclerView = view!!.findViewById<View>(R.id.recyclerview) as RecyclerView
        if (recyclerView != null) {
            val layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
            layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            recyclerView.layoutManager = layoutManager
            val imageAdapter = ImageAdapter(requireContext(), filePaths)
            recyclerView.adapter = imageAdapter
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
        Toast.makeText(activity, "Num of files selected: " + filePaths.size, Toast.LENGTH_SHORT)
                .show()
    }

    fun onPickPhoto() {
        val maxCount = MAX_ATTACHMENT_COUNT - docPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(activity, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .pickMedia(this)
        }
    }

    fun onPickDoc() {
        val maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(activity, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .enableDocSupport(true)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .pickFile(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}