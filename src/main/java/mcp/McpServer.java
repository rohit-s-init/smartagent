package mcp;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

public class McpServer {

    private McpAsyncServer server;

    public McpAsyncServer getServer(){
        return server;
    }

    public void start() {

        var transport = new StdioServerTransportProvider(
            McpJsonDefaults.getMapper()
        );

        server = io.modelcontextprotocol.server.McpServer.async(transport)
            .serverInfo("twilio-caller", "1.0.0")
            .capabilities(
                McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .build()
            )
            .build();
    }

    

    public void stop() {
        if (server != null) {
            server.close();
        }
    }
}
