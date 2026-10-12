package dto;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import model.Feedback;

@Entity
public class FeedbackDTO {

    private Long id;

    private String message;

    LocalDateTime createdAt;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public FeedbackDTO(){}

    public FeedbackDTO(Feedback feedback){
        this.id = feedback.getId();
        this.message = feedback.getMessage();
        this.createdAt = feedback.getCreatedAt();
    }

}
