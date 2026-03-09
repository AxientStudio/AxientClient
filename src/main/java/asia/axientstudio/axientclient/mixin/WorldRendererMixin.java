package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;

// Used for fire/shield height adjustments via render hooks
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    // Fire overlay height is controlled via ItemRendererMixin + InGameHudMixin
}
