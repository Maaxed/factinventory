package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.client.gui.GuiRenderHandler;
import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class InventoryItem extends Item
{
	
	public InventoryItem(Properties properties)
	{
		super(properties.maxStackSize(1));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (entity instanceof PlayerEntity && !world.isRemote)
		{
			PlayerInventory inv = ((PlayerEntity)entity).inventory;
			
			if (inv.getStackInSlot(itemSlot) == stack) this.update(stack, inv, (PlayerEntity)entity, itemSlot);
			
		}
		
	}

	protected abstract void update(ItemStack stack, PlayerInventory inv, PlayerEntity player, int itemSlot);
	
	@OnlyIn(Dist.CLIENT)
	public abstract List<GuiRenderHandler.Icon> getRenderIcons(ItemStack stack, ContainerScreen<?> gui, Slot slot, PlayerInventory inv);
	
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
	
	public static Slot findSlot(ContainerScreen<?> gui, Slot original, int newIndex)
	{
		return gui.getContainer().inventorySlots.stream().filter(slot -> slot.isSameInventory(original) && slot.getSlotIndex() == newIndex).findAny().orElse(null);
	}
	
}
