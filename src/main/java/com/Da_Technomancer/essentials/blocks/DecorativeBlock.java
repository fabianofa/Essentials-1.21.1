package com.Da_Technomancer.essentials.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public class DecorativeBlock extends Block{

	public static final MapCodec<DecorativeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("name").forGetter((DecorativeBlock block) -> block.name), BlockBehaviour.propertiesCodec()).apply(instance, DecorativeBlock::new));

	private final String name;

	public DecorativeBlock(String name, Properties properties){
		super(properties);
		this.name = name;
		ESBlocks.queueForRegister(name, this);
	}

	@Override
	protected MapCodec<? extends Block> codec(){
		return ESBlocks.DECORATIVE_TYPE.value();
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.essentials.decoration"));
	}
}
