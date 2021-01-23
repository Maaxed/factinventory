package fr.max2.factinventory.data;

import java.util.stream.Stream;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider
{
    public ModItemTagsProvider(DataGenerator gen, BlockTagsProvider blockTags, ExistingFileHelper existingFiles)
    {
        super(gen, blockTags, FactinventoryMod.MOD_ID, existingFiles);
    }

    @Override
    public void registerTags()
    {
    	
    }

    @Override
    public String getName()
    {
        return "Factinventory Block Tags";
    }
    
    protected static Builder<Item> add(Builder<Item> builder, Block... blocks)
    {
    	Stream.of(blocks).map(Block::asItem).forEach(builder::add);
    	return builder;
    }
}