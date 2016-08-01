package droidninja.filepicker.utils.image;


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