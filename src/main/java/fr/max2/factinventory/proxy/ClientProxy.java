package fr.max2.factinventory.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ISidedProxy
{
	
	@Override
	public Level getWorldByDimension(ResourceKey<Level> worldKey)
	{
		Level w = Minecraft.getInstance().level;
		return w == null ? null : (w.dimension() == worldKey ? w : null);
	}
	
}
