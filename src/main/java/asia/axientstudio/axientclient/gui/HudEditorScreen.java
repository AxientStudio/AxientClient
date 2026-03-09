package asia.axientstudio.axientclient.gui;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.hud.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.*;

public class HudEditorScreen extends Screen {

    private final Screen parent;
    private final HudManager hud;

    // Element labels for display
    private static final Map<String, String> LABELS = new LinkedHashMap<>();
    static {
        LABELS.put("keystrokes",    "Keystrokes");
        LABELS.put("coords",        "Coords");
        LABELS.put("armor_hub",     "Armor");
        LABELS.put("inventory_hub", "Inventory");
        LABELS.put("ping",          "Ping");
        LABELS.put("totem_count",   "Totems");
        LABELS.put("position_hub",  "Position");
    }

    private static final int EL_W = 70, EL_H = 16;

    public HudEditorScreen(Screen parent) {
        super(Text.literal("HUD Editor"));
        this.parent = parent;
        this.hud = AxientClient.hudManager;
        this.hud.dragMode = true;
    }

    @Override
    protected void init() {
        // Save & Exit
        addDrawableChild(ButtonWidget.builder(
                Text.literal("§aSave & Exit"),
                btn -> {
                    hud.savePositions();
                    hud.dragMode = false;
                    this.client.setScreen(parent);
                }
        ).dimensions(this.width - 110, this.height - 26, 100, 20).build());

        // Reset positions
        addDrawableChild(ButtonWidget.builder(
                Text.literal("§cReset All"),
                btn -> {
                    LABELS.keySet().forEach(id -> {
                        int[] def = getDefaultPos(id);
                        hud.setPos(id, def[0], def[1]);
                    });
                }
        ).dimensions(this.width - 220, this.height - 26, 100, 20).build());
    }

    private int[] getDefaultPos(String id) {
        return switch (id) {
            case "keystrokes"    -> new int[]{10, 100};
            case "coords"        -> new int[]{10, 10};
            case "armor_hub"     -> new int[]{10, 60};
            case "inventory_hub" -> new int[]{10, 200};
            case "ping"          -> new int[]{10, 30};
            case "totem_count"   -> new int[]{10, 130};
            case "position_hub"  -> new int[]{10, 160};
            default              -> new int[]{10, 10};
        };
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dim background
        ctx.fill(0, 0, this.width, this.height, 0x88000000);

        // Title
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                "§eHUD Editor — Drag elements to reposition",
                this.width / 2, 5, 0xFFFFFF);

        // Draw each element
        for (Map.Entry<String, String> entry : LABELS.entrySet()) {
            String id = entry.getKey();
            String label = entry.getValue();
            int[] pos = hud.getPos(id);

            boolean hovered = mouseX >= pos[0] && mouseX <= pos[0] + EL_W
                    && mouseY >= pos[1] && mouseY <= pos[1] + EL_H;
            boolean dragging = id.equals(hud.draggingElement);

            int bg = dragging ? 0xCC00AA00 : hovered ? 0xCC005577 : 0xAA333333;
            ctx.fill(pos[0], pos[1], pos[0] + EL_W, pos[1] + EL_H, bg);
            ctx.drawBorder(pos[0], pos[1], EL_W, EL_H, 0xFFFFFFFF);
            ctx.drawText(this.textRenderer, label, pos[0] + 3, pos[1] + 4, 0xFFFFFF, true);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (String id : LABELS.keySet()) {
                int[] pos = hud.getPos(id);
                if (mouseX >= pos[0] && mouseX <= pos[0] + EL_W
                        && mouseY >= pos[1] && mouseY <= pos[1] + EL_H) {
                    hud.startDrag(id, (int) mouseX, (int) mouseY);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && hud.draggingElement != null) {
            hud.updateDrag((int) mouseX, (int) mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) hud.stopDrag();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // RShift saves and exits
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT) {
            hud.savePositions();
            hud.dragMode = false;
            this.client.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        hud.dragMode = false;
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() { return false; }
}
