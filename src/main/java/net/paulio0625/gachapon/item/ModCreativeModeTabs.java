package net.paulio0625.gachapon.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.paulio0625.gachapon.Gachapon;
import net.paulio0625.gachapon.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Gachapon.MODID);

    public static final Supplier<CreativeModeTab> GACHA_ITEMS_TAB = CREATIVE_MODE_TAB.register("gacha_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.CAPSULE.get()))
                    .title(Component.translatable("creativetab.gachapon.gacha_items"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CAPSULE);
                    })
                    .build());

    public static final Supplier<CreativeModeTab> GACHA_BLOCKS_TAB = CREATIVE_MODE_TAB.register("gacha_blocks_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(Gachapon.MODID, "gacha_items_tab"))
                    .icon(() -> new ItemStack(ModBlocks.GACHA_MACHINE.get()))
                    .title(Component.translatable("creativetab.gachapon.gacha_blocks"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.GACHA_MACHINE);
                    })
                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
