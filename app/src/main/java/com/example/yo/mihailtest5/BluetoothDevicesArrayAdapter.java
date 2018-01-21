package com.example.yo.mihailtest5;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yo on 21.01.2018.
 */

public class BluetoothDevicesArrayAdapter extends ArrayAdapter<BluetoothDevice> {
    public BluetoothDevicesArrayAdapter(Context context, ArrayList<BluetoothDevice> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice bluetoothDevice = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ble_device_list_string, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.deviceName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.deviceAddress);
        // Populate the data into the template view using the data object
        tvName.setText(cutDeviceNameStringToShowOnScreen(bluetoothDevice.getName()));
        tvHome.setText(bluetoothDevice.getAddress());
        // Return the completed view to render on screen
        return convertView;
    }

    private String cutDeviceNameStringToShowOnScreen(String source) {
        return source.length() <= MAX_DEVICE_NAME_STRING_LENGTH ? source :
                source.substring(0, MAX_DEVICE_NAME_STRING_LENGTH);
    }

    private static final int MAX_DEVICE_NAME_STRING_LENGTH = 12;
}
