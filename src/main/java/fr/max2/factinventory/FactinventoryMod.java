package fr.max2.factinventory;

import static fr.max2.factinventory.FactinventoryMod.*;

import fr.max2.factinventory.init.ModCapabilities;
import fr.max2.factinventory.proxy.ISidedProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MOD_ID, name = MOD_NAME, version = "1.2.2", acceptedMinecraftVersions = "[1.12.2]", dependencies = "required-after:forge@[14.23.0.2523,);")
public class FactinventoryMod
{
	public static final String MOD_ID = "factinventory", MOD_NAME = "Factinventory";
	
	@SidedProxy(clientSide = "fr.max2.factinventory.proxy.ClientProxy", serverSide = "fr.max2.factinventory.proxy.ServerProxy")
	public static ISidedProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModCapabilities.registerCappabilities();
		proxy.preInit();
	}
	
	public static ResourceLocation loc(String path)
	{
		return new ResourceLocation(MOD_ID, path);
	}
	
}
