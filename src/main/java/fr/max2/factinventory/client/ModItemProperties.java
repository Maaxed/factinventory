package fr.max2.factinventory.client;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.init.ModItems;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.RotatableInventoryItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = Bus.MOD, modid = FactinventoryMod.MOD_ID, value = Dist.CLIENT)
public class ModItemProperties
{
	@SubscribeEvent
	public static void registerItemProperties(FMLClientSetupEvent event)
	{
		event.enqueueWork(() ->
		{
			ItemProperties.register(ModItems.INVENTORY_FURNACE.get(), InventoryFurnaceItem.BURN_TIME_GETTER_LOC, BURN_TIME_GETTER);
			ItemProperties.register(ModItems.SLOW_INVENTORY_HOPPER.get(), RotatableInventoryItem.FACING_GETTER_LOC, FACING_GETTER);
			ItemProperties.register(ModItems.FAST_INVENTORY_HOPPER.get(), RotatableInventoryItem.FACING_GETTER_LOC, FACING_GETTER);
			ItemProperties.register(ModItems.INVENTORY_PUMP.get(), RotatableInventoryItem.FACING_GETTER_LOC, FACING_GETTER);
			ItemProperties.register(ModItems.INVENTORY_PUMP.get(), InventoryPumpItem.FILL_GETTER_LOC, FILL_GETTER);
		});
	}

	public static final ItemPropertyFunction
		BURN_TIME_GETTER = (stack, worldIn, entityIn, seed) -> InventoryFurnaceItem.getStackBurnTime(stack),
		FACING_GETTER = (stack, worldIn, entityIn, seed) -> RotatableInventoryItem.getFacing(stack).get2DDataValue(),
		FILL_GETTER = (stack, world, entity, seed) ->
		{
			IFluidHandlerItem contentCapa = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).orElse(null);
			if (contentCapa == null) return 0;
			
			FluidStack fluid = contentCapa.drain(FluidAttributes.BUCKET_VOLUME, FluidAction.SIMULATE);
			if (fluid.isEmpty()) return 0;
			
			int value = InventoryPumpItem.getTransferTime(stack);
			if (value > 8) value = 8;
			if (value < 0) value = 0;
			return value;
		};
}
