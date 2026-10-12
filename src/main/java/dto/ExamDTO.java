package dto;

import model.Exam;

public class ExamDTO {

    private Long id;
    private String examName;
    private Double score;
    private String scoreType;

    public ExamDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public ExamDTO(Exam exam) {
        if (exam == null) return;
        this.id = exam.getId();
        this.examName = exam.getExamName();
        this.score = exam.getScore();
        this.scoreType = exam.getScoreType();
    }
}
