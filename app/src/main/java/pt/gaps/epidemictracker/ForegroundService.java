package pt.gaps.epidemictracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ForegroundService extends Service {
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
