package droidninja.filepicker.utils.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public class FrescoLoader extends BaseImageLoader<DraweeView, FrescoLoader.FrescoOption> {
    public static class FrescoOption implements ImageLoaderWrapper.ImageOption {
        private ResizeOptions mResizeOptions;

        public ResizeOptions getResizeOptions() {
            return mResizeOptions;
        }

        public void setResizeOptions(ResizeOptions resizeOptions) {
            mResizeOptions = resizeOptions;
        }
    }

    @Override
    public FrescoOption newOption(int width, int height) {
        FrescoOption option = new FrescoOption();
        option.setResizeOptions(new ResizeOptions(width, height));
        return option;
    }

    private ImageRequestBuilder getBuilder(Uri uri) {
        return ImageRequestBuilder.newBuilderWithSource(uri);
    }

    private ImageRequestBuilder getBuilder(@DrawableRes int id) {
        return ImageRequestBuilder.newBuilderWithResourceId(id);
    }

    public void showImage(DraweeView imageView, ImageRequestBuilder tBuilder, FrescoOption option) {
        ImageRequestBuilder builder = tBuilder;
        if (option != null) {
            if (option.getResizeOptions() != null) {
                builder.setResizeOptions(option.getResizeOptions());
            }
        }
        ImageRequest request = builder.build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .setAutoPlayAnimations(true)
                .build();

        imageView.setController(controller);
    }

    @Override
    public void showImage(DraweeView imageView, Uri uri, @Nullable FrescoOption option) {
        if (uri.equals(Uri.EMPTY)) {
            imageView.setImageURI(null);
        } else {
            showImage(imageView, getBuilder(uri), option);
        }
    }

    @Override
    public void showImage(DraweeView imageView, @DrawableRes int id, @Nullable FrescoOption option) {
        showImage(imageView, getBuilder(id), option);
    }


    public interface RequestCallback {
        void onSuccessed(Bitmap bitmap, float currentPos);
    }


    public static void requestImage(Context context, Uri uri, final float currentPos, final RequestCallback callback) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
                                 @Override
                                 public void onNewResultImpl(@Nullable Bitmap bitmap) {
                                     // You can use the bitmap in only limited ways
                                     // No need to do any cleanup.
                                     callback.onSuccessed(bitmap, currentPos);

                                 }

                                 @Override
                                 public void onFailureImpl(DataSource dataSource) {
                                     // No cleanup required here.
                                 }
                             },
                CallerThreadExecutor.getInstance());
    }
}