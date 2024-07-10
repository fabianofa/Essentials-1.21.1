package com.Da_Technomancer.essentials.gui.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class TimerCircuitContainer extends CircuitContainer{

	public TimerCircuitContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		super(ESContainers.TIMER_CIRCUIT_CONTAINER.get(), id, playerInventory, data);
	}

	@Override
	public int inputBars(){
		return 2;
	}
}
