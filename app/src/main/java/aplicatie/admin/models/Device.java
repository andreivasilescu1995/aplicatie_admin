package aplicatie.admin.models;

import java.io.Serializable;

public class Device implements Serializable {
    private String id;
    private String ip;
    private String route;

    public Device(String id, String ip, String route) {
        this.id = id;
        this.ip = ip;
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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public String toString() {
        return this.id + " " + this.ip + " " + this.route;
    }
}
