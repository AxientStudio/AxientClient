package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.CpsTracker;
import asia.axientstudio.axientclient.features.FreelookFeature;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == 1) { // PRESS
            if (button == 0) CpsTracker.recordLeft();
            else if (button == 1) CpsTracker.recordRight();
        }
    }

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void onUpdateMouse(CallbackInfo ci) {
        // When freelook is active, we allow mouse movement but don't rotate the body
        // This is handled in ClientPlayerEntityMixin
    }
}
