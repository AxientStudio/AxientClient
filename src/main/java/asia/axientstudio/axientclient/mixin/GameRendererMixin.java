package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.features.ZoomFeature;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void onGetFov(net.minecraft.client.render.Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (ZoomFeature.zooming) {
            double modded = ZoomFeature.modifyFov((float) cir.getReturnValueD());
            cir.setReturnValue(modded);
        }
    }
}
