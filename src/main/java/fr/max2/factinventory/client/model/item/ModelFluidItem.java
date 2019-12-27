package fr.max2.factinventory.client.model.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.model.item.BakedModelFluidItem.FluidItemOverride;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.RotatableInventoryItem;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.fluid.Fluid;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeBlockStateV1.Transforms;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.registries.ForgeRegistries;


@OnlyIn(Dist.CLIENT)
public class ModelFluidItem implements IUnbakedModel
{
	private static final ItemModelGenerator field_217854_z = new ItemModelGenerator();
	
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(FactinventoryMod.loc("item/dyn_fluid"), "inventory");
    
    private static final Random RAND = new Random();
    
    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public static final IUnbakedModel MODEL = new ModelFluidItem();

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
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors)
    {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        if (baseLocation != null)
            builder.add(baseLocation);
        if (liquidLocation != null)
            builder.add(liquidLocation);
        if (fluid != null)
            builder.add(fluid.getAttributes().getStillTexture());

        return builder.build();
    }

	@Override
	public Collection<ResourceLocation> getDependencies()
	{
		return ImmutableSet.of();
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
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(customData.get("fluid")));

        if (fluid == null) fluid = this.fluid;

        // create new model with correct liquid
        return new ModelFluidItem(baseLocation, liquidLocation, fluid);
    }
    
    @Override
    public IModelState getDefaultState()
    {
    	return Transforms.get("forge:default-item").get();
    }

	@Override
	@Nullable
	public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format)
	{
        TRSRTransformation transform = sprite.getState().apply(Optional.empty()).orElse(TRSRTransformation.identity());
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        
        if (liquidLocation != null && fluid != null && fluid.getAttributes().getStillTexture() != null)
        {
            TextureAtlasSprite liquid = spriteGetter.apply(liquidLocation);
            
            //Try find fluid texture
            TextureAtlasSprite fluidSprite = spriteGetter.apply(fluid.getAttributes().getStillTexture());
            int color = fluid.getAttributes().getColor();
            builder.addAll(ItemTextureQuadConverter.convertTextureVertical(format, transform, liquid, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, color, 1));
            builder.addAll(ItemTextureQuadConverter.convertTextureVertical(format, transform, liquid, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1));
            particleSprite = fluidSprite;
        }

        if (baseLocation != null)
        {
        	BlockModel baseModel = new BlockModel(null, ImmutableList.of(), ImmutableMap.of("layer0", baseLocation.toString()), false, false, ItemCameraTransforms.DEFAULT, ImmutableList.of());
            IBakedModel model = field_217854_z.makeItemModel(spriteGetter, baseModel).bake(bakery, baseModel, spriteGetter, sprite, format);
            RAND.setSeed(42);
            builder.addAll(model.getQuads(null, null, RAND));
            particleSprite = model.getParticleTexture();
        }
        
        List<FluidItemOverride> overrides = new ArrayList<>();
        
        if (baseLocation == null && liquidLocation == null && fluid == null)
        {
        	for (Direction dir : RotatableInventoryItem.ITEM_DIRECTIONS)
    		{
    			float dirValue = dir.getHorizontalIndex() - 0.01f;
    			for (int filled = 0; filled <= 8; filled++)
    			{
    				float filledValue = filled - 0.01f;
    				
    				ResourceLocation model = new ModelResourceLocation(FactinventoryMod.loc("inventory_pump_" + dir.getName() + "_" + filled), "inventory");
    				
    				overrides.add(new FluidItemOverride(model, ImmutableMap.of(RotatableInventoryItem.FACING_GETTER_LOC, dirValue, InventoryPumpItem.FILL_GETTER_LOC, filledValue)));
    			}
    		}
        }

        return new BakedModelFluidItem(bakery, this, builder.build(), particleSprite, format, sprite, Maps.newHashMap(), transform.isIdentity(), spriteGetter, overrides);
	}
	
	@OnlyIn(Dist.CLIENT)
    public enum LoaderDynFluid implements ICustomModelLoader
    {
        INSTANCE;

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            return modelLocation.getNamespace().equals(FactinventoryMod.MOD_ID) && modelLocation.getPath().contains("dyn_fluid");
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation)
        {
            return MODEL;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // No need to clear cache since we create a new model instance
        }
    }
	
}
