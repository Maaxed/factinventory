package fr.max2.factinventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class InventoryLinkerHandler extends SimpleTileEntityHandler implements ICapabilitySerializable<CompoundNBT>
{
	@Nullable
	private final ItemStack stack;
	
	public InventoryLinkerHandler(@Nullable ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
	{
		if (cap == CapabilityTileEntityHandler.CAPABILITY_TILE) return LazyOptional.of(() -> this).cast();
		
		TileEntity te = this.getTile();
		if (te == null) return LazyOptional.empty();
		
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && this.stack != null)
		{
			return te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.targetSide).lazyMap(fluidHandler -> new ToFluidItemHandler(this.stack, fluidHandler)).cast();
		}
		
		return te.getCapability(cap, this.targetSide);
	}
	
}
