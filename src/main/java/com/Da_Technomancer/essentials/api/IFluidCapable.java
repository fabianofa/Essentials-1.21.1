package com.Da_Technomancer.essentials.api;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public interface IFluidCapable {

	public static final IBlockCapabilityProvider<IFluidHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IFluidCapable fluidCapable){
			return fluidCapable.getFluidHandler(side);
		}
		return null;
	};

	@Nullable
	IFluidHandler getFluidHandler(Direction dir);
}
