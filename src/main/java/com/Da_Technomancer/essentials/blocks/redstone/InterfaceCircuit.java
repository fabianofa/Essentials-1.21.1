package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class InterfaceCircuit extends GenericACircuit{

	public InterfaceCircuit(String name, boolean usesQuartz){
		super(name, name, usesQuartz);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.INTERFACE_CIRCUIT_TYPE.value();
	}

	@Override
	public float getOutput(float in0, float in1, float in2, CircuitTileEntity te){
		if(te instanceof InterfaceCircuitTileEntity ite && ite.externalInput != null){
			//Used by computercraft integration
			//Normal output is overridden by the externalInput field if applicable.
			return ite.externalInput;
		}
		return super.getOutput(in0, in1, in2, te);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new InterfaceCircuitTileEntity(pos, state);
	}
}
