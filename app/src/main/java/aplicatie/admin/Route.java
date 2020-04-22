package aplicatie.admin;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class Route {
    private String name;
    private ArrayList<LatLng> points;
    private PolylineOptions polyline;

    public Route(String name, ArrayList<LatLng> points, PolylineOptions polyline) {
        this.name = name;
        this.points = points;
        this.polyline = polyline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LatLng> points) {
        this.points = points;
    }

    public PolylineOptions getPolyline() {
        return polyline;
    }

    public void setPolyline(PolylineOptions polyline) {
        this.polyline = polyline;
    }
}
