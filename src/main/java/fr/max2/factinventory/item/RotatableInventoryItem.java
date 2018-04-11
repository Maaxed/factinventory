package fr.max2.factinventory.item;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.mesh.IStateMesh.MeshProperty;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class RotatableInventoryItem extends InventoryItem
{
	
	@SideOnly(Side.CLIENT)
	protected static final MeshProperty PROPERTIE_ROTATION = new MeshProperty("facing", "north", "south", "west", "east")
	{
		@Override
		protected String getValue(ItemStack stack)
		{
			return getFacing(stack).getName2();
		}
	};
	
	private static final IItemPropertyGetter FACING_GETTER = new IItemPropertyGetter()
	{
		@Override
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
		{
			return getFacing(stack).getHorizontalIndex() * 0.25f;
		}
	};
	
	public RotatableInventoryItem()
	{
		super();
		this.addPropertyOverride(new ResourceLocation(FactinventoryMod.MOD_ID, "facing"), FACING_GETTER);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		rotate(stack);
		
		return new ActionResult(EnumActionResult.SUCCESS, stack);
	}
	
	
	private static final String NBT_FACING = "facing";
	
	public static EnumFacing getFacing(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_FACING, NBT.TAG_BYTE)) return EnumFacing.getHorizontal(tag.getByte(NBT_FACING));
		}
		return EnumFacing.NORTH;
	}
	
	public static void rotate(ItemStack stack)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		NBTTagCompound tag = stack.getTagCompound();
		
		EnumFacing face = tag.hasKey(NBT_FACING, NBT.TAG_BYTE) ? EnumFacing.getHorizontal(tag.getByte(NBT_FACING)) : EnumFacing.NORTH;
		face = face.rotateY();
		
		tag.setByte(NBT_FACING, (byte)face.getHorizontalIndex());
	}
	
}
