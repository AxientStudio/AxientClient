package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.config.AxientConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AxientMenuScreen extends Screen {

    private final Screen parent;

    // Scroll state
    private int scrollOffset = 0;           // pixels scrolled
    private int totalContentHeight = 0;     // set during buildRows()
    private boolean isDraggingScrollbar = false;
    private int scrollDragStartY = 0;
    private int scrollDragStartOffset = 0;

    // Layout constants
    private static final int BW = 220, BH = 20, GAP = 3;
    private static final int HEADER_H = 36;   // space for title at top
    private static final int FOOTER_H = 30;   // space for Save&Close at bottom
    private static final int SCROLLBAR_W = 6;
    private static final int SCROLLBAR_MARGIN = 4;

    // Row records — built once in init(), rendered/positioned each frame
    private record Row(String label, boolean[] state, Consumer<Boolean> setter, ButtonWidget button) {}
    private final List<Row> rows = new ArrayList<>();
    private final List<ButtonWidget> actionButtons = new ArrayList<>();  // non-toggle buttons

    public AxientMenuScreen(Screen parent) {
        super(Text.literal("AxientClient"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        rows.clear();
        actionButtons.clear();
        scrollOffset = 0;

        AxientConfig cfg = AxientClient.config;

        // ── Language toggle (special, always first) ──
        ButtonWidget langBtn = ButtonWidget.builder(
                Text.literal("Language: " + (cfg.language.equals("vi") ? "Tiếng Việt" : "English")),
                btn -> {
                    cfg.language = cfg.language.equals("en") ? "vi" : "en";
                    cfg.save();
                    btn.setMessage(Text.literal("Language: " + (cfg.language.equals("vi") ? "Tiếng Việt" : "English")));
                }
        ).dimensions(0, 0, BW, BH).build(); // position set in repositionWidgets
        addDrawableChild(langBtn);
        actionButtons.add(langBtn);

        // ── Feature toggle rows ──
        addRow("Keystrokes + CPS",    cfg.keystrokesEnabled,   v -> cfg.keystrokesEnabled = v,   cfg);
        addRow("Coords & Biome",      cfg.coordsEnabled,       v -> cfg.coordsEnabled = v,       cfg);
        addRow("Compass Bar",         cfg.positionHubEnabled,  v -> cfg.positionHubEnabled = v,  cfg);
        addRow("Armor Hub",           cfg.armorHubEnabled,     v -> cfg.armorHubEnabled = v,     cfg);
        addRow("Totem Count",         cfg.totemCountEnabled,   v -> cfg.totemCountEnabled = v,   cfg);
        addRow("Ping Display",        cfg.pingEnabled,         v -> cfg.pingEnabled = v,         cfg);
        addRow("Inventory Hub",       cfg.inventoryHubEnabled, v -> cfg.inventoryHubEnabled = v, cfg);
        addRow("Freelook (V)",        cfg.freelookEnabled,     v -> cfg.freelookEnabled = v,     cfg);
        addRow("Zoom (C)",            cfg.zoomEnabled,         v -> cfg.zoomEnabled = v,         cfg);
        addRow("Sneak Toggle",        cfg.sneakToggleEnabled,  v -> cfg.sneakToggleEnabled = v,  cfg);
        addRow("Sprint Toggle",       cfg.sprintToggleEnabled, v -> cfg.sprintToggleEnabled = v, cfg);
        addRow("Gamma 1000%",         cfg.gammaEnabled,        v -> { cfg.gammaEnabled = v; cfg.save(); }, cfg);
        addRow("Weapon Size & Swing", cfg.weaponSizeEnabled,   v -> cfg.weaponSizeEnabled = v,   cfg);
        addRow("Shader Warning",      cfg.shaderWarningEnabled,v -> cfg.shaderWarningEnabled = v,cfg);
        addRow("Auto Update",         cfg.autoUpdate,          v -> cfg.autoUpdate = v,          cfg);

        // ── Action buttons ──
        ButtonWidget hudBtn = ButtonWidget.builder(
                Text.literal("§eOpen HUD Editor"),
                btn -> { cfg.save(); this.client.setScreen(new HudEditorScreen(this)); }
        ).dimensions(0, 0, BW, BH).build();
        addDrawableChild(hudBtn);
        actionButtons.add(hudBtn);

        ButtonWidget modBtn = ButtonWidget.builder(
                Text.literal("External Mod Detector"),
                btn -> this.client.setScreen(new ModDetectorScreen(this))
        ).dimensions(0, 0, BW, BH).build();
        addDrawableChild(modBtn);
        actionButtons.add(modBtn);

        ButtonWidget updateBtn = ButtonWidget.builder(
                Text.literal("Check for Updates"),
                btn -> asia.axientstudio.axientclient.features.GitHubUpdateChecker.checkAsync()
        ).dimensions(0, 0, BW, BH).build();
        addDrawableChild(updateBtn);
        actionButtons.add(updateBtn);

        // ── Save & Close — fixed at bottom, not scrollable ──
        int saveX = this.width / 2 - BW / 2;
        ButtonWidget saveBtn = ButtonWidget.builder(
                Text.literal("§aSave & Close"),
                btn -> { cfg.save(); this.client.setScreen(parent); }
        ).dimensions(saveX, this.height - FOOTER_H, BW, BH).build();
        addDrawableChild(saveBtn);

        // Calculate total content height
        int itemCount = 1 + rows.size() + actionButtons.size() - 1; // lang + rows + action(-save)
        totalContentHeight = itemCount * (BH + GAP);

        repositionWidgets();
    }

    private void addRow(String label, boolean initial, Consumer<Boolean> setter, AxientConfig cfg) {
        final boolean[] state = {initial};
        ButtonWidget btn = ButtonWidget.builder(
                Text.literal(label + ": " + (state[0] ? "§aON" : "§cOFF")),
                b -> {
                    state[0] = !state[0];
                    setter.accept(state[0]);
                    b.setMessage(Text.literal(label + ": " + (state[0] ? "§aON" : "§cOFF")));
                    cfg.save();
                }
        ).dimensions(0, 0, BW, BH).build();
        addDrawableChild(btn);
        rows.add(new Row(label, state, setter, btn));
    }

    /** Called every frame to update button Y positions based on scroll */
    private void repositionWidgets() {
        int cx = this.width / 2;
        int x = cx - BW / 2;
        int viewH = this.height - HEADER_H - FOOTER_H;

        int y = HEADER_H - scrollOffset;

        // Language button (first action button)
        actionButtons.get(0).setPosition(x, y);
        actionButtons.get(0).visible = isInView(y, viewH);
        y += BH + GAP;

        // Toggle rows
        for (Row row : rows) {
            row.button().setPosition(x, y);
            row.button().visible = isInView(y, viewH);
            y += BH + GAP;
        }

        // Action buttons (skip 0 = lang, skip last = save which is fixed)
        for (int i = 1; i < actionButtons.size(); i++) {
            actionButtons.get(i).setPosition(x, y);
            actionButtons.get(i).visible = isInView(y, viewH);
            y += BH + GAP;
        }
    }

    private boolean isInView(int widgetY, int viewH) {
        return widgetY + BH > HEADER_H && widgetY < HEADER_H + viewH;
    }

    private int maxScroll() {
        int viewH = this.height - HEADER_H - FOOTER_H;
        return Math.max(0, totalContentHeight - viewH);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        repositionWidgets();
        super.render(ctx, mouseX, mouseY, delta);

        int screenW = this.width;
        int screenH = this.height;

        // ── Title ──
        ctx.fill(0, 0, screenW, HEADER_H, 0xCC000000);
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§b§lAxientClient §7v" + AxientClient.VERSION,
                screenW / 2, 8, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§7RShift = Menu  •  V = Freelook  •  C = Zoom",
                screenW / 2, 22, 0xAAAAAA);

        // ── Footer bar ──
        ctx.fill(0, screenH - FOOTER_H - 2, screenW, screenH, 0xCC000000);

        // ── Scrollbar ──
        int viewH = screenH - HEADER_H - FOOTER_H;
        int max = maxScroll();
        if (max > 0) {
            int sbX = screenW - SCROLLBAR_W - SCROLLBAR_MARGIN;
            int sbTrackY = HEADER_H;
            int sbTrackH = viewH;

            // Track
            ctx.fill(sbX, sbTrackY, sbX + SCROLLBAR_W, sbTrackY + sbTrackH, 0x55FFFFFF);

            // Thumb
            int thumbH = Math.max(20, sbTrackH * sbTrackH / (totalContentHeight));
            int thumbY = sbTrackY + (int)((float) scrollOffset / max * (sbTrackH - thumbH));
            boolean hovering = mouseX >= sbX && mouseX <= sbX + SCROLLBAR_W + 4
                    && mouseY >= sbTrackY && mouseY <= sbTrackY + sbTrackH;
            ctx.fill(sbX, thumbY, sbX + SCROLLBAR_W, thumbY + thumbH,
                    (isDraggingScrollbar || hovering) ? 0xFFFFFFFF : 0xAAFFFFFF);
        }

        // ── Clip hint lines (top/bottom of content area) ──
        ctx.fill(0, HEADER_H, screenW, HEADER_H + 1, 0x55FFFFFF);
        ctx.fill(0, screenH - FOOTER_H - 1, screenW, screenH - FOOTER_H, 0x55FFFFFF);

        // ── Scroll indicator text ──
        if (max > 0) {
            int pct = (int)(100f * scrollOffset / max);
            ctx.drawText(this.textRenderer, "§7↕ " + pct + "%",
                    screenW - 40, screenH - FOOTER_H - 12, 0x88FFFFFF, false);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset = Math.max(0, Math.min(maxScroll(), scrollOffset - (int)(verticalAmount * 12)));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Scrollbar drag start
        int sbX = this.width - SCROLLBAR_W - SCROLLBAR_MARGIN;
        if (button == 0 && mouseX >= sbX - 4 && mouseX <= sbX + SCROLLBAR_W + 4
                && mouseY >= HEADER_H && mouseY <= this.height - FOOTER_H) {
            isDraggingScrollbar = true;
            scrollDragStartY = (int) mouseY;
            scrollDragStartOffset = scrollOffset;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDraggingScrollbar && button == 0) {
            int viewH = this.height - HEADER_H - FOOTER_H;
            int dy = (int) mouseY - scrollDragStartY;
            int newScroll = scrollDragStartOffset + dy * totalContentHeight / viewH;
            scrollOffset = Math.max(0, Math.min(maxScroll(), newScroll));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) isDraggingScrollbar = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            AxientClient.config.save();
            this.client.setScreen(parent);
            return true;
        }
        // Arrow keys scroll
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            scrollOffset = Math.min(maxScroll(), scrollOffset + 15);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            scrollOffset = Math.max(0, scrollOffset - 15);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() { return false; }
}
