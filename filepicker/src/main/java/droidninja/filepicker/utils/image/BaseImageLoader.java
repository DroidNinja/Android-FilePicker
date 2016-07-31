package droidninja.filepicker.utils.image;

import android.net.Uri;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by someHui on 16/4/22.
 */
public abstract class BaseImageLoader<TARGET extends ImageView,OPTION extends ImageLoaderWrapper.ImageOption>
        implements ImageLoaderWrapper<TARGET,OPTION>{
    @Override
    public void showImage(TARGET imageView, String str, OPTION option) {
        showImage(imageView,str==null? Uri.EMPTY: Uri.parse(str),option);
    }
    @Override
    public void showImage(TARGET imageView, File file, OPTION option){
        showImage(imageView, Uri.fromFile(file),option);
    }
}