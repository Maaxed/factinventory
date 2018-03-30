package fr.max2.factinventory.utils;

import net.minecraft.item.ItemStack;

public class InventoryUtils
{
	
	public static boolean canCombine(ItemStack stack1, ItemStack stack2)
    {
        return stack1.getItem() == stack2.getItem() && (stack1.getMetadata() == stack2.getMetadata() && ItemStack.areItemStackTagsEqual(stack1, stack2));
    }
	
}
