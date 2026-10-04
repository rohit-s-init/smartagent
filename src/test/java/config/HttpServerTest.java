package config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpServerTest {
    
    @Test 
    public void checkStartStop(){
        Assertions.assertDoesNotThrow(()->{
            HttpServer server = new HttpServer();
            server.start();
            server.stop();
        });
    }
}
