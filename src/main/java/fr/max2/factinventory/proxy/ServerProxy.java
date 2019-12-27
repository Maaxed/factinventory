package fr.max2.factinventory.proxy;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ServerProxy implements ISidedProxy
{
	
	@Override
	public World getWorldByDimension(DimensionType dim)
	{
		return ServerLifecycleHooks.getCurrentServer().getWorld(dim);
	}
	
}
