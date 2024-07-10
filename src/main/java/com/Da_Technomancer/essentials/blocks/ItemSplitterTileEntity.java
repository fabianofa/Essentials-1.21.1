package com.Da_Technomancer.essentials.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.Da_Technomancer.essentials.blocks.ESBlocks.itemSplitter;

public class ItemSplitterTileEntity extends BasicItemSplitterTileEntity{

	public static final BlockEntityType<ItemSplitterTileEntity> TYPE = ESTileEntity.createType(ItemSplitterTileEntity::new, itemSplitter);

	public int redstone;

	public ItemSplitterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.saveAdditional(nbt, registries);
		nbt.putInt("reds", redstone);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		redstone = nbt.getInt("reds");
	}

	@Override
	public SplitDistribution getDistribution(){
		return SplitDistribution.FIFTEEN;
	}

	@Override
	public int getMode(){
		return redstone;
	}
} 
