package fr.max2.factinventory.item;

import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.capability.CapabilityTileEntityHandler;
import fr.max2.factinventory.capability.ITileEntityHandler;
import fr.max2.factinventory.capability.InventoryLinkerHandler;
import fr.max2.factinventory.init.ModTexts;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
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
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		ITileEntityHandler handler = stack.getCapability(CapabilityTileEntityHandler.CAPABILITY_TILE, null).orElse(null);
		if (handler != null && handler.hasTileData())
		{
			BlockEntity te = handler.getTile();

			if (te == null)
			{
				if (worldIn == null || worldIn.dimension() == handler.getTileWorld())
				{
					if (worldIn != null && worldIn.hasChunkAt(handler.getTilePos()))
					{
						tooltip.add(Component.translatable(ModTexts.Tooltip.LINKED_MISSING));
					}
					else tooltip.add(Component.translatable(ModTexts.Tooltip.LINKED_UNLOADED));
				}
				else tooltip.add(Component.translatable(ModTexts.Tooltip.LINKED_DIMENSION));
			}
			else
			{
				Component name = null;
				if (te instanceof MenuProvider) name = ((MenuProvider)te).getDisplayName();
				if (name == null) name = te.getBlockState().getBlock().getName();
				tooltip.add(Component.translatable(ModTexts.Tooltip.LINKED_TILE, name));
			}
		}
		else tooltip.add(Component.translatable(ModTexts.Tooltip.NOT_LINKED));
	}

	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Player player = context.getPlayer();
		if (player == null || player.isCrouching())
		{
			BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
			if (te != null && (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, context.getClickedFace()).isPresent() || te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, context.getClickedFace()).isPresent()))
			{
				context.getItemInHand().getCapability(CapabilityTileEntityHandler.CAPABILITY_TILE, null).ifPresent(handler -> handler.setTile(te, context.getClickedFace()));

				return InteractionResult.SUCCESS;
			}

			return InteractionResult.FAIL;
		}
		return super.useOn(context);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
	{
		return new InventoryLinkerHandler(stack);
	}

}
