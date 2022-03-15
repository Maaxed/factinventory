package fr.max2.factinventory.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.init.ModItemGroups;
import fr.max2.factinventory.init.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.data.DataProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLanguagesProvider implements DataProvider 
{
	private final List<LanguagePartProvider> languages = new ArrayList<>();
	
	private ModLanguagesProvider(DataGenerator gen, String modId, String... locales)
	{
		for (String locale : locales)
		{
			this.languages.add(new LanguagePartProvider(gen, modId, locale));
		}
	}
	
	public ModLanguagesProvider(DataGenerator gen)
	{
		this(gen, FactinventoryMod.MOD_ID, "en_us", "fr_fr");
	}
	
	protected void addTranslations()
    {
    	// Items
		add(ModItems.INTERACTION_MODULE, "Inventory Interaction Module", "Module d'interaction avec l'inventaire");
    	add(ModItems.SLOW_INVENTORY_HOPPER, "Inventory Hopper", "Entonnoir d'inventaire");
    	add(ModItems.FAST_INVENTORY_HOPPER, "Fast Inventory Hopper", "Entonnoir d'inventaire rapide");
    	add(ModItems.INVENTORY_FURNACE, "Inventory Furnace", "Fourneau d'inventaire");
    	add(ModItems.INVENTORY_DROPPER, "Inventory Dropper", "Dropper d'inventaire");
    	add(ModItems.INVENTORY_PUMP, "Inventory Pump", "Pompe d'inventaire");
    	add(ModItems.INVENTORY_LINKER, "[WIP] Inventory linker", "[WIP] Connecteur d'inventaire");
    	
    	// ItemGroups
    	add(ModItemGroups.ITEM_TAB, "Inventory Items", "Items d'inventaire");
    	
    	add("tooltip.interaction_info_on_shift.desc", "Hold §e§lSHIFT§r for input/output details", "Maintenir §e§lMaj§r pour afficher les détails d'entrée/surtie");
    	add("tooltip.input.desc", "Input", "Entrée");
    	add("tooltip.output.desc", "Output", "Sortie");
    	add("tooltip.ingredient_input.desc", "Ingredient input", "Entrée des ingrédients");
    	add("tooltip.fuel_input.desc", "Fuel inputs", "Entrées de combustible");
    	add("tooltip.product_output.desc", "Product output", "Sortie des produits");
    	add("tooltip.progress_bar.full", "§2█§r", "§2█§r");
    	add("tooltip.progress_bar.empty", "§8█§r", "§8█§r");
    	
    	add("tooltip.smelting_info_on_ctrl.desc", "Hold §e§lCTRL§r for smelting details", "Maintenir §e§lCTRL§r pour afficher les détails de cuisson");
    	add("tooltip.not_smelting.desc", "Empty", "Vide");
    	add("tooltip.smelting_item.desc", "Smelting: %s", "En train de cuire : %s");
    	add("tooltip.not_burning.desc", "Not burning", "Ne chauffe pas");
    	add("tooltip.burning_time.desc", "Burn time remaining: %d ticks", "Brûle encore pendant : %d ticks");
    	
    	add("tooltip.transfer_info_on_ctrl.desc", "Hold §e§lCTRL§r for transfer details", "Maintenir §e§lCTRL§r pour afficher les détails de transfert");
    	add("tooltip.not_transferring.desc", "Empty", "Vide");
    	add("tooltip.transferring_item.desc", "Transferring: %s", "En train de transférer : %s");
    	add("tooltip.transfer_progress.desc", "Transfer progress:  [%s]", "Progrès du transfert : [%s]");
    	
    	add("tooltip.drop_info_on_ctrl.desc", "Hold §e§lCTRL§r for inventory details", "Maintenir §e§lCTRL§r pour afficher les détails de l'inventaire");
    	add("tooltip.not_dropping.desc", "Inventory: Empty", "Inventaire : Vide");
    	add("tooltip.dropping_item.desc", "Inventory: %s x%d", "Inventaire : %s x%d");
    	add("tooltip.drop_time.desc", "Drop time remaining: %d ticks", "Durée de drop restante : %d ticks");
    	
    	add("tooltip.linked_tile.desc", "Linked to: %s", "Lié à : %s");
    	add("tooltip.linked_other_dimension.desc", "Linked to: another dimension", "Lié à : une autre dimension");
    	add("tooltip.linked_unloaded.desc", "Linked to: unloaded block", "Lié à : bloc non chargé");
    	add("tooltip.linked_missing.desc", "Not linked: missing block", "Non lié : bloc manquant");
    	add("tooltip.not_linked.desc", "Not linked", "Non lié");
    }
	
	@Override
	public void run(HashCache cache) throws IOException
	{
		this.addTranslations();
		for (LanguageProvider language : this.languages)
		{
			language.run(cache);
		}
	}
	
	protected void add(Supplier<? extends Item> key, String... names)
	{
		add(key.get().getDescriptionId(), names);
	}
	
	protected void add(CreativeModeTab key, String... names)
	{
		add("itemGroup." + key.getRecipeFolderName(), names);
	}
	
	protected void add(String key, String... values)
	{
		for (int i = 0; i < this.languages.size(); i++)
		{
			this.languages.get(i).add(key, values[i]);
		}
	}
	
	@Override
	public String getName()
	{
		return "Factinventory Languages";
	}

	private static class LanguagePartProvider extends LanguageProvider
	{
		public LanguagePartProvider(DataGenerator gen, String modid, String locale)
		{
			super(gen, modid, locale);
		}

		@Override
		protected void addTranslations()
		{ }
	}
}
