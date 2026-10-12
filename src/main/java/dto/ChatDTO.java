package dto;

import jakarta.persistence.*;
import model.Chat;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class ChatDTO {

    private Long id;

    private String message;

    private String role;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ChatDTO(Chat chat){

        this.id = chat.getId();
        this.role = chat.getRole();
        this.message = chat.getMessage();
        this.createdAt = chat.getCreatedAt();

    }

}