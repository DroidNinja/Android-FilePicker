package vi.filepicker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private ArrayList<String> filePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pickPhotoClicked(View view) {
        MainActivityPermissionsDispatcher.onPickPhotoWithCheck(this);
    }

    public void pickDocClicked(View view) {
        MainActivityPermissionsDispatcher.onPickDocWithCheck(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE:
                if(resultCode==RESULT_OK && data!=null)
                {
                    filePaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS);
                    addThemToView(filePaths);
                }
        }
    }

    private void addThemToView(ArrayList<String> filePaths) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        if(recyclerView!=null) {
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            ImageAdapter imageAdapter = new ImageAdapter(this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        Toast.makeText(MainActivity.this, "Num of files selected: "+ filePaths.size(), Toast.LENGTH_SHORT).show();
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickPhoto() {
        FilePickerBuilder.getInstance().setMaxCount(5)
                .setSelectedFiles(filePaths)
                .pickPhoto(MainActivity.this);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void onPickDoc() {
        FilePickerBuilder.getInstance().setMaxCount(10)
                .setSelectedFiles(filePaths)
                .pickDocument(MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }
}
