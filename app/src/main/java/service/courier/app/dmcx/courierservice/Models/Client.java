package service.courier.app.dmcx.courierservice.Models;

public class Client {

    private String image_path;
    private String id;
    private String name;
    private String admin_id;
    private String phone_no;
    private String status;
    private String current_location;
    private String work_accept;
    private String work_description;
    private String work_destination;
    private String created_at;
    private String modified_at;

    public Client(String image_path, String id, String name, String admin_id, String phone_no, String status, String current_location, String work_accept, String work_description, String work_destination, String created_at, String modified_at) {
        this.image_path = image_path;
        this.id = id;
        this.name = name;
        this.admin_id = admin_id;
        this.phone_no = phone_no;
        this.status = status;
        this.current_location = current_location;
        this.work_accept = work_accept;
        this.work_description = work_description;
        this.work_destination = work_destination;
        this.created_at = created_at;
        this.modified_at = modified_at;
    }

    public String getName() {
        return name;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getId() {
        return id;
    }

    public String getAdmin_id() {
        return admin_id;
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

    public String getWork_accept() {
        return work_accept;
    }

    public String getWork_description() {
        return work_description;
    }

    public String getWork_destination() {
        return work_destination;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getModified_at() {
        return modified_at;
    }
}
