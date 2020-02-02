package droidninja.filepicker.fragments

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar

import java.util.ArrayList

import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.adapters.SectionsPagerAdapter
import droidninja.filepicker.cursors.loadercallbacks.FileMapResultCallback
import droidninja.filepicker.models.Document
import droidninja.filepicker.models.FileType
import droidninja.filepicker.utils.MediaStoreHelper
import droidninja.filepicker.utils.TabLayoutHelper

class DocPickerFragment : BaseFragment() {

    lateinit var tabLayout: TabLayout

    lateinit var viewPager: ViewPager
    private var progressBar: ProgressBar? = null
    private var mListener: DocPickerFragmentListener? = null

    interface DocPickerFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_doc_picker, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DocPickerFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement DocPickerFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews(view)
        initView()
    }

    private fun initView() {
        setUpViewPager()
        setData()
    }

    private fun setViews(view: View) {
        tabLayout = view.findViewById(R.id.tabs)
        viewPager = view.findViewById(R.id.viewPager)
        progressBar = view.findViewById(R.id.progress_bar)

        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
    }

    private fun setData() {
        context?.let {
            MediaStoreHelper.getDocs(it.contentResolver,
                    PickerManager.getFileTypes(),
                    PickerManager.sortingType.comparator,
                    object : FileMapResultCallback {
                        override fun onResultCallback(files: Map<FileType, List<Document>>) {
                            if(isAdded) {
                                progressBar?.visibility = View.GONE
                                setDataOnFragments(files)
                            }
                        }
                    }
            )
        }
    }

    private fun setDataOnFragments(filesMap: Map<FileType, List<Document>>) {
        view.let {
            val sectionsPagerAdapter = viewPager.adapter as SectionsPagerAdapter?
            if (sectionsPagerAdapter != null) {
                for (index in 0 until sectionsPagerAdapter.count) {
                    val docFragment = childFragmentManager
                            .findFragmentByTag(
                                    "android:switcher:" + R.id.viewPager + ":" + index) as DocFragment
                    val fileType = docFragment.fileType
                    if (fileType != null) {
                        val filesFilteredByType = filesMap[fileType]
                        if (filesFilteredByType != null)
                            docFragment.updateList(filesFilteredByType)
                    }
                }
            }
        }
    }

    private fun setUpViewPager() {
        val adapter = SectionsPagerAdapter(childFragmentManager)
        val supportedTypes = PickerManager.getFileTypes()
        for (index in supportedTypes.indices) {
            adapter.addFragment(DocFragment.newInstance(supportedTypes.get(index)), supportedTypes.get(index).title)
        }

        viewPager.offscreenPageLimit = supportedTypes.size
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        val mTabLayoutHelper = TabLayoutHelper(tabLayout, viewPager)
        mTabLayoutHelper.isAutoAdjustTabModeEnabled = true
    }

    companion object {

        private val TAG = DocPickerFragment::class.java.simpleName

        fun newInstance(): DocPickerFragment {
            return DocPickerFragment()
        }
    }
}// Required empty public constructor
