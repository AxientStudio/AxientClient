package asia.axientstudio.axientclient.features;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubUpdateChecker {

    private static final String API_URL =
            "https://api.github.com/repos/" + AxientClient.GITHUB_REPO + "/releases/latest";

    public static void checkAsync() {
        Thread t = new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github+json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() != 200) return;

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                String json = sb.toString();
                // Simple parse: find "tag_name":"v1.2.3"
                String tagName = extractJsonString(json, "tag_name");
                if (tagName == null) return;

                String remote = tagName.startsWith("v") ? tagName.substring(1) : tagName;
                String local = AxientClient.VERSION;

                if (!remote.equals(local)) {
                    scheduleChat(String.format(
                            "§a[AxientClient] Update available: v%s → Run /axient update or visit GitHub.", remote));
                } else {
                    AxientClient.LOGGER.info("[AxientClient] Up to date (v{})", local);
                }
            } catch (Exception e) {
                AxientClient.LOGGER.warn("[AxientClient] Could not check for updates: {}", e.getMessage());
            }
        }, "AxientClient-UpdateChecker");
        t.setDaemon(true);
        t.start();
    }

    private static void scheduleChat(String message) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.execute(() -> {
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal(message), false);
            }
        });
    }

    private static String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }
}
