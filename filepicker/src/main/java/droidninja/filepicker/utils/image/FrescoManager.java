package droidninja.filepicker.utils.image;

import android.content.Context;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

public class FrescoManager {
    public static void init(Context context){

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
                .setDownsampleEnabled(true)
                .build();

        Fresco.initialize(context,config);
    }

    public static void clearMemoryCaches(){
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    public static void clearDiskCaches(){
        Fresco.getImagePipeline().clearDiskCaches();
    }

    public static void clearCaches(){
        Fresco.getImagePipeline().clearCaches();
    }

    public static void evictCache(Uri uri){
        if(null == uri){
            return;
        }
        Fresco.getImagePipeline().evictFromCache(uri);
    }
}