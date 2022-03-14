package fr.max2.factinventory.handler;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.capability.ItemTileEntityWrapperHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = FactinventoryMod.MOD_ID)
public class CapabilityHandler
{
	
	public static final ResourceLocation WRAPPER_NAME = new ResourceLocation(FactinventoryMod.MOD_ID, "tile_entity_wrapper");
	
	@SubscribeEvent
	public static void attachCapabilitiesToItem(AttachCapabilitiesEvent<ItemStack> event)
	{
		ItemStack stack = event.getObject();
		if (Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock)
			event.addCapability(WRAPPER_NAME, new ItemTileEntityWrapperHandler.ShulkerBox(stack, 27));
	}
	
}
