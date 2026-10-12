package langchain;

import java.util.List;
import java.util.function.Function;

import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dto.ChatDTO;
import dto.ExamDTO;
import dto.FeedbackDTO;
import dto.LeadDTO;
import dto.NoteDTO;
import io.github.cdimascio.dotenv.Dotenv;
import repository.ChatRepository;
import repository.ExamRepository;
import repository.FeedbackRepository;
import repository.LeadRepository;
import repository.NoteRepository;

public class Agent {

    public static final String SYSTEM_PROMPT = StaticPrompt.PROMPT;

    private OpenAiChatModel model;
    private Assistant assistant;

    public Agent() {

        Dotenv dotenv = Dotenv.load();

        model = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(dotenv.get("GROQ_API"))
                .modelName("openai/gpt-oss-120b")
                .reasoningEffort("medium")
                .parallelToolCalls(true)
                .build();

        assistant = AiServices.builder(Assistant.class)
                .systemMessageProvider((leadId) -> SYSTEM_PROMPT + "\nthe user details are as follows: \n\n"
                        + dynamicPromptProvider((Long) leadId))
                .chatModel(model)
                .tools(new Tools())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(25))
                .build();
    }

    private final ChatRepository chatRepository = new ChatRepository();

    public String chat(Long leadId, String message) {
        saveSafely(leadId, "user", message);

        InvocationParameters invocationParameters = InvocationParameters.from("leadId", leadId);
        String reply = assistant.chat(leadId, message, invocationParameters);

        saveSafely(leadId, "assistant", reply);
        return reply;
    }

    // a database problem must never break the live call
    private void saveSafely(Long leadId, String role, String message) {
        try {
            chatRepository.saveMessage(leadId, role, message);
        } catch (Exception e) {
            System.out.println("Could not save chat for lead " + leadId + " : " + e.getMessage());
        }
    }

    public String dynamicPromptProvider(Long userId) {
        LeadRepository leadRepository = new LeadRepository();
        System.out.println("leadid is : " + userId);
        LeadDTO lead = leadRepository.getLeadById(userId);

        NoteRepository noteRepository = new NoteRepository();
        NoteDTO note = noteRepository.getNoteDTOByLeadId(userId);

        FeedbackRepository feedbackRepo = new FeedbackRepository();
        List<FeedbackDTO> feedbacks = feedbackRepo.getAllFeedbacksDTOByLeadId(userId);
        String feedbackMessages = feedbacks.stream().map(new Function<FeedbackDTO, String>() {
            @Override
            public String apply(FeedbackDTO feedback) {
                return feedback.getMessage();
            }
        }).toList().toString();

        ExamRepository examRepo = new ExamRepository();
        List<ExamDTO> exams = examRepo.getExamsByLeadId(userId);
        String examDetails = exams.stream().map(new Function<ExamDTO, String>() {
            @Override
            public String apply(ExamDTO exam) {
                return exam.getExamName() + " : " + exam.getScore() + " " + exam.getScoreType();
            }
        }).toList().toString();

        StringBuilder dynPrompt = new StringBuilder();

        dynPrompt.append("""

                the details of the user in the database are as follows :

                name : """).append(lead.getName()).append("\n")
                .append("location : ").append(lead.getLocation()).append("\n")
                .append("email : ").append(lead.getEmail()).append("\n")
                .append("phone number : ").append(lead.getPhoneNumber()).append("\n")
                .append("schooling : ").append(lead.getSchooling()).append("\n");

        if (note != null) {
            dynPrompt.append(" \n ---------------------------")
                    .append("note is as follows : ").append("\n")
                    .append(note.getContent()).append("\n");

        }
        dynPrompt.append("\n----------------------------")
                .append("feedbacks are as follows : ").append("\n")
                .append(feedbackMessages).toString();

        dynPrompt.append("\n----------------------------")
                .append("exams are as follows : ").append("\n")
                .append(examDetails).append("\n");

        return dynPrompt.toString();

    }

}