package langchain;

import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

    String chat(
        @MemoryId Long conversationId,
        @UserMessage String message,
        InvocationParameters invocationParameters
    );
}