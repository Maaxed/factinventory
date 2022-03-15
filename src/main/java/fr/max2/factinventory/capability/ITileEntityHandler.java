package fr.max2.factinventory.capability;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ITileEntityHandler
{
	boolean hasTileData();

	@Nullable
	BlockPos getTilePos();
	@Nullable
	ResourceKey<Level> getTileWorld();

	@Nullable
	BlockEntity getTile();
	
	void setTile(@Nullable BlockEntity tile, @Nullable Direction side);

	
	default void setTile(BlockEntity tile)
	{
		this.setTile(tile, null);
	}
}
