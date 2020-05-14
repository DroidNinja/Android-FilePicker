package droidninja.filepicker.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.MediaDetailsActivity
import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.adapters.FolderGridAdapter
import droidninja.filepicker.models.Media
import droidninja.filepicker.models.PhotoDirectory
import droidninja.filepicker.utils.AndroidLifecycleUtils
import droidninja.filepicker.utils.GridSpacingItemDecoration
import droidninja.filepicker.utils.ImageCaptureManager
import droidninja.filepicker.viewmodels.VMMediaPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MediaFolderPickerFragment : BaseFragment(), FolderGridAdapter.FolderGridAdapterListener {
    lateinit var recyclerView: RecyclerView

    lateinit var emptyView: TextView
    lateinit var viewModel: VMMediaPicker

    private var mListener: PhotoPickerFragmentListener? = null
    private var photoGridAdapter: FolderGridAdapter? = null
    private var imageCaptureManager: ImageCaptureManager? = null
    private lateinit var mGlideRequestManager: RequestManager
    private var fileType: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_media_folder_picker, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PhotoPickerFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    "$context must implement PhotoPickerFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            imageCaptureManager = ImageCaptureManager(requireContext())
            val layoutManager = GridLayoutManager(activity, 2)

            val spanCount = 2 // 2 columns
            val spacing = 5 // 5px
            val includeEdge = false
            recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
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

            viewModel.lvPhotoDirsData.observe(viewLifecycleOwner, Observer { data ->
                updateList(data)
            })

            viewModel.lvDataChanged.observe(viewLifecycleOwner, Observer {
                viewModel.getPhotoDirs(mediaType = fileType)
            })

            viewModel.getPhotoDirs(mediaType = fileType)
        }
    }

    private fun updateList(dirs: List<PhotoDirectory>) {
        view?.let {
            if (dirs.isNotEmpty()) {
                emptyView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                emptyView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                return
            }

            if (photoGridAdapter == null) {
                photoGridAdapter = FolderGridAdapter(requireContext(), mGlideRequestManager, dirs, fileType == FilePickerConst.MEDIA_TYPE_IMAGE && PickerManager.isEnableCamera)
                recyclerView.adapter = photoGridAdapter
                photoGridAdapter?.setFolderGridAdapterListener(this)
            } else {
                photoGridAdapter?.setData(dirs)
                photoGridAdapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCameraClicked() {
        try {
            uiScope.launch {
                val intent = withContext(Dispatchers.IO) { imageCaptureManager?.dispatchTakePictureIntent() }
                if (intent != null) {
                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO)
                } else {
                    Toast.makeText(requireContext(), R.string.no_camera_exists, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onFolderClicked(photoDirectory: PhotoDirectory) {
        val intent = Intent(activity, MediaDetailsActivity::class.java)
        intent.putExtra(PhotoDirectory::class.java.simpleName, photoDirectory.apply {
            medias.clear()
        })
        intent.putExtra(FilePickerConst.EXTRA_FILE_TYPE, fileType)
        activity?.startActivityForResult(intent, FilePickerConst.REQUEST_CODE_MEDIA_DETAIL)
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

    companion object {

        private val TAG = MediaFolderPickerFragment::class.java.simpleName
        private val SCROLL_THRESHOLD = 30
        private val PERMISSION_WRITE_EXTERNAL_STORAGE_RC = 908

        fun newInstance(fileType: Int): MediaFolderPickerFragment {
            val photoPickerFragment = MediaFolderPickerFragment()
            val bun = Bundle()
            bun.putInt(BaseFragment.FILE_TYPE, fileType)
            photoPickerFragment.arguments = bun
            return photoPickerFragment
        }
    }
}// Required empty public constructor
