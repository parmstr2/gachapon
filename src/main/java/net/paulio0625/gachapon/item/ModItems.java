package net.paulio0625.gachapon.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.paulio0625.gachapon.Gachapon;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Gachapon.MODID);

    public static final DeferredItem<Item> CAPSULE = ITEMS.register("capsule",
            () -> new Item(new Item.Properties())
    );

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
