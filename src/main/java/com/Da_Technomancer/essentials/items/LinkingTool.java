package com.Da_Technomancer.essentials.items;

import com.Da_Technomancer.essentials.api.LinkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelReader;

import java.util.List;

public class LinkingTool extends Item{

	public LinkingTool(){
		super(ESItems.baseItemProperties().stacksTo(1));
		String name = "linking_tool";
		ESItems.queueForRegister(name, this);
	}


	@Override
	public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		LinkHelper.LinkedPosition linked = stack.get(ESItems.LINKING_POS_DATA.get());
		if(linked != null){
			tooltip.add(Component.translatable("tt.essentials.linking.info", linked.targetPos().getX(), linked.targetPos().getY(), linked.targetPos().getZ(), linked.targetWorld()));
		}else{
			tooltip.add(Component.translatable("tt.essentials.linking.none"));
		}
		tooltip.add(Component.translatable("tt.essentials.linking.desc"));
	}
}
