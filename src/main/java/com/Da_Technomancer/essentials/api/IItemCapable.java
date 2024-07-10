package com.Da_Technomancer.essentials.api;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IItemCapable{

	public static final IBlockCapabilityProvider<IItemHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IItemCapable itemCapable){
			return itemCapable.getItemHandler(side);
		}
		return null;
	};

	@Nullable
	IItemHandler getItemHandler(Direction dir);
}
