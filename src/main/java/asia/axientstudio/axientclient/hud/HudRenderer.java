package asia.axientstudio.axientclient.hud;

import asia.axientstudio.axientclient.AxientClient;
import asia.axientstudio.axientclient.features.CpsTracker;
import asia.axientstudio.axientclient.features.TotemCounter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class HudRenderer {

    public static void render(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        if (mc.options.hudHidden) return;
        if (AxientClient.hudManager.dragMode) return;

        ClientPlayerEntity player = mc.player;
        var cfg = AxientClient.config;

        if (cfg.keystrokesEnabled)   renderKeystrokes(ctx, mc, player);
        if (cfg.coordsEnabled)       renderCoords(ctx, mc, player);
        if (cfg.armorHubEnabled)     renderArmorHub(ctx, mc, player);
        if (cfg.pingEnabled)         renderPing(ctx, mc);
        if (cfg.totemCountEnabled)   renderTotemCount(ctx, mc, player);
        if (cfg.positionHubEnabled)  renderCompassBar(ctx, mc, player);
        if (cfg.inventoryHubEnabled) renderInventoryHub(ctx, mc, player);
    }

    // ── Keystrokes ──
    // Layout:  [W]
    //          [A][S][D]
    //     [SPC         ]
    //     [L:n]    [R:n]
    private static final int KW = 20, KH = 16, KG = 2;

    private static void renderKeystrokes(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity player) {
        int[] pos = AxientClient.hudManager.getPos("keystrokes");
        int x = pos[0], y = pos[1];

        boolean wk  = mc.options.forwardKey.isPressed();
        boolean ak  = mc.options.leftKey.isPressed();
        boolean sk  = mc.options.backKey.isPressed();
        boolean dk  = mc.options.rightKey.isPressed();
        boolean spc = mc.options.jumpKey.isPressed();
        boolean lmb = mc.options.attackKey.isPressed();
        boolean rmb = mc.options.useKey.isPressed();

        // Row 0: W centered over S
        drawKey(ctx, mc, x + KW + KG, y,                    "W",  wk);
        // Row 1: A S D
        drawKey(ctx, mc, x,           y + KH + KG,           "A",  ak);
        drawKey(ctx, mc, x + KW + KG, y + KH + KG,           "S",  sk);
        drawKey(ctx, mc, x+(KW+KG)*2, y + KH + KG,           "D",  dk);
        // Row 2: Space (full width)
        int spcW = KW * 3 + KG * 2;
        ctx.fill(x, y+(KH+KG)*2, x+spcW, y+(KH+KG)*2+KH, spc ? 0xAA00AA00 : 0xAA222222);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, "SPC", x + spcW/2, y+(KH+KG)*2+4, spc ? 0xFF000000 : 0xFFFFFFFF);
        // Row 3: LMB CPS | RMB CPS
        drawKey(ctx, mc, x,           y+(KH+KG)*3,           "L:" + CpsTracker.getLeftCps(),  lmb);
        drawKey(ctx, mc, x + KW + KG, y+(KH+KG)*3,           "R:" + CpsTracker.getRightCps(), rmb);
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc, int x, int y, String label, boolean pressed) {
        int bg = pressed ? 0xCC00BB00 : 0xAA222222;
        int fg = pressed ? 0xFF000000 : 0xFFFFFFFF;
        ctx.fill(x, y, x + KW, y + KH, bg);
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label, x + KW/2, y + 4, fg);
    }

    // ── Coords + Biome (same element) ──
    private static void renderCoords(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity player) {
        int[] pos = AxientClient.hudManager.getPos("coords");
        int x = pos[0], y = pos[1];
        int bx = (int) Math.floor(player.getX());
        int by = (int) Math.floor(player.getY());
        int bz = (int) Math.floor(player.getZ());

        var biomeEntry = mc.world.getBiome(player.getBlockPos());
        String biomeName = biomeEntry.getKey()
                .map(k -> k.getValue().getPath().replace('_', ' '))
                .orElse("Unknown");

        ctx.drawText(mc.textRenderer,
                String.format("§7X§f%d §7Y§f%d §7Z§f%d  §7[§a%s§7]", bx, by, bz, capitalizeWords(biomeName)),
                x, y, 0xFFFFFFFF, true);
    }

    // ── Armor Hub ──
    private static void renderArmorHub(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity player) {
        int[] pos = AxientClient.hudManager.getPos("armor_hub");
        int x = pos[0], y = pos[1];
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        int i = 0;
        for (EquipmentSlot slot : slots) {
            ItemStack stack = player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, x + i * 18, y);
                int dur = stack.getMaxDamage() - stack.getDamage();
                int maxDur = stack.getMaxDamage();
                if (maxDur > 0) {
                    int color = dur < maxDur * 0.2 ? 0xFFFF4444 : dur < maxDur * 0.5 ? 0xFFFFAA00 : 0xFF55FF55;
                    ctx.drawText(mc.textRenderer, String.valueOf(dur), x + i * 18, y + 19, color, true);
                }
            }
            i++;
        }
    }

    // ── Ping ──
    private static void renderPing(DrawContext ctx, MinecraftClient mc) {
        if (mc.getNetworkHandler() == null || mc.player == null) return;
        int[] pos = AxientClient.hudManager.getPos("ping");
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return;
        int ping = entry.getLatency();
        int color = ping < 80 ? 0xFF55FF55 : ping < 150 ? 0xFFFFFF55 : ping < 250 ? 0xFFFFAA00 : 0xFFFF4444;
        ctx.drawText(mc.textRenderer, "§7Ping §f" + ping + "ms", pos[0], pos[1], color, true);
    }

    // ── TotemCount ──
    // Own totems: above XP bar (bottom center)
    // Enemy totems used: above enemy nametag (rendered per-entity in world — approximated here as chat/actionbar)
    private static void renderTotemCount(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity player) {
        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();

        // Own totem count — above XP bar
        int ownCount = TotemCounter.count(player);
        int ownColor = ownCount == 0 ? 0xFFFF4444 : ownCount == 1 ? 0xFFFFAA00 : 0xFF55FF55;
        String ownLabel = "§6⊕ §f" + ownCount;
        int xpBarY = screenH - 29; // approx XP bar y
        ctx.drawCenteredTextWithShadow(mc.textRenderer, ownLabel, screenW / 2, xpBarY - 14, ownColor);

        // Enemy totem tracker — show totems used by nearest player
        int lost = TotemCounter.getEnemyTotemsUsed();
        if (lost > 0) {
            String lostLabel = "§c☠ §f-" + lost;
            ctx.drawText(mc.textRenderer, lostLabel,
                    AxientClient.hudManager.getPos("totem_count")[0],
                    AxientClient.hudManager.getPos("totem_count")[1],
                    0xFFFF8888, true);
        }
    }

    // ── Compass / Direction Bar (above bossbar area) ──
    private static void renderCompassBar(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity player) {
        int screenW = mc.getWindow().getScaledWidth();
        float yaw = ((player.getYaw() % 360) + 360) % 360;

        int barW = 180;
        int barH = 12;
        int barX = (screenW - barW) / 2;
        int barY = 15; // above bossbar; bossbar shifts down naturally

        // Background
        ctx.fill(barX, barY, barX + barW, barY + barH, 0xAA000000);

        // Tick marks and cardinal labels
        String[] cardinals = {"S","SW","W","NW","N","NE","E","SE","S"};
        float[] cardinalYaws = {0,45,90,135,180,225,270,315,360};

        for (int i = 0; i < cardinals.length; i++) {
            float diff = cardinalYaws[i] - yaw;
            // Wrap diff to [-180, 180]
            while (diff > 180) diff -= 360;
            while (diff < -180) diff += 360;
            // Map diff to pixel offset: ±90° maps to ±barW/2
            if (Math.abs(diff) < 95) {
                int px = barX + barW/2 + (int)(diff * barW / 180f);
                ctx.fill(px - 1, barY, px + 1, barY + barH, 0xAAFFFFFF);
                ctx.drawCenteredTextWithShadow(mc.textRenderer, cardinals[i], px, barY + 2, 0xFFFFFFFF);
            }
        }

        // Center marker (player facing)
        ctx.fill(barX + barW/2 - 1, barY, barX + barW/2 + 1, barY + barH, 0xFFFF4444);

        // Degree label below bar
        String degLabel = String.format("§7%.1f°  §f%s", yaw, getCardinalFull(yaw));
        ctx.drawCenteredTextWithShadow(mc.textRenderer, degLabel, screenW / 2, barY + barH + 2, 0xFFFFFFFF);
    }

    private static String getCardinalFull(float yaw) {
        if (yaw < 22.5 || yaw >= 337.5) return "South";
        if (yaw < 67.5)  return "SW";
        if (yaw < 112.5) return "West";
        if (yaw < 157.5) return "NW";
        if (yaw < 202.5) return "North";
        if (yaw < 247.5) return "NE";
        if (yaw < 292.5) return "East";
        return "SE";
    }

    // ── Inventory Hub ──
    private static void renderInventoryHub(DrawContext ctx, MinecraftClient mc, ClientPlayerEntity player) {
        int[] pos = AxientClient.hudManager.getPos("inventory_hub");
        int x = pos[0], y = pos[1];
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            int drawX = x + (slot % 9) * 18;
            int drawY = y + (slot / 9) * 18;
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, drawX, drawY);
                if (stack.getCount() > 1) {
                    ctx.drawText(mc.textRenderer, String.valueOf(stack.getCount()), drawX + 9, drawY + 9, 0xFFFFFF, true);
                }
            } else {
                ctx.fill(drawX, drawY, drawX + 16, drawY + 16, 0x55333333);
            }
        }
    }

    private static String capitalizeWords(String s) {
        StringBuilder sb = new StringBuilder();
        for (String p : s.split(" ")) {
            if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
