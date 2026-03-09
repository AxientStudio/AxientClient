package asia.axientstudio.axientclient.features;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.MinecraftClient;

public class ZoomFeature {

    public static boolean zooming = false;
    private static float originalFov = 70f;
    private static float currentZoom;
    private static final float SMOOTH_SPEED = 0.15f;

    public static void tick(MinecraftClient mc) {
        if (!AxientClient.config.zoomEnabled) {
            zooming = false;
            return;
        }
        zooming = AxientClient.zoomKey.isPressed();
        if (zooming) {
            currentZoom = AxientClient.config.zoomFov;
        }
    }

    /**
     * Called from GameRendererMixin to modify FOV.
     */
    public static float modifyFov(float fov) {
        if (!zooming) {
            originalFov = fov;
            return fov;
        }
        // Smooth zoom
        originalFov += (currentZoom - originalFov) * SMOOTH_SPEED;
        return originalFov;
    }
}
