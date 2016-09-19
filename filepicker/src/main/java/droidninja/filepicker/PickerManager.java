package droidninja.filepicker;

import java.util.ArrayList;

import droidninja.filepicker.models.BaseFile;

/**
 * Created by droidNinja on 29/07/16.
 */
public class PickerManager {
    private static PickerManager ourInstance = new PickerManager();
    private int maxCount = FilePickerConst.DEFAULT_MAX_COUNT;
    private int currentCount;
    private PickerManagerListener pickerManagerListener;
    private ArrayList<String> alreadySelectedFiles;

    public static PickerManager getInstance() {
        return ourInstance;
    }

    private ArrayList<BaseFile> imageFiles;
    private ArrayList<BaseFile> docFiles;

    private int theme = R.style.AppTheme;

    private PickerManager() {
        imageFiles = new ArrayList<>();
        docFiles = new ArrayList<>();
    }

    public void setMaxCount(int count)
    {
        clearSelections();
        this.maxCount = count;
    }


    public int getCurrentCount() {
        return currentCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setPickerManagerListener(PickerManagerListener pickerManagerListener)
    {
        this.pickerManagerListener = pickerManagerListener;
    }

    public void add(BaseFile file)
    {
        if(file!=null && shouldAdd() && !imageFiles.contains(file))
        {
            if(file.isImage())
                imageFiles.add(file);
            else
                docFiles.add(file);
            currentCount++;

            if(pickerManagerListener!=null)
                pickerManagerListener.onItemSelected(currentCount);
        }
    }

    public void remove(BaseFile file)
    {
        if(file.isImage() && imageFiles.contains(file))
        {
            imageFiles.remove(file);
            currentCount--;

            if (pickerManagerListener != null)
                pickerManagerListener.onItemSelected(currentCount);
        }
        else if(docFiles.contains(file)){
            docFiles.remove(file);

            currentCount--;

            if (pickerManagerListener != null)
                pickerManagerListener.onItemSelected(currentCount);
        }
    }

    public boolean shouldAdd()
    {
        return currentCount < maxCount;
    }

    public ArrayList<String> getSelectedPhotos()
    {
        return getSelectedFilePaths(imageFiles);
    }

    public ArrayList<String> getSelectedFiles()
    {
        return getSelectedFilePaths(docFiles);
    }

    public ArrayList<String> getSelectedFilePaths(ArrayList<BaseFile> files)
    {
        ArrayList<String> paths = new ArrayList<>();
        for (int index = 0; index < files.size(); index++) {
            paths.add(files.get(index).getPath());
        }
        return paths;
    }

    public void clearSelections()
    {
        docFiles.clear();
        imageFiles.clear();
        currentCount = 0;
        maxCount=0;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }
}
