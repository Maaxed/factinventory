package fr.max2.factinventory.proxy;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ServerProxy implements ISidedProxy
{

	@Override
	public void preInit()
	{ }
	
	@Override
	public World getWorldByDimension(int dim)
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
	}
	
}
