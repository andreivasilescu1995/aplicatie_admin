package aplicatie.admin.ui.devices_fragment;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import aplicatie.admin.misc_objects.Device;
import aplicatie.admin.DeviceOptionsActivity;
import aplicatie.admin.R;

class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceAdapterViewHolder> {
    private final String TAG = DeviceAdapter.class.getName();
    private ArrayList<Device> devices = new ArrayList<>();
    private Context context;

    public DeviceAdapter(Context context, ArrayList<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public DeviceAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_row,null));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceAdapterViewHolder holder, final int position) {
        Device d = devices.get(position);
        holder.value_id.setText(d.getId());
        holder.value_ip.setText(d.getIp());
        holder.value_route.setText(d.getRoute());

        holder.device_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DeviceOptionsActivity.class);
                intent.putExtra("Device", devices.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class DeviceAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView value_id;
        TextView value_ip;
        TextView value_route;
        ImageView device_options;

        public DeviceAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            value_id = itemView.findViewById(R.id.value_id);
            value_ip = itemView.findViewById(R.id.value_ip);
            value_route = itemView.findViewById(R.id.value_route);
            device_options = itemView.findViewById(R.id.device_options);
        }
    }

    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

