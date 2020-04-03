package pt.gaps.epidemictracker;

interface BluetoothLeScanHelper {
    boolean isScanning();

    void startScan();

    void stopScan();
}
