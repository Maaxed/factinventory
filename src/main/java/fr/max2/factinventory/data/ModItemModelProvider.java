package fr.max2.factinventory.data;

import javax.annotation.Nonnull;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.init.ModItems;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.RotatableInventoryItem;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicBucketModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ModItemModelProvider extends ItemModelProvider
{

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, FactinventoryMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		simpleItem(ModItems.INTERACTION_MODULE);
		ModelFile litFurnace = simpleItem(name(ModItems.INVENTORY_FURNACE) + "_lit", extend(itemTexture(ModItems.INVENTORY_FURNACE), "_lit"));
		simpleItem(ModItems.INVENTORY_FURNACE)
			.override()
				.predicate(InventoryFurnaceItem.BURN_TIME_GETTER_LOC, 0.5f)
				.model(litFurnace)
				.end();
		simpleItem(ModItems.INVENTORY_DROPPER);
		simpleItem(ModItems.INVENTORY_LINKER);
		
		// Ratatable items
		rotatableModel(name(ModItems.SLOW_INVENTORY_HOPPER), itemTexture(ModItems.SLOW_INVENTORY_HOPPER));
		rotatableModel(name(ModItems.FAST_INVENTORY_HOPPER), itemTexture(ModItems.FAST_INVENTORY_HOPPER));

		// Pomp
		ItemModelBuilder mainPump = nested()
			.parent(getExistingFile(mcLoc("item/generated")))
			.texture("layer0", extend(itemTexture(ModItems.INVENTORY_PUMP), "/pump_0"));
		
		withExistingParent(name(ModItems.INVENTORY_PUMP), new ResourceLocation("forge", "item/default"))
			.customLoader(RecursiveOverrideModelBuilder::begin)
				.base(mainPump)
				.end();
		
		for (int filled = 0; filled <= 8; filled++)
		{
			int actualFillValue = filled <= 0 ? 0 : filled <= 5 ? filled - 1 : 9 - filled;
			String fluidTextureLabel = actualFillValue + (filled > 0 && filled < 5 ? "_f" : "");
			
			ModelFile basePomp = withExistingParent(name(ModItems.INVENTORY_PUMP) + "_" + filled + "_base", new ResourceLocation("forge", "item/default"))
				.customLoader(DynamicBucketModelBuilder::begin)
					.fluid(Fluids.EMPTY)
					.flipGas(false)
					.applyTint(true)
					.coverIsMask(false)
					.applyFluidLuminosity(true)
					.end()
				.texture("base", extend(itemTexture(ModItems.INVENTORY_PUMP), "/pump_" + actualFillValue))
				.texture("fluid", extend(itemTexture(ModItems.INVENTORY_PUMP), "/fluid_" + fluidTextureLabel));
			
			ModelFile rotatablePomp = rotatableModel(name(ModItems.INVENTORY_PUMP) + "_" + filled, basePomp);
			
			mainPump.override()
				.predicate(InventoryPumpItem.FILL_GETTER_LOC, filled - 0.01f)
				.model(rotatablePomp)
				.end();
		}
	}
	
	protected ItemModelBuilder simpleItem(IForgeRegistryEntry<?> entry)
	{
		return singleTexture(name(entry), mcLoc("item/generated"), "layer0", itemTexture(entry));
	}
	
	protected ItemModelBuilder simpleItem(String modelName, ResourceLocation texture)
	{
		return singleTexture(modelName, mcLoc("item/generated"), "layer0", texture);
	}
	
	protected ItemModelBuilder rotatableModel(String modelName, ResourceLocation texture)
	{
		ModelFile baseModel = simpleItem(modelName + "_base", texture);
		return rotatableModel(modelName, baseModel);
	}
	
	protected ItemModelBuilder rotatableModel(String modelName, ModelFile baseModel)
	{
		ItemModelBuilder mainModel = null;
		ItemModelBuilder mainBase = null;
		
		for (Direction dir : RotatableInventoryItem.ITEM_DIRECTIONS)
		{
			boolean isMain = dir == Direction.SOUTH;
			String modelSuffix = isMain ? "" : "_" + dir.getName();
			
			// Hoppers
			ItemModelBuilder rotatedBase = nested()
				.parent(baseModel)
				.transforms()
					.transform(Perspective.GUI)
						.rotation(0, 0, 180 - dir.toYRot())
						.translation(0, 0, 0)
						.scale(1)
						.end()
					.transform(Perspective.FIRSTPERSON_RIGHT)
						.rotation(0, -90, 205 - dir.toYRot())
						.translation(1.13f, 3.2f, 1.13f)
						.scale(0.68f)
						.end()
					.transform(Perspective.FIRSTPERSON_LEFT)
						.rotation(0, -90,  -155 + dir.toYRot())
						.translation(1.13f, 3.2f, 1.13f)
						.scale(0.68f)
						.end()
					.end();
				
			ItemModelBuilder rotatedModel = getBuilder(modelName + modelSuffix)
				.customLoader(RecursiveOverrideModelBuilder::begin)
					.base(rotatedBase)
					.end();
			
			if (isMain)
			{
				mainModel = rotatedModel;
				mainBase = rotatedBase;
			}
			else
			{
				mainBase.override()
					.predicate(RotatableInventoryItem.FACING_GETTER_LOC, dir.get3DDataValue() - 0.01f)
					.model(rotatedModel)
					.end();
			}
		}
		
		return mainModel;
	}

    protected ResourceLocation itemTexture(IForgeRegistryEntry<?> entry)
    {
        ResourceLocation name = entry.getRegistryName();
        return new ResourceLocation(name.getNamespace(), (entry instanceof Block ? BLOCK_FOLDER : ITEM_FOLDER) + "/" + name.getPath());
    }

    protected String name(IForgeRegistryEntry<?> entry)
    {
        return entry.getRegistryName().getPath();
    }
	
	private static ResourceLocation extend(ResourceLocation rl, String suffix)
	{
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}

	@Override
	@Nonnull
	public String getName()
	{
		return "Factinventory Item Models";
	}
	
}
