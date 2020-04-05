package pt.gaps.epidemictracker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class BluetoothLeScanHelperLollipop implements BluetoothLeScanHelper {
    private class HelperCallback extends ScanCallback {
        /**
         * Callback when a BLE advertisement has been found.
         *
         * @param callbackType Determines how this callback was triggered. Could be one of {@link
         *                     ScanSettings#CALLBACK_TYPE_ALL_MATCHES}, {@link ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
         *                     {@link ScanSettings#CALLBACK_TYPE_MATCH_LOST}
         * @param result       A Bluetooth LE scan result.
         */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            LogHelper.d("BLE scan result: " + result);
        }

        /**
         * Callback when batch results are delivered.
         *
         * @param results List of scan results that are previously scanned.
         */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                LogHelper.d("BLE batch scan result: " + result);
            }
        }

        /**
         * Callback when scan could not be started.
         *
         * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
         */
        @Override
        public void onScanFailed(int errorCode) {
            String errorMsg;
            switch (errorCode) {
                case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                    errorMsg = "Already started";
                    break;
                case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    errorMsg = "Application registration failed";
                    break;
                case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                    errorMsg = "Internal error";
                    break;
                case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                    errorMsg = "Feature unsupported";
                    break;
                default:
                    errorMsg = "Unknown error";
            }
            LogHelper.d(String.format("BLE scan failed with error %d (%s)", errorCode, errorMsg));

            synchronized (BluetoothLeScanHelperLollipop.this) {
                scanning = false;
            }
        }
    }

    private static final int SCAN_MODE = ScanSettings.SCAN_MODE_LOW_POWER;
    private static final int MATCH_MODE = ScanSettings.MATCH_MODE_STICKY;
    private static final long REPORT_DELAY = 5000;

    private final BluetoothLeScanner bluetoothLeScanner;
    private final ScanCallback scanCallback = new HelperCallback();

    private boolean scanning = false;

    BluetoothLeScanHelperLollipop(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public synchronized boolean isScanning() {
        return scanning;
    }

    @Override
    public synchronized void startScan() {
        if (scanning) {
            LogHelper.d("Cannot start BLE scan: Already scanning");
            return;
        }

        // TODO: Check if the ACCESS_FINE_LOCATION permission is granted

        LogHelper.d("Starting BLE scan");
        bluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
        scanning = true;
    }

    @Override
    public synchronized void stopScan() {
        if (!scanning) {
            LogHelper.d("Cannot stop BLE scan: Not currently scanning");
            return;
        }

        LogHelper.d("Stopping BLE scan");

        bluetoothLeScanner.stopScan(scanCallback);
        scanning = false;
    }

    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(Constants.BLUETOOTH_SERVICE_UUID)
                .build();
        scanFilters.add(filter);

        return scanFilters;
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder()
                .setScanMode(SCAN_MODE)
                .setReportDelay(REPORT_DELAY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setMatchMode(MATCH_MODE);
        }
        return builder.build();
    }
}
