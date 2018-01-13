package com.example.yo.mihailtest5;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        CheckDeviceOpportunities();

        editText_devicesList = findViewById(R.id.editText_devicesList);
        devicesList = new ArrayList<BluetoothDevice>();
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

    public void OnClickLeScan(View view) {
        Log.d(TAG, "OnClickLeScan: Start scan");

        // Check if BLE module ON:
        if (mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "OnClickLeScan: BLE is enabled");
            TryToStartScan();
        }
        else {
            // Check for permission to TURN ON BLE module:
            int permissionCheckForBluetoothTurnOn = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_ADMIN);
            Log.d(TAG, "OnClickLeScan: permissionCheckForBluetoothTurnOn=" +
                    permissionCheckForBluetoothTurnOn);

            if (permissionCheckForBluetoothTurnOn == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "OnClickLeScan: permissionCheckForBluetoothTurnOn already GRANTED");
                TurnOnBluetoothAndTryToStartScan();
            }
            else {
                Log.d(TAG, "OnClickLeScan: acess DENIED, try again");

                Log.d(TAG, "OnClickLeScan: We need to turn ON Bluetooth");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_BLE_MODULE_TURN_ON);
                mIsRequestForBluetoothTurnOn = true;
            }
        }
    }

    private void TurnOnBluetoothAndTryToStartScan() {
        mBluetoothAdapter.enable();

        mIsNeedToStartScan = true;

        // Теперь нужно подписаться на события от системы об изменении состояния Bluetooth
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    public void TryToStartScan() {
        // Check for permission to TURN ON BLE module:
        int permissionCheckForBluetoothScan = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheckForBluetoothScan == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "TryToStartScan: permission to scan already GRANTED");
            scanLeDevice();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mBluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mReceiver: STATE_OFF");
                        //bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_off);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mReceiver: STATE_TURNING_OFF");
                        //bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_turning_off);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mReceiver: STATE_ON");

                        if (mIsNeedToStartScan) {
                            // Теперь нужно попробовать запустить сканирование
                            mIsNeedToStartScan = false;
                            unregisterReceiver(mReceiver);

                            ((MainActivity)context).TryToStartScan();
                        }
                        //bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_on);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mReceiver: STATE_TURNING_ON");
                        //bluetoothAdapterStatusValue.setText(R.string.bluetoot_adapter_status_value_turning_on);
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission for scan granted, start scanLeDevice");
                    scanLeDevice();
                    // Permission granted, yay! Start the Bluetooth device scan.
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission DENIED");
                    // Alert the user that this application requires the location permission to perform the scan.
                }
            }

            case REQUEST_BLE_MODULE_TURN_ON:
                Log.d(TAG, "onRequestPermissionsResult: REQUEST_BLE_MODULE_TURN_ON");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission for turn ON GRANTED");
                    if (mIsRequestForBluetoothTurnOn) {
                        mIsRequestForBluetoothTurnOn = false;
                        TurnOnBluetoothAndTryToStartScan();
                    }
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: REQUEST_BLE_MODULE_TURN_ON DENIED");
                }
                break;
        }
    }

    private boolean isLeScanEnabled = false;
    private void scanLeDevice() {

        Log.d(TAG, "scanLeDevice: start");
        if (!isLeScanEnabled) {
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            isLeScanEnabled = true;
        }
        else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            isLeScanEnabled = false;
        }
    }

    // Device scan callback.
    ArrayList<BluetoothDevice> devicesList;


    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: mLeScanCallback");

                            String deviceNameAndAddress = "" + device.getName() +
                                    " addr: " + device.getAddress() + "\r\n";
                            Log.d(TAG, "run: Find" + deviceNameAndAddress);

                            if (!devicesList.contains(device)) {
                                editText_devicesList.append(deviceNameAndAddress);
                                devicesList.add(device);
                            }
                        }
                    });
                }
            };

    private static boolean mIsNeedToStartScan = false;
    private static boolean mIsRequestForBluetoothTurnOn = false;

    private static final String TAG = "##### MainActivity";
    private static final int REQUEST_BLE_MODULE_TURN_ON = 0;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private EditText editText_devicesList;

    private BluetoothAdapter mBluetoothAdapter;


}
