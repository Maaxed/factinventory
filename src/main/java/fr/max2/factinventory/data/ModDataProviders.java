package fr.max2.factinventory.data;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = FactinventoryMod.MOD_ID, bus = Bus.MOD)
public class ModDataProviders
{

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
    	ExistingFileHelper files = event.getExistingFileHelper();

    	BlockTagsProvider blockTags = new BlockTagsProvider(gen, FactinventoryMod.MOD_ID, files)
		{
    		@Override
    		protected void addTags()
    		{ }
		};
        gen.addProvider(event.includeServer(), new ModItemTagsProvider(gen, blockTags, files));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(gen));

        gen.addProvider(event.includeClient(), new ModItemModelProvider(gen, files));
        gen.addProvider(event.includeClient(), new ModLanguagesProvider(gen));
    }
	
}
