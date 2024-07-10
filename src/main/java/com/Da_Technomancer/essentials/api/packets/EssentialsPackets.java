package com.Da_Technomancer.essentials.api.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.VarLong;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class EssentialsPackets{

	public static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS_CODEC = new StreamCodec<>() {
		public BlockPos decode(ByteBuf buf) {
			return BlockPos.of(VarLong.read(buf));
		}

		public void encode(ByteBuf buf, BlockPos pos) {
			VarLong.write(buf, pos.asLong());
		}
	};

	public static void init(RegisterPayloadHandlersEvent e){
		PayloadRegistrar registrar = e.registrar("1");
		registrar.playBidirectional(SendFloatToTE.TYPE, SendFloatToTE.STREAM_CODEC, new DirectionalPayloadHandler<>(SendFloatToTE::handlePacketClient, SendFloatToTE::handlePacketServer));
		registrar.playBidirectional(SendLongToTE.TYPE, SendLongToTE.STREAM_CODEC, new DirectionalPayloadHandler<>(SendLongToTE::handlePacketClient, SendLongToTE::handlePacketServer));
		registrar.playBidirectional(SendNBTToTE.TYPE, SendNBTToTE.STREAM_CODEC, new DirectionalPayloadHandler<>(SendNBTToTE::handlePacketClient, SendNBTToTE::handlePacketServer));
		registrar.playToServer(ConfigureWrenchOnServer.TYPE, ConfigureWrenchOnServer.STREAM_CODEC, ConfigureWrenchOnServer::handlePacketServer);
	}
}
