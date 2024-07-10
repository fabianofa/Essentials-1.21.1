package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.Da_Technomancer.essentials.gui.container.CircuitContainer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class ConsCircuit extends AbstractCircuit{

	public ConsCircuit(){
		super("cons_circuit");
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.CONS_CIRCUIT_TYPE.value();
	}

	@Override
	public boolean useInput(CircuitTileEntity.Orient or){
		return false;
	}

	@Override
	public float getOutput(float in0, float in1, float in2, CircuitTileEntity te){
		if(te instanceof ConstantCircuitTileEntity){
			return ((ConstantCircuitTileEntity) te).setting;
		}

		return 0;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult hit){
		if(playerIn instanceof ServerPlayer sPlayer && worldIn.getBlockEntity(pos) instanceof ConstantCircuitTileEntity tte){
			sPlayer.openMenu(tte, buf -> CircuitContainer.encodeData(buf, tte.getBlockPos(), tte.settingStr));
		}

		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ConstantCircuitTileEntity(pos, state);
	}

	@Override
	public boolean usesQuartz(){
		return false;//Considered a 'Basic Circuit'
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.essentials.cons_circuit"));
	}
}
