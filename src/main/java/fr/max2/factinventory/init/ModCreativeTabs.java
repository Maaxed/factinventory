package fr.max2.factinventory.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCreativeTabs
{
	
	public static final CreativeTabs ITEMS_TAB = new CreativeTabs("inventory_item_tab")
	{
		
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem()
		{
			ItemStack iStack = new ItemStack(ModItems.SLOW_INVENTORY_HOPPER);
			return iStack;
		}
		
	};
	
}
