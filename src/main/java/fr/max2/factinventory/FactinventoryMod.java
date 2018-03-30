package fr.max2.factinventory;

import static fr.max2.factinventory.FactinventoryMod.*;

import fr.max2.factinventory.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = MOD_ID, name = MOD_NAME, version = "1.0")
public class FactinventoryMod
{
	public static final String MOD_ID = "factinventory", MOD_NAME = "Factinventory";
	
	@SidedProxy(clientSide = "fr.max2.factinventory.proxy.ClientProxy", serverSide = "fr.max2.factinventory.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	@Instance(MOD_ID)
	public static FactinventoryMod instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{ }
	
}
