package fr.max2.factinventory.data;

import java.util.function.Consumer;

import fr.max2.factinventory.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;

public class ModRecipeProvider extends RecipeProvider
{

	public ModRecipeProvider(DataGenerator generatorIn)
	{
		super(generatorIn);
	}
	
	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
	{
		ShapedRecipeBuilder.shaped(ModItems.INTERACTION_MODULE)
			.pattern("GIG")
			.pattern("IEI")
			.pattern("GIG")
			.define('G', Tags.Items.INGOTS_GOLD)
			.define('I', Tags.Items.INGOTS_IRON)
			.define('E', Tags.Items.ENDER_PEARLS)
			.unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
			.save(consumer);
		
		ShapedRecipeBuilder.shaped(ModItems.SLOW_INVENTORY_HOPPER)
			.pattern("M")
			.pattern("H")
			.pattern("M")
			.define('M', ModItems.INTERACTION_MODULE)
			.define('H', Items.HOPPER)
			.unlockedBy("has_interactivity_modules", has(ModItems.INTERACTION_MODULE))
			.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.FAST_INVENTORY_HOPPER)
			.pattern("DHD")
			.pattern("DMD")
			.pattern("DHD")
			.define('M', ModItems.INTERACTION_MODULE)
			.define('H', ModItems.SLOW_INVENTORY_HOPPER)
			.define('D', Tags.Items.GEMS_DIAMOND)
			.unlockedBy("has_interactivity_modules", has(ModItems.INTERACTION_MODULE))
			.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.INVENTORY_FURNACE)
			.pattern(" M ")
			.pattern("MFM")
			.pattern(" M ")
			.define('M', ModItems.INTERACTION_MODULE)
			.define('F', Items.FURNACE)
			.unlockedBy("has_interactivity_modules", has(ModItems.INTERACTION_MODULE))
			.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.INVENTORY_DROPPER)
			.pattern(" M ")
			.pattern("MDM")
			.pattern(" M ")
			.define('M', ModItems.INTERACTION_MODULE)
			.define('D', Items.DROPPER)
			.unlockedBy("has_interactivity_modules", has(ModItems.INTERACTION_MODULE))
			.save(consumer);

		ShapedRecipeBuilder.shaped(ModItems.INVENTORY_PUMP)
			.pattern("BMB")
			.pattern("GIG")
			.pattern("GMG")
			.define('M', ModItems.INTERACTION_MODULE)
			.define('B', Items.BUCKET)
			.define('G', Tags.Items.INGOTS_GOLD)
			.define('I', Tags.Items.INGOTS_IRON)
			.unlockedBy("has_interactivity_modules", has(ModItems.INTERACTION_MODULE))
			.save(consumer);
		
		// Inventory linker is too experimental
		/*ShapedRecipeBuilder.shaped(ModItems.INVENTORY_LINKER)
			.pattern("DMD")
			.pattern("MEM")
			.pattern("DMD")
			.define('M', ModItems.INTERACTION_MODULE)
			.define('D', Tags.Items.GEMS_DIAMOND)
			.define('E', Tags.Items.ENDER_PEARLS)
			.unlockedBy("has_interactivity_modules", has(ModItems.INTERACTION_MODULE))
			.save(consumer);*/
	}
	
	@Override
	public String getName()
	{
		return "Factinventory Recipes";
	}
	
}
