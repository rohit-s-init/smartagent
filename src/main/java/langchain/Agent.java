package langchain;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class Agent {
    OpenAiChatModel model;

    public String chat(String message){
        return model.chat(message);
    }

    public Agent() {
        model = OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(System.getenv("GROQ_API"))
                .modelName("openai/gpt-oss-120b")
                .build();
    }
}
