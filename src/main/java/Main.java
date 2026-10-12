import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.jetty.server.Server;

import config.HttpServer;
import config.Ngrok;
import dto.LeadDTO;
import langchain.Agent;
import repository.LeadRepository;
import service.Twillio;

public class Main {
    public static void main(String[] args) throws Exception {

        HttpServer server = new HttpServer();
        server.start();

        Agent agent = new Agent();

        Scanner sc = new Scanner(System.in);

        LeadDTO lead = new LeadRepository().getLeadByPhoneNumber("1234567890");

        String str1 = agent.dynamicPromptProvider(lead.getId());
        System.out.println(str1);

        StringBuilder str;
        while ((str = new StringBuilder(sc.nextLine())) != null) {
            System.out.println(agent.chat(lead.getId(), str.toString()));
        }

        // Twillio.makeCall("8459781390");

        // var url = Ngrok.forwardPort();
        // System.out.println(url);

        // Twillio.makeCall("8459781390", url+"/twillio/webhook");

        // while((String str = b))

    }
}
