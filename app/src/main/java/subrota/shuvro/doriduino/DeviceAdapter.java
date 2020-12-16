package subrota.shuvro.doriduino;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder>{
    private List<DeviceInfoDataSet> devices = new ArrayList<>();
    private  Set<BluetoothDevice> pairedDevices;
    private Context context;

    public DeviceAdapter(Context context, List<DeviceInfoDataSet> devices, Set<BluetoothDevice> pairedDevices) {
        this.devices = devices;
        this.context = context;
        this.pairedDevices = pairedDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(devices.get(position).getName());
        holder.id.setText(devices.get(position).getId());
        holder.deviceRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ControlBoard.class);
                intent.putExtra("id", devices.get(position).getId());
                intent.putExtra("name", devices.get(position).getName());
                intent.putExtra("devices", (Serializable) pairedDevices);
                //ContextCompat.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, id;
        LinearLayout deviceRow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.device_name);
            id = itemView.findViewById(R.id.device_id);
            deviceRow = itemView.findViewById(R.id.deviceRow);
        }
    }
}
