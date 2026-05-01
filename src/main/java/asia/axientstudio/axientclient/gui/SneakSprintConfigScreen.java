package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.SneakSprintToggle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SneakSprintConfigScreen extends Screen {
    private final Screen parent;
    public SneakSprintConfigScreen(Screen parent){ super(Text.literal("Sneak/Sprint Config")); this.parent=parent; }

    @Override protected void init() {
        int cx=this.width/2, y=60, bw=220, bh=20, gap=4;
        var cfg = AxientClient.config;

        // Sprint mode cycle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Sprint Mode: §e"+SneakSprintToggle.sprintMode.name()),
            btn -> {
                SneakSprintToggle.sprintMode = SneakSprintToggle.SprintMode.values()[
                    (SneakSprintToggle.sprintMode.ordinal()+1) % SneakSprintToggle.SprintMode.values().length];
                cfg.sprintMode = SneakSprintToggle.sprintMode.name(); cfg.save();
                clearChildren(); init();
            }
        ).dimensions(cx-bw/2,y,bw,bh).build()); y+=bh+gap;

        // Sneak mode cycle
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Sneak Mode: §e"+SneakSprintToggle.sneakMode.name()),
            btn -> {
                SneakSprintToggle.sneakMode = SneakSprintToggle.SneakMode.values()[
                    (SneakSprintToggle.sneakMode.ordinal()+1) % SneakSprintToggle.SneakMode.values().length];
                cfg.sneakMode = SneakSprintToggle.sneakMode.name(); cfg.save();
                clearChildren(); init();
            }
        ).dimensions(cx-bw/2,y,bw,bh).build()); y+=bh+gap*4;

        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"), btn->this.client.setScreen(parent)).dimensions(cx-bw/2,y,bw,bh).build());
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eSneak / Sprint Toggle Config",this.width/2,12,0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7HOLD: hold key  |  TOGGLE: double-tap  |  VANILLA: double-tap W",this.width/2,30,0xAAAAAA);
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Sneak TOGGLE: press sneak key once to toggle",this.width/2,42,0xAAAAAA);
    }
    @Override public boolean shouldPause(){ return false; }
}
