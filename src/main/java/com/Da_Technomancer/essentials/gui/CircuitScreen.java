package com.Da_Technomancer.essentials.gui;

import com.Da_Technomancer.essentials.Essentials;
import com.Da_Technomancer.essentials.api.packets.SendNBTToTE;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.gui.container.CircuitContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Predicate;

public class CircuitScreen<T extends CircuitContainer> extends AbstractContainerScreen<T>{

	protected static final ResourceLocation SEARCH_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(Essentials.MODID, "textures/gui/search_bar.png");
	protected static final ResourceLocation UI_TEXTURE = ResourceLocation.fromNamespaceAndPath(Essentials.MODID, "textures/gui/circuit_filler_back.png");

	protected EditBox[] inputBars = new EditBox[menu.inputBars()];

	public CircuitScreen(T screenContainer, Inventory inv, Component titleIn){
		super(screenContainer, inv, titleIn);
		imageWidth = 176;
		imageHeight = 90;
	}

	private static final Predicate<String> validator = s -> {
		final String whitelist = "0123456789 xX*/+-^piPIeE().";
		for(int i = 0; i < s.length(); i++){
			if(!whitelist.contains(s.substring(i, i + 1))){
				return false;
			}
		}

		return true;
	};

	protected void createTextBar(int id, int x, int y, Component text){
		inputBars[id] = new EditBox(font, leftPos + x, topPos + y, 144 - 4, 18, text);
		inputBars[id].setCanLoseFocus(true);
		inputBars[id].setTextColor(-1);
		inputBars[id].setTextColorUneditable(-1);
		inputBars[id].setBordered(false);
		inputBars[id].setMaxLength(20);
		inputBars[id].setValue(menu.inputs[id]);
		inputBars[id].setResponder(this::entryChanged);
		inputBars[id].setFilter(validator);
		addWidget(inputBars[id]);
//		children.add(inputBars[id]);
//		setFocusedDefault(inputBars[id]);
	}

	@Override
	public void resize(Minecraft minecraft, int width, int height){
		String[] text = new String[inputBars.length];
		for(int i = 0; i < inputBars.length; i++){
			text[i] = inputBars[i].getValue();
		}
		init(minecraft, width, height);
		for(int i = 0; i < inputBars.length; i++){
			inputBars[i].setValue(text[i]);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers){
		if(keyCode == 256){
			minecraft.player.closeContainer();
		}

		for(EditBox bar : inputBars){
			if(bar.keyPressed(keyCode, scanCode, modifiers) || bar.canConsumeInput()){
				return true;
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(GuiGraphics matrix, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrix, mouseX, mouseY, partialTicks);
		super.render(matrix, mouseX, mouseY, partialTicks);
//		RenderSystem.disableLighting();
//		RenderSystem.disableBlend();
		for(EditBox bar : inputBars){
			bar.render(matrix, mouseX, mouseY, partialTicks);
		}
	}

	@Override
	protected void renderBg(GuiGraphics matrix, float partialTicks, int x, int y){
		//background
		matrix.blit(UI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, 90);

		//Text bars
		for(EditBox bar : inputBars){
			matrix.blit(SEARCH_BAR_TEXTURE, bar.getX() - 2, bar.getY() - 8, 0, 0, 144, 18, 144, 18);
		}

		//Text labelling input bars
		for(EditBox inputBar : inputBars){
			matrix.drawString(font, inputBar.getMessage(), inputBar.getX() - 2, inputBar.getY() - 16, 0x404040, false);
//			font.draw(matrix, inputBar.getMessage(), inputBar.getX() - 2, inputBar.getY() - 16, 0x404040);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics matrix, int x, int y){
		//Don't render text overlays
	}

	protected void entryChanged(String newFilter){
		CompoundTag nbt = new CompoundTag();

		for(int i = 0; i < inputBars.length; i++){
			float output = RedstoneUtil.interpretFormulaString(inputBars[i].getValue());
			menu.inputs[i] = inputBars[i].getValue();
			nbt.putFloat("value_" + i, output);
			nbt.putString("text_" + i, menu.inputs[i]);
		}

		if(menu.pos != null){
			PacketDistributor.sendToServer(new SendNBTToTE(nbt, menu.pos));
		}
	}
}
