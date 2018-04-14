package droidninja.filepicker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.FileAdapterListener;
import droidninja.filepicker.adapters.FileListAdapter;
import droidninja.filepicker.models.Document;
import droidninja.filepicker.models.FileType;
import java.util.List;

public class DocFragment extends BaseFragment implements FileAdapterListener {

  private static final String TAG = DocFragment.class.getSimpleName();
  RecyclerView recyclerView;

  TextView emptyView;

  private DocFragmentListener mListener;
  private MenuItem selectAllItem;
  private FileListAdapter fileListAdapter;

  public DocFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_photo_picker, container, false);
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof DocFragmentListener) {
      mListener = (DocFragmentListener) context;
    } else {
      throw new RuntimeException(
          context.toString() + " must implement PhotoPickerFragmentListener");
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  public static DocFragment newInstance(FileType fileType) {
    DocFragment photoPickerFragment = new DocFragment();
    Bundle bun = new Bundle();
    bun.putParcelable(FILE_TYPE, fileType);
    photoPickerFragment.setArguments(bun);
    return photoPickerFragment;
  }

  public FileType getFileType() {
    return getArguments().getParcelable(FILE_TYPE);
  }

  @Override public void onItemSelected() {
    mListener.onItemSelected();
    if (fileListAdapter != null && selectAllItem != null) {
      if (fileListAdapter.getItemCount() == fileListAdapter.getSelectedItemCount()) {
        selectAllItem.setIcon(R.drawable.ic_select_all);
        selectAllItem.setChecked(true);
      }
    }
  }

  public interface DocFragmentListener {
    void onItemSelected();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initView(view);
  }

  private void initView(View view) {
    recyclerView = view.findViewById(R.id.recyclerview);
    emptyView = view.findViewById(R.id.empty_view);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setVisibility(View.GONE);
  }

  public void updateList(List<Document> dirs) {
    if (getView() == null) return;

    if (dirs.size() > 0) {
      recyclerView.setVisibility(View.VISIBLE);
      emptyView.setVisibility(View.GONE);

      fileListAdapter = (FileListAdapter) recyclerView.getAdapter();
      if (fileListAdapter == null) {
        fileListAdapter =
            new FileListAdapter(getActivity(), dirs, PickerManager.getInstance().getSelectedFiles(),
                this);

        recyclerView.setAdapter(fileListAdapter);
      } else {
        fileListAdapter.setData(dirs);
        fileListAdapter.notifyDataSetChanged();
      }
      onItemSelected();
    } else {
      recyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.doc_picker_menu, menu);
    selectAllItem = menu.findItem(R.id.action_select);
    if (PickerManager.getInstance().hasSelectAll()) {
      selectAllItem.setVisible(true);
      onItemSelected();
    } else {
      selectAllItem.setVisible(false);
    }

    MenuItem search = menu.findItem(R.id.search);
    SearchView searchView = (SearchView) search.getActionView();
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {

        return false;
      }

      @Override public boolean onQueryTextChange(String newText) {
        if (fileListAdapter != null) {
          fileListAdapter.getFilter().filter(newText);
        }
        return true;
      }
    });

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.action_select) {
      if (fileListAdapter != null) {
        if (selectAllItem != null) {
          if (selectAllItem.isChecked()) {
            fileListAdapter.clearSelection();
            PickerManager.getInstance().clearSelections();

            selectAllItem.setIcon(R.drawable.ic_deselect_all);
          } else {
            fileListAdapter.selectAll();
            PickerManager.getInstance()
                .add(fileListAdapter.getSelectedPaths(), FilePickerConst.FILE_TYPE_DOCUMENT);
            selectAllItem.setIcon(R.drawable.ic_select_all);
          }
        }
        selectAllItem.setChecked(!selectAllItem.isChecked());
        mListener.onItemSelected();
      }
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }
}
