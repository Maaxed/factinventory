package fr.max2.factinventory.proxy;

import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public interface ISidedProxy
{
	
	World getWorldByDimension(DimensionType dim);
	
}
