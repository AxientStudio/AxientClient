package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.GammaFeature;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleOption.class)
public abstract class GammaMixin {
    @Inject(method="getValue",at=@At("RETURN"),cancellable=true)
    private void onGetValue(CallbackInfoReturnable<Object> cir){
        SimpleOption<?> self=(SimpleOption<?>)(Object)this;
        if(AxientClient.config!=null && GammaFeature.isGammaActive()
                && self==net.minecraft.client.MinecraftClient.getInstance().options.getGamma()){
            cir.setReturnValue(GammaFeature.getEffectiveGamma());
        }
    }
}
