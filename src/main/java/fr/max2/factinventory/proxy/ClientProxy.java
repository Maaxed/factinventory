package fr.max2.factinventory.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ISidedProxy
{
	
	@Override
	public World getWorldByDimension(RegistryKey<World> worldKey)
	{
		World w = Minecraft.getInstance().level;
		return w == null ? null : (w.dimension() == worldKey ? w : null);
	}
	
}
