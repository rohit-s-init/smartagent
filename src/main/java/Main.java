import config.HttpServer;
import langchain.Agent;

public class Main {
    public static void main(String[] args) throws Exception {
        Agent agent = new Agent();
        String message = agent.chat("Hello This is a test message from langchain java");
        System.out.println(message);
    }
}
