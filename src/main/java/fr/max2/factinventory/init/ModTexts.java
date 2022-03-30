package fr.max2.factinventory.init;

import fr.max2.factinventory.FactinventoryMod;

public class ModTexts
{
	public static class Tooltip
	{
		public static final String
			INTERACTION_INFO = txt("tooltip", "interaction_info_on_shift"),
			INPUT = txt("tooltip", "input"),
			OUTPUT = txt("tooltip", "output"),
			INGREDIENT_INPUT = txt("tooltip", "ingredient_input"),
			FUEL_INPUT = txt("tooltip", "fuel_input"),
			PRODUCT_OUTPUT = txt("tooltip", "product_output"),
			
			TRANSFER_INFO = txt("tooltip", "transfer_info_on_ctrl"),
			NOT_TRANSFERRING = txt("tooltip", "not_transferring"),
			TRANSFERRING = txt("tooltip", "transferring_item"),
			
			LINKED_TILE = txt("tooltip", "linked_tile"),
			LINKED_DIMENSION = txt("tooltip", "linked_other_dimension"),
			LINKED_UNLOADED = txt("tooltip", "linked_unloaded"),
			LINKED_MISSING = txt("tooltip", "linked_missing"),
			NOT_LINKED = txt("tooltip", "not_linked");
	}
	
	private static String txt(String category, String id)
	{
		return category + "." + FactinventoryMod.MOD_ID + "." + id;
	}
}
