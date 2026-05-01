package asia.axientstudio.axientclient.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class SneakSprintToggle {

    public enum SprintMode { HOLD, TOGGLE, VANILLA }
    public enum SneakMode  { HOLD, TOGGLE }

    public static SprintMode sprintMode = SprintMode.HOLD;
    public static SneakMode  sneakMode  = SneakMode.HOLD;

    public static boolean sprintToggled = false;
    public static boolean sneakToggled  = false;

    private static long lastSprintTap = 0;
    private static long lastForwardTap = 0;
    private static boolean sprintWas = false;
    private static boolean forwardWas = false;
    private static boolean sneakWas = false;
    private static final long DTAP = 300;

    public static void tick(MinecraftClient mc) {
        if (mc.player == null) return;
        long now = System.currentTimeMillis();

        boolean sprintDown  = mc.options.sprintKey.isPressed();
        boolean forwardDown = mc.options.forwardKey.isPressed();
        boolean sneakDown   = mc.options.sneakKey.isPressed();

        // Sprint: double-tap sprint key = TOGGLE mode
        if (sprintDown && !sprintWas) {
            if (now - lastSprintTap < DTAP && sprintMode != SprintMode.TOGGLE) {
                sprintMode = SprintMode.TOGGLE;
                sprintToggled = true;
            } else if (sprintMode == SprintMode.TOGGLE) {
                sprintToggled = !sprintToggled;
            }
            lastSprintTap = now;
        }

        // Sprint: double-tap W = VANILLA mode
        if (forwardDown && !forwardWas) {
            if (now - lastForwardTap < DTAP && sprintMode == SprintMode.VANILLA) {
                sprintToggled = !sprintToggled;
            }
            lastForwardTap = now;
        }
        if (!forwardDown && sprintMode == SprintMode.VANILLA) sprintToggled = false;

        // Hold sprint key = HOLD mode (resets toggle)
        if (sprintDown && sprintMode == SprintMode.TOGGLE && !sprintToggled) {
            // holding without toggle = treat as hold
        }

        if (sprintMode == SprintMode.TOGGLE && sprintToggled) {
            KeyBinding.setKeyPressed(mc.options.sprintKey.getDefaultKey(), true);
        }

        // Sneak toggle: press sneak key once = toggle
        if (sneakMode == SneakMode.TOGGLE) {
            if (sneakDown && !sneakWas) sneakToggled = !sneakToggled;
            if (sneakToggled) KeyBinding.setKeyPressed(mc.options.sneakKey.getDefaultKey(), true);
        }

        sprintWas  = sprintDown;
        forwardWas = forwardDown;
        sneakWas   = sneakDown;
    }

    public static String getStatusText() {
        String s = switch (sprintMode) {
            case HOLD    -> "§7Sprint §fHold";
            case TOGGLE  -> sprintToggled ? "§aSprint §fToggle" : "§7Sprint §fToggle";
            case VANILLA -> "§7Sprint §fVanilla";
        };
        if (sneakMode == SneakMode.TOGGLE)
            s += sneakToggled ? "  §bSneak §fToggle" : "  §7Sneak §fToggle";
        return s;
    }
}
