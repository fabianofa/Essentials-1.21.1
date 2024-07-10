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

public record SendFloatToTE(byte id, float val, BlockPos pos) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<SendFloatToTE> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Essentials.MODID, "send_float_te"));
	public static final StreamCodec<ByteBuf, SendFloatToTE> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BYTE,
			SendFloatToTE::id,
			ByteBufCodecs.FLOAT,
			SendFloatToTE::val,
			EssentialsPackets.BLOCK_POS_CODEC,
			SendFloatToTE::pos,
			SendFloatToTE::new
	);

	public SendFloatToTE(int id, float val, BlockPos pos){
		this((byte) id, val, pos);
	}

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}

	static void handlePacketClient(final SendFloatToTE packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().level.isLoaded(packet.pos) && Minecraft.getInstance().level.getBlockEntity(packet.pos) instanceof IFloatReceiver targetTE){
				targetTE.receiveFloat(packet.id, packet.val, null);
			}
		});
	}

	static void handlePacketServer(final SendFloatToTE packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			if(context.player().level().isLoaded(packet.pos) && context.player().level().getBlockEntity(packet.pos) instanceof IFloatReceiver targetTE){
				targetTE.receiveFloat(packet.id, packet.val, (ServerPlayer) context.player());
			}
		});
	}
}
