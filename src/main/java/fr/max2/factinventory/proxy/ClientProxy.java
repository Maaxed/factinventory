package fr.max2.factinventory.proxy;

import fr.max2.factinventory.client.model.item.ModelFluidItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements ISidedProxy
{
	
	@Override
	public void preInit()
	{
		ModelLoaderRegistry.registerLoader(ModelFluidItem.LoaderDynFluid.INSTANCE);
	}
	
	@Override
	public World getWorldByDimension(int dim)
	{
		World w = Minecraft.getMinecraft().world;
		return w == null ? null : (w.provider.getDimension() == dim ? w : null);
	}
	
}
