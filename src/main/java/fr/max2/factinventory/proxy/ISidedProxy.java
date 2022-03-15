package fr.max2.factinventory.proxy;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ISidedProxy
{
	
	Level getWorldByDimension(ResourceKey<Level> worldKey);
	
}
