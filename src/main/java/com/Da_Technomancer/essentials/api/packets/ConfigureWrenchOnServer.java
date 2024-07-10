package com.Da_Technomancer.essentials.api.packets;

import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.items.CircuitWrench;
import com.Da_Technomancer.essentials.items.ESItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ConfigureWrenchOnServer(int modeIndex) implements CustomPacketPayload{

	public static final CustomPacketPayload.Type<ConfigureWrenchOnServer> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Essentials.MODID, "configure_wrench_server"));
	public static final StreamCodec<ByteBuf, ConfigureWrenchOnServer> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			ConfigureWrenchOnServer::modeIndex,
			ConfigureWrenchOnServer::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type(){
		return TYPE;
	}


	static void handlePacketServer(final ConfigureWrenchOnServer packet, final IPayloadContext context){
		context.enqueueWork(() -> {
			Player player = context.player();
			if(player == null){
				Essentials.logger.warn("Player was null on packet arrival");
				return;
			}
			InteractionHand hand = null;
			if(player.getMainHandItem().getItem() == ESItems.circuitWrench){
				hand = InteractionHand.MAIN_HAND;
			}else if(player.getOffhandItem().getItem() == ESItems.circuitWrench){
				hand = InteractionHand.OFF_HAND;
			}

			if(hand != null){
				ItemStack held = player.getItemInHand(hand);
				held.set(ESItems.WRENCH_SELECTION_DATA, new CircuitWrench.Selection(packet.modeIndex));
			}
		});
	}
}
