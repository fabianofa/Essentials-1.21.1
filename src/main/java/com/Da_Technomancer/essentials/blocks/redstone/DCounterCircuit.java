package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class DCounterCircuit extends AbstractCircuit{

	public DCounterCircuit(){
		super("d_counter_circuit");
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.D_COUNTER_CIRCUIT_TYPE.value();
	}

	@Override
	public boolean useInput(CircuitTileEntity.Orient or){
		return or != CircuitTileEntity.Orient.FRONT;
	}

	@Override
	public float getOutput(float in0, float in1, float in2, CircuitTileEntity te){
		if(te instanceof DCounterCircuitTileEntity){
			return ((DCounterCircuitTileEntity) te).calculateNewOutputAndUpdate(in0, in1, in2);//This is calculated by the TE for this block
		}

		return 0;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new DCounterCircuitTileEntity(pos, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.essentials.d_counter_circuit"));
	}
}
