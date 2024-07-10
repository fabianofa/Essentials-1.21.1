package com.Da_Technomancer.essentials.api.packets;

import com.Da_Technomancer.essentials.Essentials;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendNBTToTE(CompoundTag val, BlockPos pos) implements CustomPacketPayload{

	public static final Type<SendNBTToTE> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Essentials.MODID, "send_nbt_te"));
	public static final StreamCodec<ByteBuf, SendNBTToTE> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.COMPOUND_TAG,
			SendNBTToTE::val,
			EssentialsPackets.BLOCK_POS_CODEC,
			SendNBTToTE::pos,
			SendNBTToTE::new
	);
	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}

	static void handlePacketClient(final SendNBTToTE packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().level.isLoaded(packet.pos) && Minecraft.getInstance().level.getBlockEntity(packet.pos) instanceof INBTReceiver targetTE){
				targetTE.receiveNBT(packet.val, null);
			}
		});
	}

	static void handlePacketServer(final SendNBTToTE packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player().level().isLoaded(packet.pos) && context.player().level().getBlockEntity(packet.pos) instanceof INBTReceiver targetTE){
				targetTE.receiveNBT(packet.val, (ServerPlayer) context.player());
			}
		});
	}
}
