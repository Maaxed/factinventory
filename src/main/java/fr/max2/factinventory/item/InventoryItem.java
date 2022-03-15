package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.client.gui.GuiRenderHandler;
import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.minecraft.world.item.Item.Properties;

public abstract class InventoryItem extends Item
{
	
	public InventoryItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (entity instanceof Player && !world.isClientSide)
		{
			Inventory inv = ((Player)entity).getInventory();
			
			if (inv.getItem(itemSlot) == stack) this.update(stack, inv, (Player)entity, itemSlot);
			
		}
		
	}

	protected abstract void update(ItemStack stack, Inventory inv, Player player, int itemSlot);
	
	@OnlyIn(Dist.CLIENT)
	public abstract List<GuiRenderHandler.Icon> getRenderIcons(ItemStack stack, AbstractContainerScreen<?> gui, Slot slot, Inventory inv);
	
	public static boolean canPush(ItemStack stack, ItemStack output, Direction face)
	{
		return output.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)
			.map(itemHandler -> canPush(stack, itemHandler))
			.orElseGet(() -> output.isEmpty() || InventoryUtils.canCombine(stack, output));
	}
	
	public static boolean canPush(ItemStack stack, IItemHandler output)
	{
		int insertSlots = output.getSlots();
		for (int insertIndex = 0; insertIndex < insertSlots; insertIndex++)
		{
			stack = output.insertItem(insertIndex, stack, true);
			if (stack.isEmpty())
			{
				return true;
			}
		}
		return false;
	}
	
	public static void push(ItemStack stack, IItemHandler output)
	{
		int insertSlots = output.getSlots();
		for (int insertIndex = 0; insertIndex < insertSlots; insertIndex++)
		{
			stack = output.insertItem(insertIndex, stack, false);
			if (stack.isEmpty())
			{
				return;
			}
		}
	}
	
	public static Slot findSlot(AbstractContainerScreen<?> gui, Slot original, int newIndex)
	{
		return gui.getMenu().slots.stream().filter(slot -> slot.isSameInventory(original) && slot.getSlotIndex() == newIndex).findAny().orElse(null);
	}
	
}
