package com.Da_Technomancer.essentials;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

public class ESEventHandlerServer{

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Essentials.MODID, value = Dist.DEDICATED_SERVER)
	public static class ESModEventsServer{

	}
}
