package net.paulio0625.gachapon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GachaMachineBlockEntity extends BlockEntity {
    private int color = 0xFFFFFF;
    public GachaMachineBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntites.GACHA_MACHINE_ENTITY.get(), pos, blockState);
    }

    public void setColor(int rgb){
        this.color = rgb;
        setChanged();
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Color", color);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        color = tag.getInt("Color");
    }
}
