package fr.max2.factinventory.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.init.ModItemGroups;
import fr.max2.factinventory.init.ModItems;
import fr.max2.factinventory.init.ModTexts.Tooltip;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.contents.TranslatableContents;
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
    	
    	add(Tooltip.INTERACTION_INFO, "Hold §e§lSHIFT§r for input/output details", "Maintenir §e§lMaj§r pour afficher les détails d'entrée/surtie");
    	add(Tooltip.INPUT, "Input", "Entrée");
    	add(Tooltip.OUTPUT, "Output", "Sortie");
    	add(Tooltip.INGREDIENT_INPUT, "Ingredient input", "Entrée des ingrédients");
    	add(Tooltip.FUEL_INPUT, "Fuel inputs", "Entrées de combustible");
    	add(Tooltip.PRODUCT_OUTPUT, "Product output", "Sortie des produits");
    	
    	add(Tooltip.TRANSFER_INFO, "Hold §e§lCTRL§r for transfer details", "Maintenir §e§lCTRL§r pour afficher les détails de transfert");
    	add(Tooltip.NOT_TRANSFERRING, "Empty", "Vide");
    	add(Tooltip.TRANSFERRING, "Transferring: %s", "En train de transférer : %s");
    	
    	add(Tooltip.LINKED_TILE, "Linked to: %s", "Lié à : %s");
    	add(Tooltip.LINKED_DIMENSION, "Linked to: another dimension", "Lié à : une autre dimension");
    	add(Tooltip.LINKED_UNLOADED, "Linked to: unloaded block", "Lié à : bloc non chargé");
    	add(Tooltip.LINKED_MISSING, "Not linked: missing block", "Non lié : bloc manquant");
    	add(Tooltip.NOT_LINKED, "Not linked", "Non lié");
    }
	
	@Override
	public void run(CachedOutput cache) throws IOException
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
		TranslatableContents contents = (TranslatableContents)key.getDisplayName().getContents();
		add(contents.getKey(), names);
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
