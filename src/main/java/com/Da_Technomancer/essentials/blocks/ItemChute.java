package com.Da_Technomancer.essentials.blocks;

import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ESProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class ItemChute extends Block{

	private static final VoxelShape[] BB = new VoxelShape[] {box(0, 2, 2, 16, 14, 14), box(2, 0, 2, 14, 16, 14), box(2, 2, 0, 14, 14, 16)};

	protected ItemChute(){
		super(ESBlocks.getMetalProperty());
		String name = "item_chute";
		ESBlocks.queueForRegister(name, this);
	}

	@Override
	protected MapCodec<? extends Block> codec(){
		return ESBlocks.ITEM_CHUTE_TYPE.value();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.AXIS, context.getClickedFace().getAxis());
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(ConfigUtil.isWrench(stack)){
			if(!world.isClientSide){
				world.setBlockAndUpdate(pos, state.cycle(ESProperties.AXIS));
			}
			return ItemInteractionResult.sidedSuccess(world.isClientSide);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return BB[state.getValue(ESProperties.AXIS).ordinal()];
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.essentials.item_chute.desc"));
		tooltip.add(Component.translatable("tt.essentials.decoration"));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.AXIS);
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		//Block updates are propagated down lines of Item Chutes, allowing caching of target positions for Item Shifters
		if(fromPos != null){
			Direction.Axis axis = state.getValue(ESProperties.AXIS);
			Direction dir = Direction.getNearest(pos.getX() - fromPos.getX(), pos.getY() - fromPos.getY(), pos.getZ() - fromPos.getZ());
			if(dir.getAxis() == axis){
				fromPos = pos;
				pos = pos.relative(dir);
				worldIn.getBlockState(pos).handleNeighborChanged(worldIn, pos, this, fromPos, false);
			}
		}
	}
}
