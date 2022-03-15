package fr.max2.factinventory.init;

import fr.max2.factinventory.capability.ITileEntityHandler;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModCapabilities
{
	public static void registerCappabilities()
	{
		CapabilityManager.INSTANCE.register(ITileEntityHandler.class);
	}
}
