package asia.axientstudio.axientclient;

import asia.axientstudio.axientclient.config.AxientConfig;
import asia.axientstudio.axientclient.features.GitHubUpdateChecker;
import asia.axientstudio.axientclient.gui.AxientMenuScreen;
import asia.axientstudio.axientclient.hud.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AxientClient implements ClientModInitializer {

    public static final String MOD_ID = "axientclient";
    public static final String MOD_NAME = "AxientClient";
    public static final String VERSION = "1.0.0";
    public static final String GITHUB_REPO = "AxientStudio/AxientClient";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AxientConfig config;
    public static HudManager hudManager;

    // Keybindings — registered via mixin (no KeyBindingHelper from fabric-api)
    public static KeyBinding openMenuKey;
    public static KeyBinding freelookKey;
    public static KeyBinding zoomKey;
    public static KeyBinding sneakToggleKey;
    public static KeyBinding sprintToggleKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[AxientClient] Initializing...");

        config = AxientConfig.load();
        hudManager = new HudManager();

        // Keybindings created here; injected via KeyBindingRegistryMixin
        openMenuKey    = new KeyBinding("key.axientclient.open_menu",    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.axientclient");
        freelookKey    = new KeyBinding("key.axientclient.freelook",     InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,           "category.axientclient");
        zoomKey        = new KeyBinding("key.axientclient.zoom",         InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C,           "category.axientclient");
        sneakToggleKey = new KeyBinding("key.axientclient.sneak_toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,     "category.axientclient");
        sprintToggleKey= new KeyBinding("key.axientclient.sprint_toggle",InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,     "category.axientclient");

        if (config.autoUpdate) {
            GitHubUpdateChecker.checkAsync();
        }

        LOGGER.info("[AxientClient] Initialized successfully!");
    }
}
