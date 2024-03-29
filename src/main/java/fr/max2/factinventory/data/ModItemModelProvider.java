package fr.max2.factinventory.data;

import javax.annotation.Nonnull;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.init.ModItems;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import fr.max2.factinventory.item.InventoryPumpItem;
import fr.max2.factinventory.item.RotatableInventoryItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.DynamicBucketModelBuilder;

public class ModItemModelProvider extends ItemModelProvider
{

	public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper)
	{
		super(generator, FactinventoryMod.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		simpleItem(ModItems.INTERACTION_MODULE.get());
		ModelFile litFurnace = simpleItem(name(ModItems.INVENTORY_FURNACE.get()) + "_lit", extend(itemTexture(ModItems.INVENTORY_FURNACE.get()), "_lit"));
		simpleItem(ModItems.INVENTORY_FURNACE.get())
			.override()
				.predicate(InventoryFurnaceItem.BURN_TIME_GETTER_LOC, 0.5f)
				.model(litFurnace)
				.end();
		simpleItem(ModItems.INVENTORY_DROPPER.get());
		simpleItem(ModItems.INVENTORY_LINKER.get());
		
		// Ratatable items
		rotatableModel(name(ModItems.SLOW_INVENTORY_HOPPER.get()), itemTexture(ModItems.SLOW_INVENTORY_HOPPER.get()));
		rotatableModel(name(ModItems.FAST_INVENTORY_HOPPER.get()), itemTexture(ModItems.FAST_INVENTORY_HOPPER.get()));

		// Pomp
		ItemModelBuilder mainPump = nested()
			.parent(getExistingFile(mcLoc("item/generated")))
			.texture("layer0", extend(itemTexture(ModItems.INVENTORY_PUMP.get()), "/pump_0"));
		
		withExistingParent(name(ModItems.INVENTORY_PUMP.get()), new ResourceLocation("forge", "item/default"))
			.customLoader(RecursiveOverrideModelBuilder::begin)
				.base(mainPump)
				.end();
		
		for (int filled = 0; filled <= 8; filled++)
		{
			int actualFillValue = filled <= 0 ? 0 : filled <= 5 ? filled - 1 : 9 - filled;
			String fluidTextureLabel = actualFillValue + (filled > 0 && filled < 5 ? "_f" : "");
			
			ModelFile basePomp = withExistingParent(name(ModItems.INVENTORY_PUMP.get()) + "_" + filled + "_base", new ResourceLocation("forge", "item/default"))
				.customLoader(DynamicBucketModelBuilder::begin)
					.fluid(Fluids.EMPTY)
					.flipGas(false)
					.applyTint(true)
					.coverIsMask(false)
					.applyFluidLuminosity(true)
					.end()
				.texture("base", extend(itemTexture(ModItems.INVENTORY_PUMP.get()), "/pump_" + actualFillValue))
				.texture("fluid", extend(itemTexture(ModItems.INVENTORY_PUMP.get()), "/fluid_" + fluidTextureLabel));
			
			ModelFile rotatablePomp = rotatableModel(name(ModItems.INVENTORY_PUMP.get()) + "_" + filled, basePomp);
			
			mainPump.override()
				.predicate(InventoryPumpItem.FILL_GETTER_LOC, filled - 0.01f)
				.model(rotatablePomp)
				.end();
		}
	}
	
	protected ItemModelBuilder simpleItem(ItemLike entry)
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
					.transform(TransformType.GUI)
						.rotation(0, 0, 180 - dir.toYRot())
						.translation(0, 0, 0)
						.scale(1)
						.end()
					.transform(TransformType.FIRST_PERSON_RIGHT_HAND)
						.rotation(0, -90, 205 - dir.toYRot())
						.translation(1.13f, 3.2f, 1.13f)
						.scale(0.68f)
						.end()
					.transform(TransformType.FIRST_PERSON_LEFT_HAND)
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
					.predicate(RotatableInventoryItem.FACING_GETTER_LOC, dir.get2DDataValue() - 0.01f)
					.model(rotatedModel)
					.end();
			}
		}
		
		return mainModel;
	}

    protected ResourceLocation itemTexture(ItemLike entry)
    {
        ResourceLocation name = ForgeRegistries.ITEMS.getKey(entry.asItem());
        return new ResourceLocation(name.getNamespace(), (entry instanceof Block ? BLOCK_FOLDER : ITEM_FOLDER) + "/" + name.getPath());
    }

    protected String name(ItemLike entry)
    {
        return ForgeRegistries.ITEMS.getKey(entry.asItem()).getPath();
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
