package service.courier.app.dmcx.courierservice.Models;

public class Admin {

    private String image_path;
    private String id;
    private String name;
    private String phone_no;
    private String status;
    private String latitude;
    private String longitude;
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

    public String getPhone_no() {
        return phone_no;
    }

    public String getStatus() {
        return status;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getModified_at() {
        return modified_at;
    }
}
