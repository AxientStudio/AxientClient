package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.FreelookFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class FreelookConfigScreen extends Screen {
    private final Screen parent;
    private boolean listening = false;

    public FreelookConfigScreen(Screen parent){ super(Text.literal("Freelook Config")); this.parent=parent; }

    @Override protected void init() {
        int cx=this.width/2, y=80, bw=220, bh=20;
        String keyName = InputUtil.fromKeyCode(FreelookFeature.keyScancode, 0).getLocalizedText().getString();
        addDrawableChild(ButtonWidget.builder(
            Text.literal(listening?"§ePress any key...":"Hold Key: §f"+keyName),
            btn -> listening=true
        ).dimensions(cx-bw/2,y,bw,bh).build());
        y+=bh+8;
        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"),btn->this.client.setScreen(parent)).dimensions(cx-bw/2,y,bw,bh).build());
    }

    @Override public boolean keyPressed(int kc, int sc, int mod) {
        if (listening) {
            FreelookFeature.keyScancode = kc;
            AxientClient.config.freelookKey = kc;
            AxientClient.config.save();
            listening=false; clearChildren(); init(); return true;
        }
        if (kc==GLFW.GLFW_KEY_ESCAPE&&!listening){ this.client.setScreen(parent); return true; }
        return super.keyPressed(kc,sc,mod);
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eFreelook Config",this.width/2,10,0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Hold key → third-person view, head locked",this.width/2,28,0xAAAAAA);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7You can look around freely while doing other things",this.width/2,40,0xAAAAAA);
    }
    @Override public boolean shouldPause(){ return false; }
}
