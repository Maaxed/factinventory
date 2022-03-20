package fr.max2.factinventory.item;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public abstract class RotatableInventoryItem extends InventoryItem
{
	public static final Direction[] ITEM_DIRECTIONS = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

	public static final ResourceLocation FACING_GETTER_LOC = new ResourceLocation(FactinventoryMod.MOD_ID, "facing");

	public RotatableInventoryItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		
		rotate(stack);
		
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess)
	{
		if (pAction == ClickAction.SECONDARY && pSlot.allowModification(pPlayer))
		{
			if (pOther.isEmpty())
			{
				rotate(pStack);
				return true;
			}
		}
		return false;
	}
	
	
	private static final String NBT_FACING = "facing";
	
	public static Direction getFacing(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_FACING, Tag.TAG_BYTE)) return Direction.from2DDataValue(tag.getByte(NBT_FACING));
		}
		return Direction.NORTH;
	}
	
	public static void rotate(ItemStack stack)
	{
		if (!stack.hasTag()) stack.setTag(new CompoundTag());
		
		CompoundTag tag = stack.getTag();
		
		Direction face = tag.contains(NBT_FACING, Tag.TAG_BYTE) ? Direction.from2DDataValue(tag.getByte(NBT_FACING)) : Direction.NORTH;
		face = face.getClockWise();
		
		tag.putByte(NBT_FACING, (byte)face.get2DDataValue());
	}
	
}
