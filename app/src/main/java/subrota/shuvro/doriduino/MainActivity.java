package subrota.shuvro.doriduino;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private TextView connectBTN;
    private TextView title;
    private TextView textCountTV;
    private Button sendBTN;
    private EditText messageET;
    private Spinner speedSpinner;
    private CheckBox checkBox;
    private String deviceName;
    private String deviceAddress;
    private Object speed;
    private Helper helper;
    public BluetoothAdapter bluetoothAdapter;
    public static ConnectedThread connectedThread;
    public CreateConnectThread createConnectThread;
    public static BluetoothSocket mmSocket;
    public static Handler handler;
    private final static int CONNECTING_STATUS = 1;
    private final static int MESSAGE_READ = 2;
    private static final String[] speedText = {"Default", "1X", "2X", "3X", "4X", "5X", "6X", "7X", "8X", "9X", "10X"};
    private static final String TAG = "MainActivity";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBTN = findViewById(R.id.connectBTN);
        title = findViewById(R.id.controlBoardTitle);
        sendBTN = findViewById(R.id.sendBTN);
        messageET = findViewById(R.id.controlBoardMessage);
        speedSpinner = findViewById(R.id.SpeedSpinner);
        checkBox = findViewById(R.id.controlBoardCB);
        textCountTV = findViewById(R.id.textCountTV);
        helper = new Helper(this);
        preferences = new SharedPreferences(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null){
            deviceAddress =(String) bundle.getString("address");
            deviceName =(String) bundle.getString("name");
            title.setText(deviceName+" Connecting...");

            Log.i(TAG, "Name: "+deviceName+"   Address: "+deviceAddress);

            helper.progressShow("Connecting with "+deviceName);
            connectBTN.setEnabled(false);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();


        }

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        switch(msg.arg1){
                            case 1:
                                //toolbar.setSubtitle("Connected to " + deviceName);
                                title.setText(deviceName+" Connected");
                                helper.progressDismiss();
                                connectBTN.setEnabled(true);
                                //buttonConnect.setEnabled(true);
                                //sendBTN.setEnabled(true);
                                break;
                            case -1:
                                title.setText(deviceName+" fails to connect");
                                helper.progressDismiss();
                                //progressBar.setVisibility(View.GONE);
                                connectBTN.setEnabled(true);
                                //sendBTN.setEnabled(false);
                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String arduinoMsg = msg.obj.toString();
                        switch (arduinoMsg.toLowerCase()){
                            case "led is turned on":
                                //textViewInfo.setText("Arduino Message : " + arduinoMsg);
                                break;
                            case "led is turned off":
                                //textViewInfo.setText("Arduino Message : " + arduinoMsg);
                                break;
                        }
                        break;
                }
            }
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, speedText);
        speedSpinner.setAdapter(adapter);

        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                speed = parent.getItemAtPosition(position);
                preferences.setSpeed(position);
                Log.i(TAG, "position Text: "+ speed.toString());
                Log.i(TAG, "position: "+ position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        } );

        /*=============================== Spinner operation ==============================*/

        if (preferences.getSpeed() < 0 && preferences.getSpeed() > 0 ){
            speedSpinner.setSelection(1);
        }else {
            speedSpinner.setSelection(preferences.getSpeed());
        }


        /*=============================== Checkbox operation ==============================*/
        preferences = new SharedPreferences(this);
        if (preferences.getIsChecked()){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new SharedPreferences(buttonView.getContext()).setIsChecked(isChecked);
            }
        });

        connectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeviceList.class));
            }
        });

        /*=============================== EditText operation ==============================*/
        preferences = new SharedPreferences(this);
        if (preferences.getMessage().toString() != null || !preferences.getMessage().toString().equals(" ")){
            messageET.setText(preferences.getMessage().toString());
            textCountTV.setText(900-preferences.getMessage().length()+"/900");
            messageET.setSelection(new SharedPreferences(this).getMessage().toString().length());
        }

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textCountTV.setText(900-s.toString().length() + "/900");
                preferences.setMessage(messageET.getText().toString());
            }
        });

        sendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullText = null;
                if (messageET.getText()!= null && !messageET.getText().equals("")){
                    if (checkBox.isChecked()){
                        fullText = messageET.getText().toString()+";"+speed.toString()+";"+"1;";
                    }else {
                        fullText = messageET.getText().toString()+";"+speed.toString()+";"+"0;";
                    }
                    try {
                        connectedThread.write(fullText);
                        Log.i(TAG, "Full Send String 1: \""+ fullText+"\"");
                        helper.showSnackBar(v, "Message sent to "+deviceName);
                    }catch (Exception e){
                        Log.i(TAG, "Connection Thread Error: "+ e.getMessage());
                        helper.showSnackBar(v, "Device is disconnected");
                    }


                }else {
                    messageET.setError("No message");
                }
                Log.i(TAG, "Full Send String: \""+ fullText+"\"");

            }
        });
    }



    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.i(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.i("Status", "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.i("Status", "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.i(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.run();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.i(TAG, "Could not close the client socket", e);
            }
        }
    }

    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from Arduino until termination character is reached.
                    Then send the whole String message to GUI Handler.
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n'){
                        readMessage = new String(buffer,0,bytes);
                        Log.i("Arduino Message",readMessage);
                        handler.obtainMessage(MESSAGE_READ,readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            Log.i(TAG, "String Length: " + input.length());
            byte[] bytes = input.getBytes(); //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
                Log.i(TAG, "byte Length: " + bytes.length);

            } catch (IOException e) {
                Log.i("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



}