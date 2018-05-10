package fr.max2.factinventory.proxy;

import fr.max2.factinventory.client.model.item.ModelFluidItem;
import net.minecraftforge.client.model.ModelLoaderRegistry;

public class ClientProxy implements ISidedProxy
{
	
	@Override
	public void preInit()
	{
		ModelLoaderRegistry.registerLoader(ModelFluidItem.LoaderDynFluid.INSTANCE);
	}
	
}
