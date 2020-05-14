package droidninja.filepicker

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import droidninja.filepicker.adapters.FileAdapterListener
import droidninja.filepicker.adapters.PhotoGridAdapter
import droidninja.filepicker.models.Media
import droidninja.filepicker.models.PhotoDirectory
import droidninja.filepicker.utils.AndroidLifecycleUtils
import droidninja.filepicker.viewmodels.VMMediaPicker
import java.util.ArrayList
import java.util.Comparator

class MediaDetailsActivity : BaseFilePickerActivity(), FileAdapterListener {
    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private lateinit var mGlideRequestManager: RequestManager
    private var photoGridAdapter: PhotoGridAdapter? = null
    private var fileType: Int = 0
    private var selectAllItem: MenuItem? = null
    private var photoDirectory: PhotoDirectory? = null
    lateinit var viewModel: VMMediaPicker

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_media_details)
    }

    override fun initView() {
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(VMMediaPicker::class.java)
        mGlideRequestManager = Glide.with(this)
        val intent = intent
        if (intent != null) {

            fileType = intent.getIntExtra(FilePickerConst.EXTRA_FILE_TYPE, FilePickerConst.MEDIA_TYPE_IMAGE)
            photoDirectory = intent.getParcelableExtra(PhotoDirectory::class.java.simpleName)
            if (photoDirectory != null) {
                setUpView()
                setTitle(0)
            }
        }
    }

    override fun setTitle(count: Int) {
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            val maxCount = PickerManager.getMaxCount()
            if (maxCount == -1 && count > 0) {
                actionBar.title = String.format(getString(R.string.attachments_num), count)
            } else if (maxCount > 0 && count > 0) {
                actionBar.title = String.format(getString(R.string.attachments_title_text), count, maxCount)
            } else {
                actionBar.title = photoDirectory?.name
            }
        }
    }

    private fun setUpView() {
        recyclerView = findViewById(R.id.recyclerview)
        emptyView = findViewById(R.id.empty_view)

        val layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        recyclerView?.layoutManager = layoutManager
        recyclerView?.itemAnimator = DefaultItemAnimator()

        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Log.d(">>> Picker >>>", "dy = " + dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    mGlideRequestManager.pauseRequests()
                } else {
                    resumeRequestsIfNotDestroyed()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    resumeRequestsIfNotDestroyed()
                }
            }
        })

        viewModel.lvMediaData.observe(this, Observer { data ->
            updateList(data)
        })
        viewModel.getMedia(bucketId = photoDirectory?.bucketId, mediaType = fileType)
    }

    private fun updateList(medias: List<Media>) {
        if (medias.isNotEmpty()) {
            emptyView?.visibility = View.GONE
            recyclerView?.visibility = View.VISIBLE
        } else {
            emptyView?.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
            return
        }

        if (photoGridAdapter != null) {
            photoGridAdapter?.setData(medias, PickerManager.selectedPhotos)
        } else {
            photoGridAdapter = PhotoGridAdapter(this, mGlideRequestManager, medias,
                    PickerManager.selectedPhotos, false, this)
            recyclerView?.adapter = photoGridAdapter
        }

        if (PickerManager.getMaxCount() == -1) {
            if (photoGridAdapter != null && selectAllItem != null) {
                if (photoGridAdapter?.itemCount == photoGridAdapter?.selectedItemCount) {
                    selectAllItem?.setIcon(R.drawable.ic_select_all)
                    selectAllItem?.isChecked = true
                }
            }
            setTitle(PickerManager.currentCount)
        }
    }

    private fun resumeRequestsIfNotDestroyed() {
        if (!AndroidLifecycleUtils.canLoadImage(this)) {
            return
        }

        mGlideRequestManager.resumeRequests()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.media_detail_menu, menu)
        selectAllItem = menu.findItem(R.id.action_select)
        selectAllItem?.isVisible = PickerManager.hasSelectAll()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_done) {
            setResult(Activity.RESULT_OK, null)
            finish()

            return true
        } else if (itemId == R.id.action_select) {
            selectAllItem?.let {
                photoGridAdapter?.let { adapter ->
                    if (it.isChecked) {
                        PickerManager.deleteMedia(adapter.selectedPaths)
                        adapter.clearSelection()

                        it.setIcon(R.drawable.ic_deselect_all)
                    } else {
                        adapter.selectAll()
                        PickerManager.add(adapter.selectedPaths, FilePickerConst.FILE_TYPE_MEDIA)
                        it.setIcon(R.drawable.ic_select_all)
                    }
                    it.isChecked = !it.isChecked
                    setTitle(PickerManager.currentCount)
                }
            }
            return true
        } else if (itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected() {
        val maxCount = PickerManager.getMaxCount()
        if (maxCount == 1) {
            setResult(Activity.RESULT_OK, null)
            finish()
        }
        setTitle(PickerManager.currentCount)
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }

    companion object {

        private val SCROLL_THRESHOLD = 30
    }
}
