package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.GammaFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class GammaConfigScreen extends Screen {
    private final Screen parent;
    public GammaConfigScreen(Screen parent){ super(Text.literal("Gamma Config")); this.parent=parent; }

    @Override protected void init() {
        int cx=this.width/2, y=80, bw=220, bh=20, gap=4;
        for (GammaFeature.Mode m : GammaFeature.Mode.values()) {
            GammaFeature.Mode fm = m;
            addDrawableChild(ButtonWidget.builder(
                Text.literal((GammaFeature.mode==m?"§a▶ ":"  ")+m.name().replace("_"," ")),
                btn->{ GammaFeature.mode=fm; AxientClient.config.gammaMode=fm.name(); AxientClient.config.gammaEnabled=fm!=GammaFeature.Mode.OFF; AxientClient.config.save(); clearChildren(); init(); }
            ).dimensions(cx-bw/2,y,bw,bh).build());
            y+=bh+gap;
        }
        y+=4;
        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"),btn->this.client.setScreen(parent)).dimensions(cx-bw/2,y,bw,bh).build());
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eGamma / Night Vision",this.width/2,10,0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Gamma: max brightness  |  Night Vision: simulated (no potion effect)",this.width/2,28,0xAAAAAA);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Current: §e"+GammaFeature.modeLabel(),this.width/2,42,0xFFFFFF);
    }
    @Override public boolean shouldPause(){ return false; }
}
