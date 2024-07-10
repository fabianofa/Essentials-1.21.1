package com.Da_Technomancer.essentials.blocks;

import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.TEBlock;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class SortingHopper extends TEBlock implements IReadable{

	public static final DirectionProperty FACING = HopperBlock.FACING;
	public static final BooleanProperty ENABLED = HopperBlock.ENABLED;

	//Taken from vanilla hopper to ensure similarity
	private static final VoxelShape INSIDE = box(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
	private static final VoxelShape INPUT_SHAPE = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape MIDDLE_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
	private static final VoxelShape INPUT_MIDDLE_SHAPE = Shapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
	private static final VoxelShape BASE = Shapes.join(INPUT_MIDDLE_SHAPE, INSIDE, BooleanOp.ONLY_FIRST);
	private static final VoxelShape DOWN_SHAPE = Shapes.or(BASE, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
	private static final VoxelShape EAST_SHAPE = Shapes.or(BASE, Block.box(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
	private static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
	private static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
	private static final VoxelShape WEST_SHAPE = Shapes.or(BASE, Block.box(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
	private static final VoxelShape DOWN_RAYTRACE_SHAPE = INSIDE;
	private static final VoxelShape EAST_RAYTRACE_SHAPE = Shapes.or(INSIDE, Block.box(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
	private static final VoxelShape NORTH_RAYTRACE_SHAPE = Shapes.or(INSIDE, Block.box(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
	private static final VoxelShape SOUTH_RAYTRACE_SHAPE = Shapes.or(INSIDE, Block.box(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
	private static final VoxelShape WEST_RAYTRACE_SHAPE = Shapes.or(INSIDE, Block.box(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));


	protected SortingHopper(Properties prop){
		super(prop);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.DOWN).setValue(ENABLED, true));
	}

	protected SortingHopper(){
		this(ESBlocks.getMetalProperty());
		String name = "sorting_hopper";
		ESBlocks.queueForRegister(name, this);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		switch(state.getValue(FACING)){
			case DOWN:
				return DOWN_SHAPE;
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case WEST:
				return WEST_SHAPE;
			case EAST:
				return EAST_SHAPE;
			default:
				return BASE;
		}
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.SORTING_HOPPER_TYPE.value();
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos){
		switch(state.getValue(FACING)){
			case DOWN:
				return DOWN_RAYTRACE_SHAPE;
			case NORTH:
				return NORTH_RAYTRACE_SHAPE;
			case SOUTH:
				return SOUTH_RAYTRACE_SHAPE;
			case WEST:
				return WEST_RAYTRACE_SHAPE;
			case EAST:
				return EAST_RAYTRACE_SHAPE;
			default:
				return INSIDE;
		}
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		Direction enumfacing = context.getClickedFace().getOpposite();
		if(enumfacing == Direction.UP){
			enumfacing = Direction.DOWN;
		}

		return defaultBlockState().setValue(FACING, enumfacing).setValue(ENABLED, !context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new SortingHopperTileEntity(pos, state);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand pHand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(ConfigUtil.isWrench(stack)){
				worldIn.setBlockAndUpdate(pos, state.cycle(FACING));//MCP note: cycle
				if(te instanceof SortingHopperTileEntity shTe){
					shTe.resetCache();
				}
				return ItemInteractionResult.SUCCESS;
			}

			if(te instanceof SortingHopperTileEntity shTe){
				playerIn.openMenu(shTe);
//				playerIn.addStat(Stats.INSPECT_HOPPER);
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState stack, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult pHitResult){
		if(!worldIn.isClientSide){
			BlockEntity te = worldIn.getBlockEntity(pos);

			if(te instanceof SortingHopperTileEntity shTe){
				playerIn.openMenu(shTe);
//				playerIn.addStat(Stats.INSPECT_HOPPER);
			}
		}
		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		boolean block = !worldIn.hasNeighborSignal(pos);

		if(block != state.getValue(ENABLED)){
			worldIn.setBlock(pos, state.setValue(ENABLED, block), 4);
		}
	}

	@Override
	public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos){
		//Enforce the vanilla hopper formula for comparators
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(worldIn.getBlockEntity(pos));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof Container inv){
			float f = 0.0F;

			for(int i = 0; i < inv.getContainerSize(); i++){
				ItemStack stack = inv.getItem(i);
				if(!stack.isEmpty()){
					f += (float) stack.getCount() / (float) Math.min(64, stack.getMaxStackSize());
				}
			}

			f = f / (float) inv.getContainerSize();
			f *= 15F;
			return f;
		}
		return 0;
	}

//	@Override
//	//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT_MIPPED;
//	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot){
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn){
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(FACING, ENABLED);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.essentials.sorting_hopper.desc"));
		tooltip.add(Component.translatable("tt.essentials.sorting_hopper.quip").setStyle(ConfigUtil.TT_QUIP));//MCP note: setStyle
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, SortingHopperTileEntity.TYPE);
	}
}
