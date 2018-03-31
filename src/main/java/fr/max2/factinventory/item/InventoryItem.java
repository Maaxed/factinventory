package fr.max2.factinventory.item;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class InventoryItem extends Item
{
	
	public InventoryItem()
	{
		super();
		this.maxStackSize = 1;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (entity instanceof EntityPlayer && !world.isRemote)
		{
			InventoryPlayer inv = ((EntityPlayer)entity).inventory;
			
			if (inv.getStackInSlot(itemSlot) == stack) this.update(stack, inv, itemSlot);
			
		}
		
	}

	protected abstract void update(ItemStack stack, InventoryPlayer inv, int itemSlot);
	
	public static boolean canPush(ItemStack stack, ItemStack output, EnumFacing face)
	{
		if (output.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face))
			return canPush(stack, output.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face));
		else
		{
			return output.isEmpty() || InventoryUtils.canCombine(stack, output);
		}
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
	
}
