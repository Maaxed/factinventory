package fr.max2.factinventory.proxy;

import fr.max2.factinventory.utils.KeyModifierState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface ISidedProxy
{
	Level getWorldByDimension(ResourceKey<Level> worldKey);
	
	KeyModifierState getKeyModifierState();
}
