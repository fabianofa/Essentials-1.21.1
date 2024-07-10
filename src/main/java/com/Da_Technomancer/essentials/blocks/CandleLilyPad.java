package com.Da_Technomancer.essentials.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.List;

public class CandleLilyPad extends WaterlilyBlock{

	protected CandleLilyPad(){
		super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).instabreak().sound(SoundType.LILY_PAD).lightLevel(s -> 14).noOcclusion());
		String name = "candle_lilypad";
		ESBlocks.queueForRegister(name, this, false);
	}

	@Override
	public MapCodec<WaterlilyBlock> codec(){
		return ESBlocks.CANDLE_LILYPAD.value();
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.essentials.candle_lilypad.desc"));
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
		float f = random.nextFloat();
		if (f < 0.3F) {
			world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0.0, 0.0, 0.0);
			if (f < 0.17F) {
				world.playLocalSound(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
			}
		}

		world.addParticle(ParticleTypes.SMALL_FLAME, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0.0, 0.0, 0.0);
	}
}
