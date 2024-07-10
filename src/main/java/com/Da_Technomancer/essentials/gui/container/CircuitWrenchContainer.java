package com.Da_Technomancer.essentials.gui.container;

import com.Da_Technomancer.essentials.items.ESItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CircuitWrenchContainer extends AbstractContainerMenu{

	public CircuitWrenchContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		super(ESContainers.CIRCUIT_WRENCH_CONTAINER.get(), id);
	}

	@Override
	public boolean stillValid(Player playerIn){
		return playerIn.getOffhandItem().getItem() == ESItems.circuitWrench || playerIn.getMainHandItem().getItem() == ESItems.circuitWrench;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int fromSlot){
		return ItemStack.EMPTY;//No-op
	}
}
