package asia.axientstudio.axientclient.config;

import asia.axientstudio.axientclient.AxientClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class AxientConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("axientclient.json");

    // ── Language ──
    public String language = "en"; // "en" or "vi"

    // ── Keystrokes ──
    public boolean keystrokesEnabled = true;

    // ── Coords & Biome ──
    public boolean coordsEnabled = true;

    // ── SneakSprintToggle ──
    public boolean sneakToggleEnabled = false;
    public boolean sprintToggleEnabled = false;

    // ── Freelook ──
    public boolean freelookEnabled = true;

    // ── Gamma ──
    public boolean gammaEnabled = false;
    public double gammaValue = 10.0; // 1000%

    // ── Shader Warning ──
    public boolean shaderWarningEnabled = true;

    // ── Quick Server Switch ──
    public boolean quickServerEnabled = true;
    public String quickServerAddress = "";

    // ── Mod Detector ──
    public boolean modDetectorEnabled = true;

    // ── Armor Hub ──
    public boolean armorHubEnabled = true;

    // ── Inventory Hub ──
    public boolean inventoryHubEnabled = true;

    // ── GitHub Update ──
    public boolean autoUpdate = true;

    // ── Fire & Shield Height ──
    public boolean fireHeightEnabled = false;
    public float fireHeightOffset = 0.0f;
    public boolean shieldHeightEnabled = false;
    public float shieldHeightOffset = 0.0f;

    // ── Weapon Size & Swing Speed ──
    public boolean weaponSizeEnabled = false;
    public float weaponScale = 1.0f;
    public boolean weaponSwingEnabled = false;
    public float weaponSwingSpeed = 1.0f;

    // ── Scoreboard ──
    public boolean scoreboardEnabled = true;
    public float scoreboardScale = 1.0f;   // 0 = hidden
    public boolean scoreboardVisible = true;

    // ── Ping ──
    public boolean pingEnabled = true;

    // ── TotemCount ──
    public boolean totemCountEnabled = true;

    // ── Zoom ──
    public boolean zoomEnabled = true;
    public float zoomFov = 15.0f;

    // ── Position Hub ──
    public boolean positionHubEnabled = true;

    // ──────────────────────────────────────────
    public static AxientConfig load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                AxientConfig cfg = GSON.fromJson(reader, AxientConfig.class);
                if (cfg != null) return cfg;
            } catch (Exception e) {
                AxientClient.LOGGER.error("[AxientClient] Failed to load config, using defaults", e);
            }
        }
        AxientConfig def = new AxientConfig();
        def.save();
        return def;
    }

    public void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(this, writer);
        } catch (Exception e) {
            AxientClient.LOGGER.error("[AxientClient] Failed to save config", e);
        }
    }
}
