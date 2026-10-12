package dto;

import java.time.LocalTime;

public class ScheduleDAO {

    private Long id;
    private LocalTime scheduledTime;
    private LeadDTO lead;

    public ScheduleDAO() {
    }

    public ScheduleDAO(Long id, LocalTime scheduledTime, LeadDTO lead) {
        this.id = id;
        this.scheduledTime = scheduledTime;
        this.lead = lead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LeadDTO getLead() {
        return lead;
    }

    public void setLead(LeadDTO lead) {
        this.lead = lead;
    }

    @Override
    public String toString() {
        return "ScheduleDAO [id=" + id + ", scheduledTime=" + scheduledTime + ", lead=" + lead + "]";
    }
}
