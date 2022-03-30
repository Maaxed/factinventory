package fr.max2.factinventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class StackItemHandlerProvider extends ItemStackHandler implements ICapabilitySerializable<CompoundTag>
{
	private final LazyOptional<IItemHandler> lazyHandler = LazyOptional.of(() -> this);
	
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
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, this.lazyHandler);
	}
	
}
