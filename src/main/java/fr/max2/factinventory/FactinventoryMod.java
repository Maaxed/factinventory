package fr.max2.factinventory;

import static fr.max2.factinventory.FactinventoryMod.*;

import fr.max2.factinventory.init.ModItems;
import fr.max2.factinventory.proxy.ClientProxy;
import fr.max2.factinventory.proxy.ISidedProxy;
import fr.max2.factinventory.proxy.ServerProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MOD_ID)
public class FactinventoryMod
{
	public static final String MOD_ID = "factinventory";
	
	public static final ISidedProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	
	public FactinventoryMod()
	{
		ModItems.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	public static ResourceLocation loc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
}
