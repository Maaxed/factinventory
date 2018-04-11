package fr.max2.factinventory.client.model.item;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModelFluidItem implements IModel
{
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(FactinventoryMod.loc("item/dyn_fluid"), "inventory");

    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final IModel MODEL = new ModelFluidItem();

    @Nullable
    private final ResourceLocation baseLocation;
    @Nullable
    private final ResourceLocation liquidLocation;
    @Nullable
    private final Fluid fluid;

    public ModelFluidItem()
    {
        this(null, null, null);
    }

    public ModelFluidItem(@Nullable ResourceLocation baseLocation, @Nullable ResourceLocation liquidLocation, @Nullable Fluid fluid)
    {
        this.baseLocation = baseLocation;
        this.liquidLocation = liquidLocation;
        this.fluid = fluid;
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (baseLocation != null)
            builder.add(baseLocation);
        if (liquidLocation != null)
            builder.add(liquidLocation);

        return builder.build();
    }

    /**
     * Allows to use different textures for the model.
     * There are 3 layers:
     * base - The empty bucket/container
     * fluid - A texture representing the liquid portion. Non-transparent = liquid
     */
    @Override
    public ModelFluidItem retexture(ImmutableMap<String, String> textures)
    {

        ResourceLocation base = baseLocation;
        ResourceLocation liquid = liquidLocation;

        if (textures.containsKey("base"))
            base = new ResourceLocation(textures.get("base"));
        if (textures.containsKey("fluid"))
            liquid = new ResourceLocation(textures.get("fluid"));

        return new ModelFluidItem(base, liquid, this.fluid);
    }

    /**
     * Sets the liquid in the model.
     * fluid - Name of the fluid in the FluidRegistry
     */
    @Override
    public ModelFluidItem process(ImmutableMap<String, String> customData)
    {
        Fluid fluid = FluidRegistry.getFluid(customData.get("fluid"));

        if (fluid == null) fluid = this.fluid;

        // create new model with correct liquid
        return new ModelFluidItem(baseLocation, liquidLocation, fluid);
    }

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
	{
		ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
		
        TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (baseLocation != null)
        {
            IBakedModel model = (new ItemLayerModel(ImmutableList.of(baseLocation))).bake(state, format, bakedTextureGetter);
            builder.addAll(model.getQuads(null, null, 0));
            particleSprite = model.getParticleTexture();
        }
        
        if (liquidLocation != null && fluid != null)
        {
            TextureAtlasSprite liquid = bakedTextureGetter.apply(liquidLocation);
            TextureAtlasSprite fluidSprite = bakedTextureGetter.apply(fluid.getStill());
            int color = fluid.getColor();
            builder.addAll(ItemTextureQuadConverter.convertTextureVertical(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, EnumFacing.NORTH, color));
            builder.addAll(ItemTextureQuadConverter.convertTextureVertical(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, EnumFacing.SOUTH, color));
        }

        return new BakedModelFluidItem(this, builder.build(), particleSprite, format, Maps.immutableEnumMap(transformMap), Maps.newHashMap());
	}

    public enum LoaderDynBucket implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getResourceDomain().equals(FactinventoryMod.MOD_ID) && modelLocation.getResourcePath().contains("dyn_fluid");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation)
        {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // no need to clear cache since we create a new model instance
        }
    }
	
}
