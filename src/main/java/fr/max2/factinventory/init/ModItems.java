package fr.max2.factinventory.init;

import java.util.function.Function;
import java.util.function.Supplier;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.FastInventoryHopperItem;
import fr.max2.factinventory.item.InventoryDropperItem;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryLinkerItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.SlowInventoryHopperItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(bus = Bus.MOD, modid = FactinventoryMod.MOD_ID)
public class ModItems
{
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FactinventoryMod.MOD_ID);
	public static final RegistryObject<SlowInventoryHopperItem> SLOW_INVENTORY_HOPPER = register("slow_inventory_hopper", SlowInventoryHopperItem::new);
	public static final RegistryObject<FastInventoryHopperItem> FAST_INVENTORY_HOPPER = register("fast_inventory_hopper", FastInventoryHopperItem::new);
	public static final RegistryObject<InventoryFurnaceItem> INVENTORY_FURNACE = register("inventory_furnace", InventoryFurnaceItem::new);
	public static final RegistryObject<InventoryDropperItem> INVENTORY_DROPPER = register("inventory_dropper", InventoryDropperItem::new);
	public static final RegistryObject<InventoryPumpItem> INVENTORY_PUMP = register("inventory_pump", InventoryPumpItem::new);
	public static final RegistryObject<InventoryLinkerItem> INVENTORY_LINKER = register("inventory_linker", prop -> new InventoryLinkerItem(prop.tab(null)));
	public static final RegistryObject<Item> INTERACTION_MODULE = register("interaction_module", Item::new);
	
	private static final Supplier<Properties> DEFAULT_PROPERTIES = () -> new Properties().tab(ModItemGroups.ITEM_TAB);

	private static <I extends Item> RegistryObject<I> register(String name, Function<Properties, I> itemConstructor)
	{
	    return register(name, () -> itemConstructor.apply(DEFAULT_PROPERTIES.get()));
	}

	private static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item)
	{
	    return REGISTRY.register(name, item);
	}
}
