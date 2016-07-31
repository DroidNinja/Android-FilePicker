package droidninja.filepicker;

/**
 * Created by droidNinja on 28/07/16.
 */
public class FilePickerConst {
    public static final int REQUEST_CODE             = 233;

    public final static int DEFAULT_MAX_COUNT        = 9;
    public final static int DEFAULT_COLUMN_NUMBER    = 3;

    public final static int PHOTO_PICKER    = 0x11;
    public final static int DOC_PICKER    = 0x12;

    public final static String KEY_SELECTED_PHOTOS   = "SELECTED_PHOTOS";

    public final static String EXTRA_PICKER_TYPE     = "EXTRA_PICKER_TYPE";
    public final static String EXTRA_SHOW_GIF        = "SHOW_GIF";
    public final static String PPT_MIME_TYPE = "application/mspowerpoint";



    public enum FILE_TYPE{
        PDF,
        WORD,
        EXCEL,
        PPT,
        TXT,
        UNKNOWN
    }
}
