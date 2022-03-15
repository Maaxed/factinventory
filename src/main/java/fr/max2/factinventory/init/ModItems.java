package fr.max2.factinventory.init;

import java.util.function.Function;
import java.util.function.Supplier;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.model.item.RecursiveOverrideModel;
import fr.max2.factinventory.item.FastInventoryHopperItem;
import fr.max2.factinventory.item.InventoryDropperItem;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryLinkerItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.RotatableInventoryItem;
import fr.max2.factinventory.item.SlowInventoryHopperItem;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void initRendering(FMLClientSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			ItemProperties.register(INVENTORY_FURNACE.get(), InventoryFurnaceItem.BURN_TIME_GETTER_LOC, InventoryFurnaceItem.BURN_TIME_GETTER);
			ItemProperties.register(SLOW_INVENTORY_HOPPER.get(), RotatableInventoryItem.FACING_GETTER_LOC, RotatableInventoryItem.FACING_GETTER);
			ItemProperties.register(FAST_INVENTORY_HOPPER.get(), RotatableInventoryItem.FACING_GETTER_LOC, RotatableInventoryItem.FACING_GETTER);
			ItemProperties.register(INVENTORY_PUMP.get(), RotatableInventoryItem.FACING_GETTER_LOC, RotatableInventoryItem.FACING_GETTER);
			ItemProperties.register(INVENTORY_PUMP.get(), InventoryPumpItem.FILL_GETTER_LOC, InventoryPumpItem.FILL_GETTER);
		});
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerModelLoaders(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(RecursiveOverrideModel.Loader.ID, RecursiveOverrideModel.Loader.INSTANCE);
	}

	private static <I extends Item> RegistryObject<I> register(String name, Function<Properties, I> itemConstructor)
	{
	    return register(name, () -> itemConstructor.apply(DEFAULT_PROPERTIES.get()));
	}

	private static <I extends Item> RegistryObject<I> register(String name, Supplier<I> item)
	{
	    return REGISTRY.register(name, item);
	}
}
