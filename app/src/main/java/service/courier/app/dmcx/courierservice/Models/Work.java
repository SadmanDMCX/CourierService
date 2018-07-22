package service.courier.app.dmcx.courierservice.Models;

public class Work {

    private String id;
    private String work_id;
    private String work_title;
    private String work_description;
    private String work_status;
    private long created_at;
    private long modified_at;

    public String getId() {
        return id;
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
