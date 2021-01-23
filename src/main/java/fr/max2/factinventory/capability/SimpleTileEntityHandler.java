package fr.max2.factinventory.capability;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.common.util.INBTSerializable;

public class SimpleTileEntityHandler implements ITileEntityHandler, INBTSerializable<CompoundNBT>
{
	@Nullable
	protected BlockPos targetPos;
	@Nullable
	protected Direction targetSide;
	@Nullable
	protected RegistryKey<World> worldKey;
	
	@Override
	public boolean hasTileData()
	{
		return this.targetPos != null;
	}
	
	@Override
	@Nullable
	public BlockPos getTilePos()
	{
		return this.targetPos;
	}
	
	@Override
	@Nullable
	public RegistryKey<World> getTileWorld()
	{
		return this.worldKey;
	}

	@Nullable
	protected World getWorld()
	{
		if (this.worldKey == null) return null;
		return EffectiveSide.get().isClient()
			? FactinventoryMod.proxy.getWorldByDimension(this.worldKey)
			: ServerLifecycleHooks.getCurrentServer().getWorld(this.worldKey);
	}
	
	@Override
	@Nullable
	public TileEntity getTile()
	{
		World world = this.getWorld();
		
		return world == null || this.targetPos == null ? null : world.getTileEntity(this.targetPos);
	}

	@Override
	public void setTile(@Nullable TileEntity tile, @Nullable Direction side)
	{
		if (tile == null)
		{
			this.reset();
		}
		else
		{
			this.targetPos = tile.getPos();
			this.worldKey = tile.getWorld().getDimensionKey();
			this.targetSide = side;
		}
	}
	
	protected void reset()
	{
		this.targetPos = null;
		this.worldKey = null;
		this.targetSide = null;
	}

	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT data = new CompoundNBT();
		if (hasTileData())
		{
			data.putInt("link_x", this.targetPos.getX());
			data.putInt("link_y", this.targetPos.getY());
			data.putInt("link_z", this.targetPos.getZ());
			data.putString("link_dimension", this.worldKey.getLocation().toString());
			data.putByte("link_side", (byte) (this.targetSide == null ? -1 : this.targetSide.getIndex()));
		}
		return data;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		if (nbt.contains("link_dimension", NBT.TAG_STRING))
		{
			this.targetPos = new BlockPos(nbt.getInt("link_x"),
										  nbt.getInt("link_y"),
										  nbt.getInt("link_z"));
			this.worldKey = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(nbt.getString("link_dimension")));
			byte side = nbt.getByte("link_side");
			
			this.targetSide = side == (byte)-1 ? null : Direction.byIndex(side);
		}
		else
		{
			this.reset();
		}
	}
	
}
