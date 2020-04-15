package aplicatie.admin.misc_objects;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class JsonRequest {
    private final String TAG = JsonRequest.class.getName();

    public static JsonObjectRequest send_request(JSONObject js_obj, String url, final CallbackResponse cb) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, js_obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cb.handleResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                cb.handleError(error);
            }
        });
        return jsonObjectRequest;
    }
}