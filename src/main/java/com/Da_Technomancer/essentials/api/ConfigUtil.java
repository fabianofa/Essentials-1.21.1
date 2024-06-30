package com.Da_Technomancer.essentials.api;

import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ConfigUtil{
	/**
	 * A common style applied to "quip" lines in tooltips
	 */
	public static final Style TT_QUIP = Style.EMPTY.applyFormat(ChatFormatting.AQUA).withItalic(true);

	public static final ToolAction WRENCH_ACTION = ToolAction.get("wrench");//No single standard for wrench tool action name has emerged yet
	private static final TagKey<Item> WRENCH = ItemTags.create(new ResourceLocation("forge:tools/wrench"));

	/**
	 * @param stack The stack to test
	 * @return Whether this item is considered a wrench
	 */
	public static boolean isWrench(ItemStack stack){
		//Essentials prefers wrenches defined via the forge:item/wrench.json, but will also check tool actions- which some mods use to define their wrench
		return stack.is(WRENCH) || stack.canPerformAction(WRENCH_ACTION);
	}

	private static final NumberFormat plain = new DecimalFormat("0.000");
	private static final NumberFormat scientific = new DecimalFormat("0.000E0");
	private static final NumberFormat engineering = new DecimalFormat("##0.000E0");

	/**
	 * Formats floating point values for display
	 * @param value The value to format
	 * @param format The format to conform the value to. Uses the value in the config if null.
	 * @return The formatted string version, for display
	 */
	public static String formatFloat(float value, @Nullable NumberTypes format){
		if(format == null){
			format = ESConfig.numberDisplay.get();
		}
		float absValue = Math.abs(value);
		switch(format){
			case HEX:
				//This option exists mainly for debugging. It shows the entire hex definition of the float value
				return Float.toHexString(value);
			case SCIENTIFIC:
				if(absValue >= 10_000 || absValue < 0.001F){
					return scientific.format(value);
				}
				break;
			case ENGINEERING:
				if(absValue >= 10_000 || absValue < 0.001F){
					return engineering.format(value);
				}
				break;
		}
		if(absValue == 0){
			return "0";
		}
		return plain.format(value);
	}

	public enum NumberTypes{

		NORMAL(),
		SCIENTIFIC(),//Scientific notation when magnitude outside of 0.001-10000
		ENGINEERING(),//Engineering notation when magnitude outside of 0.001-10000
		HEX()//Display the raw float hexadecimal. This exists mainly for debugging. You want this? WHAT IS WRONG WITH YOU?
	}
}
