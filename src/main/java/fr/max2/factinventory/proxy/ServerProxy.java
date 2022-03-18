package fr.max2.factinventory.proxy;

import fr.max2.factinventory.utils.KeyModifierState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class ServerProxy implements ISidedProxy
{
	
	@Override
	public Level getWorldByDimension(ResourceKey<Level> worldKey)
	{
		return ServerLifecycleHooks.getCurrentServer().getLevel(worldKey);
	}
	
	@Override
	public KeyModifierState getKeyModifierState()
	{
		return KeyModifierState.DEFAULT;
	}
	
}
