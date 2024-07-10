package com.Da_Technomancer.essentials.items;

import com.Da_Technomancer.essentials.blocks.ESBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemCandleLily extends PlaceOnWaterBlockItem{

	public ItemCandleLily(){
		super(ESBlocks.candleLilyPad, ESBlocks.itemBlockProp);
		String name = "candle_lilypad";
		ESItems.queueForRegister(name, this);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.essentials.candle_lilypad.desc"));
	}
}
