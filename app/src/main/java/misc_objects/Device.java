package misc_objects;

import java.io.Serializable;

public class Device implements Serializable {
    private String id;
    private String ip;
    private boolean online;
    private String route;

    public Device(String id, String ip, boolean online, String route) {
        this.id = id;
        this.ip = ip;
        this.online = online;
        this.route = route;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return this.id + " " + this.ip + " " + this.online + " " + this.route;
    }
}
