package com.example.smartlog;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

public class Fresh_Start extends AppCompatActivity {

    Button save;
    AutoCompleteTextView deviceNames;
    EditText deviceAddress;

    String[] devicesN;
    String[] devicesA;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_start);

        save = findViewById(R.id.save_button);
        deviceNames = findViewById(R.id.Bluetooth_Device_Name);
        deviceAddress = findViewById(R.id.Bluetooth_Device_Address);

        //Disable save button to avoid exception. Set MCaddress uneditable.
        deviceAddress.setFocusable(false);

        save.setOnClickListener(v -> {
                try {
                    String name = String.valueOf(deviceNames.getText());
                    String add = String.valueOf(deviceAddress.getText());

                    if(name == null || add == null){
                        Toast.makeText(Fresh_Start.this, "Please select a server", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(Fresh_Start.this, name + " | " + add, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Fresh_Start.this, name +" > "+result, Toast.LENGTH_SHORT).show();
                        insertServer(name, add);
                        startActivity(new Intent(Fresh_Start.this, BaseActivity.class));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Fresh_Start.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                }

        });

        Toast.makeText(Fresh_Start.this, "Searching Paired Devices", Toast.LENGTH_SHORT).show();
        searchPairedDevices();
        Toast.makeText(Fresh_Start.this, "Search Complete Device", Toast.LENGTH_SHORT).show();

        deviceNames.setOnItemClickListener((parent, view, position, id) -> {
            deviceAddress.setText((String) devicesA[position]);
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void searchPairedDevices(){
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if(bluetoothAdapter == null){
            Toast.makeText(Fresh_Start.this, "Device does not support bluetooth.", Toast.LENGTH_SHORT).show();
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        devicesN = new String[pairedDevices.size()];
        devicesA = new String[pairedDevices.size()];

        int i = 0;
        if(pairedDevices.size() > 0){
            //There are paired devices. Get the name and add
            for(BluetoothDevice device: pairedDevices){
                String deviceName = device.getName();
                String deviceAdd = device.getAddress();
                devicesN[i] = deviceName;
                devicesA[i] = deviceAdd;
                i++;
            }
            String[] items = devicesN;
            ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(Fresh_Start.this, R.layout.devices_dropdown, items);
            deviceNames.setAdapter(itemAdapter);
        }
    }

    public void insertServer(String Name, String add) throws IOException {
        SQLiteQueries myDB = new SQLiteQueries(Fresh_Start.this);
        myDB.addServer(Name,add);
    }
}