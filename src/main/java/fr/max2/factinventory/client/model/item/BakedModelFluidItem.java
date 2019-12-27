package fr.max2.factinventory.client.model.item;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.fluids.FluidUtil;

@OnlyIn(Dist.CLIENT)
public class BakedModelFluidItem extends BakedItemModel
{
	
	private final ModelFluidItem parent;
	private final ISprite modelState;
	// contains all the baked models since they'll never change
	private final Map<String, IBakedModel> cache;
	private final VertexFormat format;
	
	public BakedModelFluidItem(ModelBakery bakery, ModelFluidItem parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
		VertexFormat format, ISprite modelState, Map<String, IBakedModel> cache, boolean untransformed,
		Function<ResourceLocation, TextureAtlasSprite> textureGetter, List<FluidItemOverride> overrides)
	{
		super(quads, particle, PerspectiveMapWrapper.getTransforms(modelState.getState()), new FluidItemOverrideHandler(bakery, parent, textureGetter, overrides, format), untransformed);
		this.parent = parent;
		this.modelState = modelState;
		this.cache = cache;
		this.format = format;
	}
	
	public static final class FluidItemOverride extends ItemOverride
	{
		private final Map<ResourceLocation, Float> mapResourceValues;
		
		public FluidItemOverride(ResourceLocation locationIn, Map<ResourceLocation, Float> propertyValues)
		{
			super(locationIn, propertyValues);
			this.mapResourceValues = propertyValues;
		}
		
		protected boolean matchesFluidItemStack(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity livingEntity)
		{
			Item item = stack.getItem();
			
			for (Entry<ResourceLocation, Float> entry : this.mapResourceValues.entrySet())
			{
				IItemPropertyGetter iitempropertygetter = item.getPropertyGetter(entry.getKey());
				if (iitempropertygetter == null || iitempropertygetter.call(stack, worldIn, livingEntity) < entry.getValue())
				{
					return false;
				}
			}
			
			return true;
		}
	}
	
	private static final class FluidItemOverrideHandler extends ItemOverrideList
	{
		
		private final ModelBakery bakery;
		
		private final List<FluidItemOverride> overrides = Lists.newArrayList();
		private final List<IBakedModel> overrideBakedModels;
		
		public FluidItemOverrideHandler(ModelBakery bakery, IUnbakedModel parent, Function<ResourceLocation, TextureAtlasSprite> textureGetter, List<FluidItemOverride> overrides, VertexFormat format)
		{
			this.bakery = bakery;
			this.overrideBakedModels = overrides.stream().map(override -> bakery.func_217846_a().get(override.getLocation())).collect(Collectors.toList());
			Collections.reverse(this.overrideBakedModels);
			
			for (int i = overrides.size() - 1; i >= 0; --i)
			{
				this.overrides.add(overrides.get(i));
			}
		}
		
		@Override
		public ImmutableList<ItemOverride> getOverrides()
		{
			return ImmutableList.copyOf(overrides);
		}
		
		@Override
		public IBakedModel getModelWithOverrides(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity)
		{
			IBakedModel base = superGetModelWithOverrides(originalModel, stack, world, entity);
			
			if (!(base instanceof BakedModelFluidItem))
				return base;
			
			return FluidUtil.getFluidContained(stack).map(fluidStack ->
			{
				BakedModelFluidItem model = (BakedModelFluidItem) base;
				
				String name = fluidStack.getFluid().getRegistryName().toString();
				
				if (!model.cache.containsKey(name))
				{
					IUnbakedModel parent = model.parent.process(ImmutableMap.of("fluid", name));
					
					IBakedModel bakedModel = parent.bake(bakery, ModelLoader.defaultTextureGetter(), model.modelState, model.format);
					model.cache.put(name, bakedModel);
					return bakedModel;
				}
				
				return model.cache.get(name);
			}).orElse(base);
		}
		
		@Nullable
		public IBakedModel superGetModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn)
		{
			for (int i = 0; i < this.overrides.size(); ++i)
			{
				FluidItemOverride itemoverride = this.overrides.get(i);
				if (itemoverride.matchesFluidItemStack(stack, worldIn, entityIn))
				{
					IBakedModel ibakedmodel = this.overrideBakedModels.get(i);
					if (ibakedmodel == null)
					{
						return model;
					}
					
					return ibakedmodel;
				}
			}
			
			return model;
		}
	}
	
}
