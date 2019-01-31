package fr.max2.factinventory.proxy;

import net.minecraft.world.World;

public interface ISidedProxy
{
	
	void preInit();
	
	World getWorldByDimension(int dim);
	
}
