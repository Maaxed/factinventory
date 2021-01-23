package fr.max2.factinventory.proxy;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

public interface ISidedProxy
{
	
	World getWorldByDimension(RegistryKey<World> worldKey);
	
}
