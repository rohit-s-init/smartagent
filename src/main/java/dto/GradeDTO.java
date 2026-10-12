package dto;

import model.Exam;
import model.Grade;

public class GradeDTO {

    private Long id;
    private String qualification;
    private Double percentage;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getQualification() {
        return qualification;
    }
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    public Double getPercentage() {
        return percentage;
    }
    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
    public GradeDTO(){}
    public GradeDTO(Grade grade){
        this.id = grade.getId();
        this.qualification = grade.getQualification();
        this.percentage = grade.getPercentage();
    }
}
