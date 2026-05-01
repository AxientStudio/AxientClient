package asia.axientstudio.axientclient;

import asia.axientstudio.axientclient.config.AxientConfig;
import asia.axientstudio.axientclient.gui.RShiftScreen;
import asia.axientstudio.axientclient.hud.HudManager;
import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AxientClient implements ClientModInitializer {
    public static final String MOD_ID = "axientclient";
    public static final String VERSION = "1.0.1";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static AxientConfig config;
    public static HudManager   hudManager;
    public static KeyBinding   openMenuKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[AxientClient] Initializing...");
        config     = AxientConfig.load();
        hudManager = new HudManager();
        config.applyToFeatures();

        openMenuKey = new KeyBinding("key.axientclient.menu",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "AxientClient");

        LOGGER.info("[AxientClient] v{} initialized!", VERSION);
    }
}
