package fr.max2.factinventory.utils;

import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class ChatComponentUtils
{
	
	public static Component multiply(Supplier<Component> value, int amount)
	{
		TextComponent test = new TextComponent("");
		for (int i = 0; i < amount; i++)
		{
			test.append(value.get());
		}
		return test;
	}
	
	public static Component progress(int value, int max)
	{
		return new TranslatableComponent("tooltip.progress_bar.combine", multiply(() -> new TranslatableComponent("tooltip.progress_bar.full"), value), multiply(() -> new TranslatableComponent("tooltip.progress_bar.empty"), max - value));
	}
	
}
