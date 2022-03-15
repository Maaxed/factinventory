package fr.max2.factinventory.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityTileEntityHandler
{
	@CapabilityInject(ITileEntityHandler.class)
	public static Capability<ITileEntityHandler> CAPABILITY_TILE;
	
	/*public static enum Storage implements IStorage<ITileEntityHandler>
	{
		INSTANCE;

		@Override
		public Tag writeNBT(Capability<ITileEntityHandler> capability, ITileEntityHandler instance, Direction side)
		{
			return ((InventoryLinkerHandler)instance).serializeNBT();
		}

		@Override
		public void readNBT(Capability<ITileEntityHandler> capability, ITileEntityHandler instance, Direction side, Tag nbt)
		{
			if (nbt instanceof CompoundTag)
				((InventoryLinkerHandler)instance).deserializeNBT((CompoundTag)nbt);
		}
		
	}*/
	
}
