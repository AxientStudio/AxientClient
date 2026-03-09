package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.FreelookFeature;
import asia.axientstudio.axientclient.features.ShaderWarningFeature;
import asia.axientstudio.axientclient.features.SneakSprintToggle;
import asia.axientstudio.axientclient.features.ZoomFeature;
import asia.axientstudio.axientclient.gui.AxientMenuScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    private float freelookSavedBodyYaw;
    private boolean wasFreelooking = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;

        // ── Open menu on RShift (only when no screen open) ──
        while (AxientClient.openMenuKey.wasPressed()) {
            if (mc.currentScreen == null) {
                mc.setScreen(new AxientMenuScreen(null));
            }
        }

        // ── Freelook: lock body yaw ──
        if (FreelookFeature.active) {
            if (!wasFreelooking) {
                freelookSavedBodyYaw = self.bodyYaw;
                wasFreelooking = true;
            }
            self.bodyYaw = freelookSavedBodyYaw;
            self.headYaw = freelookSavedBodyYaw;
        } else {
            wasFreelooking = false;
        }

        // ── Gamma ──
        if (AxientClient.config.gammaEnabled) {
            mc.options.getGamma().setValue(AxientClient.config.gammaValue);
        }

        // ── Feature ticks ──
        FreelookFeature.tick(mc);
        ZoomFeature.tick(mc);
        SneakSprintToggle.tick(mc);
        ShaderWarningFeature.tick(mc);
    }
}
