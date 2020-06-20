package aplicatie.admin.controllers.device_options;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import aplicatie.admin.DeviceOptionsActivity;
import aplicatie.admin.R;
import aplicatie.admin.misc_objects.StaticMethods;
import aplicatie.admin.models.LocationModel;
import aplicatie.admin.views.LocationView;

public class LocationFragment extends Fragment implements OnMapReadyCallback {
    private final String TAG = LocationFragment.class.getName();
    private LocationModel model;
    private LocationView view;
    public static Context context;
    private Timer bus_location_timer;

    public LocationFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (context == null) context = requireContext();
        if (model == null) model = new LocationModel();
        if (view == null) view = new LocationView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location, container, false);
        view.initViews(v);
        view.getMapView().onCreate(savedInstanceState);
        view.getMapView().getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        view.setMap(googleMap);
        if (model.getSelectedRoutePline(DeviceOptionsActivity.getSelectedDevice().getRoute()) != null)
            view.getMap().addPolyline(model.getSelectedRoutePline(DeviceOptionsActivity.getSelectedDevice().getRoute()));
        updateBusLocation();
    }

    private void updateBusLocation() {
        final LocationModel.LocationUpdate locationUpdate = new LocationModel.LocationUpdate() {
            @Override
            public void onSuccess(LatLng location) {
                if (location == null) {
                    if (LocationFragment.this.isAdded())
                        StaticMethods.showErrorDialog((AppCompatActivity) requireActivity(), "Eroare locatie", "Autobuzul nu poate fi localizat!");
//                    bus_location_timer.cancel();
                    return;
                }
                if (view.getBusMarker() == null)
                    view.setBusMarker(view.getMap().addMarker(new MarkerOptions().title("Locatia autobuzului")
                        .position(location).icon(StaticMethods.getBitmapFromVector(requireContext(), R.drawable.ic_directions_bus_black_24dp, Color.BLUE))));
                else
                    view.getBusMarker().setPosition(location);

                Log.e(TAG,"BUS LOCATION POSITION: " + location.latitude + " " + location.longitude);
                animateCamera(location);
            }
            @Override
            public void onFail(String errorMessage) {
                Log.e(TAG, "Nici un autobuz online pe ruta " + model.getSelectedRoute() + "!");
                bus_location_timer.cancel();
                if (LocationFragment.this.isAdded())
                    StaticMethods.showErrorDialog((AppCompatActivity) requireActivity(), "Eroare preluare locatie", errorMessage);
            }
        };

        bus_location_timer = new Timer();
        bus_location_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.getBusLocation(locationUpdate);
            }
        }, 100, 10000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view.getMapView().onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        view.getMapView().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        view.getMapView().onPause();
        view.setBusMarker(null);
        bus_location_timer.cancel();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        view.getMapView().onLowMemory();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        view.setMap_normal(menu.findItem(R.id.action_maps_normal));
        view.setMap_satellite(menu.findItem(R.id.action_maps_satellite));
        view.setMap_terrain(menu.findItem(R.id.action_maps_terrain));
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
                view.getMap().setMapType(view.getMap().MAP_TYPE_NORMAL);
                view.getMap_normal().setChecked(true);
                view.getMap_satellite().setChecked(false);
                view.getMap_terrain().setChecked(false);
                break;
            case R.id.action_maps_satellite:
                view.getMap().setMapType(view.getMap().MAP_TYPE_SATELLITE);
                view.getMap_normal().setChecked(false);
                view.getMap_satellite().setChecked(true);
                view.getMap_terrain().setChecked(false);
                break;
            case R.id.action_maps_terrain:
                view.getMap().setMapType(view.getMap().MAP_TYPE_TERRAIN);
                view.getMap_normal().setChecked(false);
                view.getMap_satellite().setChecked(false);
                view.getMap_terrain().setChecked(true);
                break;
        }
        return true;
    }

    private void animateCamera(LatLng position) {
        if (view.getMap().getCameraPosition().equals(new CameraPosition.Builder().target(position).zoom(16).build())) {
            return;
        }
        else {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
            view.getMap().animateCamera(cameraUpdate);
        }
    }
}