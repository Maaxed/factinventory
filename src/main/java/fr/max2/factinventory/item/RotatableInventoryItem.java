package fr.max2.factinventory.item;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;

import net.minecraft.world.item.Item.Properties;

public abstract class RotatableInventoryItem extends InventoryItem
{
	public static final Direction[] ITEM_DIRECTIONS = new Direction[]{Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

	public static final ResourceLocation FACING_GETTER_LOC = new ResourceLocation(FactinventoryMod.MOD_ID, "facing");
	@OnlyIn(Dist.CLIENT)
	public static final ItemPropertyFunction
		FACING_GETTER = (stack, worldIn, entityIn, seed) -> getFacing(stack).get2DDataValue();
	
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
	
	
	private static final String NBT_FACING = "facing";
	
	public static Direction getFacing(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_FACING, NBT.TAG_BYTE)) return Direction.from2DDataValue(tag.getByte(NBT_FACING));
		}
		return Direction.NORTH;
	}
	
	public static void rotate(ItemStack stack)
	{
		if (!stack.hasTag()) stack.setTag(new CompoundTag());
		
		CompoundTag tag = stack.getTag();
		
		Direction face = tag.contains(NBT_FACING, NBT.TAG_BYTE) ? Direction.from2DDataValue(tag.getByte(NBT_FACING)) : Direction.NORTH;
		face = face.getClockWise();
		
		tag.putByte(NBT_FACING, (byte)face.get2DDataValue());
	}
	
}
