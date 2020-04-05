package pt.gaps.epidemictracker;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

class RequirementsHelper {
    private static BluetoothAdapter getBluetoothAdapter(Context c) {
        BluetoothManager bluetoothManager = (BluetoothManager) c.getSystemService(Context.BLUETOOTH_SERVICE);
        return bluetoothManager != null ? bluetoothManager.getAdapter() : null;
    }

    static boolean isBluetoothCapable(Context c) {
        return getBluetoothAdapter(c) != null;
    }

    static boolean needsEnablingBluetooth(Context c) {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter(c);
        return bluetoothAdapter != null && !bluetoothAdapter.isEnabled();
    }

    static boolean needsLocationPermission(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (c.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    static boolean needsEnablingLocation(Context c) {
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            for (String provider : locationManager.getAllProviders()) {
                if (locationManager.isProviderEnabled(provider)) {
                    return false;
                }
            }
        }
        return true;
    }
}
