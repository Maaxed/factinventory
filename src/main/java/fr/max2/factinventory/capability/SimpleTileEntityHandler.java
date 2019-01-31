package fr.max2.factinventory.capability;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class SimpleTileEntityHandler implements ITileEntityHandler, INBTSerializable<NBTTagCompound>
{
	@Nullable
	protected BlockPos targetPos;
	@Nullable
	protected EnumFacing targetSide;
	protected int dim;
	
	@Override
	public boolean hasTileData()
	{
		return this.targetPos != null;
	}
	
	@Override
	public BlockPos getTilePos()
	{
		return this.targetPos;
	}
	
	@Override
	public int getTileDim()
	{
		return this.dim;
	}
	
	protected World getWorld()
	{
		FMLCommonHandler handler = FMLCommonHandler.instance();
		return handler.getEffectiveSide() == Side.CLIENT
			? FactinventoryMod.proxy.getWorldByDimension(this.dim)
			: handler.getMinecraftServerInstance().getWorld(dim);
	}
	
	@Override
	public TileEntity getTile()
	{
		World world = this.getWorld();
		
		return world == null || this.targetPos == null ? null : world.getTileEntity(this.targetPos);
	}

	@Override
	public void setTile(TileEntity tile, EnumFacing side)
	{
		if (tile == null)
		{
			this.reset();
		}
		else
		{
			this.targetPos = tile.getPos();
			this.dim = tile.getWorld().provider.getDimension();
			this.targetSide = side;
		}
	}
	
	protected void reset()
	{
		this.targetPos = null;
		this.dim = 0;
		this.targetSide = null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound data = new NBTTagCompound();
		if (this.targetPos != null)
		{
			data.setInteger("link_x", this.targetPos.getX());
			data.setInteger("link_y", this.targetPos.getY());
			data.setInteger("link_z", this.targetPos.getZ());
			data.setInteger("link_dimension", this.dim);
			data.setByte("link_side", (byte) (this.targetSide == null ? -1 : this.targetSide.getIndex()));
		}
		return data;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("link_dimension", NBT.TAG_INT))
		{
			this.targetPos = new BlockPos(nbt.getInteger("link_x"),
										  nbt.getInteger("link_y"),
										  nbt.getInteger("link_z"));
			this.dim = nbt.getInteger("link_dimension");
			byte side = nbt.getByte("link_side");
			
			this.targetSide = side == (byte)-1 ? null : EnumFacing.getFront(side);
		}
		else
		{
			this.reset();
		}
	}
	
}
