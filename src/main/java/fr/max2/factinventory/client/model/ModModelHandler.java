package fr.max2.factinventory.client.model;

import com.google.common.collect.ImmutableMap;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.model.item.ModelFluidItem;
import fr.max2.factinventory.init.ModItems;
import fr.max2.factinventory.item.RotatableInventoryItem;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelStateComposition;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD)
public class ModModelHandler
{
	@SubscribeEvent
	public static void onModelBakeEvent(TextureStitchEvent.Pre event)
	{
		if (event.getMap().getBasePath().equals("textures"))
		{
			for (int filled = 0; filled <= 8; filled++)
			{
				int actualFillValue = filled <= 0 ? 0 : filled <= 5 ? filled - 1 : 9 - filled;
				String fluidTextureLabel = actualFillValue + (filled > 0 && filled < 5 ? "_f" : ""); 
				
				event.addSprite(FactinventoryMod.loc("items/inventory_pump/pump_" + actualFillValue));
				event.addSprite(FactinventoryMod.loc("items/inventory_pump/fluid_" + fluidTextureLabel));
			}
		}
	}
	
	@SubscribeEvent
	public static void onModelBakeEvent(ModelBakeEvent event)
	{
		for (Direction dir : RotatableInventoryItem.ITEM_DIRECTIONS)
		{
			for (int filled = 0; filled <= 8; filled++)
			{
				int actualFillValue = filled <= 0 ? 0 : filled <= 5 ? filled - 1 : 9 - filled;
				String fluidTextureLabel = actualFillValue + (filled > 0 && filled < 5 ? "_f" : ""); 
				
				ResourceLocation model = new ModelResourceLocation(FactinventoryMod.loc("inventory_pump_" + dir.getName() + "_" + filled), "inventory");
				
				IUnbakedModel modelBuilder = ModelLoaderRegistry.getModelOrLogError(ModelFluidItem.LOCATION, "Error loading Custom Model for model : " + model)
					.retexture(ImmutableMap.of("base", FactinventoryMod.MOD_ID + ":items/inventory_pump/pump_" + actualFillValue, "fluid", FactinventoryMod.MOD_ID + ":items/inventory_pump/fluid_" + fluidTextureLabel));
				
				IBakedModel bakedModel = modelBuilder.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new ModelStateComposition(TRSRTransformation.blockCenterToCorner(new TRSRTransformation(null, TRSRTransformation.toVecmath(new Quaternion(new Vector3f(0, 0, 1), 180 - 90 * dir.getHorizontalIndex(), true)), null, null)), modelBuilder.getDefaultState(), false), DefaultVertexFormats.ITEM);
				event.getModelRegistry().put(model, bakedModel);
			}
		}
		
		IUnbakedModel modelBuilder = ModelLoaderRegistry.getModelOrLogError(ModelFluidItem.LOCATION, "Error loading Custom Model for item : " + ModItems.INVENTORY_PUMP.getRegistryName());
		
		IBakedModel bakedModel = modelBuilder.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new BasicState(modelBuilder.getDefaultState(), false), DefaultVertexFormats.ITEM);
		event.getModelRegistry().put(new ModelResourceLocation(ModItems.INVENTORY_PUMP.getRegistryName(), "inventory"), bakedModel);
	}
}
