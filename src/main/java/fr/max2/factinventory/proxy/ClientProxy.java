package fr.max2.factinventory.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements ISidedProxy
{
	
	@Override
	public World getWorldByDimension(DimensionType dim)
	{
		World w = Minecraft.getInstance().world;
		return w == null ? null : (w.getDimension().getType() == dim ? w : null);
	}
	
}
