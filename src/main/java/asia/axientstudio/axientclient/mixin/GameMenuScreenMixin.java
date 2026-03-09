package asia.axientstudio.axientclient.mixin;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At("TAIL"))
    private void onInitWidgets(CallbackInfo ci) {
        if (!AxientClient.config.quickServerEnabled) return;

        String label = AxientClient.config.language.equals("vi")
                ? "Nhiều Người Chơi"
                : "Multiplayer";

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("§b[AC] " + label),
                btn -> this.client.setScreen(new MultiplayerScreen(this))
        ).dimensions(this.width / 2 - 102, this.height / 4 + 136, 204, 20).build());
    }
}
