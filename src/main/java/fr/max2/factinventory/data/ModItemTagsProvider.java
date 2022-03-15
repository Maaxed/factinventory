package fr.max2.factinventory.data;

import java.util.stream.Stream;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;

import net.minecraft.data.tags.TagsProvider.TagAppender;

public class ModItemTagsProvider extends ItemTagsProvider
{
    public ModItemTagsProvider(DataGenerator gen, BlockTagsProvider blockTags, ExistingFileHelper existingFiles)
    {
        super(gen, blockTags, FactinventoryMod.MOD_ID, existingFiles);
    }

    @Override
    protected void addTags()
    { }

    @Override
    public String getName()
    {
        return "Factinventory Block Tags";
    }
    
    protected static TagAppender<Item> add(TagAppender<Item> builder, Block... blocks)
    {
    	Stream.of(blocks).map(Block::asItem).forEach(builder::add);
    	return builder;
    }
}