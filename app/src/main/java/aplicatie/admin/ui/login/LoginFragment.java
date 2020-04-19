package aplicatie.admin.ui.login;

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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import aplicatie.admin.R;
import aplicatie.admin.misc_objects.Constants;
import aplicatie.admin.ui.DelayedProgressDialog;
import aplicatie.admin.misc_objects.RequestQueueSingleton;
import aplicatie.admin.misc_objects.StaticMethods;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getName();
    private TextView username;
    private TextView password;
    private TextView server_ip;
    private Button login;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        username = v.findViewById(R.id.edit_username);
        password = v.findViewById(R.id.edit_password);
        server_ip = v.findViewById(R.id.edit_server_ip);
        login = v.findViewById(R.id.btn_login);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        server_ip.setText(Constants.server_ip.replace("http://", "").replace("/backend", ""));

        server_ip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Constants.server_ip = "http://" + server_ip.getText().toString() + "/backen";
            return true;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void login() {
        final DelayedProgressDialog delayedProgressDialog = new DelayedProgressDialog();
        delayedProgressDialog.message = "Loging in";
        delayedProgressDialog.show(getActivity().getSupportFragmentManager(), "ProgressBar");

        StringRequest request = new StringRequest(Request.Method.POST, Constants.server_ip + "/login",
                new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    NavHostFragment.findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_fragment_devices);
                    delayedProgressDialog.cancel();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = StaticMethods.volleyError(error);
                    if (getActivity() != null)
                        StaticMethods.getErrorFragment("Eroare login", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                    Log.e(TAG, message);
                    delayedProgressDialog.cancel();
                }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("username", username.getText().toString());
                params.put("password", hashPassword(password.getText().toString()));
                return params;
            }
        };
        request.setTag("LoginFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(2000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
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
