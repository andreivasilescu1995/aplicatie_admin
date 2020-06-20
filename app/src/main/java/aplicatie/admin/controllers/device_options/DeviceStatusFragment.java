package aplicatie.admin.controllers.device_options;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

import aplicatie.admin.DeviceOptionsActivity;
import aplicatie.admin.R;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.models.DeviceStatusModel;
import aplicatie.admin.models.StringRequestCallback;
import aplicatie.admin.views.DeviceStatusView;

public class DeviceStatusFragment extends Fragment {
    private final String TAG = DeviceStatusFragment.class.getName();
    private DeviceStatusModel model;
    private DeviceStatusView view;
    public static Context context;
    public static Timer timer_status;

    public DeviceStatusFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) context = requireContext();
        if (model == null) model = new DeviceStatusModel();
        if (view == null) view = new DeviceStatusView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_device_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        view.initViews(v);
        status();

        view.getBtn_start().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        view.getBtn_stop().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        view.getBtn_poweroff().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poweroff();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timer_status.cancel();
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
        model.start(DeviceOptionsActivity.getSelectedDevice().getIp(), new StringRequestCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.equals("succes")) {
                    Toast.makeText(requireContext(), "Succes", Toast.LENGTH_SHORT).show();
                    set_status(true);
                }
            }
            @Override
            public void onFail(String errorMessage) {
                if (DeviceStatusFragment.this.isAdded())
                    StaticMethods.showErrorDialog((AppCompatActivity) getActivity(), "Eroare start", errorMessage);
            }
        });
    }

    private void stop() {
        model.stop(DeviceOptionsActivity.getSelectedDevice().getIp(), new StringRequestCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.equals("succes")) {
                    Toast.makeText(getContext(), "Succes", Toast.LENGTH_SHORT).show();
                    set_status(false);
                }
            }
            @Override
            public void onFail(String errorMessage) {
                if (DeviceStatusFragment.this.isAdded())
                StaticMethods.showErrorDialog((AppCompatActivity) getActivity(), "Eroare stop", errorMessage);
            }
        });
    }

    private void status() {
//        if (timer_status == null)
        timer_status = new Timer("status_timer");
        timer_status.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                model.getStatus(DeviceOptionsActivity.getSelectedDevice().getIp(), new StringRequestCallback() {
                    @Override
                    public void onSuccess(String response) {
                        if (response.equals("on"))
                            set_status(true);
                        else
                            set_status(false);
                    }
                    @Override
                    public void onFail(String errorMessage) {
                        if (DeviceStatusFragment.this.isAdded())
                            StaticMethods.showErrorDialog((AppCompatActivity) getActivity(), "Eroare preluare status device", errorMessage);
                    }
                });
            }
        }, 750, 10000);
    }

    private void poweroff() {
        model.powerOff(DeviceOptionsActivity.getSelectedDevice().getIp(), new StringRequestCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e(TAG, "RESPONSE POWEROFF: " + response);
                Toast.makeText(requireContext(), "Succes", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFail(String errorMessage) {
                if (DeviceStatusFragment.this.isAdded())
                    StaticMethods.showErrorDialog((AppCompatActivity) getActivity(), "Eroare poweroff", errorMessage);
            }
        });
        getActivity().finish();
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
