package asia.axientstudio.axientclient.features;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public class TotemCounter {
    private static int enemyTotemsUsed = 0;

    public static int count(ClientPlayerEntity player) {
        if (player == null) return 0;
        int total = 0;
        if (player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING)) total += player.getMainHandStack().getCount();
        if (player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING))  total += player.getOffHandStack().getCount();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack s = player.getInventory().getStack(i);
            if (s.isOf(Items.TOTEM_OF_UNDYING)) total += s.getCount();
        }
        return total;
    }

    public static void onEnemyTotemUsed() { enemyTotemsUsed++; }
    public static void resetEnemyCount()  { enemyTotemsUsed = 0; }
    public static int getEnemyTotemsUsed() { return enemyTotemsUsed; }
}
