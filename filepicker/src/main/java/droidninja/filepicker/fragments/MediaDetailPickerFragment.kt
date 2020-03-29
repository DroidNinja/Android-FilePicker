package droidninja.filepicker.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.adapters.FileAdapterListener
import droidninja.filepicker.adapters.PhotoGridAdapter
import droidninja.filepicker.models.Media
import droidninja.filepicker.models.PhotoDirectory
import droidninja.filepicker.utils.AndroidLifecycleUtils
import droidninja.filepicker.utils.ImageCaptureManager
import droidninja.filepicker.viewmodels.VMMediaPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


class MediaDetailPickerFragment : BaseFragment(), FileAdapterListener {
    lateinit var recyclerView: RecyclerView

    lateinit var emptyView: TextView
    lateinit var viewModel: VMMediaPicker

    private var mListener: PhotoPickerFragmentListener? = null
    private var photoGridAdapter: PhotoGridAdapter? = null
    private var imageCaptureManager: ImageCaptureManager? = null
    private lateinit var mGlideRequestManager: RequestManager
    private var fileType: Int = 0
    private var selectAllItem: MenuItem? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_picker, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PhotoPickerFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement PhotoPickerFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onItemSelected() {
        mListener?.onItemSelected()
        photoGridAdapter?.let { adapter ->
            selectAllItem?.let { menuItem ->
                if (adapter.itemCount == adapter.selectedItemCount) {
                    menuItem.setIcon(R.drawable.ic_select_all)
                    menuItem.isChecked = true
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(PickerManager.hasSelectAll())
        mGlideRequestManager = Glide.with(this)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(requireActivity().application)).get(VMMediaPicker::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerview)
        emptyView = view.findViewById(R.id.empty_view)
        arguments?.let {
            fileType = it.getInt(BaseFragment.FILE_TYPE)
            activity?.let {
                imageCaptureManager = ImageCaptureManager(it)
            }
            val layoutManager = StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL)
            layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        }

        viewModel.lvMediaData.observe(viewLifecycleOwner, Observer { data ->
            updateList(data)
        })

        viewModel.lvDataChanged.observe(viewLifecycleOwner, Observer {
            viewModel.getMedia(mediaType = fileType)
        })

        viewModel.getMedia(mediaType = fileType)
    }

    private fun updateList(medias: List<Media>) {
        view?.let { _ ->
            if (medias.isNotEmpty()) {
                emptyView.visibility = View.GONE
            } else {
                emptyView.visibility = View.VISIBLE
            }

            context?.let {
                if (photoGridAdapter != null) {
                    photoGridAdapter?.setData(medias, PickerManager.selectedPhotos)
                } else {
                    photoGridAdapter = PhotoGridAdapter(it, mGlideRequestManager, medias, PickerManager.selectedPhotos, fileType == FilePickerConst.MEDIA_TYPE_IMAGE && PickerManager.isEnableCamera, this)
                    recyclerView.adapter = photoGridAdapter
                    photoGridAdapter?.setCameraListener(View.OnClickListener {
                        try {
                            uiScope.launch {
                                val intent = withContext(Dispatchers.IO) { imageCaptureManager?.dispatchTakePictureIntent() }
                                if (intent != null)
                                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO)
                                else
                                    Toast.makeText(activity, R.string.no_camera_exists, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    })
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ImageCaptureManager.REQUEST_TAKE_PHOTO -> if (resultCode == Activity.RESULT_OK) {
                val imagePath = imageCaptureManager?.currentPhotoPath
                if (imagePath != null) {
                    if (PickerManager.getMaxCount() == 1) {
                        PickerManager.add(imagePath, FilePickerConst.FILE_TYPE_MEDIA)
                        mListener?.onItemSelected()
                    }
                }
            } else {
                uiScope.launch(Dispatchers.IO) {
                    imageCaptureManager?.deleteContentUri(imageCaptureManager?.currentPhotoPath)
                }
            }
        }
    }

    private fun resumeRequestsIfNotDestroyed() {
        if (!AndroidLifecycleUtils.canLoadImage(this)) {
            return
        }

        mGlideRequestManager.resumeRequests()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.select_menu, menu)
        selectAllItem = menu.findItem(R.id.action_select)
        onItemSelected()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_select) {
            photoGridAdapter?.let { adapter ->
                adapter.selectAll()
                selectAllItem?.let {
                    if (it.isChecked) {
                        PickerManager.clearSelections()
                        adapter.clearSelection()

                        it.setIcon(R.drawable.ic_deselect_all)
                    } else {
                        adapter.selectAll()
                        PickerManager.add(adapter.selectedPaths, FilePickerConst.FILE_TYPE_MEDIA)
                        it.setIcon(R.drawable.ic_select_all)
                    }
                    selectAllItem?.isChecked = !it.isChecked
                    mListener?.onItemSelected()
                }
            }
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private val TAG = MediaDetailPickerFragment::class.java.simpleName
        private val SCROLL_THRESHOLD = 30

        fun newInstance(fileType: Int): MediaDetailPickerFragment {
            val mediaDetailPickerFragment = MediaDetailPickerFragment()
            val bun = Bundle()
            bun.putInt(BaseFragment.Companion.FILE_TYPE, fileType)
            mediaDetailPickerFragment.arguments = bun
            return mediaDetailPickerFragment
        }
    }
}// Required empty public constructor
