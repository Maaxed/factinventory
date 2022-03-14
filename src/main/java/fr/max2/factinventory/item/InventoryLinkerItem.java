package fr.max2.factinventory.item;

import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.capability.CapabilityTileEntityHandler;
import fr.max2.factinventory.capability.ITileEntityHandler;
import fr.max2.factinventory.capability.InventoryLinkerHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

public class InventoryLinkerItem extends Item
{
	
	public InventoryLinkerItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
    @OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		ITileEntityHandler handler = stack.getCapability(CapabilityTileEntityHandler.CAPABILITY_TILE, null).orElse(null);
		if (handler != null && handler.hasTileData())
		{
			TileEntity te = handler.getTile();
			
			if (te == null)
			{
				if (worldIn == null || worldIn.dimension() == handler.getTileWorld())
				{
					if (worldIn != null && worldIn.hasChunkAt(handler.getTilePos()))
					{
						tooltip.add(new TranslationTextComponent("tooltip.linked_missing.desc"));
					}
					else tooltip.add(new TranslationTextComponent("tooltip.linked_unloaded.desc"));
				}
				else tooltip.add(new TranslationTextComponent("tooltip.linked_other_dimension.desc"));
			}
			else
			{
				ITextComponent name = null;
				if (te instanceof INamedContainerProvider) name = ((INamedContainerProvider)te).getDisplayName();
				if (name == null) name = te.getBlockState().getBlock().getName();
				tooltip.add(new TranslationTextComponent("tooltip.linked_tile.desc", name));
			}
		}
		else tooltip.add(new TranslationTextComponent("tooltip.not_linked.desc"));
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context)
	{
		PlayerEntity player = context.getPlayer();
		if (player == null || player.isCrouching())
		{
			TileEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
			if (te != null && (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, context.getClickedFace()).isPresent() || te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, context.getClickedFace()).isPresent()))
			{
				context.getItemInHand().getCapability(CapabilityTileEntityHandler.CAPABILITY_TILE, null).ifPresent(handler -> handler.setTile(te, context.getClickedFace()));
				
				return ActionResultType.SUCCESS;
			}
			
			return ActionResultType.FAIL;
		}
		return super.useOn(context);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
	{
		return new InventoryLinkerHandler(stack);
	}
	
}
