package fr.max2.factinventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
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

	private ListTag getItems()
	{
		if (this.stack.hasTag())
		{
			CompoundTag tag = this.stack.getTag();
			
			if (tag.contains("BlockEntityTag", NBT.TAG_COMPOUND))
			{
				CompoundTag data = tag.getCompound("BlockEntityTag");
				if (data.contains("Items", NBT.TAG_LIST))
				{
					return data.getList("Items", NBT.TAG_COMPOUND);
				}
			}
		}
		return new ListTag();
	}
	
	private ListTag getOrCreateItems()
	{
		CompoundTag tag = this.stack.getTag();
		
		if (tag == null)
		{
			tag = new CompoundTag();
			this.stack.setTag(tag);
		}
		
		
		if (!tag.contains("BlockEntityTag", NBT.TAG_COMPOUND))
		{
			CompoundTag data = new CompoundTag();
			tag.put("BlockEntityTag", data);
		}
		
		CompoundTag data = tag.getCompound("BlockEntityTag");
		
		if (data.contains("Items", NBT.TAG_LIST))
		{
			return data.getList("Items", NBT.TAG_COMPOUND);
		}
		else
		{
			ListTag items = new ListTag();
			data.put("Items", items);
			return items;
		}
	}
	
	@Override
	public int getSlots()
	{
		return this.slots;
	}

	@Override
	public @Nonnull ItemStack getStackInSlot(int slot)
	{
		ListTag items = this.getItems();
		for (int i = 0, count = items.size(); i < count; i++)
		{
			CompoundTag item = items.getCompound(i);
			int index = item.getByte("Slot");
			if (index == slot)
			{
				return ItemStack.of(item);
			}
		}
		
		return ItemStack.EMPTY;
	}

	@Override
	public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
	{
		if (!this.isItemValid(slot, stack)) return stack;
		
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
	public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate)
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
	public void setStackInSlot(int slot, @Nonnull ItemStack stack)
	{
		ListTag items = this.getOrCreateItems();
		
		CompoundTag newItemTag = new CompoundTag();
		newItemTag.putByte("Slot", (byte)slot);
		stack.save(newItemTag);
		
		for (int i = 0, count = items.size(); i < count; i++)
		{
			CompoundTag item = items.getCompound(i);
			int index = item.getByte("Slot");
			if (index == slot)
			{
				if (stack.isEmpty())
				{
					items.remove(i);
				}
				else items.set(i, newItemTag);
				return;
			}
		}
		// if not found
		items.add(newItemTag);
	}
	
	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack)
	{
		return true;
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this));
	}
	
	public static class ShulkerBox extends ItemTileEntityWrapperHandler
	{

		public ShulkerBox(ItemStack stack, int slots)
		{
			super(stack, slots);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack)
		{
			return !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
		}
		
	}
	
}
