package fr.max2.factinventory.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class StackItemHandlerProvider extends ItemStackHandler implements ICapabilitySerializable<NBTTagCompound>
{
	
	public StackItemHandlerProvider()
	{
		super();
	}
	
	
	public StackItemHandlerProvider(int size)
	{
		super(size);
	}
	
	public StackItemHandlerProvider(NonNullList<ItemStack> stacks)
	{
		super(stacks);
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
