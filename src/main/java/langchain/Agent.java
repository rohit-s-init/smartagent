package langchain;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import io.github.cdimascio.dotenv.Dotenv;

public class Agent {

    private OpenAiChatModel model;
    private Assistant assistant;

    public Agent() {

        Dotenv dotenv = Dotenv.load();

        model = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(dotenv.get("GROQ_API"))
                .modelName("openai/gpt-oss-120b")
                .build();

        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .tools(new Tools())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .build();
    }

    public String chat(Long leadId, String message) {
        InvocationParameters invocationParameters = InvocationParameters.from("leadId", leadId);
        return assistant.chat(leadId, message, invocationParameters);
    }
}