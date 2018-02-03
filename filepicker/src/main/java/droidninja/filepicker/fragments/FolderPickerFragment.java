package droidninja.filepicker.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.adapters.FolderAdapter;
import droidninja.filepicker.models.Folder;

public class FolderPickerFragment extends BaseFragment implements View.OnClickListener, FolderAdapter.OnFolderClick {
    private List<Folder> foldersList = new ArrayList<>();
    private RecyclerView rcFolder;
    private TextView tv_location;

    private String ROOT_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String location = ROOT_FOLDER;

    private FolderPickerFragmentListener mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fp_main_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_location = view.findViewById(R.id.fp_tv_location);
        rcFolder = view.findViewById(R.id.fp_listView);
        rcFolder.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        view.findViewById(R.id.btnGoParentFolder).setOnClickListener(this);
        view.findViewById(R.id.btnCreateFolder).setOnClickListener(this);

        loadLists(location);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FolderPickerFragmentListener) {
            mListener = (FolderPickerFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FolderPickerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void loadLists(String location) {
        try {
            File folder = new File(location);
            if (!folder.isDirectory())
                return;

            tv_location.setText(folder.getAbsolutePath());
            File[] files = folder.listFiles();

            List<Folder> folders = new ArrayList<>();

            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    folders.add(new Folder(currentFile.getName(), currentFile.getPath()));
                }
            }

            Collections.sort(folders, new Comparator<Folder>() {
                @Override
                public int compare(Folder folder, Folder folder1) {
                    return folder.getName().compareToIgnoreCase(folder1.getName());
                }
            });

            foldersList.clear();
            foldersList.addAll(folders);

            List<String> selectedPath = PickerManager.getInstance().getSelectedFolder();

            FolderAdapter folderAdapter = new FolderAdapter(foldersList, selectedPath, this);
            rcFolder.setAdapter(folderAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void goBack() {
        if (ROOT_FOLDER.equals(location)) {
            return;
        }
        if (location != null && !location.equals("") && !location.equals("/")) {
            int start = location.lastIndexOf('/');
            location = location.substring(0, start);
            loadLists(location);
        }

    }

    public void newFolder(String filename) {
        try {
            File file = new File(location + File.separator + filename);
            if (file.exists() || file.mkdir()) {
                loadLists(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.common_error) + e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void newFolderDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setTitle(R.string.create_folder_title);

        final EditText edtFolderName = new EditText(getActivity());
        dialog.setView(edtFolderName);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.create_folder_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        newFolder(edtFolderName.getText().toString());
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.create_folder_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        dialog.show();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.btnGoParentFolder == id) {
            goBack();
        } else if (R.id.btnCreateFolder == id) {
            newFolderDialog();
        }
    }

    @Override
    public void onSelect(Folder folder) {
        location = location + File.separator + folder.getName();
        loadLists(location);
    }

    @Override
    public void onCheck(Folder folder, boolean isChecked) {
        if (isChecked) {
            PickerManager.getInstance().add(folder.getPath(), FilePickerConst.FILE_TYPE_FOLDER);
        } else {
            PickerManager.getInstance().remove(folder.getPath(), FilePickerConst.FILE_TYPE_FOLDER);
        }
        mListener.onItemSelected();
    }

    public interface FolderPickerFragmentListener {
        void onItemSelected();
    }
}
