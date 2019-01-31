package fr.max2.factinventory.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface ITileEntityHandler
{
	boolean hasTileData();
	
	BlockPos getTilePos();
	int getTileDim();
	
	TileEntity getTile();
	
	void setTile(TileEntity tile, EnumFacing side);

	
	default void setTile(TileEntity tile)
	{
		this.setTile(tile, null);
	}
}
