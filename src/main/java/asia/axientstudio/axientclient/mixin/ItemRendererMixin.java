package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class ItemRendererMixin {
    @Inject(method="renderFirstPersonItem",at=@At("HEAD"))
    private void onRender(AbstractClientPlayerEntity player,float td,float pitch,Hand hand,float swing,ItemStack item,float equip,MatrixStack mat,VertexConsumerProvider vcp,int light,CallbackInfo ci){
        var cfg=AxientClient.config;
        if(cfg.weaponSizeEnabled){
            mat.translate(cfg.weaponX,cfg.weaponY,0);
            mat.scale(cfg.weaponScale,cfg.weaponScale,cfg.weaponScale);
        }
    }
}
