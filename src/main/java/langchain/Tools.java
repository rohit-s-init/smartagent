
package langchain;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.invocation.InvocationParameters;

public class Tools {

    @Tool("Get information about the current student")
    public String getLead(InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing getLead: " + leadId);

        return "Lead ID: " + leadId
                + ", Name: Rohit, Schooling: 12th Science, "
                + "Interested course: B.Tech CSE";
    }

    @Tool("Get information about a college course")
    public String getCourseDetails(
            @P("Name of the course") String course) {

        System.out.println("Executing getCourseDetails: " + course);

        return course + " is a four-year undergraduate program.";
    }

    @Tool("Get admission fees for a course")
    public String getAdmissionFees(
            @P("Name of the course") String course) {

        System.out.println("Executing getAdmissionFees: " + course);

        if (course.equalsIgnoreCase("B.Tech CSE")) {
            return "The test annual fee is Rs. 2,50,000.";
        }

        return "Fee information is unavailable.";
    }

    @Tool("Check whether the current student meets the test admission criteria")
    public String checkEligibility(
            @P("Student's 12th percentage") double percentage,
            @P("Name of the course") String course,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Checking eligibility for lead: " + leadId);

        if (percentage >= 50) {
            return "Lead " + leadId
                    + " meets the simulated eligibility criteria for "
                    + course;
        }

        return "Lead " + leadId
                + " does not meet the simulated eligibility criteria for "
                + course;
    }

    @Tool("Schedule a test follow-up for the current student")
    public String scheduleFollowUp(
            @P("Follow-up date in YYYY-MM-DD format") String date,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing scheduleFollowUp for lead: " + leadId);

        return "Test follow-up recorded for lead "
                + leadId + " on " + date;
    }

    @Tool("Update the current student's preferred course")
    public String updateCoursePreference(
            @P("Student's preferred course") String course,
            InvocationParameters parameters) {

        Long leadId = parameters.get("leadId");

        System.out.println("Executing updateCoursePreference for lead: " + leadId);

        return "Test preference updated to "
                + course + " for lead " + leadId;
    }
}
