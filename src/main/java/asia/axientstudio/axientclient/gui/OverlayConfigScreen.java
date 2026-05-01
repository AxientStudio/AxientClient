package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OverlayConfigScreen extends Screen {
    private final Screen parent;
    private final String type; // "fire" or "shield"

    public OverlayConfigScreen(Screen parent, String type){ super(Text.literal("Overlay Config")); this.parent=parent; this.type=type; }

    @Override protected void init() {
        int cx=this.width/2, y=100, bw=200, bh=20, gap=4;
        var cfg = AxientClient.config;
        float val = type.equals("fire")?cfg.lowFireHeight:cfg.lowShieldHeight;

        addDrawableChild(ButtonWidget.builder(Text.literal("Height: §f"+(int)(val*100)+"% §7[−]"),
            btn->{ if(type.equals("fire")) cfg.lowFireHeight=Math.max(0,cfg.lowFireHeight-0.05f); else cfg.lowShieldHeight=Math.max(0,cfg.lowShieldHeight-0.05f); cfg.save(); clearChildren(); init(); }
        ).dimensions(cx-bw/2,y,bw/2-2,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[+]"),
            btn->{ if(type.equals("fire")) cfg.lowFireHeight=Math.min(1,cfg.lowFireHeight+0.05f); else cfg.lowShieldHeight=Math.min(1,cfg.lowShieldHeight+0.05f); cfg.save(); clearChildren(); init(); }
        ).dimensions(cx+2,y,bw/2-2,bh).build()); y+=bh+gap*3;

        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"),btn->this.client.setScreen(parent)).dimensions(cx-bw/2,y,bw,bh).build());
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eLow "+(type.equals("fire")?"Fire":"Shield")+" Config",this.width/2,10,0xFFFFFF);
        // Preview: draw a semi-transparent fire/shield rectangle
        var cfg = AxientClient.config;
        float h = type.equals("fire")?cfg.lowFireHeight:cfg.lowShieldHeight;
        int previewH=(int)(80*h);
        int col = type.equals("fire")?0x88FF4400:0x8888AAFF;
        ctx.fill(this.width/2-40, 50+(80-previewH), this.width/2+40, 130, col);
        ctx.drawBorder(this.width/2-40,50,80,80,0x44FFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Preview",this.width/2,134,0xAAAAAA);
    }
    @Override public boolean shouldPause(){ return false; }
}
