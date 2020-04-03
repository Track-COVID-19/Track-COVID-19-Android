package pt.gaps.epidemictracker;

class BluetoothLeAdvertiseHelperStub implements BluetoothLeAdvertiseHelper {
    @Override
    public boolean isAdvertising() {
        return false;
    }

    @Override
    public void startAdvertising() {
    }

    @Override
    public void stopAdvertising() {
    }
}
