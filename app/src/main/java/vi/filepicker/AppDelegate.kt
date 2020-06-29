package vi.filepicker


import android.os.StrictMode
import androidx.multidex.MultiDexApplication

/**
 * Created by droidNinja on 03/06/17.
 */
class AppDelegate : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .penaltyDeath()
                .build());

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
    }
}