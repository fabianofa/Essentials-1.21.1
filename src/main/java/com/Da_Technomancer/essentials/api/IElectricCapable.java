package com.Da_Technomancer.essentials.api;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public interface IElectricCapable {

	public static final IBlockCapabilityProvider<IEnergyStorage, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IElectricCapable electricCapable){
			return electricCapable.getElectricHandler(side);
		}
		return null;
	};
	
	@Nullable
	IEnergyStorage getElectricHandler(Direction dir);
}
