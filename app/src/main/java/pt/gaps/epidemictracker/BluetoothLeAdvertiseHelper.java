package pt.gaps.epidemictracker;

interface BluetoothLeAdvertiseHelper {
    boolean isAdvertising();

    void startAdvertising();

    void stopAdvertising();
}
