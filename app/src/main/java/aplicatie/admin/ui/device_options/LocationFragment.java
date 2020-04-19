package aplicatie.admin.ui.device_options;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import aplicatie.admin.DeviceOptionsActivity;
import aplicatie.admin.R;
import aplicatie.admin.misc_objects.CallbackResponse;
import aplicatie.admin.misc_objects.Device;
import aplicatie.admin.misc_objects.JsonRequest;
import aplicatie.admin.misc_objects.RequestQueueSingleton;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.ui.ErrorFragment;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private final String TAG = LocationFragment.class.getName();
    private MapView mapView;
    private GoogleMap map;
    private MenuItem map_normal;
    private MenuItem map_satellite;
    private MenuItem map_terrain;
    public static Timer timer_location;

    public LocationFragment() { }

    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        final Device d = DeviceOptionsActivity.getSelectedDevice();

        map = googleMap;
        map_normal.setChecked(true);

        timer_location = new Timer("location_timer");
        timer_location.schedule(new TimerTask() {
            LatLng position = null;
            Marker marker = null;

            @Override
            public void run() {
                JsonObjectRequest request = JsonRequest.send_request(null, "http://" + d.getIp() + "/location", new CallbackResponse() {
                    @Override
                    public void handleResponse(Object response) {
                        map_normal.setChecked(true);
                        position = new LatLng(((JSONObject) response).optDouble("latitude"), ((JSONObject) response).optDouble("longitude"));
                        if (marker == null) {
                            marker = map.addMarker(new MarkerOptions().position(position));
                        } else {
                            marker.setPosition(position);
                        }
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
                        map.animateCamera(cameraUpdate);
                    }
                    @Override
                    public void handleError(VolleyError error) {
                        timer_location.cancel();
                        String message = StaticMethods.volleyError(error);
                        Log.d(TAG, error.toString());
                        if (getView() != null)
                            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
                        if (getActivity() != null)
                            StaticMethods.getErrorFragment("Eroare localizare device", message).show(getActivity().getSupportFragmentManager(), "fragment_error");
                    }
                });
                request.setTag("LocationFragment");
                request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
                Log.d(TAG, "UPDATEZ LOCATIE");
            }
        }, 500, 5000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        timer_location.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        map_normal = menu.findItem(R.id.action_maps_normal);
        map_satellite = menu.findItem(R.id.action_maps_satellite);
        map_terrain = menu.findItem(R.id.action_maps_terrain);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_location, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_maps_normal:
                map.setMapType(map.MAP_TYPE_NORMAL);
                map_normal.setChecked(true);
                map_satellite.setChecked(false);
                map_terrain.setChecked(false);
                break;
            case R.id.action_maps_satellite:
                map.setMapType(map.MAP_TYPE_SATELLITE);
                map_normal.setChecked(false);
                map_satellite.setChecked(true);
                map_terrain.setChecked(false);
                break;
            case R.id.action_maps_terrain:
                map.setMapType(map.MAP_TYPE_TERRAIN);
                map_normal.setChecked(false);
                map_satellite.setChecked(false);
                map_terrain.setChecked(true);
                break;
        }
        return true;
    }
}
