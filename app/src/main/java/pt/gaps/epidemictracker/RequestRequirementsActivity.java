package pt.gaps.epidemictracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

public class RequestRequirementsActivity extends AppCompatActivity {
    private boolean requesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_requirements);
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkMissingRequirements();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        requesting = false;
        checkMissingRequirements();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        requesting = false;
        checkMissingRequirements();
    }

    private void checkMissingRequirements() {
        // TODO: Display a message describing the requirements that are missing before asking for them

        if (requesting) {
            return;
        }

        requesting = true;
        if (RequirementsHelper.needsEnablingBluetooth(this)) {
            requestEnableBluetooth();
        } else if (RequirementsHelper.needsLocationPermission(this)) {
            requestLocationPermission();
        } else if (RequirementsHelper.needsEnablingLocation(this)) {
            requestEnableLocation();
        } else {
            requesting = false;
            finish();
        }
    }

    private void requestEnableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, Constants.REQUEST_CODE_ENABLE_BLUETOOTH);
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void requestEnableLocation() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, Constants.REQUEST_CODE_ENABLE_LOCATION);
    }
}
