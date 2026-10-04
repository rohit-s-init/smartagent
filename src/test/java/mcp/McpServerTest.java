package mcp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class McpServerTest {
    
    @Test 
    void testStart(){
        McpServer server = new McpServer();

        Assertions.assertDoesNotThrow(()->{
            server.start();
            server.stop();
        });

    }
}
