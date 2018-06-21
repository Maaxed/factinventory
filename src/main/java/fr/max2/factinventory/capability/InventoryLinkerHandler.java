package fr.max2.factinventory.capability;

import fr.max2.factinventory.item.InventoryLinkerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class InventoryLinkerHandler implements ICapabilityProvider
{
	
	private final ItemStack stack;
	
	public InventoryLinkerHandler(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		TileEntity te = InventoryLinkerItem.getLikedTileEntity(this.stack);
		if (te == null) return false;
		
		EnumFacing dir = EnumFacing.getFront(this.stack.getTagCompound().getInteger("link_direction"));
		
		return te.hasCapability(capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY : capability, dir);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		TileEntity te = InventoryLinkerItem.getLikedTileEntity(this.stack);
		if (te == null) return null;
		
		EnumFacing side = EnumFacing.getFront(this.stack.getTagCompound().getInteger("link_side"));
		
		return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(new ToFluidItemHandler(this.stack, te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))) : te.getCapability(capability, side);
	}
	
}
