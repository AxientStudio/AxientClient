package asia.axientstudio.axientclient.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class FreelookFeature {

    public static boolean active = false;
    public static int keyScancode = GLFW.GLFW_KEY_LEFT_ALT;

    private static float savedYaw   = 0;
    private static float savedPitch = 0;
    private static Perspective savedPerspective = Perspective.FIRST_PERSON;

    public static void tick(MinecraftClient mc) {
        if (mc.player == null) return;

        boolean pressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), keyScancode);

        if (pressed && !active) {
            active = true;
            savedYaw   = mc.player.getYaw();
            savedPitch = mc.player.getPitch();
            savedPerspective = mc.options.getPerspective();
            if (savedPerspective == Perspective.FIRST_PERSON) {
                mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            }
        } else if (!pressed && active) {
            active = false;
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
            if (savedPerspective == Perspective.FIRST_PERSON) {
                mc.options.setPerspective(Perspective.FIRST_PERSON);
            }
        }

        if (active && mc.player != null) {
            mc.player.setYaw(savedYaw);
            mc.player.setPitch(savedPitch);
            mc.player.bodyYaw = savedYaw;
            mc.player.headYaw = savedYaw;
        }
    }
}
