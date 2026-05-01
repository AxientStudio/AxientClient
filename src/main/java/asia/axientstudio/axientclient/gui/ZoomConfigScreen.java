package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.ZoomFeature;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ZoomConfigScreen extends Screen {
    private final Screen parent;
    private boolean listening = false;

    public ZoomConfigScreen(Screen parent){ super(Text.literal("Zoom Config")); this.parent=parent; }

    @Override protected void init() {
        int cx=this.width/2, y=80, bw=220, bh=20, gap=4;
        String keyName = InputUtil.fromKeyCode(ZoomFeature.keyScancode, 0).getLocalizedText().getString();
        addDrawableChild(ButtonWidget.builder(
            Text.literal(listening?"§ePress any key...":"Hold Key: §f"+keyName),
            btn -> listening=true
        ).dimensions(cx-bw/2,y,bw,bh).build()); y+=bh+gap;

        // FOV slider (+/-)
        addDrawableChild(ButtonWidget.builder(Text.literal("FOV: §f"+(int)ZoomFeature.zoomFov+"  §7[−]"),
            btn->{ ZoomFeature.zoomFov=Math.max(5,ZoomFeature.zoomFov-5); AxientClient.config.save(); clearChildren(); init(); }
        ).dimensions(cx-bw/2,y,bw/2-2,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[+]"),
            btn->{ ZoomFeature.zoomFov=Math.min(60,ZoomFeature.zoomFov+5); AxientClient.config.save(); clearChildren(); init(); }
        ).dimensions(cx+2,y,bw/2-2,bh).build()); y+=bh+gap*3;

        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"),btn->this.client.setScreen(parent)).dimensions(cx-bw/2,y,bw,bh).build());
    }

    @Override public boolean keyPressed(int kc, int sc, int mod) {
        if (listening) {
            ZoomFeature.keyScancode=kc; AxientClient.config.zoomKey=kc; AxientClient.config.save();
            listening=false; clearChildren(); init(); return true;
        }
        if (kc==GLFW.GLFW_KEY_ESCAPE&&!listening){ this.client.setScreen(parent); return true; }
        return super.keyPressed(kc,sc,mod);
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eZoom Config",this.width/2,10,0xFFFFFF);
    }
    @Override public boolean shouldPause(){ return false; }
}
