package fr.max2.factinventory.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class ToFluidItemHandler implements IFluidHandlerItem
{
	private final ItemStack container;
	private final IFluidHandler baseHandler;
	
	public ToFluidItemHandler(ItemStack container, IFluidHandler baseHandler)
	{
		this.container = container;
		this.baseHandler = baseHandler;
	}

	@Override
	public IFluidTankProperties[] getTankProperties()
	{
		return this.baseHandler.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill)
	{
		return this.baseHandler.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain)
	{
		return this.baseHandler.drain(resource, doDrain);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain)
	{
		return this.baseHandler.drain(maxDrain, doDrain);
	}

	@Override
	public ItemStack getContainer()
	{
		return this.container;
	}
	
}
