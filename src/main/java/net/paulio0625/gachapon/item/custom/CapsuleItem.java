package net.paulio0625.gachapon.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Random;

public class CapsuleItem extends Item {
    public CapsuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Only execute on the server side.
        if (!world.isClientSide) {
            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW,
                    SoundSource.PLAYERS,
                    0.5F, 0.3F
            );

            boolean isSneaking = player.isShiftKeyDown();
            // If the player is sneaking, use all loot boxes in the stack;
            // otherwise, only use one.
            int uses = isSneaking ? stack.getCount() : 1;

            for (int i = 0; i < uses; i++) {
                // Get random loot from the defined pool
                ItemStack loot = getRandomLoot();
                // Try to add the loot to the player's inventory;
                // if inventory is full, drop it in the world.
                if (!player.addItem(loot)) {
                    player.drop(loot, false);
                }
            }
            // Consume the used loot boxes.
            stack.shrink(uses);
        }
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    /**
     * Picks a random loot item from the pool.
     */
    private ItemStack getRandomLoot() {
        // Define your loot pool here.
        // For example: diamond, gold ingot, and iron ingot.
        Item[] lootPool = new Item[] {
                Items.DIAMOND,
                Items.GOLD_INGOT,
                Items.IRON_INGOT
                // Add more items as desired.
        };
        int index = new Random().nextInt(lootPool.length);
        return new ItemStack(lootPool[index]);
    }
}
