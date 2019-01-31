package fr.max2.factinventory.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityTileEntityHandler
{
	@CapabilityInject(ITileEntityHandler.class)
	public static Capability<ITileEntityHandler> CAPABILITY_TILE;
	
	public static enum Storage implements IStorage<ITileEntityHandler>
	{
		INSTANCE;

		@Override
		public NBTBase writeNBT(Capability<ITileEntityHandler> capability, ITileEntityHandler instance, EnumFacing side)
		{
			return ((InventoryLinkerHandler)instance).serializeNBT();
		}

		@Override
		public void readNBT(Capability<ITileEntityHandler> capability, ITileEntityHandler instance, EnumFacing side, NBTBase nbt)
		{
			if (nbt instanceof NBTTagCompound)
				((InventoryLinkerHandler)instance).deserializeNBT((NBTTagCompound)nbt);
		}
		
	}
	
}
