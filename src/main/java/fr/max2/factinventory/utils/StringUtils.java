package fr.max2.factinventory.utils;

import net.minecraft.client.resources.I18n;

public class StringUtils
{
	
	public static String multiply(String value, int amount)
	{
		return new String(new char[amount]).replace("\0", value);
	}
	
	public static String progress(int value, int max)
	{
		return multiply(I18n.get("tooltip.progress_bar.full"), value) + multiply(I18n.get("tooltip.progress_bar.empty"), max - value);
	}
	
}
