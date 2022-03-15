package fr.max2.factinventory.init;

import fr.max2.factinventory.capability.ITileEntityHandler;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class ModCapabilities
{
	public static void registerCappabilities(RegisterCapabilitiesEvent event)
	{
		event.register(ITileEntityHandler.class);
	}
}
