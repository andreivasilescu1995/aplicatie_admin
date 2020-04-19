package aplicatie.admin.ui.device_options;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import aplicatie.admin.DeviceOptionsActivity;
import aplicatie.admin.R;
import aplicatie.admin.misc_objects.CallbackResponse;
import aplicatie.admin.misc_objects.JsonRequest;
import aplicatie.admin.misc_objects.RequestQueueSingleton;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.ui.ErrorFragment;

public class DeviceStatusFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final String TAG = DeviceStatusFragment.class.getName();
    public static Timer timer_status;

    private String mParam1;
    private String mParam2;

    public DeviceStatusFragment() {}

    public static DeviceStatusFragment newInstance(String param1, String param2) {
        DeviceStatusFragment fragment = new DeviceStatusFragment();
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

        Button btn_start = view.findViewById(R.id.btn_start);
        Button btn_stop = view.findViewById(R.id.btn_stop);

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer_status.cancel();
    }

    private void start() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/start", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("succes")) {
                    Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
                    set_status(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                try {
                    StaticMethods.getErrorFragment("Eroare eroare stop device", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                } catch (NullPointerException ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        request.setTag("DeviceOptionsFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void stop() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/stop", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("succes")) {
                    Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
                    set_status(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = StaticMethods.volleyError(error);
                Log.e(TAG, message);
                if (getView() != null)
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                try {
                    StaticMethods.getErrorFragment("Eroare eroare stop device", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                } catch (NullPointerException ex) {
                    Log.e(TAG, ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        request.setTag("DeviceOptionsFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void status() {
        timer_status = new Timer("status_timer");
        timer_status.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                StringRequest request = new StringRequest(Request.Method.POST, "http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/status", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("on"))
                            set_status(true);
                        else
                            set_status(false);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        timer_status.cancel();
                        String message = StaticMethods.volleyError(error);
                        Log.e(TAG, message);
                        if (getView() != null && this != null)
                            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
                        if (getActivity() != null)
                            StaticMethods.getErrorFragment("Eroare preluare status", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                    }
                });
                request.setTag("DeviceOptionsFragment");
                request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
            }
        }, 750, 10000);
    }

    private void set_status(boolean on) {
        TextView tv_status_value = getActivity().findViewById(R.id.tv_status_value);
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
