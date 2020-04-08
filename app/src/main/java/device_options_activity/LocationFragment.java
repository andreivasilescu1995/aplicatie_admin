package device_options_activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

import aplicatie.admin.ErrorFragment;
import main_activity.MainActivity;
import misc_objects.CallbackResponse;
import misc_objects.Device;
import misc_objects.JsonRequest;
import aplicatie.admin.R;
import misc_objects.RequestQueueSingleton;
import misc_objects.StaticMethods;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public static boolean flag_locatie = false;

    private final String TAG = LocationFragment.class.getName();
    private MapView mapView;
    private GoogleMap map;
    private MenuItem map_normal;
    private MenuItem map_satellite;
    private MenuItem map_terrain;

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
        flag_locatie = true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map_normal.setChecked(true);

        final Device d = DeviceOptionsActivity.getSelectedDevice();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            LatLng position = null;
            Marker marker = null;

            @Override
            public void run() {
                    if (flag_locatie) {
                        JsonObjectRequest request = JsonRequest.send_request(null, "http://" + d.getIp() + "/location", new CallbackResponse() {
                            @Override
                            public void handleResponse(Object response) {
                                map_normal.setChecked(true);

                                JSONObject jo = (JSONObject) response;
                                position = new LatLng(jo.optDouble("latitude"), jo.optDouble("longitude"));
                                if (marker == null) {
                                    marker = map.addMarker(new MarkerOptions().position(position));
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
                                    map.animateCamera(cameraUpdate);
                                } else {
                                    marker.setPosition(position);
                                    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
                                    //map.animateCamera(cameraUpdate);
                                }
                            }

                            @Override
                            public void handleError(VolleyError error) {
                                String message = StaticMethods.volleyError(error);
                                flag_locatie = false;
                                Log.d(TAG, error.toString());
                                if (getView() != null)
                                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                if (fragmentManager != null) {
                                    ErrorFragment errorFragment = ErrorFragment.newInstance("Eroare preluare locatie device", message);
                                    errorFragment.show(fragmentManager, "fragment_error");
                                }
                            }
                        });
                        request.setTag("LocationFragment");
                        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(request);
                        Log.d(TAG, "UPDATEZ LOCATIE");
                    }
            }
        }, 1000, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        flag_locatie = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        flag_locatie = false;
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
