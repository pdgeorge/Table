package com.surrealism.princ.bluenao;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //for Debugging
    private static final String TAG = "MainActivity";
    private static final boolean D = true;

    Button b1,b2,b3,b4,b5,b6,b7,b8; //The buttons in the app
    TextView status, replyView, txtString, txtStringLength, txtDevice;

    public static Handler bluetoothIn;

    final int handlerState = 0;

    private BluetoothAdapter btAdapter = null; //The bluetooth adapter in the device
    private BluetoothSocket btSocket = null;
    private String recDataString = "";
    ListView lv;

    private ConnectedThread mConnectedThread;

    //SPP UUID service should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //string for MAC address
    private static String address = ".";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4 = (Button) findViewById(R.id.button4);

        b5 = (Button) findViewById(R.id.button5);
        b6 = (Button) findViewById(R.id.button6);
        b7 = (Button) findViewById(R.id.button7);
        b8 = (Button) findViewById(R.id.button8);

        status = (TextView)findViewById(R.id.status);
        replyView = (TextView)findViewById(R.id.reply);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView)findViewById(R.id.listView);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(msg.what == handlerState) {                                                      //if message is what we want
                    String readMessage = (String) msg.obj;                                          //msg.arg1 = bytes from connect thread
                    recDataString += readMessage;                                                   //keep appending to string until new line carriage
                    if (recDataString.endsWith("\n")){
                        Log.i(TAG, "Message received = " + recDataString + " String Length = " + String.valueOf(recDataString.length())); //logs the message received as well as how long it was.

                        //Below is only useful for splitting full string into substrings
//                        if (recDataString.charAt(0) =='#') {                                        //# is start of line, determined by sender program
//                            String reply = recDataString.substring(1, 5);                           //reads indices 1-5 and assigns them to reply.
//                        }

                        replyView.setText(recDataString);                                           //sets the replyView textView to what was received
                    }
                    recDataString = "";                                                             //clears the string
                }
            }
        };
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            if(btSocket != null) {
                //Don't leave Bluetooth sockets open when leaving activity
                if (btSocket.isConnected()) {
                    btSocket.close();
                }
            }
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) { Log.i(TAG, "It's enabled");
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    public void forward(View v) {
        mConnectedThread.write("1");                                                                //sends 1 to connected device
        Toast.makeText(getBaseContext(), "Goes forward", Toast.LENGTH_SHORT).show();                //Tells user what they did
    }

    public void reverse(View v) {
        mConnectedThread.write("2");                                                                //sends 2 to connected device
        Toast.makeText(getBaseContext(), "Goes reverse", Toast.LENGTH_SHORT).show();                //Tells user what they did
    }

    public void left(View v) {
        mConnectedThread.write("3");                                                                //sends 3 to connected device
        Toast.makeText(getBaseContext(), "Goes left", Toast.LENGTH_SHORT).show();                   //Tells user what they did
    }

    public void right(View v) {
        mConnectedThread.write("4");                                                                //sends 4 to connected device
        Toast.makeText(getBaseContext(), "Goes right", Toast.LENGTH_SHORT).show();                  //Tells user what they did
    }

    //For activating Bluetooth
    public void on(View v){
        if(!btAdapter.isEnabled()) { //if Bluetooth isn't enabled, turn it on
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        btAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }

    public void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v){
        Set<BluetoothDevice>pairedDevices;
        pairedDevices = btAdapter.getBondedDevices();

        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_LONG).show();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from ListDeviceActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the ListDeviceActivity via EXTRA
        address = intent.getStringExtra(ListDeviceActivity.EXTRA_DEVICE_ADDRESS);
        Log.i(TAG, "The chosen address is: " + address);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
            Log.i(TAG, "Socket created");
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            Log.i(TAG, "Connecting...");
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                status.setText(R.string.failed_connection);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}
