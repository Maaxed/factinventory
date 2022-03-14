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
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;

public class RecursiveOverrideModel implements IModelGeometry<RecursiveOverrideModel>
{
	private final BlockModel baseModel;

	public RecursiveOverrideModel(BlockModel baseModel)
	{
		this.baseModel = baseModel;
	}

	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
	{
		return new BakedModel(this.baseModel.bake(bakery, this.baseModel, spriteGetter, modelTransform, modelLocation, owner.isSideLit()));
	}

	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
	{
		return this.baseModel.getMaterials(modelGetter, missingTextureErrors);
	}
	
	public static class BakedModel extends BakedModelWrapper<IBakedModel>
	{
		private final RecursiveOverrideList overrides;
		public BakedModel(IBakedModel originalModel)
		{
			super(originalModel);
			this.overrides = new RecursiveOverrideList(originalModel.getOverrides());
		}
		
		@Override
		public ItemOverrideList getOverrides()
		{
			return this.overrides;
		}
	}
	
	public static class RecursiveOverrideList extends ItemOverrideList
	{
		private final ItemOverrideList base;

		public RecursiveOverrideList(ItemOverrideList base)
		{
			this.base = base;
		}
		
		@Override
		@Nullable
		public IBakedModel resolve(IBakedModel model, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity)
		{
			IBakedModel overrideModel = this.base.resolve(model, stack, world, livingEntity);
			if (overrideModel == model || overrideModel == null)
				return overrideModel;
			return overrideModel.getOverrides().resolve(overrideModel, stack, world, livingEntity);
		}
		
		@Override
		public ImmutableList<ItemOverride> getOverrides()
		{
			return this.base.getOverrides();
		}
	}
	
	public static enum Loader implements IModelLoader<RecursiveOverrideModel>
	{
		INSTANCE;
		public static final ResourceLocation ID = FactinventoryMod.loc("recursive_overrides");

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager)
		{
			// Nothing to do
		}

		@Override
		public RecursiveOverrideModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
		{
			BlockModel baseModel = deserializationContext.deserialize(JSONUtils.getAsJsonObject(modelContents, "base"), BlockModel.class);
			return new RecursiveOverrideModel(baseModel);
		}
		
	}
}
