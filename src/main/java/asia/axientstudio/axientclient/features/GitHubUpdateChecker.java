package asia.axientstudio.axientclient.features;

import asia.axientstudio.axientclient.AxientClient;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GitHubUpdateChecker {
    public static String latestVersion = null;
    public static void checkAsync() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/AxientStudio/AxientClient/releases/latest"))
                    .header("Accept", "application/vnd.github+json").build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                String body = resp.body();
                int idx = body.indexOf("\"tag_name\"");
                if (idx >= 0) {
                    int s = body.indexOf("\"", idx + 11) + 1;
                    int e = body.indexOf("\"", s);
                    latestVersion = body.substring(s, e);
                }
            } catch (Exception ex) {
                AxientClient.LOGGER.warn("[AxientClient] Update check failed: {}", ex.getMessage());
            }
        }, "AxientClient-UpdateChecker").start();
    }
}
