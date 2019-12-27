package fr.max2.factinventory.capability;

import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public interface ITileEntityHandler
{
	boolean hasTileData();

	@Nullable
	BlockPos getTilePos();
	@Nullable
	DimensionType getTileDim();

	@Nullable
	TileEntity getTile();
	
	void setTile(@Nullable TileEntity tile, @Nullable Direction side);

	
	default void setTile(TileEntity tile)
	{
		this.setTile(tile, null);
	}
}
