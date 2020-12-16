package subrota.shuvro.doriduino;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    private Context context;
    private List<Object> deviceList;
    private  DeviceListDataSet deviceListDataSet;
    private static final String TAG = "DeviceAdapter";

    public DeviceAdapter(Context context, List<Object> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.device_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        deviceListDataSet = (DeviceListDataSet) deviceList.get(position);
        holder.name.setText(deviceListDataSet.getName());
        holder.id.setText(deviceListDataSet.getAddress());
        Log.i(TAG, "Name: "+ deviceListDataSet.getName()+ "   Address: "+ deviceListDataSet.getAddress());

        holder.deviceRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("name", deviceListDataSet.getName());
                intent.putExtra("address",deviceListDataSet.getAddress());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
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
