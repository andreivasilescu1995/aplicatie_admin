package aplicatie.admin.ui.devices_fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
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

public class DevicesFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = DevicesFragment.class.getName();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Device> devices = new ArrayList<>();
    private DeviceAdapter adapter;

    private String mParam1;
    private String mParam2;

    public DevicesFragment() {}

    public static DevicesFragment newInstance(String param1, String param2) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_devices, container, false);
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
                devices.clear();
                getDevicesFromServer();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void getDevicesFromServer() {
        JsonObjectRequest request = JsonRequest.send_request(null, Constants.server_ip + "/get_online_devices", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                JSONObject jo = (JSONObject) response;
                try {
                    JSONArray ja = jo.getJSONArray("devices");
                    for (int i = 0; i < ja.length(); i++) {
                        devices.add(new Device(ja.getJSONObject(i).optString("device_id"), ja.getJSONObject(i).optString("ip"), ja.getJSONObject(i).optBoolean("online"), ja.getJSONObject(i).optString("ruta")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateDevicesList();
            }
            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), StaticMethods.volleyError(error), Snackbar.LENGTH_SHORT).show();
                updateDevicesList();

                try {
                    StaticMethods.getErrorFragment("Eroare preluare device-uri", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                } catch (NullPointerException ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        request.setTag("DevicesFragment");
        request.setTag("DevicesFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void updateDevicesList() {
        adapter.notifyDataSetChanged();
    }
}
