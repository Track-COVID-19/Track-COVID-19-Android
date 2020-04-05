package pt.gaps.epidemictracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BluetoothLeAdvertiseHelperLollipop implements BluetoothLeAdvertiseHelper {
    private class HelperCallback extends AdvertiseCallback {
        /**
         * Callback triggered in response to {@link BluetoothLeAdvertiser#startAdvertising} indicating
         * that the advertising has been started successfully.
         *
         * @param settingsInEffect The actual settings used for advertising, which may be different from
         *                         what has been requested.
         */
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            LogHelper.d("BLE advertise started successfully with settings: " + settingsInEffect);
        }

        /**
         * Callback when advertising could not be started.
         *
         * @param errorCode Error code (see ADVERTISE_FAILED_* constants) for advertising start
         *                  failures.
         */
        @Override
        public void onStartFailure(int errorCode) {
            String errorMsg;
            switch (errorCode) {
                case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                    errorMsg = "Data too large";
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    errorMsg = "Too many advertisers";
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                    errorMsg = "Already started";
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                    errorMsg = "Internal error";
                    break;
                case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    errorMsg = "Feature unsupported";
                    break;
                default:
                    errorMsg = "Unknown error";
            }
            LogHelper.d(String.format("BLE advertise failed with error %d (%s)", errorCode, errorMsg));

            synchronized (BluetoothLeAdvertiseHelperLollipop.this) {
                advertising = false;
            }
        }
    }

    private final BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private final AdvertiseCallback advertiseCallback = new HelperCallback();

    private boolean advertising = false;

    BluetoothLeAdvertiseHelperLollipop(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    @Override
    public synchronized boolean isAdvertising() {
        return advertising;
    }

    @Override
    public synchronized void startAdvertising() {
        if (advertising) {
            LogHelper.d("Cannot start BLE advertising: Already advertising");
            return;
        }

        LogHelper.d("Starting BLE advertising");
        bluetoothLeAdvertiser.startAdvertising(buildAdvertiseSettings(), buildAdvertiseData(),
                advertiseCallback);
        advertising = true;
    }

    @Override
    public synchronized void stopAdvertising() {
        if (!advertising) {
            LogHelper.d("Cannot stop BLE advertising: Not currently advertising");
            return;
        }

        LogHelper.d("Stopping BLE advertising");
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        advertising = false;
    }

    private AdvertiseSettings buildAdvertiseSettings() {
        return new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(true)
                .setTimeout(0)
                .build();
    }

    private AdvertiseData buildAdvertiseData() {
        /**
         * Note: There is a strict limit of 31 Bytes on packets sent over BLE Advertisements.
         *  This includes everything put into AdvertiseData including UUIDs, device info, &
         *  arbitrary service or manufacturer data.
         *  Attempting to send packets over this limit will result in a failure with error code
         *  AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE. Catch this error in the
         *  onStartFailure() method of an AdvertiseCallback implementation.
         */
        return new AdvertiseData.Builder()
                .addServiceUuid(Constants.BLUETOOTH_SERVICE_UUID)
                .build();
    }
}
