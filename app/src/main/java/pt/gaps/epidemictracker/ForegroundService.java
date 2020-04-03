package pt.gaps.epidemictracker;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class ForegroundService extends Service {
    private BluetoothLeAdvertiseHelper bleAdvertiseHelper;
    private BluetoothLeScanHelper bleScanHelper;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground(NotificationHelper.FOREGROUND_NOTIFICATION_ID,
                NotificationHelper.createForegroundNotification(this));

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;

        if (bluetoothAdapter == null) {
            // Bluetooth is not supported
            stopSelf();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleAdvertiseHelper = new BluetoothLeAdvertiseHelperLollipop(bluetoothAdapter);
            bleScanHelper = new BluetoothLeScanHelperLollipop(bluetoothAdapter);
        } else {
            bleAdvertiseHelper = new BluetoothLeAdvertiseHelperStub();
            bleScanHelper = new BluetoothLeScanHelperStub();
        }

        if (!bluetoothAdapter.isEnabled()) {
            // TODO: Prompt user to turn on Bluetooth
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);

        super.onDestroy();
    }
}
