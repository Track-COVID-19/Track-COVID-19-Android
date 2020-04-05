package pt.gaps.epidemictracker;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class ForegroundService extends Service {
    private static boolean running = false;

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
        synchronized (ForegroundService.class) {
            running = true;
        }

        super.onCreate();

        startForeground(NotificationHelper.FOREGROUND_NOTIFICATION_ID,
                NotificationHelper.createForegroundNotification(this));
        startBluetoothLeOperations();
    }

    @Override
    public void onDestroy() {
        stopBluetoothLeOperations();
        stopForeground(true);

        super.onDestroy();

        synchronized (ForegroundService.class) {
            running = false;
        }
    }

    private void startBluetoothLeOperations() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;

        if (!RequirementsHelper.isBluetoothCapable(this)) {
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

        if (RequirementsHelper.needsEnablingBluetooth(this)) {
            requestEnableBluetooth();
        }

        bleAdvertiseHelper.startAdvertising();
        bleScanHelper.startScan();
    }

    private void stopBluetoothLeOperations() {
        if (bleAdvertiseHelper != null) {
            bleAdvertiseHelper.stopAdvertising();
            bleAdvertiseHelper = null;
        }
        if (bleScanHelper != null) {
            bleScanHelper.stopScan();
            bleScanHelper = null;
        }
    }

    private void requestEnableBluetooth() {
        // TODO: Show a proper dialog asking the user to enable bluetooth

        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(enableBtIntent);
    }

    /**
     * Starts the service.
     */
    static synchronized void startService(Context c) {
        if (running) {
            return;
        }

        Intent intent = getServiceIntent(c);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            c.startForegroundService(intent);
        } else {
            c.startService(intent);
        }
        running = true;
    }

    /**
     * Stops the service.
     */
    static synchronized void stopService(Context c) {
        if (!running) {
            return;
        }

        c.stopService(getServiceIntent(c));
        running = false;
    }

    private static Intent getServiceIntent(Context c) {
        return new Intent(c, ForegroundService.class);
    }
}
