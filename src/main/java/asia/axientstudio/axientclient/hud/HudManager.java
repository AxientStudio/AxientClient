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
    private static final Path POS_PATH   = FabricLoader.getInstance().getConfigDir().resolve("axientclient_hud.json");
    private static final Path SCALE_PATH = FabricLoader.getInstance().getConfigDir().resolve("axientclient_hud_scale.json");

    public boolean dragMode = false;
    public String draggingElement = null;
    private int dragOffsetX, dragOffsetY;

    private final Map<String, int[]>  positions = new LinkedHashMap<>();
    private final Map<String, Float>  scales    = new LinkedHashMap<>();

    private static final Map<String, int[]> DEFAULT_POS = new LinkedHashMap<>();
    static {
        DEFAULT_POS.put("keystrokes",    new int[]{10, 100});
        DEFAULT_POS.put("coords",        new int[]{10, 10});
        DEFAULT_POS.put("armor_hub",     new int[]{10, 60});
        DEFAULT_POS.put("inventory_hub", new int[]{10, 200});
        DEFAULT_POS.put("ping",          new int[]{10, 30});
        DEFAULT_POS.put("totem_count",   new int[]{10, 130});
    }

    public HudManager() { load(); }

    public int[]  getPos(String id)   { return positions.getOrDefault(id, DEFAULT_POS.getOrDefault(id, new int[]{10,10})); }
    public void   setPos(String id, int x, int y) { positions.put(id, new int[]{x, y}); }
    public float  getScale(String id) { return scales.getOrDefault(id, 1.0f); }
    public void   setScale(String id, float s) { scales.put(id, Math.max(0.5f, Math.min(3.0f, s))); }

    public void savePositions() {
        save(POS_PATH,   positions);
        save(SCALE_PATH, scales);
    }

    private void load() {
        DEFAULT_POS.forEach((k, v) -> positions.put(k, v.clone()));
        DEFAULT_POS.keySet().forEach(k -> scales.put(k, 1.0f));
        loadMap(POS_PATH,   new TypeToken<Map<String,int[]>>(){}.getType(),  positions);
        loadMap(SCALE_PATH, new TypeToken<Map<String,Float>>(){}.getType(),  scales);
    }

    @SuppressWarnings("unchecked")
    private <T> void loadMap(Path p, Type type, Map<String,T> target) {
        if (!p.toFile().exists()) return;
        try (Reader r = new FileReader(p.toFile())) {
            Map<String,T> m = GSON.fromJson(r, type);
            if (m != null) target.putAll(m);
        } catch (Exception e) { AxientClient.LOGGER.error("HUD load fail", e); }
    }

    private void save(Path p, Object obj) {
        try (Writer w = new FileWriter(p.toFile())) { GSON.toJson(obj, w); }
        catch (Exception e) { AxientClient.LOGGER.error("HUD save fail", e); }
    }

    public void startDrag(String id, int mx, int my) {
        draggingElement = id;
        int[] pos = getPos(id);
        dragOffsetX = mx - pos[0];
        dragOffsetY = my - pos[1];
    }
    public void updateDrag(int mx, int my) {
        if (draggingElement != null) setPos(draggingElement, mx - dragOffsetX, my - dragOffsetY);
    }
    public void stopDrag() { draggingElement = null; }
}
