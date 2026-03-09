package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(
        method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD")
    )
    private void onRenderFirstPersonItem(
            AbstractClientPlayerEntity player,
            float tickDelta, float pitch, Hand hand,
            float swingProgress, ItemStack item, float equipProgress,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light, CallbackInfo ci) {
        var cfg = AxientClient.config;
        if (cfg.weaponSizeEnabled) {
            float scale = cfg.weaponScale;
            matrices.scale(scale, scale, scale);
        }
    }
}
