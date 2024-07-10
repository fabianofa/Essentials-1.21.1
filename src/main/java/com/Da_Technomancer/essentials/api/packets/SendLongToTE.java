package com.Da_Technomancer.essentials.api.packets;

import com.Da_Technomancer.essentials.Essentials;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendLongToTE(byte id, long val, BlockPos pos) implements CustomPacketPayload{

	public static final Type<SendLongToTE> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Essentials.MODID, "send_long_te"));
	public static final StreamCodec<ByteBuf, SendLongToTE> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE,
			SendLongToTE::id,
			ByteBufCodecs.VAR_LONG,
			SendLongToTE::val,
			EssentialsPackets.BLOCK_POS_CODEC,
			SendLongToTE::pos,
			SendLongToTE::new
	);

	public SendLongToTE(int id, long val, BlockPos pos){
		this((byte) id, val, pos);
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}

	static void handlePacketClient(final SendLongToTE packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().level.isLoaded(packet.pos) && Minecraft.getInstance().level.getBlockEntity(packet.pos) instanceof ILongReceiver targetTE){
				targetTE.receiveLong(packet.id, packet.val, null);
			}
		});
	}

	static void handlePacketServer(final SendLongToTE packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player().level().isLoaded(packet.pos) && context.player().level().getBlockEntity(packet.pos) instanceof ILongReceiver targetTE){
				targetTE.receiveLong(packet.id, packet.val, (ServerPlayer) context.player());
			}
		});
	}
}
