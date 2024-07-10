package com.Da_Technomancer.essentials.api.redstone;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;

import javax.annotation.Nullable;

public interface IRedstoneCapable{

	public static final IBlockCapabilityProvider<IRedstoneHandler, Direction> CAPABLE_PROVIDER = (level, pos, state, te, side) -> {
		if(te instanceof IRedstoneCapable redstoneCapable){
			return redstoneCapable.getRedstoneHandler(side);
		}
		return null;
	};
	
	@Nullable
	IRedstoneHandler getRedstoneHandler(Direction dir);
}
