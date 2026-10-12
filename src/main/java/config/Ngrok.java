package config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.cdimascio.dotenv.Dotenv;

public class Ngrok {

    private static final String NGROK_API = "http://127.0.0.1:4040/api/tunnels";
    private static final Pattern PUBLIC_URL = Pattern.compile("\"public_url\"\\s*:\\s*\"(https://[^\"]+)\"");
    private static final Pattern ERROR_CODE = Pattern.compile("ERR_NGROK_\\d+");

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    // ngrok's "ERROR:" lines, kept so we can show why it stopped
    private static final List<String> errorLines = Collections.synchronizedList(new ArrayList<>());

    private static Process ngrokProcess;
    private static String publicUrl;

    // starts ngrok for the given port and returns the public https url
    public static synchronized String forwardPort(int port) throws Exception {
        if (ngrokProcess != null && ngrokProcess.isAlive() && publicUrl != null) {
            return publicUrl;
        }

        // read the token only from the .env file, dotenv.get() would prefer an
        // NGROK_AUTHTOKEN environment variable left over in the terminal
        String authToken = Dotenv.load()
                .entries(Dotenv.Filter.DECLARED_IN_ENV_FILE)
                .stream()
                .filter(entry -> entry.getKey().equals("NGROK_AUTHTOKEN"))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(null);

        if (authToken == null || authToken.isBlank()) {
            throw new Exception("NGROK_AUTHTOKEN is missing in .env");
        }

        // the token is passed with --authtoken because the NGROK_AUTHTOKEN env variable is
        // overridden by any authtoken saved in ngrok's own config file (ngrok.yml)
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ngrok", "http", String.valueOf(port),
                "--authtoken", authToken.trim(),
                "--log=stdout");
        processBuilder.redirectErrorStream(true);

        errorLines.clear();
        ngrokProcess = processBuilder.start();
        readOutput(ngrokProcess);

        // stop ngrok when the application exits
        Runtime.getRuntime().addShutdownHook(new Thread(Ngrok::stop));

        // ngrok takes a moment to open the tunnel, so keep asking its local api
        for (int i = 0; i < 30; i++) {
            if (!ngrokProcess.isAlive()) {
                // give the reader thread a moment to collect the last lines
                Thread.sleep(200);
                throw new Exception("ngrok exited with code " + ngrokProcess.exitValue() + " : " + getErrorReason());
            }

            try {
                String url = getPublicUrl();
                if (url != null) {
                    publicUrl = url;
                    System.out.println("Port " + port + " forwarded to : " + url);
                    return url;
                }
            } catch (Exception e) {
                // api not ready yet
            }

            Thread.sleep(500);
        }

        stop();
        throw new Exception("Could not get ngrok url for port " + port + " : " + getErrorReason());
    }

    public static String forwardPort() throws Exception {
        return forwardPort(8081);
    }

    // reads ngrok's output in the background so the process never blocks on a full buffer
    private static void readOutput(Process process) {
        Thread reader = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("ERROR:")) {
                        continue;
                    }
                    String message = line.substring("ERROR:".length()).trim();
                    // ngrok prints the authtoken in its error, never keep that line
                    if (message.isEmpty() || message.toLowerCase().contains("authtoken:")) {
                        continue;
                    }
                    errorLines.add(message);
                }
            } catch (Exception e) {
                // process ended
            }
        });
        reader.setDaemon(true);
        reader.start();
    }

    private static String getErrorReason() {
        synchronized (errorLines) {
            if (errorLines.isEmpty()) {
                return "no error message from ngrok";
            }

            String reason = errorLines.get(0);
            for (String line : errorLines) {
                Matcher matcher = ERROR_CODE.matcher(line);
                if (matcher.find()) {
                    return reason + " (" + matcher.group() + ")";
                }
            }
            return reason;
        }
    }

    private static String getPublicUrl() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(NGROK_API))
                .timeout(Duration.ofSeconds(2))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Matcher matcher = PUBLIC_URL.matcher(response.body());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static synchronized void stop() {
        if (ngrokProcess != null && ngrokProcess.isAlive()) {
            ngrokProcess.destroy();
        }
        publicUrl = null;
    }
}
