package droidninja.filepicker.fragments

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import droidninja.filepicker.FilePickerConst
import droidninja.filepicker.PickerManager
import droidninja.filepicker.R
import droidninja.filepicker.adapters.FileAdapterListener
import droidninja.filepicker.adapters.FileListAdapter
import droidninja.filepicker.models.Document
import droidninja.filepicker.models.FileType

class DocFragment : BaseFragment(), FileAdapterListener {
    lateinit var recyclerView: RecyclerView

    lateinit var emptyView: TextView

    private var mListener: DocFragmentListener? = null
    private var selectAllItem: MenuItem? = null
    private var fileListAdapter: FileListAdapter? = null

    val fileType: FileType?
        get() = arguments?.getParcelable(BaseFragment.Companion.FILE_TYPE)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo_picker, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DocFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException(
                    context?.toString() + " must implement PhotoPickerFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onItemSelected() {
        mListener?.onItemSelected()
        fileListAdapter?.let { adapter->
            selectAllItem?.let { menuItem ->
                if (adapter.itemCount == adapter.selectedItemCount) {
                    menuItem.setIcon(R.drawable.ic_select_all)
                    menuItem.isChecked = true
                }
            }
        }
    }

    interface DocFragmentListener {
        fun onItemSelected()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.recyclerview)
        emptyView = view.findViewById(R.id.empty_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.visibility = View.GONE
    }

    fun updateList(dirs: List<Document>) {
        view?.let {
            if (dirs.isNotEmpty()) {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE

                context?.let {
                    fileListAdapter = recyclerView.adapter as? FileListAdapter
                    if (fileListAdapter == null) {
                        fileListAdapter = FileListAdapter(it, dirs, PickerManager.selectedFiles,
                                this)

                        recyclerView.adapter = fileListAdapter
                    } else {
                        fileListAdapter?.setData(dirs, PickerManager.selectedFiles)
                    }
                    onItemSelected()
                }
            } else {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.doc_picker_menu, menu)
        selectAllItem = menu.findItem(R.id.action_select)
        if (PickerManager.hasSelectAll()) {
            selectAllItem?.isVisible = true
            onItemSelected()
        } else {
            selectAllItem?.isVisible = false
        }

        val search = menu.findItem(R.id.search)
        val searchView = search?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                    fileListAdapter?.filter?.filter(newText)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.action_select) {
            fileListAdapter?.let { adapter->
                selectAllItem?.let { menuItem ->
                    if (menuItem.isChecked) {
                        adapter.clearSelection()
                        PickerManager.clearSelections()

                        menuItem.setIcon(R.drawable.ic_deselect_all)
                    } else {
                        adapter.selectAll()
                        PickerManager
                                .add(adapter.selectedPaths, FilePickerConst.FILE_TYPE_DOCUMENT)
                        menuItem.setIcon(R.drawable.ic_select_all)
                    }

                    menuItem.isChecked = !menuItem.isChecked
                    mListener?.onItemSelected()
                }
            }
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private val TAG = DocFragment::class.java.simpleName

        fun newInstance(fileType: FileType): DocFragment {
            val photoPickerFragment = DocFragment()
            val bun = Bundle()
            bun.putParcelable(BaseFragment.Companion.FILE_TYPE, fileType)
            photoPickerFragment.arguments = bun
            return photoPickerFragment
        }
    }
}// Required empty public constructor
