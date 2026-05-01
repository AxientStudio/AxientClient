package asia.axientstudio.axientclient.hud;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.config.AxientConfig;
import asia.axientstudio.axientclient.features.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HudRenderer {

    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        if (mc.options.hudHidden) return;
        if (AxientClient.hudManager.dragMode) return;

        AxientConfig cfg = AxientClient.config;
        ClientPlayerEntity p = mc.player;

        if (cfg.keystrokesEnabled)   renderElement("keystrokes",    ctx, mc, AxientClient.hudManager.getPos("keystrokes"));
        if (cfg.coordsEnabled)       renderElement("coords",        ctx, mc, AxientClient.hudManager.getPos("coords"));
        if (cfg.armorHubEnabled)     renderElement("armor_hub",     ctx, mc, AxientClient.hudManager.getPos("armor_hub"));
        if (cfg.pingEnabled)         renderElement("ping",          ctx, mc, AxientClient.hudManager.getPos("ping"));
        if (cfg.totemCountEnabled)   renderTotemCount(ctx, mc, p);
        if (cfg.compassBarEnabled)   renderCompassBar(ctx, mc, p);
        if (cfg.inventoryHubEnabled) renderElement("inventory_hub", ctx, mc, AxientClient.hudManager.getPos("inventory_hub"));
        if (cfg.sneakSprintEnabled)  renderSprintStatus(ctx, mc);
    }

    public static void renderElement(String id, DrawContext ctx, MinecraftClient mc, int[] pos) {
        float scale = AxientClient.hudManager.getScale(id);
        if (scale != 1.0f) {
            ctx.getMatrices().push();
            ctx.getMatrices().translate(pos[0], pos[1], 0);
            ctx.getMatrices().scale(scale, scale, 1f);
            ctx.getMatrices().translate(-pos[0], -pos[1], 0);
        }
        ClientPlayerEntity player = mc.player;
        switch (id) {
            case "keystrokes"    -> renderKeystrokes(ctx, mc, player, pos);
            case "coords"        -> renderCoords(ctx, mc, player, pos);
            case "armor_hub"     -> renderArmorHub(ctx, mc, player, pos);
            case "ping"          -> renderPing(ctx, mc, pos);
            case "totem_count"   -> renderTotemAt(ctx, mc, player, pos);
            case "inventory_hub" -> renderInventoryHub(ctx, mc, player, pos);
        }
        if (scale != 1.0f) ctx.getMatrices().pop();
    }

    // ── Keystrokes ──
    private static final int KW = 20, KH = 16, KG = 2;

    public static void renderKeystrokes(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p, int[] pos) {
        int x = pos[0], y = pos[1];
        boolean w   = p != null && mc.options.forwardKey.isPressed();
        boolean a   = p != null && mc.options.leftKey.isPressed();
        boolean s   = p != null && mc.options.backKey.isPressed();
        boolean d   = p != null && mc.options.rightKey.isPressed();
        boolean spc = p != null && mc.options.jumpKey.isPressed();
        boolean lmb = p != null && mc.options.attackKey.isPressed();
        boolean rmb = p != null && mc.options.useKey.isPressed();

        drawKey(ctx, mc, x+KW+KG, y,          "W",  w);
        drawKey(ctx, mc, x,       y+KH+KG,    "A",  a);
        drawKey(ctx, mc, x+KW+KG, y+KH+KG,    "S",  s);
        drawKey(ctx, mc, x+(KW+KG)*2, y+KH+KG,"D",  d);
        int sw = KW*3+KG*2;
        ctx.fill(x, y+(KH+KG)*2, x+sw, y+(KH+KG)*2+KH, spc?0xAA00AA00:0xAA222222);
        ctx.drawCenteredTextWithShadow(mc.textRenderer,"SPC",x+sw/2,y+(KH+KG)*2+4,spc?0xFF000000:0xFFFFFFFF);
        drawKey(ctx, mc, x,       y+(KH+KG)*3, "L:"+CpsTracker.getLeftCps(),  lmb);
        drawKey(ctx, mc, x+KW+KG, y+(KH+KG)*3, "R:"+CpsTracker.getRightCps(), rmb);
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc, int x, int y, String label, boolean pressed) {
        ctx.fill(x, y, x+KW, y+KH, pressed?0xCC00BB00:0xAA222222);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label, x+KW/2, y+4, pressed?0xFF000000:0xFFFFFFFF);
    }

    // ── Coords + Biome (vertical layout) ──
    public static void renderCoords(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p, int[] pos) {
        int x = pos[0], y = pos[1];
        int bx = p!=null?(int)Math.floor(p.getX()):0;
        int by = p!=null?(int)Math.floor(p.getY()):64;
        int bz = p!=null?(int)Math.floor(p.getZ()):0;

        String biome = "Unknown";
        int biomeColor = 0xFF55FF55;
        if (p != null && mc.world != null) {
            var key = mc.world.getBiome(p.getBlockPos()).getKey();
            if (key.isPresent()) {
                String path = key.get().getValue().getPath();
                biome = capitalizeWords(path.replace('_',' '));
                // Color by biome category
                if (path.contains("desert") || path.contains("badland") || path.contains("savanna")) biomeColor = 0xFFFF6644;
                else if (path.contains("snow") || path.contains("ice") || path.contains("frozen"))   biomeColor = 0xFF88CCFF;
                else if (path.contains("ocean") || path.contains("river") || path.contains("beach")) biomeColor = 0xFF4488FF;
                else if (path.contains("jungle") || path.contains("forest") || path.contains("taiga")) biomeColor = 0xFF44FF44;
                else if (path.contains("nether") || path.contains("basalt") || path.contains("soul")) biomeColor = 0xFFFF4444;
                else if (path.contains("end"))                                                          biomeColor = 0xFFDDDD44;
            }
        }

        ctx.drawText(mc.textRenderer, "§7X: §f" + bx, x, y,    0xFFFFFFFF, true);
        ctx.drawText(mc.textRenderer, "§7Y: §f" + by, x, y+10, 0xFFFFFFFF, true);
        ctx.drawText(mc.textRenderer, "§7Z: §f" + bz, x, y+20, 0xFFFFFFFF, true);
        ctx.drawText(mc.textRenderer, "§7Biome: ", x, y+30, 0xFFFFFFFF, true);
        int biomeX = x + mc.textRenderer.getWidth("Biome: ") + 2;
        ctx.drawText(mc.textRenderer, biome, biomeX, y+30, biomeColor, true);
    }

    // ── Armor Hub ──
    public static void renderArmorHub(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p, int[] pos) {
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = p != null ? p.getEquippedStack(slots[i]) : ItemStack.EMPTY;
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, pos[0]+i*18, pos[1]);
                if (stack.getMaxDamage() > 0) {
                    int dur = stack.getMaxDamage() - stack.getDamage();
                    int pct = dur * 100 / stack.getMaxDamage();
                    int col = pct > 50 ? 0xFF55FF55 : pct > 20 ? 0xFFFFAA00 : 0xFFFF4444;
                    ctx.drawText(mc.textRenderer, String.valueOf(dur), pos[0]+i*18, pos[1]+19, col, true);
                }
            }
        }
    }

    // ── Ping ──
    public static void renderPing(DrawContext ctx, MinecraftClient mc, int[] pos) {
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) ping = e.getLatency();
        }
        int col = ping<80?0xFF55FF55:ping<150?0xFFFFFF55:ping<250?0xFFFFAA00:0xFFFF4444;
        ctx.drawText(mc.textRenderer, "§7Ping: §f"+ping+"ms", pos[0], pos[1], col, true);
    }

    // ── Totem (fixed screen positions) ──
    public static void renderTotemCount(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p) {
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        int own = TotemCounter.count(p);
        int col = own==0?0xFFFF4444:own==1?0xFFFFAA00:0xFF55FF55;
        ctx.drawCenteredTextWithShadow(mc.textRenderer, "§6⊕ §f"+own, sw/2, sh-43, col);
        int lost = TotemCounter.getEnemyTotemsUsed();
        if (lost>0) {
            int[] pos = AxientClient.hudManager.getPos("totem_count");
            ctx.drawText(mc.textRenderer, "§c☠ §f-"+lost, pos[0], pos[1], 0xFFFF8888, true);
        }
    }

    public static void renderTotemAt(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p, int[] pos) {
        int own = p!=null?TotemCounter.count(p):2;
        int col = own==0?0xFFFF4444:own==1?0xFFFFAA00:0xFF55FF55;
        ctx.drawText(mc.textRenderer, "§6⊕ Own: §f"+own+"  §c☠ Enemy: §f"+TotemCounter.getEnemyTotemsUsed(), pos[0], pos[1], col, true);
    }

    // ── Compass Bar ──
    public static void renderCompassBar(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p) {
        int sw = mc.getWindow().getScaledWidth();
        float yaw = p!=null?(((p.getYaw()%360)+360)%360):0;
        int bgColor = AxientClient.config.compassBgColor;

        int barW=180, barH=12, barX=(sw-barW)/2, barY=15;
        ctx.fill(barX, barY, barX+barW, barY+barH, bgColor);

        String[] cards = {"S","SW","W","NW","N","NE","E","SE","S"};
        float[]  cyaws = {0,45,90,135,180,225,270,315,360};
        for (int i=0;i<cards.length;i++) {
            float diff = cyaws[i]-yaw;
            while(diff>180) diff-=360; while(diff<-180) diff+=360;
            if(Math.abs(diff)<95){
                int px=barX+barW/2+(int)(diff*barW/180f);
                ctx.fill(px-1,barY,px+1,barY+barH,0xAAFFFFFF);
                ctx.drawCenteredTextWithShadow(mc.textRenderer,cards[i],px,barY+2,0xFFFFFFFF);
            }
        }
        ctx.fill(barX+barW/2-1,barY,barX+barW/2+1,barY+barH,0xFFFF4444);
        ctx.drawCenteredTextWithShadow(mc.textRenderer,
            String.format("§7%.1f°  §f%s", yaw, getCardinalFull(yaw)),
            sw/2, barY+barH+2, 0xFFFFFFFF);
    }

    // ── Sprint/Sneak Status (right of compass) ──
    public static void renderSprintStatus(DrawContext ctx, MinecraftClient mc) {
        int sw = mc.getWindow().getScaledWidth();
        String text = SneakSprintToggle.getStatusText();
        int barX = (sw-180)/2 + 180 + 4;
        ctx.drawText(mc.textRenderer, text, barX, 18, 0xFFFFFFFF, true);
    }

    // ── Inventory Hub (27 slots = main inv only, no hotbar) ──
    public static void renderInventoryHub(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity p, int[] pos) {
        // Slots 9–35 = main inventory (27 slots), hotbar = 0–8 (skip)
        for (int slot = 9; slot < 36; slot++) {
            ItemStack stack = p!=null ? p.getInventory().getStack(slot) : ItemStack.EMPTY;
            int i = slot - 9;
            int dx = pos[0] + (i%9)*18;
            int dy = pos[1] + (i/9)*18;
            ctx.fill(dx, dy, dx+16, dy+16, 0x55222222);
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, dx, dy);
                if (stack.getCount()>1)
                    ctx.drawText(mc.textRenderer, String.valueOf(stack.getCount()), dx+9, dy+9, 0xFFFFFF, true);
            }
        }
    }

    private static String getCardinalFull(float yaw) {
        if (yaw<22.5||yaw>=337.5) return "South";
        if (yaw<67.5)  return "SW";   if (yaw<112.5) return "West";
        if (yaw<157.5) return "NW";   if (yaw<202.5) return "North";
        if (yaw<247.5) return "NE";   if (yaw<292.5) return "East";
        return "SE";
    }

    private static String capitalizeWords(String s) {
        StringBuilder sb = new StringBuilder();
        for (String w : s.split(" ")) if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
