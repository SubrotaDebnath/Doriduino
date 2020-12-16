package subrota.shuvro.doriduino;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private TextView connectBTN;
    private TextView title;
    private String deviceName;
    private String deviceAddress;
    private Helper helper;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectedThread connectedThread;
    private CreateConnectThread createConnectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBTN = findViewById(R.id.connectBTN);
        title = findViewById(R.id.controlBoardTitle);
        helper = new Helper(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null){
            deviceAddress =(String) bundle.getString("address");
            deviceName =(String) bundle.getString("name");
            title.setText(deviceName+" Connecting...");

            helper.progressShow("Connecting with "+deviceName);
            connectBTN.setEnabled(false);


           bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter,deviceAddress);
            createConnectThread.start();


        }


        connectBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DeviceList.class));
            }
        });
    }




}