package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.api.ESProperties;
import com.Da_Technomancer.essentials.api.ILinkTE;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.ESTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;

import static com.Da_Technomancer.essentials.blocks.ESBlocks.redstoneReceiver;

public class RedstoneReceiverTileEntity extends BlockEntity implements ILinkTE, IRedstoneCapable{

	private BlockPos src = null;

	public static final BlockEntityType<RedstoneReceiverTileEntity> TYPE = ESTileEntity.createType(RedstoneReceiverTileEntity::new, redstoneReceiver);

	public RedstoneReceiverTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		//No-Op, doesn't create links
	}

	@Override
	public boolean canBeginLinking(){
		return false;
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable Player player){
		return false;//No-Op, doesn't create links
	}

	@Override
	public void removeLinkSource(BlockPos end){
		//No-op, doesn't create links
	}

	@Override
	public void createLinkEnd(ILinkTE newSrcTE){
		if(src != null){
			//Unlink from the previous source if applicable
			BlockPos worldSrc = worldPosition.offset(src);
			BlockEntity srcTE = level.getBlockEntity(worldSrc);
			if(srcTE instanceof RedstoneTransmitterTileEntity){
				((RedstoneTransmitterTileEntity) srcTE).removeLinkSource(worldPosition.subtract(worldSrc));
			}
		}
		src = newSrcTE == null ? null : newSrcTE.getTE().getBlockPos().subtract(worldPosition);
		if(newSrcTE instanceof RedstoneTransmitterTileEntity){
			//Dye this block to match the source
			BlockState srcState = newSrcTE.getTE().getBlockState();
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(ESProperties.COLOR, srcState.getBlock() == ESBlocks.redstoneTransmitter ? srcState.getValue(ESProperties.COLOR) : DyeColor.WHITE));
		}
		setChanged();
		notifyOutputChange();
	}

	@Override
	public void removeLinkEnd(BlockPos src){
		createLinkEnd(null);
	}

	public void dye(DyeColor color){
		if(level.getBlockState(worldPosition).getValue(ESProperties.COLOR) != color){
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(ESProperties.COLOR, color));
			if(src != null){
				BlockPos worldSrc = worldPosition.offset(src);
				BlockEntity srcTE = level.getBlockEntity(worldSrc);
				if(srcTE instanceof RedstoneTransmitterTileEntity transmitterTE){
					transmitterTE.dye(color);
				}
			}
		}
	}

	protected void notifyOutputChange(){
		//Notify dependents and/or neighbors that getPower output has changed
		level.updateNeighborsAt(worldPosition, ESBlocks.redstoneReceiver);
		for(int i = 0; i < dependents.size(); i++){
			IRedstoneHandler depend = dependents.get(i);
			//Validate dependent
			if(depend == null || depend.isInvalid()){
				dependents.remove(i);
				i--;
				continue;
			}
			//Notify the dependent of a change
			depend.notifyInputChange(circRef);
		}
	}

	//Rebuilds the list of dependents
	public void buildDependents(){
		dependents.clear();//Wipe the old dependents list

		//Check in all 6 directions because this block outputs in every direction
		for(Direction dir : Direction.values()){
			IRedstoneHandler otherHandler = level.getCapability(RedstoneUtil.REDSTONE_CAPABILITY, worldPosition.relative(dir), dir.getOpposite());
			if(otherHandler != null){
				otherHandler.findDependents(circRef, 0, dir.getOpposite(), dir);
			}
		}
	}

	public float getPower(){
		if(src != null){
			BlockEntity te = level.getBlockEntity(worldPosition.offset(src));
			if(te instanceof RedstoneTransmitterTileEntity transmitterTE){
				return transmitterTE.getOutput();
			}
		}
		return 0;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		if(nbt.contains("src")){
			src = BlockPos.of(nbt.getLong("src"));
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		if(src != null){
			nbt.putLong("src", src.asLong());
		}
	}

	@Override
	public BlockEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		//Receiving only
		return false;
	}

	@Override
	public HashSet<BlockPos> getLinks(){
		return new HashSet<>(1);
	}

	@Override
	public int getMaxLinks(){
		return 0;
	}

	@Override
	public int getRange(){
		return ESConfig.wirelessRange.get();
	}

	private IRedstoneHandler circRef = new CircHandler();
	private final ArrayList<IRedstoneHandler> dependents = new ArrayList<>(1);

	@Nullable
	@Override
	public IRedstoneHandler getRedstoneHandler(Direction dir){
		return circRef;
	}

	private class CircHandler implements IRedstoneHandler{

		@Override
		public boolean isInvalid(){
			return isRemoved();
		}

		@Override
		public float getOutput(){
			return getPower();
		}

		@Override
		public void findDependents(IRedstoneHandler src, int dist, Direction fromSide, Direction nominalSide){
			//No-Op
		}

		@Override
		public void requestSrc(IRedstoneHandler dependency, int dist, Direction toSide, Direction nominalSide){
			if(dependency != null && !dependency.isInvalid()){
				dependency.addSrc(circRef, nominalSide);
				if(!dependents.contains(dependency)){
					dependents.add(dependency);
				}
			}
		}

		@Override
		public void addSrc(IRedstoneHandler src, Direction fromSide){
			//No-Op
		}

		@Override
		public void addDependent(IRedstoneHandler dependent, Direction toSide){
			if(!dependents.contains(dependent)){
				dependents.add(dependent);
			}
		}

		@Override
		public void notifyInputChange(IRedstoneHandler src){
			//No-Op
		}
	}
}
