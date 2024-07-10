package com.Da_Technomancer.essentials.blocks;

import com.Da_Technomancer.essentials.api.BlockUtil;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.TEBlock;
import com.Da_Technomancer.essentials.api.redstone.IReadable;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class SlottedChest extends TEBlock implements IReadable{

	protected SlottedChest(){
		super(Properties.of().mapColor(MapColor.WOOD).strength(2).sound(SoundType.WOOD));
		String name = "slotted_chest";
		ESBlocks.queueForRegister(name, this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new SlottedChestTileEntity(pos, state);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.SLOTTED_CHEST_TYPE.value();
	}

	@Nullable
	@Override
	protected MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos){
		return super.getMenuProvider(pState, pLevel, pPos);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player playerIn, BlockHitResult hit){
		if(!worldIn.isClientSide && playerIn instanceof ServerPlayer serverPlayer){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof SlottedChestTileEntity ste){
				ItemStack[] filter = ste.lockedInv;
				serverPlayer.openMenu(ste, (buf) -> {
					buf.writeBlockPos(pos);
					HolderLookup.Provider registries = worldIn.registryAccess();
					for(ItemStack lock : filter){
						BlockUtil.stackToBuffer(lock, buf, registries);
					}
				});
			}
		}
		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.essentials.slotted_chest.desc"));
		tooltip.add(Component.translatable("tt.essentials.slotted_chest.quip").setStyle(ConfigUtil.TT_QUIP));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof SlottedChestTileEntity ste){
			return ste.calcComparator() * 15F;
		}
		return 0;
	}
}
