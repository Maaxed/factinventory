package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.SlowInventoryHopperItem;
import fr.max2.factinventory.item.FastInventoryHopperItem;
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
	public static SlowInventoryHopperItem SLOW_INVENTORY_HOPPER = name("slow_inventory_hopper", new SlowInventoryHopperItem());
	public static FastInventoryHopperItem FAST_INVENTORY_HOPPER = name("fast_inventory_hopper", new FastInventoryHopperItem());
	public static InventoryFurnaceItem INVENTORY_FURNACE = name("inventory_furnace", new InventoryFurnaceItem());
	public static Item INTERACTION_MODULE = name("interaction_module", new Item().setCreativeTab(ModCreativeTabs.ITEMS_TAB));
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(SLOW_INVENTORY_HOPPER.setCreativeTab(ModCreativeTabs.ITEMS_TAB),
										FAST_INVENTORY_HOPPER.setCreativeTab(ModCreativeTabs.ITEMS_TAB),
										INVENTORY_FURNACE.setCreativeTab(ModCreativeTabs.ITEMS_TAB),
										INTERACTION_MODULE);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event)
	{
		registerRenderAll(SLOW_INVENTORY_HOPPER, FAST_INVENTORY_HOPPER, INVENTORY_FURNACE, INTERACTION_MODULE);
	}

	@SideOnly(Side.CLIENT)
	protected static void registerRenderAll(Item... items)
	{
		for (Item item : items)
		{
			registerRender(item);
		}
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
