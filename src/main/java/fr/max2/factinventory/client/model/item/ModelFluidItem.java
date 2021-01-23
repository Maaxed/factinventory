package fr.max2.factinventory.client.model.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.RotatableInventoryItem;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.fluid.Fluid;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelTransformComposition;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.registries.ForgeRegistries;


@OnlyIn(Dist.CLIENT)
public class ModelFluidItem //implements IModelGeometry<ModelFluidItem> //TODO fix or remove
{
	/*private static final ItemModelGenerator field_217854_z = new ItemModelGenerator();
	
    public static final ModelResourceLocation LOCATION = new ModelResourceLocation(FactinventoryMod.loc("item/dyn_fluid"), "inventory");
    
    private static final Random RAND = new Random();
    
    // minimal Z offset to prevent depth-fighting
    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;
    
    private final Fluid fluid;

    public ModelFluidItem(Fluid fluid)
    {
        this.fluid = fluid;
    }
    
    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        Set<RenderMaterial> textures = new HashSet<>();
        
        if (owner.isTexturePresent("particle")) textures.add(owner.resolveTexture("particle"));
        if (owner.isTexturePresent("base")) textures.add(owner.resolveTexture("base"));
        if (owner.isTexturePresent("fluid")) textures.add(owner.resolveTexture("fluid"));

        return textures;
    }

	@Override
	@Nullable
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
	{
		RenderMaterial particleLocation = owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : null;
        RenderMaterial baseLocation = owner.isTexturePresent("base") ? owner.resolveTexture("base") : null;
        RenderMaterial fluidMaskLocation = owner.isTexturePresent("fluid") ? owner.resolveTexture("fluid") : null;
        
        
        IModelTransform transform = new ModelTransformComposition(owner.getCombinedTransform(), modelTransform);
        TextureAtlasSprite particleSprite = null;
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        
        if (liquidLocation != null && fluid != null && fluid.getAttributes().getStillTexture() != null)
        {
            TextureAtlasSprite liquid = spriteGetter.apply(liquidLocation);
            
            //Try find fluid texture
            TextureAtlasSprite fluidSprite = spriteGetter.apply(fluid.getAttributes().getStillTexture());
            int color = fluid.getAttributes().getColor();
            builder.addAll(ItemTextureQuadConverter.convertTextureVertical(transform, liquid, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, color, 1));
            builder.addAll(ItemTextureQuadConverter.convertTextureVertical(transform, liquid, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1));
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
    public enum LoaderDynFluid implements IModelLoader<ModelFluidItem>
    {
        INSTANCE;

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // No need to clear cache since we create a new model instance
        }
        
        @Override
        public ModelFluidItem read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
        {
        	if (!modelContents.has("fluid"))
                throw new RuntimeException("Bucket model requires 'fluid' value.");
        	
        	ResourceLocation fluidName = new ResourceLocation(modelContents.get("fluid").getAsString());
        	Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
        	
            return new ModelFluidItem(fluid);
        }
    }*/
	
}
