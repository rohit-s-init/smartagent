package langchain;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.invocation.InvocationParameters;
import dto.GradeDTO;
import dto.LeadDTO;
import dto.ScheduleDAO;
import model.Feedback;
import model.Note;
import repository.ExamRepository;
import repository.FeedbackRepository;
import repository.GradeRepository;
import repository.LeadRepository;
import repository.NoteRepository;
import repository.ScheduleRepository;

public class Tools {

    // getLead, updateLead, getResourceDetails, scheduleFollowUp, makeNote, getNote,
    // getFeedback, addFeedback, addExam

    private final LeadRepository leadRepository = new LeadRepository();
    private final ScheduleRepository scheduleRepository = new ScheduleRepository();
    private final NoteRepository noteRepository = new NoteRepository();
    private final FeedbackRepository feedbackRepository = new FeedbackRepository();
    private final ExamRepository examRepository = new ExamRepository();
    private final GradeRepository gradeRepository = new GradeRepository();

    // @Tool("Get information about the current student")
    // public String getLead(InvocationParameters parameters) {

    // Long leadId = parameters.get("leadId");

    // System.out.println("Executing getLead: " + leadId);

    // try {
    // LeadDTO lead = leadRepository.getLeadById(leadId);

    // return "Lead ID: " + lead.getId()
    // + ", Name: " + lead.getName()
    // + ", Phone: " + lead.getPhoneNumber()
    // + ", Location: " + lead.getLocation()
    // + ", Schooling: " + lead.getSchooling()
    // + ", Email: " + lead.getEmail();
    // } catch (Exception e) {
    // return "Could not find student with ID: " + leadId;
    // }
    // }

    @Tool("Update information about the current student. Only pass the fields the student has provided.")
    public String updateLead(
            @P(value = "Student's name", required = false) String name,
            @P(value = "Student's location / city", required = false) String location,
            @P(value = "Student's schooling, e.g. 12th Science", required = false) String schooling,
            @P(value = "Student's email address", required = false) String email,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing updateLead: " + leadId);

        try {
            // make sure the lead exists before updating
            leadRepository.getLeadById(leadId);
        } catch (Exception e) {
            return "Could not find student with ID: " + leadId;
        }

        // empty or placeholder values from the ai are treated as "not provided"
        name = cleanValue(name);
        location = cleanValue(location);
        schooling = cleanValue(schooling);
        email = cleanValue(email);

        if (name == null && location == null && schooling == null && email == null) {
            return "No information provided to update for ID: " + leadId;
        }

        LeadDTO lead = new LeadDTO(leadId, null, name, location, schooling, email);
        leadRepository.update(lead);

        return "Lead information updated for ID: " + leadId;
    }

    // placeholder values the ai may send instead of leaving a field out
    private static final Set<String> PLACEHOLDER_VALUES = Set.of(
            "null", "none", "nil", "n/a", "na", "unknown", "not provided",
            "not specified", "not available", "undefined", "-", "string");

    private String cleanValue(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        if (trimmed.isEmpty() || PLACEHOLDER_VALUES.contains(trimmed.toLowerCase())) {
            return null;
        }

        return trimmed;
    }

    // no repository method to find a resource by name, so this is not executed
    // against the database
    @Tool("Get information about a resource")
    public String getResourceDetails(
            @P("Name of the resource") String resource) {

        System.out.println("Executing getResourceDetails: " + resource);

        return resource + " is a valuable resource for students.";
    }

    @Tool("Schedule a follow-up call for the current student")
    public String scheduleFollowUp(
            @P("Follow-up time in 24-hour HH:mm format, e.g. 14:30") String time,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing scheduleFollowUp for lead: " + leadId);

        try {
            LocalTime scheduledTime = LocalTime.parse(time);

            scheduleRepository.makeSchedule(leadId, scheduledTime);

            ScheduleDAO schedule = scheduleRepository.getScheduleByLeadId(leadId);

            return "Follow-up scheduled for lead "
                    + leadId + " at " + schedule.getScheduledTime();
        } catch (DateTimeParseException e) {
            return "Invalid time '" + time + "'. Use HH:mm format, e.g. 14:30";
        } catch (Exception e) {
            return "Could not schedule follow-up: " + e.getMessage();
        }
    }

    @Tool("Make a note for the current student")
    public String makeNote(
            @P("Content of the note") String content,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing makeNote for lead: " + leadId);

        try {
            noteRepository.makeNote(leadId, content);

            return "Note created for lead "
                    + leadId + ": " + content;
        } catch (Exception e) {
            return "Could not create note: " + e.getMessage();
        }
    }

    @Tool("Book an offline admission counselling appointment for the current student")
    public String bookAdmissionSlot(
            @P("Appointment date in YYYY-MM-DD format") String date,
            @P("Appointment time in HH:mm format") String time,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        // TODO: Check availability and persist the appointment.

        return "Booking not implemented yet";
    }

