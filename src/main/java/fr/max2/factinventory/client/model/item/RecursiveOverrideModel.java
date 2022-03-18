package fr.max2.factinventory.client.model.item;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, modid = FactinventoryMod.MOD_ID, value = Dist.CLIENT)
public class RecursiveOverrideModel implements IModelGeometry<RecursiveOverrideModel>
{
	private final BlockModel baseModel;

	public RecursiveOverrideModel(BlockModel baseModel)
	{
		this.baseModel = baseModel;
	}

	@Override
	public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
	{
		return new CustomBakedModel(this.baseModel.bake(bakery, this.baseModel, spriteGetter, modelTransform, modelLocation, owner.isSideLit()));
	}

	@Override
	public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
	{
		return this.baseModel.getMaterials(modelGetter, missingTextureErrors);
	}

	@SubscribeEvent
	public static void registerModelLoader(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(RecursiveOverrideModel.Loader.ID, RecursiveOverrideModel.Loader.INSTANCE);
	}
	
	public static class CustomBakedModel extends BakedModelWrapper<BakedModel>
	{
		private final RecursiveOverrideList overrides;
		public CustomBakedModel(BakedModel originalModel)
		{
			super(originalModel);
			this.overrides = new RecursiveOverrideList(originalModel.getOverrides());
		}
		
		@Override
		public ItemOverrides getOverrides()
		{
			return this.overrides;
		}
	}
	
	public static class RecursiveOverrideList extends ItemOverrides
	{
		private final ItemOverrides base;

		public RecursiveOverrideList(ItemOverrides base)
		{
			this.base = base;
		}
		
		@Override
		@Nullable
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int pSeed)
		{
			BakedModel overrideModel = this.base.resolve(model, stack, world, livingEntity, pSeed);
			if (overrideModel == model || overrideModel == null)
				return overrideModel;
			return overrideModel.getOverrides().resolve(overrideModel, stack, world, livingEntity, pSeed);
		}
		
		@Override
		public ImmutableList<BakedOverride> getOverrides()
		{
			return this.base.getOverrides();
		}
	}
	
	public static enum Loader implements IModelLoader<RecursiveOverrideModel>
	{
		INSTANCE;
		public static final ResourceLocation ID = FactinventoryMod.loc("recursive_overrides");

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager)
		{
			// Nothing to do
		}

		@Override
		public RecursiveOverrideModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
		{
			BlockModel baseModel = deserializationContext.deserialize(GsonHelper.getAsJsonObject(modelContents, "base"), BlockModel.class);
			return new RecursiveOverrideModel(baseModel);
		}
		
	}
}
