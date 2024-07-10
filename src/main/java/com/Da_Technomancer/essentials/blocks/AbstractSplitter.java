package com.Da_Technomancer.essentials.blocks;

import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ESProperties;
import com.Da_Technomancer.essentials.api.TEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public abstract class AbstractSplitter extends TEBlock{

	protected AbstractSplitter(String name, Properties prop){
		super(prop);
		ESBlocks.queueForRegister(name, this);
	}

	protected abstract boolean isBasic();

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag){
		if(worldIn.getBlockEntity(pos) instanceof AbstractSplitterTE<?> te){
			te.refreshCache();
		}
	}

	protected abstract Component getModeComponent(AbstractSplitterTE te, int newMode);

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit){
		if(ConfigUtil.isWrench(player.getItemInHand(hand))){
			if(!world.isClientSide){
				if(isBasic() && player.isShiftKeyDown()){
					if(world.getBlockEntity(pos) instanceof AbstractSplitterTE<?> splitter){
						int mode = splitter.increaseMode();
						player.displayClientMessage(getModeComponent(splitter, mode), true);
					}
				}else{
					world.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
				}
			}
			return ItemInteractionResult.sidedSuccess(world.isClientSide);
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(ESProperties.FACING, (context.getPlayer() == null) ? Direction.NORTH : context.getNearestLookingDirection());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}
}
