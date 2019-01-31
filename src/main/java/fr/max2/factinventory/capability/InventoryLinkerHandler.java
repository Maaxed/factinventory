package fr.max2.factinventory.capability;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class InventoryLinkerHandler extends SimpleTileEntityHandler implements ICapabilitySerializable<NBTTagCompound>
{
	@Nullable
	private final ItemStack stack;
	
	
	public InventoryLinkerHandler()
	{
		this(null);
	}
	
	public InventoryLinkerHandler(@Nullable ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (capability == CapabilityTileEntityHandler.CAPABILITY_TILE) return true;
		
		TileEntity te = this.getTile();
		if (te == null) return false;
		
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && this.stack != null)
		{
			capability = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
		}
		
		return te.hasCapability(capability, this.targetSide);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (capability == CapabilityTileEntityHandler.CAPABILITY_TILE) return CapabilityTileEntityHandler.CAPABILITY_TILE.cast(this);
		
		TileEntity te = this.getTile();
		if (te == null) return null;
		
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY && this.stack != null)
		{
			IFluidHandlerItem handler = new ToFluidItemHandler(this.stack, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.targetSide));
			return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(handler);
		}
		
		return te.getCapability(capability, this.targetSide);
	}
	
}
