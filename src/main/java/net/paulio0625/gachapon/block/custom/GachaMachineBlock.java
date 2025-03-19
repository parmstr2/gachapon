package net.paulio0625.gachapon.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.paulio0625.gachapon.item.ModItems;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class GachaMachineBlock extends HorizontalDirectionalBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape LOWER = Shapes.box(0, 0, 0, 1, 1, 1);
    private static final VoxelShape UPPER = Shapes.box(0, 0, 0, 1, 1, 1);
    public static final MapCodec<GachaMachineBlock> CODEC = simpleCodec(GachaMachineBlock::new);

    public GachaMachineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        BlockPos pos = context.getClickedPos();
        LevelAccessor level = context.getLevel();

        if(pos.getY() < level.getMaxBuildHeight() - 1 &&  level.getBlockState(pos.above()).canBeReplaced(context)){
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        else {
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF).add(FACING);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack){
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos otherPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState otherState = level.getBlockState(otherPos);

        if (otherState.is(this)) {
            // Remove the other half unconditionally
            level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
        }

        // In survival, if breaking the upper half, explicitly drop an item at the lower half's position.
        if (!player.isCreative() && half == DoubleBlockHalf.UPPER) {
            // Drop the item at the lower half's position
            Block.popResource(level, otherPos, new ItemStack(this));
        }

        super.playerWillDestroy(level, pos, state, player);
        return otherState;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // Upper half always returns no drops.
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return Collections.emptyList();
        }

        // For the lower half, if a creative-mode player broke the block,
        // return an empty drop; otherwise, use the default loot.
        Level level = builder.getLevel();
        Vec3 origin = builder.getOptionalParameter(LootContextParams.ORIGIN);
        if (origin != null) {
            BlockPos pos = BlockPos.containing(origin);
            Player player = level.getNearestPlayer(origin.x(), origin.y(), origin.z(), 10, false);
            if (player != null && player.isCreative()) {
                return Collections.emptyList();
            }
        }
        return super.getDrops(state, builder);
    }


    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if ((half == DoubleBlockHalf.LOWER && direction == Direction.UP && !neighborState.is(this)) ||
                (half == DoubleBlockHalf.UPPER && direction == Direction.DOWN && !neighborState.is(this))){
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER : UPPER;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        DoubleBlockHalf half = state.getValue(HALF);
        ItemStack heldItem = player.getItemInHand(hand);

        if(heldItem.getItem() == Items.EMERALD && !level.isClientSide){
            if (half == DoubleBlockHalf.UPPER) {
                pos = pos.below();
            }
            ItemStack itemToEject = new ItemStack(ModItems.CAPSULE.get());

            popResourceFromFace(level, pos, hitResult.getDirection(), itemToEject);
            player.getItemInHand(hand).consume(1, player);
        }
        return ItemInteractionResult.SUCCESS;
    }
}
