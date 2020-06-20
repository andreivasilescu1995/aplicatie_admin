package aplicatie.admin.controllers.devices_fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import aplicatie.admin.R;
import aplicatie.admin.models.Device;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.models.DevicesListModel;
import aplicatie.admin.models.JsonRequestCallback;
import aplicatie.admin.controllers.DelayedProgressDialog;

public class DevicesListFragment extends Fragment {
    private final String TAG = DevicesListFragment.class.getName();
    private DevicesListModel model;
    private DeviceAdapter adapter;
    public static Context context;
    private TextView no_devices_online;

    public DevicesListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) context = requireContext();
        if (model == null) model = new DevicesListModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_devices, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar_main);
        ((AppCompatActivity)requireActivity()).setSupportActionBar(toolbar);
        no_devices_online = v.findViewById(R.id.tv_no_devices);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ActionBar toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (toolbar != null) toolbar.setTitle("Placi Raspberry online");
        initSwipeContainer();
        initRecyclerView();
        getDevicesFromServer();
    }

    private void initSwipeContainer() {
        final SwipeRefreshLayout swipeContainer = requireActivity().findViewById(R.id.refresh_devices);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.getDevices().clear();
                getDevicesFromServer();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = requireActivity().findViewById(R.id.device_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        adapter = new DeviceAdapter(requireContext());
        recyclerView.setAdapter(adapter);
    }

    private void getDevicesFromServer() {
        final DelayedProgressDialog pd = new DelayedProgressDialog();
        DelayedProgressDialog.message = "Getting devices list";
        pd.show(requireActivity().getSupportFragmentManager(), "TEST");
        model.getDevicesFromServer(new JsonRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                ArrayList<Device> devices = new ArrayList<>();
                JSONArray ja = null;
                try {
                    ja = response.getJSONArray("devices");
                    if (ja.length() > 0) {
                        for (int i = 0; i < ja.length(); i++) {
                            devices.add(new Device(ja.getJSONObject(i).optString("device_id"), ja.getJSONObject(i).optString("ip"), ja.getJSONObject(i).optString("route")));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "DEVICE URI ONLINE: " + devices.toString());
                adapter.setDevices(devices);
                updateDevicesList();
                pd.cancel();
            }
            @Override
            public void onFail(String errorMessage) {
                pd.cancel();
                updateDevicesList();
                if (DevicesListFragment.this.isAdded())
                    StaticMethods.showErrorDialog((AppCompatActivity) requireActivity(), "Eroare preluare device-uri din server", errorMessage);
            }
        });
    }

    private void updateDevicesList() {
        if (adapter.getDevices().isEmpty()) {
//            no_devices_online.setVisibility(View.VISIBLE);
            no_devices_online.setVisibility(View.GONE);
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.111", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.112", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.113", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.114", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.115", "TEST"));
            adapter.notifyDataSetChanged();
        }
        else {
            no_devices_online.setVisibility(View.GONE);
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.111", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.112", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.113", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.114", "TEST"));
            adapter.getDevices().add(new Device("TEST DEVICE", "192.168.0.115", "TEST"));
            adapter.notifyDataSetChanged();
        }
    }
}
