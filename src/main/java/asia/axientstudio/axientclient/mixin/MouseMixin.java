package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.features.CpsTracker;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Inject(method="onMouseButton",at=@At("HEAD"))
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci){
        if(action==1){ if(button==0) CpsTracker.recordLeft(); else if(button==1) CpsTracker.recordRight(); }
    }
}
