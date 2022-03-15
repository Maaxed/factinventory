package fr.max2.factinventory.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityTileEntityHandler
{
	
	public static Capability<ITileEntityHandler> CAPABILITY_TILE = CapabilityManager.get(new CapabilityToken<>(){});
	
}
