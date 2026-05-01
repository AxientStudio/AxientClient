package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.hud.HudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CompassConfigScreen extends Screen {
    private final Screen parent;
    // Preset colors ARGB
    private static final int[] COLORS = {0xAA000000,0xAA1A3A1A,0xAA001A3A,0xAA3A001A,0xAA2A2A00,0xAA3A2A00,0x00000000};
    private static final String[] LABELS = {"Black","Dark Green","Dark Blue","Dark Red","Dark Yellow","Orange","Transparent"};

    public CompassConfigScreen(Screen parent){ super(Text.literal("Compass Config")); this.parent=parent; }

    @Override protected void init() {
        int cx=this.width/2, y=70, bw=180, bh=20, gap=4;
        for (int i=0;i<COLORS.length;i++){
            int c=COLORS[i]; String l=LABELS[i];
            addDrawableChild(ButtonWidget.builder(
                Text.literal((AxientClient.config.compassBgColor==c?"§a▶ ":"  ")+l),
                btn->{ AxientClient.config.compassBgColor=c; AxientClient.config.save(); clearChildren(); init(); }
            ).dimensions(cx-bw/2,y,bw,bh).build());
            y+=bh+gap;
        }
        // Custom RGB input placeholder
        y+=4;
        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"),btn->this.client.setScreen(parent)).dimensions(cx-bw/2,y,bw,bh).build());
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eCompass Bar Config",this.width/2,10,0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Background color:",this.width/2,28,0xAAAAAA);
        // Preview compass
        HudRenderer.renderCompassBar(ctx, this.client, this.client.player);
    }
    @Override public boolean shouldPause(){ return false; }
}
