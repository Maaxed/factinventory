package fr.max2.factinventory.handler;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.capability.ItemTileEntityWrapperHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class CapabilityHandler
{
	
	public static final ResourceLocation WRAPPER_NAME = new ResourceLocation(FactinventoryMod.MOD_ID, "tile_entity_wrapper");
	
	@SubscribeEvent
	public static void attachCapabilitiesToItem(AttachCapabilitiesEvent<ItemStack> event)
	{
		ItemStack stack = event.getObject();
		if (Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox)
			event.addCapability(WRAPPER_NAME, new ItemTileEntityWrapperHandler.ShulkerBox(stack, 27));
	}
	
}
