package fr.max2.factinventory;

import static fr.max2.factinventory.FactinventoryMod.*;

import fr.max2.factinventory.client.model.item.ModelFluidItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MOD_ID, name = MOD_NAME, version = "1.1")
public class FactinventoryMod
{
	public static final String MOD_ID = "factinventory", MOD_NAME = "Factinventory";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModelLoaderRegistry.registerLoader(ModelFluidItem.LoaderDynBucket.INSTANCE);
	}
	
	public static ResourceLocation loc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
}
