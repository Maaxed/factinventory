package fr.max2.factinventory.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class SimpleItemTooltip implements TooltipComponent
{
	private final NonNullList<ItemStack> items;
	private final int selectedIndex;
	
	public SimpleItemTooltip(NonNullList<ItemStack> items)
	{
		this(items, 0);
	}
	
	public SimpleItemTooltip(NonNullList<ItemStack> items, int selectedIndex)
	{
		this.items = items;
		this.selectedIndex = selectedIndex;
	}
	
	public NonNullList<ItemStack> getItems()
	{
		return this.items;
	}
	
	public int getSelectedIndex()
	{
		return this.selectedIndex;
	}
}