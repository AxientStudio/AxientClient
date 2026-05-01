package asia.axientstudio.axientclient.features;

import net.minecraft.client.MinecraftClient;

public class GammaFeature {
    public enum Mode { OFF, GAMMA, NIGHTVISION }
    public static Mode mode = Mode.OFF;
    public static double gammaValue = 10.0;

    // Night vision: we simulate by overriding the brightness similarly but
    // also hook into the light map via mixin to boost ambient light
    public static boolean isGammaActive() { return mode == Mode.GAMMA || mode == Mode.NIGHTVISION; }
    public static double getEffectiveGamma() {
        return isGammaActive() ? gammaValue : -1;
    }
    public static void cycle() {
        mode = Mode.values()[(mode.ordinal() + 1) % Mode.values().length];
    }
    public static String modeLabel() {
        return switch (mode) {
            case OFF -> "Off";
            case GAMMA -> "Gamma 1000%";
            case NIGHTVISION -> "Night Vision";
        };
    }
}
