package fr.max2.factinventory.data;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = FactinventoryMod.MOD_ID, bus = Bus.MOD)
public class ModDataProviders
{

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
    	ExistingFileHelper files = event.getExistingFileHelper();

        if (event.includeServer())
        {
        	BlockTagsProvider blockTags = new BlockTagsProvider(gen, FactinventoryMod.MOD_ID, files)
    		{
        		@Override
        		protected void addTags()
        		{ }
    		};
            gen.addProvider(new ModItemTagsProvider(gen, blockTags, files));
            gen.addProvider(new ModRecipeProvider(gen));
        }
        
        if (event.includeClient())
        {
            gen.addProvider(new ModItemModelProvider(gen, files));
            gen.addProvider(new ModLanguagesProvider(gen));
        }
    }
	
}
