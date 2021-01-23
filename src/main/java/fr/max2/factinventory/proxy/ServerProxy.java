package fr.max2.factinventory.proxy;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerProxy implements ISidedProxy
{
	
	@Override
	public World getWorldByDimension(RegistryKey<World> worldKey)
	{
		return ServerLifecycleHooks.getCurrentServer().getWorld(worldKey);
	}
	
}
