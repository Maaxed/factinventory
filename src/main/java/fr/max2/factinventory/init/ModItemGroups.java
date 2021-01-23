package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModItemGroups
{
	public static final ItemGroup ITEM_TAB = new ItemGroup(FactinventoryMod.MOD_ID + ".inventory_item_tab")
	{
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon()
		{
			ItemStack iStack = new ItemStack(ModItems.SLOW_INVENTORY_HOPPER);
			return iStack;
		}
		
	};
}
