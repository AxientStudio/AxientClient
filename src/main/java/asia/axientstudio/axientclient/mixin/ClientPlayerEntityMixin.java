package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.*;
import asia.axientstudio.axientclient.gui.RShiftScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method="tick",at=@At("HEAD"))
    private void onTick(CallbackInfo ci){
        MinecraftClient mc = MinecraftClient.getInstance();
        while(AxientClient.openMenuKey.wasPressed()){
            if(mc.currentScreen==null) mc.setScreen(new RShiftScreen(null));
        }
        FreelookFeature.tick(mc);
        ZoomFeature.tick(mc);
        SneakSprintToggle.tick(mc);
        ShaderWarningFeature.tick(mc);
    }
}
