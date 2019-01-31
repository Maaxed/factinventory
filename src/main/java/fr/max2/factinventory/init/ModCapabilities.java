package fr.max2.factinventory.init;

import fr.max2.factinventory.capability.CapabilityTileEntityHandler;
import fr.max2.factinventory.capability.ITileEntityHandler;
import fr.max2.factinventory.capability.SimpleTileEntityHandler;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModCapabilities
{
	public static void registerCappabilities()
	{
		CapabilityManager.INSTANCE.register(ITileEntityHandler.class, CapabilityTileEntityHandler.Storage.INSTANCE, SimpleTileEntityHandler::new);
	}
}
