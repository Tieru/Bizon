package pro.asdgroup.bizon;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;

/**
 * Created by Voronov Viacheslav on 4/12/2015.
 */
public class BizonApp extends Application {


    private static Context context;

    public void onCreate(){
        super.onCreate();
        if(!BuildConfig.DEBUG) {
            Crashlytics.start(this);
        }

        BizonApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BizonApp.context;
    }

    public static int getAppVersion() {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}