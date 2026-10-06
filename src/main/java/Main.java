import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.jetty.server.Server;

import langchain.Agent;

public class Main {
    public static void main(String[] args) throws Exception {
        Agent agent = new Agent();
        Scanner sc = new Scanner(System.in);

        StringBuilder str;
        while((str = new StringBuilder(sc.nextLine()))!=null){
            System.out.println(agent.chat(1l, str.toString()));
        }

        
        // while((String str = b))

    }
}
