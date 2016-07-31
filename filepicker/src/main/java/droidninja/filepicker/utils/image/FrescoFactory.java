package droidninja.filepicker.utils.image;


/**
 * Created by someHui on 16/4/22.
 * TODO may return other Loader which most fit the TARGET
 */
public class FrescoFactory {
    private static FrescoLoader sInstance;

    private FrescoFactory() {

    }
    public static FrescoLoader  getLoader() {
        if (sInstance == null) {
            synchronized (FrescoFactory.class) {
                if (sInstance == null) {
                    sInstance = new FrescoLoader();
                }
            }
        }
        return sInstance;
    }

    public static FrescoLoader.FrescoOption newOption(int resizeW, int resizeH){

        return getLoader().newOption(resizeW,resizeH);
    }

}