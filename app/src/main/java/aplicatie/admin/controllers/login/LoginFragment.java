package aplicatie.admin.controllers.login;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aplicatie.admin.R;
import aplicatie.admin.misc_objects.Constants;
import aplicatie.admin.models.LoginModel;
import aplicatie.admin.controllers.DelayedProgressDialog;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.views.LoginView;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getName();
    private LoginView view;
    private LoginModel model;
    public static Context context;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (context == null) context = requireContext();
        if (view == null) view = new LoginView();
        if (model == null) model = new LoginModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        view.initViews(v);
        return v;
    }

    @Override
    public void onViewCreated(View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        view.getServer_ip().setText(Constants.server_ip.replace("http://", "").replace("/backend", ""));

        view.getServer_ip().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Constants.server_ip = "http://" + view.getServer_ip().getText().toString() + "/backend";
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        view.getLogin().setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                final DelayedProgressDialog delayedProgressDialog = new DelayedProgressDialog();
                DelayedProgressDialog.message = "Loging in";
                delayedProgressDialog.show(requireActivity().getSupportFragmentManager(), "ProgressBar");
                model.login(view.getUsername().getText().toString(), view.getPassword().getText().toString(), new LoginModel.LoginCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Successfully logged in to " + Constants.server_ip);
                        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_fragment_devices);
                        delayedProgressDialog.cancel();
                    }

                    @Override
                    public void onFail(String errorMessage) {
                        Log.d(TAG, "Failed to login to " + Constants.server_ip);
                        StaticMethods.showErrorDialog((AppCompatActivity) getActivity(), "Eroare login", errorMessage);
                        delayedProgressDialog.cancel();
                    }
                });
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        context = null;
    }
}
