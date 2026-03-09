package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.features.TotemCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("RETURN"))
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity)(Object) this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        if (self.getUuid().equals(mc.player.getUuid())) return;
        if (self.getHealth() <= 0.5f && amount > self.getHealth() + 1) {
            TotemCounter.onEnemyTotemUsed();
        }
    }
}
