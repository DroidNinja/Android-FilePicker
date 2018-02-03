package droidninja.filepicker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import droidninja.filepicker.PickerManager;
import droidninja.filepicker.R;
import droidninja.filepicker.models.Folder;
import droidninja.filepicker.views.SmoothCheckBox;


public class FolderAdapter extends SelectableAdapter<FolderAdapter.FolderViewHolder, Folder> {

    private OnFolderClick onFolderClick;

    public FolderAdapter(List<Folder> folders, List<String> selectedPath, OnFolderClick onFolderClick) {
        super(folders, selectedPath);
        this.onFolderClick = onFolderClick;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FolderViewHolder holder, final int position) {
        final Folder folder = getItems().get(position);
        holder.name.setText(folder.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onFolderClick) {
                    onFolderClick.onSelect(folder);
                }
            }
        });

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClicked(folder, holder);
            }
        });
        boolean isSelected = isSelected(folder);
        holder.cbSelect.setChecked(isSelected);
        holder.itemView.setBackgroundResource(isSelected ? R.color.bg_gray : android.R.color.white);
        holder.cbSelect.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                toggleSelection(folder);
                holder.itemView.setBackgroundResource(isChecked ? R.color.bg_gray : android.R.color.white);
            }
        });
    }

    private void onItemClicked(Folder folder, FolderViewHolder holder) {
        if (holder.cbSelect.isChecked()) {
            holder.cbSelect.setChecked(!holder.cbSelect.isChecked(), true);
            holder.cbSelect.setVisibility(View.GONE);
        } else if (PickerManager.getInstance().shouldAdd()) {
            holder.cbSelect.setChecked(!holder.cbSelect.isChecked(), true);
            holder.cbSelect.setVisibility(View.VISIBLE);
        }

        if (null != onFolderClick) {
            onFolderClick.onCheck(folder, holder.cbSelect.isChecked());
        }
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name;
        SmoothCheckBox cbSelect;

        FolderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.fp_iv_icon);
            name = itemView.findViewById(R.id.fp_tv_name);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }
    }

    public interface OnFolderClick {
        void onSelect(Folder folder);

        void onCheck(Folder folder, boolean isChecked);
    }

}