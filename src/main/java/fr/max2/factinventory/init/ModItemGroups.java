package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ModItemGroups
{
	public static final CreativeModeTab ITEM_TAB = new CreativeModeTab(FactinventoryMod.MOD_ID + ".inventory_item_tab")
	{
		
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon()
		{
			ItemStack iStack = new ItemStack(ModItems.SLOW_INVENTORY_HOPPER);
			return iStack;
		}
		
	};
}
