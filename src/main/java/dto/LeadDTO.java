package dto;

public class LeadDTO {

    private long id;
    private String phoneNumber;
    private String name;
    private String location;
    private String schooling;
    private String email;
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LeadDTO() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchooling() {
        return schooling;
    }

    public void setSchooling(String schooling) {
        this.schooling = schooling;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LeadDTO(Long id, String phoneNumber, String name, String location, String schooling, String email) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.location = location;
        this.schooling = schooling;
        this.email = email;
    }

    @Override
    public String toString() {
        return "LeadDTO [id=" + id + ", phoneNumber=" + phoneNumber + ", name=" + name + ", location=" + location
                + ", schooling=" + schooling + ", email=" + email + "]";
    }
}