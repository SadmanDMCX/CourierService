package service.courier.app.dmcx.courierservice.Models;

import java.util.List;

public class Admin {

    private String image_path;
    private String id;
    private String name;
    private String phone_no;
    private String status;
    private String lat;
    private String lon;
    private String created_at;
    private String modified_at;
    private List<Client> clientList;

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

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getModified_at() {
        return modified_at;
    }

    public List<Client> getClientList() {
        return clientList;
    }
}
