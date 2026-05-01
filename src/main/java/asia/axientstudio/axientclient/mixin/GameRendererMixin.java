package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.features.ZoomFeature;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov,
                          CallbackInfoReturnable<Double> cir) {
        if (ZoomFeature.active) {
            cir.setReturnValue((double) ZoomFeature.modifyFov((float)(double) cir.getReturnValue()));
        }
    }
}
