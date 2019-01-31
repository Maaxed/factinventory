package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.capability.CapabilityTileEntityHandler;
import fr.max2.factinventory.capability.ITileEntityHandler;
import fr.max2.factinventory.capability.InventoryLinkerHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
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
		ITileEntityHandler handler = stack.getCapability(CapabilityTileEntityHandler.CAPABILITY_TILE, null);
		if (handler.hasTileData())
		{
			TileEntity te = handler.getTile();
			
			if (te == null)
			{
				if (worldIn.provider.getDimension() == handler.getTileDim())
				{
					if (worldIn.isBlockLoaded(handler.getTilePos(), false))
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
				ITileEntityHandler handler = player.getHeldItem(hand).getCapability(CapabilityTileEntityHandler.CAPABILITY_TILE, null);
				handler.setTile(te, side);
				
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
	
}
