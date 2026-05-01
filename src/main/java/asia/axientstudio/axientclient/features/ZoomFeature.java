package asia.axientstudio.axientclient.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomFeature {
    public static boolean active = false;
    public static float   zoomFov = 15.0f;
    public static int     keyScancode = GLFW.GLFW_KEY_C;

    public static void tick(MinecraftClient mc) {
        if (mc.player == null) return;
        active = InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyScancode);
    }

    public static float modifyFov(float original) {
        if (!active) return original;
        return zoomFov;
    }
}
