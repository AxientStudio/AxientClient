package asia.axientstudio.axientclient.config;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.GammaFeature;
import asia.axientstudio.axientclient.features.SneakSprintToggle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;

public class AxientConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("axientclient.json");

    // Modules
    public boolean keystrokesEnabled    = true;
    public boolean coordsEnabled        = true;
    public boolean pingEnabled          = true;
    public boolean totemCountEnabled    = true;
    public boolean inventoryHubEnabled  = true;
    public boolean armorHubEnabled      = true;
    public boolean sneakSprintEnabled   = true;
    public boolean compassBarEnabled    = true;
    public boolean freelookEnabled      = true;
    public boolean zoomEnabled          = true;
    public boolean gammaEnabled         = false;

    // Settings - Overlays
    public boolean lowFireEnabled       = false;
    public float   lowFireHeight        = 0.5f;
    public boolean lowShieldEnabled     = false;
    public float   lowShieldHeight      = 0.5f;
    public boolean weaponSizeEnabled    = false;
    public float   weaponX              = 0.0f;
    public float   weaponY              = 0.0f;
    public float   weaponScale          = 1.0f;

    // Settings - Utilities
    public boolean quickServerEnabled   = true;
    public String  quickServerAddress   = "";
    public boolean shaderWarningEnabled = true;
    public boolean autoUpdate           = true;

    // Module configs
    public String  gammaMode            = "GAMMA";   // GammaFeature.Mode name
    public double  gammaValue           = 10.0;
    public String  sprintMode           = "HOLD";    // SneakSprintToggle.SprintMode name
    public String  sneakMode            = "HOLD";
    public int     freelookKey          = 344;        // GLFW_KEY_LEFT_ALT
    public int     zoomKey              = 67;         // GLFW_KEY_C
    public int     compassBgColor       = 0xAA000000; // ARGB
    public boolean zoomEnabled2         = true;

    public static AxientConfig load() {
        if (PATH.toFile().exists()) {
            try (Reader r = new FileReader(PATH.toFile())) {
                AxientConfig c = GSON.fromJson(r, AxientConfig.class);
                if (c != null) return c;
            } catch (Exception e) { AxientClient.LOGGER.error("Config load fail", e); }
        }
        AxientConfig def = new AxientConfig();
        def.save();
        return def;
    }

    public void save() {
        try (Writer w = new FileWriter(PATH.toFile())) { GSON.toJson(this, w); }
        catch (Exception e) { AxientClient.LOGGER.error("Config save fail", e); }
    }

    public void applyToFeatures() {
        try { GammaFeature.mode = GammaFeature.Mode.valueOf(gammaMode); } catch (Exception ignored) {}
        GammaFeature.gammaValue = gammaValue;
        try { SneakSprintToggle.sprintMode = SneakSprintToggle.SprintMode.valueOf(sprintMode); } catch (Exception ignored) {}
        try { SneakSprintToggle.sneakMode  = SneakSprintToggle.SneakMode.valueOf(sneakMode);   } catch (Exception ignored) {}
        asia.axientstudio.axientclient.features.FreelookFeature.keyScancode = freelookKey;
        asia.axientstudio.axientclient.features.ZoomFeature.keyScancode = zoomKey;
        asia.axientstudio.axientclient.features.ZoomFeature.zoomFov = 15.0f;
    }

    public void syncFromFeatures() {
        gammaMode  = GammaFeature.mode.name();
        gammaValue = GammaFeature.gammaValue;
        sprintMode = SneakSprintToggle.sprintMode.name();
        sneakMode  = SneakSprintToggle.sneakMode.name();
    }
}
