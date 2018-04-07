package fr.max2.factinventory.capability;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class SingleStackItemHandler implements IItemHandlerModifiable, ICapabilityProvider
{
	
	private final ItemStack stack;
	private final String inventoryTag;
	
	public SingleStackItemHandler(ItemStack stack, String inventoryTag)
	{
		this.stack = stack;
		this.inventoryTag = inventoryTag;
	}

	private ItemStack getItem()
	{
		if (this.stack.hasTagCompound())
		{
			NBTTagCompound tag = this.stack.getTagCompound();
			
			if (tag.hasKey(this.inventoryTag, NBT.TAG_COMPOUND))
			{
				return new ItemStack(tag.getCompoundTag(this.inventoryTag));
			}
		}
		return ItemStack.EMPTY;
	}
	
	private void setItem(ItemStack stack)
	{
		NBTTagCompound items = this.getOrCreateItems();
		stack.writeToNBT(items);
	}
	
	private NBTTagCompound getOrCreateItems()
	{
		NBTTagCompound tag = this.stack.getTagCompound();
		
		if (tag == null)
		{
			tag = new NBTTagCompound();
			this.stack.setTagCompound(tag);
		}
		
		if (tag.hasKey(this.inventoryTag, NBT.TAG_COMPOUND))
		{
			return tag.getCompoundTag(this.inventoryTag);
		}
		else
		{
			NBTTagCompound item = new NBTTagCompound();
			tag.setTag(this.inventoryTag, item);
			return item;
		}
	}
	
	@Override
	public int getSlots()
	{
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot == 0)
		{
			return this.getItem();
		}
		else return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (slot != 0) return stack;
		
		ItemStack actualStack = this.getItem();
		
		if (actualStack.isEmpty())
		{
			if (!simulate) this.setStackInSlot(slot, stack);
			
			return ItemStack.EMPTY;
		}
		
		if (InventoryUtils.canCombine(actualStack, stack))
		{
			ItemStack remainer = stack.copy();
			int added = Math.min(stack.getCount(), actualStack.getMaxStackSize() - actualStack.getCount());
			remainer.shrink(added);
			
			if (!simulate)
			{
				actualStack.grow(added);
				this.setItem(actualStack);
			}
			return remainer;
		}
		
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		if (slot != 0) return ItemStack.EMPTY;
		
		ItemStack actualStack = this.getItem();
		int removed = Math.min(actualStack.getCount(), amount);
		
		if (!simulate)
		{
			ItemStack newStack = actualStack.copy();
			newStack.shrink(removed);
			this.setItem(newStack);
		}
		
		actualStack.setCount(removed);
		return actualStack;
	}

	@Override
	public int getSlotLimit(int slot)
	{
		return 64;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack)
	{
		if (slot != 0) return;
		
		this.setItem(stack);
	}
	
	

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this) : null;
	}
	
}