    // @Tool("Get the note for the current student")
    // public String getNote(InvocationParameters parameters) {

    // Long leadId = parameters.get("leadId");

    // System.out.println("Executing getNote for lead: " + leadId);

    // Note note = noteRepository.getNoteByLeadId(leadId);

    // if (note == null) {
    // return "No note found for lead " + leadId;
    // }

    // return "Note for lead " + leadId + ": " + note.getContent();
    // }

    @Tool("Get all feedbacks for the current student")
    public String getFeedback(InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing getFeedback for lead: " + leadId);

        List<Feedback> feedbacks = feedbackRepository.getAllFeedbacksByLeadId(leadId);

        if (feedbacks.isEmpty()) {
            return "No feedback found for lead " + leadId;
        }

        StringBuilder result = new StringBuilder("Feedbacks for lead " + leadId + " (newest first):");
        for (Feedback feedback : feedbacks) {
            result.append("\n- ").append(feedback.getMessage());
        }

        return result.toString();
    }

    @Tool("Add feedback for the current student")
    public String addFeedback(
            @P("Content of the feedback") String content,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing addFeedback for lead: " + leadId);

        try {
            feedbackRepository.makeFeedback(leadId, content);

            return "Feedback added for lead "
                    + leadId + ": " + content;
        } catch (Exception e) {
            return "Could not add feedback: " + e.getMessage();
        }
    }

    @Tool("Save competitive entrance exam results only, such as "
            + "JEE Main, MHT-CET, NEET or CUET. "
            + "Never use this tool for 10th or 12th board results.")
    public String addExam(
            @P("Name of the exam, e.g. JEE Main, MHT-CET, 12th board") String examName,
            @P("Score the student got in the exam") Double score,
            @P("Type of the score, e.g. percentile, percentage, marks") String scoreType,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing addExam for lead: " + leadId);

        try {
            examRepository.createExam(leadId, examName, score, scoreType);

            return "Exam added for lead "
                    + leadId + ": " + examName + " - " + score + " " + scoreType;
        } catch (Exception e) {
            return "Could not add exam: " + e.getMessage();
        }
    }

    @Tool("Save school academic results only, such as 10th or 12th "
            + "board examination marks. Never use this tool for "
            + "JEE Main, MHT-CET, NEET or other entrance exams.")
    public String addGrades(
            @P("Qualification, e.g. 10th maharashtra board, 12th maharashtra board, Diploma, Graduation") String qualification,
            @P("Percentage obtained, between 0 and 100. Convert CGPA to percentage before calling") Double percentage,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing addGrades for lead: " + leadId);

        try {
            GradeDTO grade = gradeRepository.saveOrUpdateGrade(leadId, qualification, percentage);

            return "Grade saved for lead "
                    + leadId + ": " + grade.getQualification() + " - " + grade.getPercentage() + "%";
        } catch (Exception e) {
            return "Could not save grade: " + e.getMessage();
        }
    }

    // @Tool("Get information about a college course")
    // public String getCourseDetails(
    // @P("Name of the course") String course) {

    // System.out.println("Executing getCourseDetails: " + course);

    // return course + " is a four-year undergraduate program.";
    // }

    // @Tool("Get admission fees for a course")
    // public String getAdmissionFees(
    // @P("Name of the course") String course) {

    // System.out.println("Executing getAdmissionFees: " + course);

    // if (course.equalsIgnoreCase("B.Tech CSE")) {
    // return "The test annual fee is Rs. 2,50,000.";
    // }

    // return "Fee information is unavailable.";
    // }

    // @Tool("Check whether the current student meets the test admission criteria")
    // public String checkEligibility(
    // @P("Student's 12th percentage") double percentage,
    // @P("Name of the course") String course,
    // InvocationParameters parameters) {

    // Long leadId = parameters.get("leadId");

    // System.out.println("Checking eligibility for lead: " + leadId);

    // if (percentage >= 50) {
    // return "Lead " + leadId
    // + " meets the simulated eligibility criteria for "
    // + course;
    // }

    // return "Lead " + leadId
    // + " does not meet the simulated eligibility criteria for "
    // + course;
    // }

    // @Tool("Schedule a test follow-up for the current student")
    // public String scheduleFollowUp(
    // @P("Follow-up date in YYYY-MM-DD format") String date,
    // InvocationParameters parameters) {

    // Long leadId = parameters.get("leadId");

    // System.out.println("Executing scheduleFollowUp for lead: " + leadId);

    // return "Test follow-up recorded for lead "
    // + leadId + " on " + date;
    // }

    // @Tool("Update the current student's preferred course")
    // public String updateCoursePreference(
    // @P("Student's preferred course") String course,
    // InvocationParameters parameters) {

    // Long leadId = parameters.get("leadId");

    // System.out.println("Executing updateCoursePreference for lead: " + leadId);

    // return "Test preference updated to "
    // + course + " for lead " + leadId;
    // }
}
