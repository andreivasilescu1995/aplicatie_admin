package device_options_activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import aplicatie.admin.ErrorFragment;
import misc_objects.CallbackResponse;
import misc_objects.JsonRequest;
import aplicatie.admin.R;
import misc_objects.RequestQueueSingleton;
import misc_objects.StaticMethods;

public class LogFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final String TAG = LogFragment.class.getName();

    public LogFragment() { }

    public static LogFragment newInstance(String param1, String param2) {
        LogFragment fragment = new LogFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout swipeContainer = getActivity().findViewById(R.id.refresh_log);

        getDeviceLog();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeviceLog();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void getDeviceLog() {
        final TextView log = getActivity().findViewById(R.id.tv_log);
        JsonObjectRequest request = JsonRequest.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/log", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                JSONObject jo = (JSONObject) response;
                StringBuilder log_string = new StringBuilder();
                try {
                    JSONArray ja = jo.getJSONArray("log");
                    for (int i = 0; i < ja.length(); i++) {
                        log_string.append(ja.optString(i)).append("\n");
                    }
                    log.setText(log_string.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.d(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null) {
                    ErrorFragment errorFragment = ErrorFragment.newInstance("Eroare preluare log device", message);
                    errorFragment.show(fragmentManager, "fragment_error");
                }
            }
        });
        request.setTag("LogFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }
}
