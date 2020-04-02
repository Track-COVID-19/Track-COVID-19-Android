package pt.gaps.epidemictracker;

import android.util.Log;

class LogHelper {
    private static String TAG = "EpidemicTracker";

    static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    static void d(String msg) {
        d(TAG, msg);
    }
}
