package com.Da_Technomancer.essentials.gui.container;

import com.Da_Technomancer.essentials.Essentials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ESContainers{

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(BuiltInRegistries.MENU, Essentials.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<ItemShifterContainer>> ITEM_SHIFTER_CONTAINER = CONTAINERS.register("item_shifter", () -> conType(ItemShifterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<FluidShifterContainer>> FLUID_SHIFTER_CONTAINER = CONTAINERS.register("fluid_shifter", () -> conType(FluidShifterContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<SlottedChestContainer>> SLOTTED_CHEST_CONTAINER = CONTAINERS.register("slotted_chest", () -> conType(SlottedChestContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<CircuitWrenchContainer>> CIRCUIT_WRENCH_CONTAINER = CONTAINERS.register("circuit_wrench", () -> conType(CircuitWrenchContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<ConstantCircuitContainer>> CONSTANT_CIRCUIT_CONTAINER = CONTAINERS.register("cons_circuit", () -> conType(ConstantCircuitContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<TimerCircuitContainer>> TIMER_CIRCUIT_CONTAINER = CONTAINERS.register("timer_circuit", () -> conType(TimerCircuitContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<DelayCircuitContainer>> DELAY_CIRCUIT_CONTAINER = CONTAINERS.register("delay_circuit", () -> conType(DelayCircuitContainer::new));
	public static final DeferredHolder<MenuType<?>, MenuType<PulseCircuitContainer>> PULSE_CIRCUIT_CONTAINER = CONTAINERS.register("pulse_circuit", () -> conType(PulseCircuitContainer::new));


	private static <T extends AbstractContainerMenu> MenuType<T> conType(IContainerFactory<T> cons){
		return new MenuType<>(cons, FeatureFlags.VANILLA_SET);
	}

	public static void init(IEventBus modBus){
		CONTAINERS.register(modBus);
	}
}
