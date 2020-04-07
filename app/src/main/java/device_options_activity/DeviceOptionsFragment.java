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

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import misc_objects.CallbackResponse;
import misc_objects.JsonRequest;
import aplicatie.admin.R;
import misc_objects.RequestQueueSingleton;
import misc_objects.StaticMethods;

public class DeviceOptionsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = DeviceOptionsFragment.class.getName();
    private TextView tv_status_value;
    public static boolean flag_status = false;
    private JsonRequest jr = new JsonRequest();

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
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jr.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/start", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                JSONObject jo = (JSONObject) response;
                if (jo.optString("start").equals("true")) {
                    set_status(true);
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    private void stop() {
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jr.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/stop", new CallbackResponse() {
            @Override
            public void handleResponse(Object response) {
                JSONObject jo = (JSONObject) response;
                if (jo.optString("stop").equals("true")) {
                    set_status(false);
                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(), "Error " + response.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void handleError(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    private void status() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    if (flag_status) {
                        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(jr.send_request(null, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/status", new CallbackResponse() {
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
                                String message = StaticMethods.volleyError(error);
                                Log.e(TAG, message);
                                if (getView() != null)
                                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
                                flag_status = false;
                            }
                        }));
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
