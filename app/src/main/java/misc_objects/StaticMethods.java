package misc_objects;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class StaticMethods {
    public static String volleyError(VolleyError error) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found... Please try again after some time!";
        } else if (error instanceof AuthFailureError) {
            message = "Cannot connect to Internet... Please check your connection!";
        } else if (error instanceof ParseError) {
            message = "Parsing error... Please try again after some time!";
        } else if (error instanceof NoConnectionError) {
            message = "Cannot connect to Internet... Please check your connection!";
        } else if (error instanceof TimeoutError) {
            message = "Connection timed out... Please check your internet connection!";
        }
        return message;
    }
}