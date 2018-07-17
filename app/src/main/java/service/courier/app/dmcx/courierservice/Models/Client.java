package service.courier.app.dmcx.courierservice.Models;

public class Client {

    private String image_path;
    private String id;
    private String name;
    private String admin_id;
    private String phone_no;
    private String status;
    private String current_location;
    private String work;
    private long created_at;
    private long modified_at;

    public Client() {
    }

    public Client(String image_path, String id, String name, String admin_id, String phone_no, String status, String current_location, String work, long created_at, long modified_at) {
        this.image_path = image_path;
        this.id = id;
        this.name = name;
        this.admin_id = admin_id;
        this.phone_no = phone_no;
        this.status = status;
        this.current_location = current_location;
        this.work = work;
        this.created_at = created_at;
        this.modified_at = modified_at;
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

    public String getWork() {
        return work;
    }

    public long getCreated_at() {
        return created_at;
    }

    public long getModified_at() {
        return modified_at;
    }

    @Override
    public String toString() {
        return id+" "+image_path+" "+name+" "+admin_id+" "+phone_no+" "+status+" "+current_location+" "+work+" "+created_at+" "+modified_at;
    }
}
