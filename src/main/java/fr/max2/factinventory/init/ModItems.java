package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.FastInventoryHopperItem;
import fr.max2.factinventory.item.InventoryDropperItem;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryLinkerItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.SlowInventoryHopperItem;
import fr.max2.factinventory.item.mesh.IVarientMesh;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
@ObjectHolder(FactinventoryMod.MOD_ID)
public class ModItems
{
	public static final SlowInventoryHopperItem SLOW_INVENTORY_HOPPER = null;
	public static final FastInventoryHopperItem FAST_INVENTORY_HOPPER = null;
	public static final InventoryFurnaceItem INVENTORY_FURNACE = null;
	public static final InventoryDropperItem INVENTORY_DROPPER = null;
	public static final InventoryPumpItem INVENTORY_PUMP = null;
	public static final InventoryLinkerItem INVENTORY_LINKER = null;
	public static final Item INTERACTION_MODULE = null;
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		Item slowInventoryHopper = name("slow_inventory_hopper", new SlowInventoryHopperItem()),
			fastInventoryHopper = name("fast_inventory_hopper", new FastInventoryHopperItem()),
			inventoryFurnace = name("inventory_furnace", new InventoryFurnaceItem()),
			inventoryDropper = name("inventory_dropper", new InventoryDropperItem()),
			inventoryPump = name("inventory_pump", new InventoryPumpItem()),
			inventoryLinker = name("inventory_linker", new InventoryLinkerItem()),
			interactionModule = name("interaction_module", new Item());
		event.getRegistry().registerAll(slowInventoryHopper, fastInventoryHopper,
										inventoryFurnace, inventoryDropper,
										inventoryPump,
										inventoryLinker,
										interactionModule);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event)
	{
		registerRenderAll(SLOW_INVENTORY_HOPPER, FAST_INVENTORY_HOPPER,
						  INVENTORY_FURNACE, INVENTORY_DROPPER,
						  INVENTORY_LINKER,
						  INTERACTION_MODULE);
		registerCustomRender(INVENTORY_PUMP, InventoryPumpItem.MESH);
	}

	@SideOnly(Side.CLIENT)
	private static void registerRenderAll(Item... items)
	{
		for (Item item : items)
		{
			registerRender(item);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	private static void registerCustomRender(Item item, IVarientMesh state)
	{
		ResourceLocation loc = item.getRegistryName();
		ModelLoader.setCustomMeshDefinition(item, stack -> new ModelResourceLocation(loc, state.getVarient(stack)));
		for (String varient : state.varients())
		{
			ModelBakery.registerItemVariants(item, new ModelResourceLocation(loc, varient));
		}
	}

	private static <I extends Item> I name(String name, I item)
	{
		return name(name, item, ModCreativeTabs.ITEMS_TAB);
	}

	private static <I extends Item> I name(String name, I item, CreativeTabs tab)
	{
		item.setRegistryName(FactinventoryMod.MOD_ID, name).setUnlocalizedName(name).setCreativeTab(tab);
	    return item;
	}
}
