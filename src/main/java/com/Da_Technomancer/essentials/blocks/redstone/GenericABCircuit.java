package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.blocks.ESBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BaseEntityBlock;

import java.util.List;
import java.util.function.BiFunction;

public class GenericABCircuit extends AbstractCircuit{

	private final String name;
	private final String ttName;
	private final String functionName;
	private final BiFunction<Float, Float, Float> function;

	public static final MapCodec<GenericABCircuit> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("name").forGetter((GenericABCircuit circuit) -> circuit.name), Codec.STRING.fieldOf("functionName").forGetter((GenericABCircuit circuit) -> circuit.functionName)).apply(instance, GenericABCircuit::new));

	public GenericABCircuit(String name){
		this(name, name);
	}

	/**
	 * Creates a circuit to perform a pure state-based operation, with 2 distinct inputs and 1 output
	 * @param name The name of this circuit
	 * @param functionName The operation function (see AbstractCircuit.MATH_FUNCTION_MAPS); Float 1 is side input, Float 2 is back input. Output is sanitized
	 */
	public GenericABCircuit(String name, String functionName){
		super(name + "_circuit");
		this.name = name;
		this.ttName = "tt." + Essentials.MODID + "." + name + "_circuit";
		this.functionName = functionName;
		this.function = AbstractCircuit.MATH_FUNCTION_MAPS.get(functionName);
		assert function != null;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.GENERIC_AB_CIRCUIT_TYPE.value();
	}

	@Override
	public boolean useInput(CircuitTileEntity.Orient or){
		return or != CircuitTileEntity.Orient.FRONT;
	}

	@Override
	public float getOutput(float in0, float in1, float in2, CircuitTileEntity te){
		return function.apply(RedstoneUtil.chooseInput(in0, in2), in1);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable(ttName));
	}
}
