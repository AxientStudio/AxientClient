package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class WeaponSizeConfigScreen extends Screen {
    private final Screen parent;
    public WeaponSizeConfigScreen(Screen parent){ super(Text.literal("Weapon Size Config")); this.parent=parent; }

    @Override protected void init() {
        int cx=this.width/2, y=60, hw=90, bh=20, gap=4;
        var cfg=AxientClient.config;

        // X offset
        addLabel(cx, y, "§7Offset X: §f"+String.format("%.2f",cfg.weaponX)); y+=12;
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[−]"),btn->{cfg.weaponX-=0.1f;cfg.save();clearChildren();init();}).dimensions(cx-hw-2,y,hw,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[+]"),btn->{cfg.weaponX+=0.1f;cfg.save();clearChildren();init();}).dimensions(cx+2,y,hw,bh).build()); y+=bh+gap*2;

        // Y offset
        addLabel(cx, y, "§7Offset Y: §f"+String.format("%.2f",cfg.weaponY)); y+=12;
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[−]"),btn->{cfg.weaponY-=0.1f;cfg.save();clearChildren();init();}).dimensions(cx-hw-2,y,hw,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[+]"),btn->{cfg.weaponY+=0.1f;cfg.save();clearChildren();init();}).dimensions(cx+2,y,hw,bh).build()); y+=bh+gap*2;

        // Scale
        addLabel(cx, y, "§7Scale: §f"+String.format("%.2f",cfg.weaponScale)); y+=12;
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[−]"),btn->{cfg.weaponScale=Math.max(0.1f,cfg.weaponScale-0.1f);cfg.save();clearChildren();init();}).dimensions(cx-hw-2,y,hw,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§7[+]"),btn->{cfg.weaponScale=Math.min(3f,cfg.weaponScale+0.1f);cfg.save();clearChildren();init();}).dimensions(cx+2,y,hw,bh).build()); y+=bh+gap*3;

        // Reset
        addDrawableChild(ButtonWidget.builder(Text.literal("§cReset"),btn->{cfg.weaponX=0;cfg.weaponY=0;cfg.weaponScale=1;cfg.save();clearChildren();init();}).dimensions(cx-hw-2,y,hw,bh).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§aBack"),btn->this.client.setScreen(parent)).dimensions(cx+2,y,hw,bh).build());
    }

    private void addLabel(int cx, int y, String text) {
        // stored as widget placeholder — drawn in render
    }

    @Override public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx,mx,my,delta);
        var cfg=AxientClient.config;
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§eWeapon Size & Position Config",this.width/2,10,0xFFFFFF);
        int y=60;
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Offset X: §f"+String.format("%.2f",cfg.weaponX),this.width/2,y,0xFFFFFF); y+=32;
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Offset Y: §f"+String.format("%.2f",cfg.weaponY),this.width/2,y,0xFFFFFF); y+=32;
        ctx.drawCenteredTextWithShadow(this.textRenderer,"§7Scale: §f"+String.format("%.2f",cfg.weaponScale),this.width/2,y,0xFFFFFF);
    }
    @Override public boolean shouldPause(){ return false; }
}
