package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.blocks.ESTileEntity;
import com.Da_Technomancer.essentials.gui.container.CircuitContainer;
import com.Da_Technomancer.essentials.gui.container.PulseCircuitContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;

import static com.Da_Technomancer.essentials.blocks.ESBlocks.*;

public class PulseCircuitTileEntity extends CircuitTileEntity implements MenuProvider, INBTReceiver, ITickableTileEntity{

	public static final BlockEntityType<PulseCircuitTileEntity> TYPE = ESTileEntity.createType(PulseCircuitTileEntity::new, pulseCircuitRising, pulseCircuitFalling, pulseCircuitDual);

	private static final int MIN_DURATION = 1;

	public int settingDuration = 1;
	public String settingStrDuration = "1";

	private long ticksExisted = 0;
	private long pulseStTime = -10;//Negative value means no pulse in progress
	private boolean hadInput = false;

	public PulseCircuitTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public float currentOutput(int offset){
		long currTime = ticksExisted + offset;
		return pulseStTime <= currTime && settingDuration > (currTime - pulseStTime - 1) / RedstoneUtil.DELAY ? 1 : 0;
	}

	private PulseCircuit.Edge getEdge(){
		Block b = getBlockState().getBlock();
		if(b instanceof PulseCircuit){
			return ((PulseCircuit) b).edge;
		}
		setRemoved();
		return PulseCircuit.Edge.RISING;
	}

	@Override
	public void handleInputChange(TickPriority priority){
		//Instead of using the vanilla block tick queue, we use our own to allow several different values to be queued in order
		float[] inputs = getInputs(getOwner());
		float input = inputs[1];

		boolean activeInput = input > 0;
		if(activeInput != hadInput){
			hadInput = activeInput;
			boolean addPulse;
			if(activeInput){
				addPulse = getEdge().start;
			}else{
				addPulse = getEdge().end;
			}

			if(addPulse){
				pulseStTime = ticksExisted;
			}
			setChanged();
		}
	}

	@Override
	public void tick(){
		ticksExisted++;
		if(!level.isClientSide && RedstoneUtil.didChange(currentOutput(-2), currentOutput(-1))){
			//Force circuits to recalculate when output changes
			recalculateOutput();
			setChanged();
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		nbt.putInt("setting_d", settingDuration);
		nbt.putString("setting_s_d", settingStrDuration);
		nbt.putLong("existed", ticksExisted);
		nbt.putLong("st_time", pulseStTime);
		nbt.putBoolean("input", hadInput);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		settingDuration = nbt.getInt("setting_d");
		settingStrDuration = nbt.getString("setting_s_d");
		ticksExisted = nbt.getLong("existed");
		pulseStTime = nbt.getLong("st_time");
		hadInput = nbt.getBoolean("input");
	}

	@Override
	public Component getDisplayName(){
		return Component.translatable("container.pulse_circuit_" + getEdge().name);
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player){
		return new PulseCircuitContainer(id, playerInv, CircuitContainer.encodeData(CircuitContainer.createEmptyBuf(), worldPosition, settingStrDuration));
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer sender){
		settingDuration = Math.max(Math.round(nbt.getFloat("value_0")), MIN_DURATION);
		settingStrDuration = nbt.getString("text_0");
		setChanged();
		recalculateOutput();
	}
}
