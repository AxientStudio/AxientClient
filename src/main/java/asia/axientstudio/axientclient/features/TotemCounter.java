package asia.axientstudio.axientclient.features;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public class TotemCounter {

    // Track totems used by enemies (incremented via mixin on totem pop event)
    private static int enemyTotemsUsed = 0;

    public static int count(ClientPlayerEntity player) {
        int total = 0;
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand  = player.getOffHandStack();
        if (mainHand.isOf(Items.TOTEM_OF_UNDYING)) total += mainHand.getCount();
        if (offHand.isOf(Items.TOTEM_OF_UNDYING))  total += offHand.getCount();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.TOTEM_OF_UNDYING)) total += stack.getCount();
        }
        return total;
    }

    public static void onEnemyTotemUsed() {
        enemyTotemsUsed++;
    }

    public static void resetEnemyCount() {
        enemyTotemsUsed = 0;
    }

    public static int getEnemyTotemsUsed() {
        return enemyTotemsUsed;
    }
}
