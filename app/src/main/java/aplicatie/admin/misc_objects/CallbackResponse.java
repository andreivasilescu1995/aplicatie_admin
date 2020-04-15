package aplicatie.admin.misc_objects;

import com.android.volley.VolleyError;

public interface CallbackResponse {
    void handleResponse(Object response);
    void handleError(VolleyError error);
}
