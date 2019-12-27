package fr.max2.factinventory.init;

import java.util.function.Function;
import java.util.function.Supplier;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.model.item.ModelFluidItem;
import fr.max2.factinventory.item.FastInventoryHopperItem;
import fr.max2.factinventory.item.InventoryDropperItem;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryLinkerItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.SlowInventoryHopperItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@EventBusSubscriber(bus = Bus.MOD, modid = FactinventoryMod.MOD_ID)
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
	
	private static final Supplier<Properties> DEFAULT_PROPERTIES = () -> new Properties().group(ModCreativeTabs.ITEMS_TAB);
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(
			name("slow_inventory_hopper", SlowInventoryHopperItem::new),
			name("fast_inventory_hopper", FastInventoryHopperItem::new),
			name("inventory_furnace", InventoryFurnaceItem::new),
			name("inventory_dropper", InventoryDropperItem::new),
			name("inventory_pump", InventoryPumpItem::new),
			name("inventory_linker", InventoryLinkerItem::new),
			name("interaction_module", Item::new));
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void initRendering(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(ModelFluidItem.LoaderDynFluid.INSTANCE);
	}

	private static <I extends Item> I name(String name, Function<Properties, I> itemConstructor)
	{
	    return nameItem(name, itemConstructor.apply(DEFAULT_PROPERTIES.get()));
	}

	private static <I extends Item> I nameItem(String name, I item)
	{
		item.setRegistryName(FactinventoryMod.MOD_ID, name);
	    return item;
	}
}
