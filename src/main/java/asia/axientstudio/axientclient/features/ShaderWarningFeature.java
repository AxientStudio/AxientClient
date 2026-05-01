package asia.axientstudio.axientclient.features;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class ShaderWarningFeature {
    public static boolean shadersDetected = false;
    public static void tick(MinecraftClient mc) {
        shadersDetected = FabricLoader.getInstance().isModLoaded("iris")
                       || FabricLoader.getInstance().isModLoaded("oculus");
    }
}
