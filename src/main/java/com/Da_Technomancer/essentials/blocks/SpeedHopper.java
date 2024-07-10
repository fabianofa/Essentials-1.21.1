package com.Da_Technomancer.essentials.blocks;

import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class SpeedHopper extends SortingHopper{

	protected SpeedHopper(){
		super(ESBlocks.getMetalProperty());
		String name = "speed_hopper";
		ESBlocks.queueForRegister(name, this);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.SPEED_HOPPER_TYPE.value();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new SpeedHopperTileEntity(pos, state);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.essentials.speed_hopper.sort"));
		tooltip.add(Component.translatable("tt.essentials.speed_hopper.desc"));
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, SpeedHopperTileEntity.TYPE);
	}
}
