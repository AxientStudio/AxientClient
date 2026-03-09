package asia.axientstudio.axientclient.hud;

import asia.axientstudio.axientclient.AxientClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

public class HudManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path HUD_POS_PATH = FabricLoader.getInstance().getConfigDir().resolve("axientclient_hud.json");

    public boolean dragMode = false;
    public String draggingElement = null;
    private int dragOffsetX, dragOffsetY;

    // HUD element positions: id -> [x, y]
    private final Map<String, int[]> positions = new LinkedHashMap<>();

    // Default positions
    private static final Map<String, int[]> DEFAULTS = new LinkedHashMap<>();
    static {
        DEFAULTS.put("keystrokes",     new int[]{10, 100});
        DEFAULTS.put("coords",         new int[]{10, 10});
        DEFAULTS.put("armor_hub",      new int[]{10, 60});
        DEFAULTS.put("inventory_hub",  new int[]{10, 200});
        DEFAULTS.put("ping",           new int[]{10, 30});
        DEFAULTS.put("totem_count",    new int[]{10, 130});
        DEFAULTS.put("position_hub",   new int[]{10, 160});
    }

    public HudManager() {
        loadPositions();
    }

    public int[] getPos(String id) {
        return positions.getOrDefault(id, DEFAULTS.getOrDefault(id, new int[]{10, 10}));
    }

    public void setPos(String id, int x, int y) {
        positions.put(id, new int[]{x, y});
    }

    public void savePositions() {
        try (Writer writer = new FileWriter(HUD_POS_PATH.toFile())) {
            GSON.toJson(positions, writer);
        } catch (Exception e) {
            AxientClient.LOGGER.error("[AxientClient] Failed to save HUD positions", e);
        }
    }

    private void loadPositions() {
        // Start with defaults
        DEFAULTS.forEach((k, v) -> positions.put(k, v.clone()));

        if (HUD_POS_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(HUD_POS_PATH.toFile())) {
                Type type = new TypeToken<Map<String, int[]>>(){}.getType();
                Map<String, int[]> loaded = GSON.fromJson(reader, type);
                if (loaded != null) positions.putAll(loaded);
            } catch (Exception e) {
                AxientClient.LOGGER.error("[AxientClient] Failed to load HUD positions", e);
            }
        }
    }

    // Drag logic
    public void startDrag(String id, int mouseX, int mouseY) {
        draggingElement = id;
        int[] pos = getPos(id);
        dragOffsetX = mouseX - pos[0];
        dragOffsetY = mouseY - pos[1];
    }

    public void updateDrag(int mouseX, int mouseY) {
        if (draggingElement != null) {
            setPos(draggingElement, mouseX - dragOffsetX, mouseY - dragOffsetY);
        }
    }

    public void stopDrag() {
        draggingElement = null;
    }

    public Set<String> getElementIds() {
        return positions.keySet();
    }
}
