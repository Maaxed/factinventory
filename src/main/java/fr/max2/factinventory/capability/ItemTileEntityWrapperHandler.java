package fr.max2.factinventory.capability;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemTileEntityWrapperHandler implements IItemHandlerModifiable, ICapabilityProvider
{
	
	private final ItemStack stack;
	private final int slots;
	
	public ItemTileEntityWrapperHandler(ItemStack stack, int slots)
	{
		this.stack = stack;
		this.slots = slots;
	}

	private NBTTagList getItems()
	{
		if (this.stack.hasTagCompound())
		{
			NBTTagCompound tag = this.stack.getTagCompound();
			
			if (tag.hasKey("BlockEntityTag", NBT.TAG_COMPOUND))
			{
				NBTTagCompound data = tag.getCompoundTag("BlockEntityTag");
				if (data.hasKey("Items", NBT.TAG_LIST))
				{
					return data.getTagList("Items", NBT.TAG_COMPOUND);
				}
			}
		}
		return new NBTTagList();
	}
	
	private NBTTagList getOrCreateItems()
	{
		NBTTagCompound tag = this.stack.getTagCompound();
		
		if (tag == null)
		{
			tag = new NBTTagCompound();
			this.stack.setTagCompound(tag);
		}
		
		
		if (!tag.hasKey("BlockEntityTag", NBT.TAG_COMPOUND))
		{
			NBTTagCompound data = new NBTTagCompound();
			tag.setTag("BlockEntityTag", data);
		}
		
		NBTTagCompound data = tag.getCompoundTag("BlockEntityTag");
		
		if (data.hasKey("Items", NBT.TAG_LIST))
		{
			return data.getTagList("Items", NBT.TAG_COMPOUND);
		}
		else
		{
			NBTTagList items = new NBTTagList();
			data.setTag("Items", items);
			return items;
		}
	}
	
	@Override
	public int getSlots()
	{
		return this.slots;
	}

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		NBTTagList items = this.getItems();
		for (int i = 0, count = items.tagCount(); i < count; i++)
		{
			NBTTagCompound item = items.getCompoundTagAt(i);
			int index = item.getByte("Slot");
			if (index == slot)
			{
				return new ItemStack(item);
			}
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
	{
		if (!this.accepts(stack)) return stack;
		
		ItemStack actualStack = this.getStackInSlot(slot);
		
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
				this.setStackInSlot(slot, actualStack);
			}
			return remainer;
		}
		
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		ItemStack actualStack = this.getStackInSlot(slot);
		int removed = Math.min(actualStack.getCount(), amount);
		
		if (!simulate)
		{
			ItemStack newStack = actualStack.copy();
			newStack.shrink(removed);
			this.setStackInSlot(slot, newStack);
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
		NBTTagList items = this.getOrCreateItems();
		
		NBTTagCompound newItemTag = new NBTTagCompound();
		newItemTag.setByte("Slot", (byte)slot);
		stack.writeToNBT(newItemTag);
		
		for (int i = 0, count = items.tagCount(); i < count; i++)
		{
			NBTTagCompound item = items.getCompoundTagAt(i);
			int index = item.getByte("Slot");
			if (index == slot)
			{
				items.set(i, newItemTag);
				return;
			}
		}
		// if not found
		items.appendTag(newItemTag);
	}
	
	protected boolean accepts(ItemStack stack)
	{
		return true;
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
	
	public static class ShulkerBox extends ItemTileEntityWrapperHandler
	{

		public ShulkerBox(ItemStack stack, int slots)
		{
			super(stack, slots);
		}
		
		@Override
		protected boolean accepts(ItemStack stack)
		{
			return !(Block.getBlockFromItem(stack.getItem()) instanceof BlockShulkerBox);
		}
		
	}
	
}
