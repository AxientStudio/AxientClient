package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.features.ExternalModDetector;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;

public class ModDetectorScreen extends Screen {

    private final Screen parent;
    private final List<ExternalModDetector.ModInfo> mods;

    public ModDetectorScreen(Screen parent) {
        super(Text.literal("External Mod Detector"));
        this.parent = parent;
        this.mods = ExternalModDetector.detectExternal();
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                btn -> this.client.setScreen(parent)
        ).dimensions(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§b§lDetected External Mods (" + mods.size() + ")",
                this.width / 2, 10, 0xFFFFFF);

        if (mods.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                    "§aNo external client-side mods detected.",
                    this.width / 2, this.height / 2, 0xFFFFFF);
        } else {
            int y = 30;
            for (ExternalModDetector.ModInfo mod : mods) {
                String prefix = mod.isKnownExternal ? "§c[!] " : "§7[?] ";
                String line = prefix + "§f" + mod.name + " §7(" + mod.id + ") §8v" + mod.version;
                ctx.drawText(this.textRenderer, line, 10, y, 0xFFFFFF, false);
                y += 12;
                if (y > this.height - 40) break;
            }
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
