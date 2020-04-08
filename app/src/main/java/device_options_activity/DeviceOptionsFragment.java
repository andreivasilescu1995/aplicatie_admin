package device_options_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import aplicatie.admin.ErrorFragment;
import main_activity.MainActivity;
import misc_objects.CallbackResponse;
import misc_objects.JsonRequest;
import aplicatie.admin.R;
import misc_objects.RequestQueueSingleton;
import misc_objects.StaticMethods;

public class DeviceOptionsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = DeviceOptionsFragment.class.getName();
    private FragmentManager fragmentManager;
    private TextView tv_status_value;
    public static boolean flag_status = false;

    private String mParam1;
    private String mParam2;

    public DeviceOptionsFragment() {}

    public static DeviceOptionsFragment newInstance(String param1, String param2) {
        DeviceOptionsFragment fragment = new DeviceOptionsFragment();
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
        return inflater.inflate(R.layout.fragment_options, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();

        tv_status_value = getActivity().findViewById(R.id.tv_status_value);
        Button btn_start = view.findViewById(R.id.btn_start);
        Button btn_stop = view.findViewById(R.id.btn_stop);

        flag_status = true;
        status();

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        flag_status = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag_status = false;
    }

    private void start() {
        JsonObjectRequest request = JsonRequest.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/start", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null) {
                    ErrorFragment errorFragment = ErrorFragment.newInstance("Eroare start device", message);
                    errorFragment.show(fragmentManager, "fragment_error");
                }
            }
        });
        request.setTag("DeviceOptionsFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void stop() {
        JsonObjectRequest request = JsonRequest.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/stop", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null) {
                    ErrorFragment errorFragment = ErrorFragment.newInstance("Eroare stop device", message);
                    errorFragment.show(fragmentManager, "fragment_error");
                }
            }
        });
        request.setTag("DeviceOptionsFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void status() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    if (flag_status) {
                        JsonObjectRequest request = JsonRequest.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/status", new CallbackResponse() {
                            @Override
                            public void handleResponse(Object response) {
                                JSONObject jo = (JSONObject) response;
                                if (jo.optString("status").equals("on")) {
                                    set_status(true);
                                } else {
                                    set_status(false);
                                }
                            }
                            @Override
                            public void handleError(VolleyError error) {
                                flag_status = false;
                                String message = StaticMethods.volleyError(error);
                                Log.e(TAG, message);
                                if (getView() != null && this != null)
                                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                if (fragmentManager != null) {
                                    ErrorFragment errorFragment = ErrorFragment.newInstance("Eroare preluare status device", message);
                                    errorFragment.show(fragmentManager, "fragment_error");
                                }
                            }
                        });
                        request.setTag("DeviceOptionsFragment");
                        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
                        Log.d(TAG, "UPDATEZ STATUS");
                    }
            }

        }, 1000, 5000);
    }

    private void set_status(boolean on) {
        if (on) {
            tv_status_value.setText(R.string.status_on);
            tv_status_value.setTextColor(Color.GREEN);
        }
        else {
            tv_status_value.setText(R.string.status_off);
            tv_status_value.setTextColor(Color.RED);
        }
    }

}
