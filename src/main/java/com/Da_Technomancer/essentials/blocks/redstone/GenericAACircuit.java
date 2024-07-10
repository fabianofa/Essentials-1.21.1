package com.Da_Technomancer.essentials.blocks.redstone;

import com.Da_Technomancer.essentials.Essentials;
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

public class GenericAACircuit extends AbstractCircuit{

	private final String name;
	private final String ttName;
	private final String functionName;
	private final BiFunction<Float, Float, Float> function;

	public static final MapCodec<GenericAACircuit> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("name").forGetter((GenericAACircuit circuit) -> circuit.name), Codec.STRING.fieldOf("functionName").forGetter((GenericAACircuit circuit) -> circuit.functionName)).apply(instance, GenericAACircuit::new));

	public GenericAACircuit(String name){
		this(name, name);
	}

	public GenericAACircuit(String name, String functionName){
		super(name + "_circuit");
		this.name = name;
		this.ttName = "tt." + Essentials.MODID + "." + name + "_circuit";
		this.functionName = functionName;
		this.function = AbstractCircuit.MATH_FUNCTION_MAPS.get(functionName);
		assert function != null;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec(){
		return ESBlocks.GENERIC_AA_CIRCUIT_TYPE.value();
	}

	@Override
	public boolean useInput(CircuitTileEntity.Orient or){
		return or == CircuitTileEntity.Orient.CCW || or == CircuitTileEntity.Orient.CW;
	}

	@Override
	public float getOutput(float in0, float in1, float in2, CircuitTileEntity te){
		return function.apply(in0, in2);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable(ttName));
	}
}
