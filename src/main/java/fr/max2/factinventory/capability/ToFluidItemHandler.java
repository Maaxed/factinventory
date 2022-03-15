package fr.max2.factinventory.capability;

import javax.annotation.Nonnull;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

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
	public int getTanks()
	{
		return this.baseHandler.getTanks();
	}

	@Override
	public @Nonnull FluidStack getFluidInTank(int tank)
	{
		return this.baseHandler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank)
	{
		return this.baseHandler.getTankCapacity(tank);
	}
	
	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
	{
		return this.baseHandler.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action)
	{
		return this.baseHandler.fill(resource, action);
	}

	@Override
	public @Nonnull FluidStack drain(FluidStack resource, FluidAction action)
	{
		return this.baseHandler.drain(resource, action);
	}

	@Override
	public @Nonnull FluidStack drain(int maxDrain, FluidAction action)
	{
		return this.baseHandler.drain(maxDrain, action);
	}

	@Override
	public @Nonnull ItemStack getContainer()
	{
		return this.container;
	}
	
}
