package aplicatie.admin.ui.devices_fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import aplicatie.admin.R;
import aplicatie.admin.misc_objects.CallbackResponse;
import aplicatie.admin.misc_objects.Constants;
import aplicatie.admin.misc_objects.Device;
import aplicatie.admin.misc_objects.JsonRequest;
import aplicatie.admin.misc_objects.RequestQueueSingleton;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.ui.DelayedProgressDialog;

public class DevicesFragment extends Fragment {
    private final String TAG = DevicesFragment.class.getName();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView no_devices_online;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Device> devices = new ArrayList<>();
    private DeviceAdapter adapter;

    public DevicesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devices.add(new Device("TEST DEVICE", "192.168.0.111", "TEST"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_devices, container, false);
        no_devices_online = v.findViewById(R.id.tv_no_devices);
        toolbar = v.findViewById(R.id.toolbar_main);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSwipeContainer();
        initRecyclerView();
        getDevicesFromServer();
    }

    private void initRecyclerView() {
        recyclerView = getActivity().findViewById(R.id.device_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        adapter = new DeviceAdapter(getContext(), devices);
        recyclerView.setAdapter(adapter);
    }

    private void initSwipeContainer() {
        swipeContainer = getActivity().findViewById(R.id.refresh_devices);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DelayedProgressDialog pd = new DelayedProgressDialog();
                pd.message = "Getting devices list";
                pd.show(getActivity().getSupportFragmentManager(), "ProgressBar");
                devices.clear();
                getDevicesFromServer();
                swipeContainer.setRefreshing(false);
                pd.cancel();
            }
        });
    }

    private void getDevicesFromServer() {
        final DelayedProgressDialog pd = new DelayedProgressDialog();
        pd.message = "Getting devices list";
        pd.show(getActivity().getSupportFragmentManager(), "TEST");
        JsonObjectRequest request = JsonRequest.send_request(null, Constants.server_ip + "/get_online_devices", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                JSONObject jo = (JSONObject) response;
                try {
                    JSONArray ja = jo.getJSONArray("devices");
                    for (int i = 0; i < ja.length(); i++) {
                        devices.add(new Device(ja.getJSONObject(i).optString("device_id"), ja.getJSONObject(i).optString("ip"), ja.getJSONObject(i).optString("route")));
                    }
                    Log.e(TAG, "Device uri preluate: " + devices);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateDevicesList();
                pd.cancel();
            }
            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), StaticMethods.volleyError(error), Snackbar.LENGTH_SHORT).show();
                try {
                    StaticMethods.getErrorFragment("Eroare preluare device-uri", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                } catch (NullPointerException ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }
                pd.cancel();
                updateDevicesList();
            }
        });
        request.setTag("DevicesFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void updateDevicesList() {
        adapter.notifyDataSetChanged();
        if (devices.size() == 0) {
            no_devices_online.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}
