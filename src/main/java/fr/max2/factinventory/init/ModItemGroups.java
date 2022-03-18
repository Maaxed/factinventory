package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups
{
	public static final CreativeModeTab ITEM_TAB = new CreativeModeTab(FactinventoryMod.MOD_ID + ".inventory_item_tab")
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(ModItems.SLOW_INVENTORY_HOPPER.get());
		}
	};
}
