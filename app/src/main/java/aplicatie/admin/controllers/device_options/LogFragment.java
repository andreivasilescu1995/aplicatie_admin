package aplicatie.admin.controllers.device_options;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aplicatie.admin.DeviceOptionsActivity;
import aplicatie.admin.R;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.models.LogModel;
import aplicatie.admin.models.StringRequestCallback;
import aplicatie.admin.views.LogView;

public class LogFragment extends Fragment {
    private final String TAG = LogFragment.class.getName();
    private LogModel model;
    private LogView view;
    public static Context context;

    public LogFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) context = requireContext();
        if (model == null) model = new LogModel();
        if (view == null) view = new LogView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_log, container, false);
        view.initViews(v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SwipeRefreshLayout swipeContainer = requireActivity().findViewById(R.id.refresh_log);

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
        model.getLog("http://" + DeviceOptionsActivity.getSelectedDevice().getIp() + "/log", new StringRequestCallback() {
            @Override
            public void onSuccess(String response) {
                view.getLogTextView().setText(response);
            }
            @Override
            public void onFail(String errorMessage) {
                if (LogFragment.this.isAdded())
                    StaticMethods.showErrorDialog((AppCompatActivity) requireActivity(), "Eroare preluare log device", errorMessage);
            }
        });
    }
}
