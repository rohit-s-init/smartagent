package model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leads")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String name;
    @Column(nullable = true)
    private String location;
    @Column(nullable = true)
    private String schooling;
    @Column(nullable = true)
    private String email;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<Exam> exams = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<Grade> grades = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks = new ArrayList<>();

    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, optional = true)
    private Note note;

    @OneToOne(mappedBy = "lead", cascade = CascadeType.ALL, optional = true)
    private Schedule schedule;

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Lead(){

    }
    public Lead(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Lead [id=" + id + ", phoneNumber=" + phoneNumber + ", name=" + name + ", location=" + location
                + ", schooling=" + schooling + ", email=" + email + "]";
    }
}