package fr.max2.factinventory;

import static fr.max2.factinventory.FactinventoryMod.*;

import fr.max2.factinventory.init.ModCapabilities;
import fr.max2.factinventory.proxy.ClientProxy;
import fr.max2.factinventory.proxy.ISidedProxy;
import fr.max2.factinventory.proxy.ServerProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MOD_ID)
public class FactinventoryMod
{
	public static final String MOD_ID = "factinventory";
	
	public static final ISidedProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	
	
	public FactinventoryMod()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(FactinventoryMod::setup);
	}
	
	public static void setup(FMLCommonSetupEvent event)
	{
		ModCapabilities.registerCappabilities();
	}
	
	public static ResourceLocation loc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
}
