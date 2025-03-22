package net.paulio0625.gachapon.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.paulio0625.gachapon.block.entity.GachaMachineBlockEntity;
import net.paulio0625.gachapon.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class GachaMachineBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final MapCodec<GachaMachineBlock> CODEC = simpleCodec(GachaMachineBlock::new);

    public GachaMachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context){
        return Shapes.box(0, 0, 0, 1, 1, 1);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.box(0, 0, 0, 1, 1, 1);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new GachaMachineBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        BlockPos pos = context.getClickedPos();
        LevelAccessor level = context.getLevel();

        if(pos.getY() < level.getMaxBuildHeight()-1 && level.getBlockState(pos.above()).canBeReplaced(context)){
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        else{
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState>builder){
        builder.add(HALF).add(FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
        level.setBlock(pos, state.setValue(HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER),3);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player){
        if (!level.isClientSide) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
            BlockState otherState = level.getBlockState(otherPos);

            if (otherState.getBlock() == this && otherState.getValue(HALF) != half) {
                level.destroyBlock(otherPos, false);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
        return state;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult){
        DoubleBlockHalf half = state.getValue(HALF);
        ItemStack heldItem = player.getItemInHand(hand);

        if (heldItem.getItem() == Items.EMERALD && !level.isClientSide) {
            if(half == DoubleBlockHalf.UPPER){
                pos = pos.below();
            }
            ItemStack itemToEject = new ItemStack(ModItems.CAPSULE.get());

            popResourceFromFace(level, pos, state.getValue(FACING), itemToEject);
            player.getItemInHand(hand).consume(1,player);
        }
        return ItemInteractionResult.SUCCESS;
    }
}
