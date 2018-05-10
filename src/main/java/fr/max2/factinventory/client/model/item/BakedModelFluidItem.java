package fr.max2.factinventory.client.model.item;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BakedModelFluidItem extends BakedItemModel
{
	
    private final ModelFluidItem parent;
    private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
    private final VertexFormat format;

    public BakedModelFluidItem(ModelFluidItem parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
    	VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache)
    {
        super(quads, particle, transforms, FluidItemOverrideHandler.INSTANCE);
        this.format = format;
        this.parent = parent;
        this.cache = cache;
    }
    
    @SideOnly(Side.CLIENT)
    private static final class FluidItemOverrideHandler extends ItemOverrideList
    {
        public static final FluidItemOverrideHandler INSTANCE = new FluidItemOverrideHandler();
        private FluidItemOverrideHandler()
        {
            super(ImmutableList.of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
        {
            FluidStack fluidStack = FluidUtil.getFluidContained(stack);
            
            // not a fluid item apparently
            if (fluidStack == null)
            {
                // empty container
                return originalModel;
            }

            BakedModelFluidItem model = (BakedModelFluidItem)originalModel;
            
            String name = fluidStack.getFluid().getName();

            if (!model.cache.containsKey(name))
            {
                IModel parent = model.parent.process(ImmutableMap.of("fluid", name));

                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format, ModelLoader.defaultTextureGetter());
                model.cache.put(name, bakedModel);
                return bakedModel;
            }

            return model.cache.get(name);
        }
    }
    
}
