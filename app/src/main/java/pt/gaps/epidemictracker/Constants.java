package pt.gaps.epidemictracker;

import android.os.ParcelUuid;

class Constants {
    static final ParcelUuid BLUETOOTH_SERVICE_UUID = ParcelUuid
            .fromString("037ef55d-94bb-48ac-aa80-4e2774334bf7");

    static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    static final int REQUEST_CODE_ENABLE_LOCATION = 3;
}
