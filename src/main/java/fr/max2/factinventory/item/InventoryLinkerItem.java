package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.capability.InventoryLinkerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

public class InventoryLinkerItem extends Item
{
	
	public InventoryLinkerItem()
	{
		super();
		this.maxStackSize = 1;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("link_side", NBT.TAG_BYTE))
		{
			TileEntity te = getLikedTileEntity(stack);
			
			if (te == null)
			{
				NBTTagCompound tags = stack.getTagCompound();
				if (worldIn.provider.getDimension() == tags.getInteger("link_dimension"))
				{
					int x = tags.getInteger("link_x");
					int y = tags.getInteger("link_y");
					int z = tags.getInteger("link_z");
					
					if (worldIn.isBlockLoaded(new BlockPos(x, y, z), false))
					{
						tooltip.add(I18n.format("tooltip.linked_missing.desc"));
					}
					else tooltip.add(I18n.format("tooltip.linked_unloaded.desc"));
				}
				else tooltip.add(I18n.format("tooltip.linked_other_dimension.desc"));
			}
			else
			{
				ITextComponent name = te.getDisplayName();
				String displayName = name == null ? te.getBlockType().getLocalizedName() : name.getFormattedText();
				tooltip.add(I18n.format("tooltip.linked_tile.desc", displayName));
			}
		}
		else tooltip.add(I18n.format("tooltip.not_linked.desc"));
	}
	
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if (player.isSneaking())
		{
			TileEntity te = world.getTileEntity(pos);
			if (te != null && (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) || te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)))
			{
				setData(player.getHeldItem(hand), world, pos, side);
				
				return EnumActionResult.SUCCESS;
			}
			
			return EnumActionResult.FAIL;
		}
		return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new InventoryLinkerHandler(stack);
	}
	
	private static void setData(ItemStack stack, World world, BlockPos pos, EnumFacing side)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		NBTTagCompound tags = stack.getTagCompound();
		
		tags.setByte("link_side", (byte) side.getIndex());
		tags.setInteger("link_x", pos.getX());
		tags.setInteger("link_y", pos.getY());
		tags.setInteger("link_z", pos.getZ());
		tags.setInteger("link_dimension", world.provider.getDimension());
	}
	
	public static TileEntity getLikedTileEntity(ItemStack stack)
	{
		if (!stack.hasTagCompound()) return null;
		
		NBTTagCompound tags = stack.getTagCompound();
		
		if (!tags.hasKey("link_side", NBT.TAG_BYTE)) return null;
		
		int dim = tags.getInteger("link_dimension");
		World world = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? getClientWorld(dim) : FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dim);
		
		if (world == null) return null;
		
		int x = tags.getInteger("link_x");
		int y = tags.getInteger("link_y");
		int z = tags.getInteger("link_z");
		
		return world.getTileEntity(new BlockPos(x, y, z));
	}
	
	@SideOnly(Side.CLIENT)
	private static World getClientWorld(int dim)
	{
		World w = Minecraft.getMinecraft().world;
		return w.provider.getDimension() == dim ? w : null;
	}
	
}
