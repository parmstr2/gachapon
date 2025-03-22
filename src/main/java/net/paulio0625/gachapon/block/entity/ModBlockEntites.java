package net.paulio0625.gachapon.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.paulio0625.gachapon.Gachapon;
import net.paulio0625.gachapon.block.ModBlocks;

import java.util.function.Supplier;

public class ModBlockEntites {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Gachapon.MODID);

    public static final Supplier<BlockEntityType<GachaMachineBlockEntity>> GACHA_MACHINE_ENTITY = BLOCK_ENTITIES.register(
            "gacha_machine_entity",
            () -> BlockEntityType.Builder.of(
                    GachaMachineBlockEntity::new,
                    ModBlocks.GACHA_MACHINE.get()
            ).build(null)
    );

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
