package com.example.smartlog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "mainActivity";
    private static final UUID serverUUID = UUID.fromString("5ba27d66-6c6d-40fc-8332-1c45f996142e");
    BluetoothAdapter mBluetoothAdapter;
    TextView textView;
    Button logIn;
    EditText ID_in;
    EditText Password_in;
    static OutputStream outputStream;
    AutoCompleteTextView autoCompleteTextView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), " Turning on bluetooth.", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter.enable();
            Log.d(null," Turning on bluetooth.");
        }else{
            if (!mBluetoothAdapter.isEnabled()) { Log.d(null," Unsupported BluetoothStack.");}
        }

        autoCompleteTextView = findViewById(R.id.editTxtLogType);
        textView = findViewById(R.id.LogTypeDisplay);

        logIn = findViewById(R.id.LogButton);
        ID_in = findViewById(R.id.editTxtIDNumber);
        Password_in = findViewById(R.id.editTxtPassword);

        String[] items = {"Morning In", "Morning Out", "Afternoon In", "Afternoon Out"};
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.dropdown_item, items);
        autoCompleteTextView.setAdapter(itemAdapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> textView.setText((String) parent.getItemAtPosition(position)));

        logIn.setOnClickListener(v -> {
            String logType = "Mi";
            if(textView.getText() == "Morning In"){logType = "Mi";}
            else if(textView.getText() == "Afternoon In"){logType = "Ai";}
            else if(textView.getText() == "Morning Out"){logType = "Mo";}
            else if(textView.getText() == "Afternoon Out"){logType = "Ao";}
            String credentials = ID_in.getText() + " " + Password_in.getText() + " " + logType;

            //Establish connection to the server and send data
            String[] serverInformation = fetchServerInfo();
            Toast.makeText(getApplicationContext(), "Connecting to server", Toast.LENGTH_SHORT).show();
            getBluetoothSocket(serverInformation[1],serverInformation[0]);

            try {
                send(credentials);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Sending error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void send(String credentials) throws IOException {
            Toast.makeText(getApplicationContext(), "Sending", Toast.LENGTH_SHORT).show();
            byte[] msgBuffer = credentials.getBytes();
            outputStream.write(msgBuffer);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getApplicationContext(), "Sent successfully", Toast.LENGTH_SHORT).show();
    }

    public String[] fetchServerInfo(){
        SQLiteQueries myDB = new SQLiteQueries(MainActivity.this);
        return myDB.serverInfo();
    }

    public void getBluetoothSocket(String add, String name){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        BluetoothDevice serverDevice = null;

        //if there are paired devices
        if (pairedDevices.size() > 0){
            //loop through paired devices
            for (BluetoothDevice device: pairedDevices) {
                if(device.getName().equalsIgnoreCase((name)) && device.getAddress().equalsIgnoreCase(add)){
                    serverDevice = device;
                    break;
                }
                Log.d(null, device.toString());
            }

            assert serverDevice != null;
            Log.d(null,serverDevice.getName()+ " "+ serverDevice.getAddress()+" Find.");

            try {
                BluetoothSocket socket = serverDevice.createInsecureRfcommSocketToServiceRecord(serverUUID);

                if (socket != null){
                    try {
                        socket.connect();
                        Log.e("","Connected");
                        Toast.makeText(getApplicationContext(), "Server Connection successful", Toast.LENGTH_SHORT).show();
                    }catch (IOException e){
                        Log.e("",e.getMessage());
                        try{
                            Log.e("","trying fallback...");
                            socket = (BluetoothSocket) serverDevice.getClass().getMethod("createRfcommSocket",new Class[]{int.class}).invoke(serverDevice,1);
                            if (socket != null) {
                                socket.connect();
                                Log.e("","...Connected");
                                Toast.makeText(getApplicationContext(), "Server connection successful", Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e1){
                            Log.e("","Couldn't establish Bluetooth connection!");
                            Toast.makeText(getApplicationContext(), "Server Connection Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                Log.d(TAG,"Error creating socket ");
            }
        }
    }


    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }

}

