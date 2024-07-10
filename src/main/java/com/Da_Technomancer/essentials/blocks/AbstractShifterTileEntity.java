package com.Da_Technomancer.essentials.blocks;

import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.ESProperties;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class AbstractShifterTileEntity<H> extends BlockEntity implements ITickableTileEntity, MenuProvider{

	protected BlockPos endPos = null;
	protected BlockCapabilityCache<H, Direction> outputCache;

	protected <T extends AbstractShifterTileEntity<?>> AbstractShifterTileEntity(BlockEntityType<T> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	protected abstract BlockCapability<H, Direction> getCapability();

	@Override
	public void setBlockState(BlockState state){
		super.setBlockState(state);
		level.invalidateCapabilities(worldPosition);
		refreshCache();
	}

	public void refreshCache(){
		if(level instanceof ServerLevel sLevel){
			Direction dir = BlockUtil.evaluateProperty(getBlockState(), ESProperties.FACING, Direction.DOWN);
			int extension;
			int maxChutes = ESConfig.itemChuteRange.get();

			for(extension = 1; extension <= maxChutes; extension++){
				BlockState target = level.getBlockState(worldPosition.relative(dir, extension));
				if(target.getBlock() != ESBlocks.itemChute || target.getValue(ESProperties.AXIS) != dir.getAxis()){
					break;
				}
			}

			endPos = worldPosition.relative(dir, extension);
			outputCache = BlockCapabilityCache.create(getCapability(), sLevel, endPos, dir.getOpposite());
		}
	}

	public static ItemStack ejectItem(Level world, BlockPos outputPos, ItemStack stack, BlockCapabilityCache<IItemHandler, Direction> outputHandlerCache){
		if(stack.isEmpty()){
			return ItemStack.EMPTY;
		}
		
		//Capability item handlers
		IItemHandler handler = outputHandlerCache.getCapability();

		if(handler != null){
			//Found an item handler. Interact with it
			for(int i = 0; i < handler.getSlots(); i++){
				ItemStack outStack = handler.insertItem(i, stack, false);
				if(outStack.getCount() != stack.getCount()){
					return outStack;
				}
			}
			return stack;
		}

		//No item handler found
		//Drop the item in the world
		ItemEntity ent = new ItemEntity(world, outputPos.getX() + 0.5D, outputPos.getY() + 0.5D, outputPos.getZ() + 0.5D, stack);
		ent.setDeltaMovement(Vec3.ZERO);
		world.addFreshEntity(ent);
		return ItemStack.EMPTY;
	}

	public static FluidStack ejectFluid(Level world, BlockPos outputPos, FluidStack stack, BlockCapabilityCache<IFluidHandler, Direction> outputCache){
		if(stack.isEmpty()){
			return FluidStack.EMPTY;
		}

		IFluidHandler outHandler = outputCache.getCapability();
		if(outHandler != null){
			int filled = outHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
			FluidStack out = stack.copy();
			out.shrink(filled);
			return out;
		}

		return stack;
	}
}
