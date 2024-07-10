package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.blocks.ESTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.Da_Technomancer.essentials.blocks.ESBlocks.interfaceCircuit;

public class InterfaceCircuitTileEntity extends CircuitTileEntity{

	public static final BlockEntityType<InterfaceCircuitTileEntity> TYPE = ESTileEntity.createType(InterfaceCircuitTileEntity::new, interfaceCircuit);

	public InterfaceCircuitTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	/*
	 * This block having a custom tile entity is specifically to implement computercraft support,
	 * so that computercraft can set an output value that overrides normal circuit behavior
	 */

	public Float externalInput = null;

	@Override
	protected AbstractCircuit getOwner(){
		return ESBlocks.interfaceCircuit;
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		if(externalInput != null){
			nbt.putFloat("external_input", externalInput);
		}else{
			nbt.remove("external_input");
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		if(nbt.contains("external_input")){
			externalInput = nbt.getFloat("external_input");
		}else{
			externalInput = null;
		}
	}

	@Override
	public void setBlockState(BlockState state){
		externalInput = null;
		super.setBlockState(state);
	}
}
