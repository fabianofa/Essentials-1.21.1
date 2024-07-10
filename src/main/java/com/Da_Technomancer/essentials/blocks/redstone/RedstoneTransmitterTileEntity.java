package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.api.ESProperties;
import com.Da_Technomancer.essentials.api.ILinkTE;
import com.Da_Technomancer.essentials.api.LinkHelper;
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
import net.minecraft.world.ticks.TickPriority;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

import static com.Da_Technomancer.essentials.blocks.ESBlocks.redstoneTransmitter;

public class RedstoneTransmitterTileEntity extends BlockEntity implements ILinkTE, IRedstoneCapable{

	public static final BlockEntityType<RedstoneTransmitterTileEntity> TYPE = ESTileEntity.createType(RedstoneTransmitterTileEntity::new, redstoneTransmitter);

	public final LinkHelper linkHelper = new LinkHelper(this);

	private boolean builtConnections = false;
	//The current output, regardless of a pending update
	private float output;

	public RedstoneTransmitterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

//	@Override
//	public AABB getRenderBoundingBox(){
//		return linkHelper.frustrum();
//	}

	@Override
	public boolean canBeginLinking(){
		return true;
	}

	public void dye(DyeColor color){
		if(getBlockState().getValue(ESProperties.COLOR) != color){
			level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(ESProperties.COLOR, color));

			for(BlockPos link : linkHelper.getLinksAbsolute()){
				BlockState linkState = level.getBlockState(link);
				if(linkState.getBlock() == ESBlocks.redstoneReceiver){
					level.setBlockAndUpdate(link, linkState.setValue(ESProperties.COLOR, color));
				}
			}
		}
	}

	public float getOutput(){
		if(!builtConnections){
			buildConnections();
		}
		return output;
	}

	@Override
	public void removeLinkSource(BlockPos end){
		linkHelper.removeLink(end);
	}

	public void buildConnections(){
		//Rebuild the sources list

		if(!level.isClientSide){
			builtConnections = true;
			ArrayList<Pair<IRedstoneHandler, Direction>> preSrc = new ArrayList<>(sources.size());
			preSrc.addAll(sources);
			//Wipe old sources
			sources.clear();

			for(Direction checkDir : Direction.values()){
				BlockEntity te = level.getBlockEntity(worldPosition.relative(checkDir));
				IRedstoneHandler otherHandler = level.getCapability(RedstoneUtil.REDSTONE_CAPABILITY, worldPosition.relative(checkDir), checkDir.getOpposite());
				if(otherHandler != null){
					otherHandler.requestSrc(circRef, 0, checkDir.getOpposite(), checkDir);
				}
			}

			//if sources changed, schedule an update
			if(sources.size() != preSrc.size() || !sources.containsAll(preSrc)){
				level.scheduleTick(worldPosition, ESBlocks.redstoneTransmitter, RedstoneUtil.DELAY, TickPriority.NORMAL);
			}
		}
	}

	public void refreshOutput(){
		//Immediately recalculates the output, without a 2-tick delay
		if(!builtConnections){
			buildConnections();//Can be needed when reloading
		}

		float input = 0;
		Direction[] sidesToCheck = Direction.values();//Don't check sides for vanilla redstone w/ a circuit

		for(int i = 0; i < sources.size(); i++){
			Pair<IRedstoneHandler, Direction> ref = sources.get(i);
			IRedstoneHandler handl;
			//Remove invalid entries to speed up future checks
			if(ref == null || (handl = ref.getLeft()).isInvalid()){
				sources.remove(i);
				i--;
				continue;
			}

			sidesToCheck[ref.getRight().get3DDataValue()] = null;
			input = RedstoneUtil.chooseInput(input, RedstoneUtil.sanitize(handl.getOutput()));
		}

		//Any input without a circuit input uses vanilla redstone instead
		//Don't check any side with a circuit
		for(Direction dir : sidesToCheck){
			if(dir != null){
				input = RedstoneUtil.chooseInput(input, RedstoneUtil.getRedstoneOnSide(level, worldPosition, dir));
			}
		}

		input = RedstoneUtil.sanitize(input);

		if(RedstoneUtil.didChange(output, input)){
			output = input;
			for(BlockPos link : linkHelper.getLinksAbsolute()){
				BlockEntity te = level.getBlockEntity(link);
				if(te instanceof RedstoneReceiverTileEntity){
					((RedstoneReceiverTileEntity) te).notifyOutputChange();
				}
			}
			setChanged();
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries){
		CompoundTag nbt = super.getUpdateTag(registries);
		linkHelper.writeNBT(nbt);
		return nbt;
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		output = nbt.getFloat("out");
		linkHelper.readNBT(nbt);
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		nbt.putFloat("out", output);
		linkHelper.writeNBT(nbt);
	}

	@Override
	public BlockEntity getTE(){
		return this;
	}

	@Override
	public Color getColor(){
		return new Color(getBlockState().getValue(ESProperties.COLOR).getTextColor());
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return otherTE instanceof RedstoneReceiverTileEntity;
	}

	@Override
	public Set<BlockPos> getLinks(){
		return linkHelper.getLinksRelative();
	}

	@Override
	public int getRange(){
		return ESConfig.wirelessRange.get();
	}

	@Override
	public int getMaxLinks(){
		return 64;
	}

	@Override
	public boolean createLinkSource(ILinkTE endpoint, @Nullable Player player){
		return linkHelper.addLink(endpoint, player);
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		linkHelper.handleIncomingPacket(identifier, message);
	}

	private final IRedstoneHandler circRef = new CircuitHandler();

	private final ArrayList<Pair<IRedstoneHandler, Direction>> sources = new ArrayList<>(1);

	@Nullable
	@Override
	public IRedstoneHandler getRedstoneHandler(Direction dir){
		return circRef;
	}

	private class CircuitHandler implements IRedstoneHandler{

		@Override
		public boolean isInvalid(){
			return isRemoved();
		}

		@Override
		public float getOutput(){
			return 0;
		}

		@Override
		public void findDependents(IRedstoneHandler src, int dist, Direction fromSide, Direction nominalSide){
			if(src != null && !src.isInvalid()){
				src.addDependent(circRef, nominalSide);
				Pair<IRedstoneHandler, Direction> srcPair = Pair.of(src, fromSide);
				if(!sources.contains(srcPair)){
					sources.add(srcPair);
				}
			}
		}

		@Override
		public void requestSrc(IRedstoneHandler dependency, int dist, Direction toSide, Direction nominalSide){
			//No-Op
		}

		@Override
		public void addSrc(IRedstoneHandler src, Direction fromSide){
			Pair<IRedstoneHandler, Direction> srcPair = Pair.of(src, fromSide);
			if(!sources.contains(srcPair)){
				sources.add(srcPair);
				notifyInputChange(src);
			}
		}

		@Override
		public void addDependent(IRedstoneHandler dependent, Direction toSide){
			//No-Op
		}

		@Override
		public void notifyInputChange(IRedstoneHandler src){
			level.scheduleTick(worldPosition, ESBlocks.redstoneTransmitter, RedstoneUtil.DELAY, TickPriority.HIGH);
		}
	}
}
