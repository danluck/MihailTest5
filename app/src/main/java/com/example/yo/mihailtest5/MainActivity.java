package com.example.yo.mihailtest5;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckDeviceOpportunities();
    }


    private void CheckDeviceOpportunities() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth is not supported on this device");
            Toast.makeText(this,"", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static final String TAG = "##### MainActivity";

    private BluetoothAdapter mBluetoothAdapter;
}
