package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.config.AxientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ModHubScreen extends Screen {
    private final Screen parent;
    private boolean showModules = true;

    public ModHubScreen(Screen parent) {
        super(Text.literal("Mod Hub"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2, tabW = 110, tabH = 20, tabY = 30;

        addDrawableChild(ButtonWidget.builder(
                Text.literal(showModules ? "§e§lModules" : "Modules"),
                btn -> { showModules = true;  clearChildren(); init(); }
        ).dimensions(cx - tabW - 2, tabY, tabW, tabH).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal(!showModules ? "§e§lSettings" : "Settings"),
                btn -> { showModules = false; clearChildren(); init(); }
        ).dimensions(cx + 2, tabY, tabW, tabH).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("§7← Back"),
                btn -> this.client.setScreen(parent)
        ).dimensions(4, 4, 60, 16).build());

        if (showModules) buildModules();
        else             buildSettings();
    }

    private void buildModules() {
        AxientConfig cfg = AxientClient.config;
        int cx = this.width / 2, y = 60, bw = 200, bh = 20, gap = 3;

        // Each row: toggle btn + optional ⚙ btn
        y = modRow(cx, y, bw, bh, gap, "Keystrokes + CPS", cfg.keystrokesEnabled,
                () -> { cfg.keystrokesEnabled = !cfg.keystrokesEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Coords & Biome", cfg.coordsEnabled,
                () -> { cfg.coordsEnabled = !cfg.coordsEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Ping Display", cfg.pingEnabled,
                () -> { cfg.pingEnabled = !cfg.pingEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Totem Count", cfg.totemCountEnabled,
                () -> { cfg.totemCountEnabled = !cfg.totemCountEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Inventory Hub", cfg.inventoryHubEnabled,
                () -> { cfg.inventoryHubEnabled = !cfg.inventoryHubEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Armor Hub", cfg.armorHubEnabled,
                () -> { cfg.armorHubEnabled = !cfg.armorHubEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Sneak/Sprint Toggle", cfg.sneakSprintEnabled,
                () -> { cfg.sneakSprintEnabled = !cfg.sneakSprintEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new SneakSprintConfigScreen(this)));
        y = modRow(cx, y, bw, bh, gap, "Compass Bar", cfg.compassBarEnabled,
                () -> { cfg.compassBarEnabled = !cfg.compassBarEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new CompassConfigScreen(this)));
        y = modRow(cx, y, bw, bh, gap, "Freelook", cfg.freelookEnabled,
                () -> { cfg.freelookEnabled = !cfg.freelookEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new FreelookConfigScreen(this)));
        y = modRow(cx, y, bw, bh, gap, "Zoom", cfg.zoomEnabled,
                () -> { cfg.zoomEnabled = !cfg.zoomEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new ZoomConfigScreen(this)));
        y = modRow(cx, y, bw, bh, gap, "Gamma / Night Vision", cfg.gammaEnabled,
                () -> { cfg.gammaEnabled = !cfg.gammaEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new GammaConfigScreen(this)));
    }

    private int modRow(int cx, int y, int bw, int bh, int gap,
                       String label, boolean enabled,
                       Runnable toggle, Runnable configScreen) {
        int btnW = configScreen != null ? bw - 24 : bw;
        addDrawableChild(ButtonWidget.builder(
                Text.literal((enabled ? "§a✔ " : "§c✘ ") + label),
                btn -> toggle.run()
        ).dimensions(cx - bw / 2, y, btnW, bh).build());

        if (configScreen != null) {
            Runnable cs = configScreen;
            addDrawableChild(ButtonWidget.builder(Text.literal("§e⚙"),
                    btn -> cs.run()
            ).dimensions(cx + bw / 2 - 22, y, 22, bh).build());
        }
        return y + bh + gap;
    }

    private void buildSettings() {
        AxientConfig cfg = AxientClient.config;
        int cx = this.width / 2, y = 60, bw = 200, bh = 20, gap = 3;

        // Overlays
        y = modRow(cx, y, bw, bh, gap, "Low Fire", cfg.lowFireEnabled,
                () -> { cfg.lowFireEnabled = !cfg.lowFireEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new OverlayConfigScreen(this, "fire")));
        y = modRow(cx, y, bw, bh, gap, "Low Shield", cfg.lowShieldEnabled,
                () -> { cfg.lowShieldEnabled = !cfg.lowShieldEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new OverlayConfigScreen(this, "shield")));
        y = modRow(cx, y, bw, bh, gap, "Weapon Size", cfg.weaponSizeEnabled,
                () -> { cfg.weaponSizeEnabled = !cfg.weaponSizeEnabled; cfg.save(); clearChildren(); init(); },
                () -> this.client.setScreen(new WeaponSizeConfigScreen(this)));

        y += 12; // gap before utilities

        y = modRow(cx, y, bw, bh, gap, "Quick Server Switch", cfg.quickServerEnabled,
                () -> { cfg.quickServerEnabled = !cfg.quickServerEnabled; cfg.save(); clearChildren(); init(); }, null);
        y = modRow(cx, y, bw, bh, gap, "Shader Warning", cfg.shaderWarningEnabled,
                () -> { cfg.shaderWarningEnabled = !cfg.shaderWarningEnabled; cfg.save(); clearChildren(); init(); }, null);
        modRow(cx, y, bw, bh, gap, "Auto Update Check", cfg.autoUpdate,
                () -> { cfg.autoUpdate = !cfg.autoUpdate; cfg.save(); clearChildren(); init(); }, null);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        boolean vi = this.client.options.language.startsWith("vi");
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§b§lAxientClient §7v" + AxientClient.VERSION,
                this.width / 2, 8, 0xFFFFFF);

        if (!showModules) {
            // Section labels
            ctx.drawCenteredTextWithShadow(this.textRenderer, "§7— Overlays —",   this.width / 2, 56, 0x888888);
            ctx.drawCenteredTextWithShadow(this.textRenderer, "§7— Utilities —",  this.width / 2, 56 + 3 * 23 + 14, 0x888888);
        }

        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§7⚙ = open config  |  click = toggle",
                this.width / 2, this.height - 14, 0x666666);
    }

    @Override
    public boolean keyPressed(int kc, int sc, int mod) {
        if (kc == GLFW.GLFW_KEY_ESCAPE) { this.client.setScreen(parent); return true; }
        return super.keyPressed(kc, sc, mod);
    }

    @Override public boolean shouldPause() { return false; }
}
