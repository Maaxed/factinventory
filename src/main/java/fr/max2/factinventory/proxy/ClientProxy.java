package fr.max2.factinventory.proxy;

import fr.max2.factinventory.utils.KeyModifierState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ClientProxy implements ISidedProxy
{
	
	@Override
	public Level getWorldByDimension(ResourceKey<Level> worldKey)
	{
		Level w = Minecraft.getInstance().level;
		return w == null ? null : (w.dimension() == worldKey ? w : null);
	}
	
	@Override
	public KeyModifierState getKeyModifierState()
	{
		return new KeyModifierState(Screen.hasShiftDown(), Screen.hasControlDown());
	}
	
}
