package fr.max2.factinventory.capability;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class SimpleTileEntityHandler implements ITileEntityHandler, INBTSerializable<CompoundTag>
{
	@Nullable
	protected BlockPos targetPos;
	@Nullable
	protected Direction targetSide;
	@Nullable
	protected ResourceKey<Level> worldKey;
	
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
	public ResourceKey<Level> getTileWorld()
	{
		return this.worldKey;
	}

	@Nullable
	protected Level getWorld()
	{
		if (this.worldKey == null) return null;
		return EffectiveSide.get().isClient()
			? FactinventoryMod.proxy.getWorldByDimension(this.worldKey)
			: ServerLifecycleHooks.getCurrentServer().getLevel(this.worldKey);
	}
	
	@Override
	@Nullable
	public BlockEntity getTile()
	{
		Level world = this.getWorld();
		
		return world == null || this.targetPos == null ? null : world.getBlockEntity(this.targetPos);
	}

	@Override
	public void setTile(@Nullable BlockEntity tile, @Nullable Direction side)
	{
		if (tile == null)
		{
			this.reset();
		}
		else
		{
			this.targetPos = tile.getBlockPos();
			this.worldKey = tile.getLevel().dimension();
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
	public CompoundTag serializeNBT()
	{
		CompoundTag data = new CompoundTag();
		if (hasTileData())
		{
			data.putInt("link_x", this.targetPos.getX());
			data.putInt("link_y", this.targetPos.getY());
			data.putInt("link_z", this.targetPos.getZ());
			data.putString("link_dimension", this.worldKey.location().toString());
			data.putByte("link_side", (byte) (this.targetSide == null ? -1 : this.targetSide.get3DDataValue()));
		}
		return data;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		if (nbt.contains("link_dimension", Tag.TAG_STRING))
		{
			this.targetPos = new BlockPos(nbt.getInt("link_x"),
										  nbt.getInt("link_y"),
										  nbt.getInt("link_z"));
			this.worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("link_dimension")));
			byte side = nbt.getByte("link_side");
			
			this.targetSide = side == (byte)-1 ? null : Direction.from3DDataValue(side);
		}
		else
		{
			this.reset();
		}
	}
	
}
