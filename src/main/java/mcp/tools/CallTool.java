package mcp.tools;

import java.util.List;
import java.util.Map;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import reactor.core.publisher.Mono;

public class CallTool {

    public McpSchema.Tool getTool() {

        Map<String, Object> schema = Map.of(
            "type", "object",
            "properties", Map.of(
                "phoneNumber", Map.of("type", "string")
            ),
            "required", List.of("phoneNumber")
        );

        return McpSchema.Tool.builder("make_call", schema)
            .description("Make an outbound phone call")
            .build();
    }

    public Mono<McpSchema.CallToolResult> makeCall(
        McpAsyncServerExchange exchange,
        McpSchema.CallToolRequest request
    ) {

        String number = (String) request.arguments()
            .get("phoneNumber");

        String message = "Calling " + number;

        return Mono.just(
            McpSchema.CallToolResult.builder()
                .content(List.of(
                    new McpSchema.TextContent(message)
                ))
                .build()
        );
    }
}