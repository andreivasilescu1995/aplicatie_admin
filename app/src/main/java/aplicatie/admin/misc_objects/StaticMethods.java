package aplicatie.admin.misc_objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import aplicatie.admin.controllers.ErrorDialog;

public class StaticMethods {

    public static String volleyError(VolleyError error) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet. Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!";
        } else if (error instanceof AuthFailureError) {
            message = "Cannot connect to Internet. Please check your connection!";
        } else if (error instanceof ParseError) {
            message = "Parsing error. Please try again after some time!";
        } else if (error instanceof NoConnectionError) {
            message = "Cannot connect to Internet. Please check your connection!";
        } else if (error instanceof TimeoutError) {
            message = "Connection timed out. Please check server connection!";
        }
        return message;
    }

    public static BitmapDescriptor getBitmapFromVector(@NonNull Context context, @DrawableRes int vectorResourceId, @ColorInt int tintColor) {

        Drawable vectorDrawable = ResourcesCompat.getDrawable(
                context.getResources(), vectorResourceId, null);
        if (vectorDrawable == null) {
            return BitmapDescriptorFactory.defaultMarker();
        }
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, tintColor);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static void showErrorDialog(@NonNull AppCompatActivity activity, String title, String message) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm.findFragmentByTag("Error") != null) {
            fm.beginTransaction().remove(fm.findFragmentByTag("Error")).commitAllowingStateLoss();
        }
        ErrorDialog errorDialog = ErrorDialog.newInstance(title, message);
        errorDialog.show(fm, "fragment_edit_name");
    }
}
