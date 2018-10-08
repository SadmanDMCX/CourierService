package service.courier.app.dmcx.courierservice.Models;

public class Work {

    private String work_id;
    private String work_title;
    private String work_description;
    private String work_time;
    private String work_pickup;
    private String work_drop;
    private String work_fare;
    private String work_status;
    private double latitude;
    private double longitude;
    private long created_at;
    private long modified_at;

    public String getWork_time() {
        return work_time;
    }

    public String getWork_pickup() {
        return work_pickup;
    }

    public String getWork_drop() {
        return work_drop;
    }

    public String getWork_fare() {
        return work_fare;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getWork_id() {
        return work_id;
    }

    public String getWork_title() {
        return work_title;
    }

    public String getWork_description() {
        return work_description;
    }

    public String getWork_status() {
        return work_status;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getModified_at() {
        return modified_at;
    }
}
