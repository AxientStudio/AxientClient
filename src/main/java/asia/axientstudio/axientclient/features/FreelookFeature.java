package asia.axientstudio.axientclient.features;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.MinecraftClient;

public class FreelookFeature {

    public static boolean active = false;
    private static float savedYaw, savedPitch;

    public static void tick(MinecraftClient mc) {
        if (!AxientClient.config.freelookEnabled) {
            if (active) stopFreelook(mc);
            return;
        }
        boolean pressed = AxientClient.freelookKey.isPressed();
        if (pressed && !active) {
            startFreelook(mc);
        } else if (!pressed && active) {
            stopFreelook(mc);
        }
    }

    private static void startFreelook(MinecraftClient mc) {
        active = true;
        if (mc.player != null) {
            savedYaw = mc.player.getYaw();
            savedPitch = mc.player.getPitch();
        }
    }

    private static void stopFreelook(MinecraftClient mc) {
        active = false;
        if (mc.player != null) {
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
        }
    }
}
