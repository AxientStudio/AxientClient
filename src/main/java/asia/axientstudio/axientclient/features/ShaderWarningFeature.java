package asia.axientstudio.axientclient.features;

import asia.axientstudio.axientclient.AxientClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ShaderWarningFeature {

    private static boolean warned = false;

    public static void tick(MinecraftClient mc) {
        if (!AxientClient.config.shaderWarningEnabled) {
            warned = false;
            return;
        }
        if (mc.player == null) return;

        boolean irisPresent = FabricLoader.getInstance().isModLoaded("iris")
                || FabricLoader.getInstance().isModLoaded("oculus");
        boolean gammaEnabled = AxientClient.config.gammaEnabled;

        if (irisPresent && gammaEnabled) {
            if (!warned) {
                warned = true;
                mc.player.sendMessage(
                        Text.literal("§c[AxientClient] WARNING: Iris Shaders detected while Gamma is active! Disable one to avoid visual glitches."),
                        false
                );
            }
        } else {
            warned = false;
        }
    }
}
