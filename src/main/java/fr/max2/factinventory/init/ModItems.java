package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryHopperItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class ModItems
{
	public static InventoryHopperItem INVENTORY_HOPPER = name("inventory_hopper", new InventoryHopperItem());
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(INVENTORY_HOPPER.setCreativeTab(ModCreativeTabs.ITEMS_TAB));
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event)
	{
		registerRender(INVENTORY_HOPPER);
	}

	@SideOnly(Side.CLIENT)
	protected static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(FactinventoryMod.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

	private static <I extends Item> I name(String name, I item)
	{
		item.setRegistryName(FactinventoryMod.MOD_ID, name).setUnlocalizedName(name);
	    return item;
	}
}
