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
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
	{
		ShapedRecipeBuilder.shapedRecipe(ModItems.INTERACTION_MODULE)
			.patternLine("GIG")
			.patternLine("IEI")
			.patternLine("GIG")
			.key('G', Tags.Items.INGOTS_GOLD)
			.key('I', Tags.Items.INGOTS_IRON)
			.key('E', Tags.Items.ENDER_PEARLS)
			.addCriterion("has_ender_pearl", hasItem(Items.ENDER_PEARL))
			.build(consumer);
		
		ShapedRecipeBuilder.shapedRecipe(ModItems.SLOW_INVENTORY_HOPPER)
			.patternLine("M")
			.patternLine("H")
			.patternLine("M")
			.key('M', ModItems.INTERACTION_MODULE)
			.key('H', Items.HOPPER)
			.addCriterion("has_interactivity_modules", hasItem(ModItems.INTERACTION_MODULE))
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.FAST_INVENTORY_HOPPER)
			.patternLine("DHD")
			.patternLine("DMD")
			.patternLine("DHD")
			.key('M', ModItems.INTERACTION_MODULE)
			.key('H', ModItems.SLOW_INVENTORY_HOPPER)
			.key('D', Tags.Items.GEMS_DIAMOND)
			.addCriterion("has_interactivity_modules", hasItem(ModItems.INTERACTION_MODULE))
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.INVENTORY_FURNACE)
			.patternLine(" M ")
			.patternLine("MFM")
			.patternLine(" M ")
			.key('M', ModItems.INTERACTION_MODULE)
			.key('F', Items.FURNACE)
			.addCriterion("has_interactivity_modules", hasItem(ModItems.INTERACTION_MODULE))
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.INVENTORY_DROPPER)
			.patternLine(" M ")
			.patternLine("MDM")
			.patternLine(" M ")
			.key('M', ModItems.INTERACTION_MODULE)
			.key('D', Items.DROPPER)
			.addCriterion("has_interactivity_modules", hasItem(ModItems.INTERACTION_MODULE))
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(ModItems.INVENTORY_PUMP)
			.patternLine("BMB")
			.patternLine("GIG")
			.patternLine("GMG")
			.key('M', ModItems.INTERACTION_MODULE)
			.key('B', Items.BUCKET)
			.key('G', Tags.Items.INGOTS_GOLD)
			.key('I', Tags.Items.INGOTS_IRON)
			.addCriterion("has_interactivity_modules", hasItem(ModItems.INTERACTION_MODULE))
			.build(consumer);
		
		// Inventory linker is too experimental
		/*ShapedRecipeBuilder.shapedRecipe(ModItems.INVENTORY_LINKER)
			.patternLine("DMD")
			.patternLine("MEM")
			.patternLine("DMD")
			.key('M', ModItems.INTERACTION_MODULE)
			.key('D', Tags.Items.GEMS_DIAMOND)
			.key('E', Tags.Items.ENDER_PEARLS)
			.addCriterion("has_interactivity_modules", hasItem(ModItems.INTERACTION_MODULE))
			.build(consumer);*/
	}
	
	@Override
	public String getName()
	{
		return "Factinventory Recipes";
	}
	
}
