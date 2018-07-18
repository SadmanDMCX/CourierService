package service.courier.app.dmcx.courierservice.Models;

public class Client {

    private String image_path;
    private String id;
    private String name;
    private String admin_id;
    private String phone_no;
    private String status;
    private String lat;
    private String lon;
    private long created_at;
    private long modified_at;

    public String getImage_path() {
        return image_path;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getStatus() {
        return status;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getModified_at() {
        return modified_at;
    }

    @Override
    public String toString() {
        return id+" "+image_path+" "+name+" "+admin_id+" "+phone_no+" "+status+" "+created_at+" "+modified_at;
    }
}
