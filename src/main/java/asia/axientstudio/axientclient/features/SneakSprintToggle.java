package asia.axientstudio.axientclient.features;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.MinecraftClient;

public class SneakSprintToggle {

    private static boolean sneakOn = false;
    private static boolean sprintOn = false;

    private static boolean sneakWasPressed = false;
    private static boolean sprintWasPressed = false;

    public static void tick(MinecraftClient mc) {
        if (mc.player == null) return;

        // Sneak toggle
        if (AxientClient.config.sneakToggleEnabled) {
            boolean pressed = AxientClient.sneakToggleKey.isPressed();
            if (pressed && !sneakWasPressed) {
                sneakOn = !sneakOn;
            }
            sneakWasPressed = pressed;
            if (sneakOn) {
                mc.options.sneakKey.setPressed(true);
            }
        }

        // Sprint toggle
        if (AxientClient.config.sprintToggleEnabled) {
            boolean pressed = AxientClient.sprintToggleKey.isPressed();
            if (pressed && !sprintWasPressed) {
                sprintOn = !sprintOn;
            }
            sprintWasPressed = pressed;
            if (sprintOn && mc.player.isOnGround()) {
                mc.player.setSprinting(true);
            }
        }
    }

    public static boolean isSneakOn() { return sneakOn; }
    public static boolean isSprintOn() { return sprintOn; }
}
