package service.courier.app.dmcx.courierservice.Models;

public class Status {

    private double latitude;
    private double longitude;
    private String state;
    private String device_name;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getState() {
        return state;
    }
}
