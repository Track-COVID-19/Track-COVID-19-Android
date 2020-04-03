package pt.gaps.epidemictracker;

class BluetoothLeScanHelperStub implements BluetoothLeScanHelper {
    @Override
    public boolean isScanning() {
        return false;
    }

    @Override
    public void startScan() {
    }

    @Override
    public void stopScan() {
    }
}
