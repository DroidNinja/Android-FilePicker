package vi.filepicker

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import droidninja.filepicker.FilePickerBuilder.Companion.instance
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.models.sort.SortingTypes
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.util.*

class MainActivity : AppCompatActivity(), PermissionCallbacks {


    private val MAX_ATTACHMENT_COUNT = 10
    private var photoPaths = ArrayList<Uri>()
    private var docPaths = ArrayList<Uri>()

    companion object {
        const val RC_PHOTO_PICKER_PERM = 123
        const val RC_FILE_PICKER_PERM = 321
        private const val CUSTOM_REQUEST_CODE = 532
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.pick_photo).setOnClickListener { pickPhotoClicked() }
        findViewById<View>(R.id.pick_doc).setOnClickListener { pickDocClicked() }
    }

    @AfterPermissionGranted(RC_PHOTO_PICKER_PERM)
    fun pickPhotoClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickPhoto()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER)
        }
    }

    @AfterPermissionGranted(RC_FILE_PICKER_PERM)
    fun pickDocClicked() {
        if (EasyPermissions.hasPermissions(this, FilePickerConst.PERMISSIONS_FILE_PICKER)) {
            onPickDoc()
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_doc_picker),
                    RC_FILE_PICKER_PERM, FilePickerConst.PERMISSIONS_FILE_PICKER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CUSTOM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val dataList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null) {
                    photoPaths = ArrayList()
                    photoPaths.addAll(dataList)
                }
            }
            FilePickerConst.REQUEST_CODE_DOC -> if (resultCode == Activity.RESULT_OK && data != null) {
                val dataList = data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_DOCS)
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

        /*lifecycleScope.launch {
            for (filePath in filePaths) {
                try {
                    Log.e("path:", "$filePath -")
                    val path = getFilePath(this@MainActivity1, filePath)
                    if (path != null) {
                        Log.e("path:", path)
                    }
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            }
        }*/


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        if (recyclerView != null) {
            val layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
            layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            recyclerView.layoutManager = layoutManager
            val imageAdapter = ImageAdapter(this, filePaths)
            recyclerView.adapter = imageAdapter
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
        Toast.makeText(this, "Num of files selected: " + filePaths.size, Toast.LENGTH_SHORT).show()
    }

    fun onPickPhoto() {
        val maxCount = MAX_ATTACHMENT_COUNT - docPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select media")
                    .enableVideoPicker(true)
                    .enableCameraSupport(true)
                    .showGifs(true)
                    .showFolderView(false)
                    .enableSelectAll(false)
                    .enableImagePicker(true)
                    //.setCameraImagePlaceholder()
                    //.setCameraVideoPlaceholder()
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickMedia(this, CUSTOM_REQUEST_CODE)
        }
    }

    fun onPickDoc() {
        val zips = arrayOf("zip", "rar")
        val pdfs = arrayOf("pdf")
        val maxCount = MAX_ATTACHMENT_COUNT - photoPaths.size
        if (docPaths.size + photoPaths.size == MAX_ATTACHMENT_COUNT) {
            Toast.makeText(this, "Cannot select more than $MAX_ATTACHMENT_COUNT items",
                    Toast.LENGTH_SHORT).show()
        } else {
            instance
                    .setMaxCount(maxCount)
                    .setSelectedFiles(docPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select doc")
                    .addFileSupport("ZIP", zips)
                    .addFileSupport("PDF", pdfs, R.drawable.pdf_blue)
                    .enableDocSupport(true)
                    .enableSelectAll(true)
                    .sortDocumentsBy(SortingTypes.name)
                    .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .pickFile(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun onOpenFragmentClicked(view: View?) {
        val intent = Intent(this, FragmentActivity::class.java)
        startActivity(intent)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }


}