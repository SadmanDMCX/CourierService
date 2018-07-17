package service.courier.app.dmcx.courierservice.Models;

import java.util.List;

public class Admin {

    private String image_path;
    private String id;
    private String name;
    private String phone_no;
    private String status;
    private String current_location;
    private String created_at;
    private String modified_at;
    private List<Client> clientList;

    public Admin(String image_path, String id, String name, String phone_no, String status, String current_location, String created_at, String modified_at) {
        this.image_path = image_path;
        this.id = id;
        this.name = name;
        this.phone_no = phone_no;
        this.status = status;
        this.current_location = current_location;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.clientList = clientList;
    }

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

    public String getCurrent_location() {
        return current_location;
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
