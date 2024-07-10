package com.Da_Technomancer.essentials.gui.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class PulseCircuitContainer extends CircuitContainer{

	public PulseCircuitContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		super(ESContainers.PULSE_CIRCUIT_CONTAINER.get(), id, playerInventory, data);
	}

	@Override
	public int inputBars(){
		return 1;
	}
}
