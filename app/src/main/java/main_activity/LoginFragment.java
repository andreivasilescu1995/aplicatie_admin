package main_activity;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import aplicatie.admin.R;
import misc_objects.CallbackResponse;
import misc_objects.JsonRequest;
import misc_objects.RequestQueueSingleton;
import misc_objects.StaticMethods;

public class LoginFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = LoginFragment.class.getName();
    public static String server_ip;

    private String mParam1;
    private String mParam2;

    public LoginFragment() {
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final TextView username = getActivity().findViewById(R.id.textbox_username);
        final TextView password = getActivity().findViewById(R.id.textbox_password);
        final TextView server_ip = getActivity().findViewById(R.id.tv_server_ip);
        LoginFragment.server_ip = "http://" + server_ip.getText().toString();

        server_ip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LoginFragment.server_ip = "http://" + server_ip.getText().toString();
                return true;
            }
        });

        Button b = getActivity().findViewById(R.id.btn_login);
        b.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                JsonRequest jr = new JsonRequest();
                JSONObject jo = new JSONObject();
                try {
                    jo.put("username", username.getText());
                    jo.put("password", hashPassword(password.getText().toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                com.android.volley.toolbox.JsonRequest request = jr.send_request(jo, LoginFragment.server_ip + "/login", new CallbackResponse() {
                    @Override
                    public void handleResponse(Object response) {
                        NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_fragment_devices);
                    }
                    @Override
                    public void handleError(VolleyError error) {
                        String message = StaticMethods.volleyError(error);
                        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String hashPassword(String password) {
        String hashPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hashPassword = sb.toString();
            Log.d(TAG, "HASHED PASSWORD:" + hashPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashPassword;
    }
}
