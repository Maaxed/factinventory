package fr.max2.factinventory.item;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class RotatableInventoryItem extends InventoryItem
{
	public static final Direction[] ITEM_DIRECTIONS = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

	public static final ResourceLocation FACING_GETTER_LOC = new ResourceLocation(FactinventoryMod.MOD_ID, "facing");
	@OnlyIn(Dist.CLIENT)
	public static final IItemPropertyGetter
		FACING_GETTER = (stack, worldIn, entityIn) -> getFacing(stack).getHorizontalIndex();
	
	public RotatableInventoryItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		rotate(stack);
		
		return new ActionResult<>(ActionResultType.SUCCESS, stack);
	}
	
	
	private static final String NBT_FACING = "facing";
	
	public static Direction getFacing(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_FACING, NBT.TAG_BYTE)) return Direction.byHorizontalIndex(tag.getByte(NBT_FACING));
		}
		return Direction.NORTH;
	}
	
	public static void rotate(ItemStack stack)
	{
		if (!stack.hasTag()) stack.setTag(new CompoundNBT());
		
		CompoundNBT tag = stack.getTag();
		
		Direction face = tag.contains(NBT_FACING, NBT.TAG_BYTE) ? Direction.byHorizontalIndex(tag.getByte(NBT_FACING)) : Direction.NORTH;
		face = face.rotateY();
		
		tag.putByte(NBT_FACING, (byte)face.getHorizontalIndex());
	}
	
}
