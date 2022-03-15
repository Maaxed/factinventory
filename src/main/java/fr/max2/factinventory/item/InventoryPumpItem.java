package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import fr.max2.factinventory.utils.StringUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class InventoryPumpItem extends RotatableInventoryItem
{
	public static final ResourceLocation FILL_GETTER_LOC = new ResourceLocation(FactinventoryMod.MOD_ID, "filled");
	@OnlyIn(Dist.CLIENT)
	public static final ItemPropertyFunction
		FILL_GETTER = (stack, world, entity, seed) ->
		{
			IFluidHandlerItem contentCapa = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).orElse(null);
			if (contentCapa == null) return 0;
			
			FluidStack fluid = contentCapa.drain(FluidAttributes.BUCKET_VOLUME, FluidAction.SIMULATE);
			if (fluid.isEmpty()) return 0;
			
			int value = getTransferTime(stack);
			if (value > 8) value = 8;
			if (value < 0) value = 0;
			return value;
		};
	
	public InventoryPumpItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		if (Screen.hasShiftDown())
		{
			tooltip.add(new TranslatableComponent("tooltip.input.desc").withStyle(ChatFormatting.BLUE));
			tooltip.add(new TranslatableComponent("tooltip.output.desc").withStyle(ChatFormatting.GOLD));
		}
		else
		{
			tooltip.add(new TranslatableComponent("tooltip.interaction_info_on_shift.desc"));
		}
		
		if (Screen.hasControlDown())
		{
			FluidStack transferringItem = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
			if (transferringItem.isEmpty())
			{
				tooltip.add(new TranslatableComponent("tooltip.not_transferring.desc"));
			}
			else
			{
				tooltip.add(new TranslatableComponent("tooltip.transferring_item.desc", transferringItem.getDisplayName()));
			}
			
			
			tooltip.add(new TranslatableComponent("tooltip.transfer_progress.desc", StringUtils.progress(8 - getTransferTime(stack), 8)));
		}
		else
		{
			tooltip.add(new TranslatableComponent("tooltip.transfer_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getCount() != oldStack.getCount());
	}
	
	@Override
	protected void update(ItemStack stack, Inventory inv, Player player, int itemSlot)
	{
		Direction face = getFacing(stack);
		
		int width = Inventory.getSelectionSize(),
			height = inv.items.size() / width,
			x = itemSlot % width,
			y = itemSlot / width,
			fillX = x - face.getStepX(),
			fillY = y - face.getStepZ();

		if (fillY == 0 && y != 0) fillY = height;
		else if (y == 0 && fillY ==  1) fillY = -1;
		else if (y == 0 && fillY == -1) fillY = height - 1;
		else if (fillY == height) fillY = 0;
		
		
		if (fillX >= 0 && fillX < width &&
			fillY >= 0 && fillY < height)
		{
			int fillSlot = fillX + width * fillY;
			ItemStack fillStack = inv.getItem(fillSlot);
			
			IFluidHandlerItem contentCapa = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).orElseThrow(IllegalStateException::new);
			
			fillStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face).ifPresent(fillCapa ->
			{
				int transferTime = getTransferTime(stack);
				
				FluidStack fillFluid = contentCapa.drain(FluidAttributes.BUCKET_VOLUME, FluidAction.SIMULATE);
				boolean canTransfer = fillFluid.isEmpty() || transferTime > 4;
				
				if (!canTransfer)
				{
					int val = fillCapa.fill(fillFluid, FluidAction.SIMULATE);
					if (val > 0)
					{
						canTransfer = true;
					}
				}
				
				if (canTransfer)
				{
					
					transferTime--;
					if (transferTime <= 0)
					{
						transferTime = 8;
						
						//updatePump(stack, inv, itemSlot);
						
						
						if (!fillFluid.isEmpty())
						{
							int val = fillCapa.fill(fillFluid, FluidAction.EXECUTE);
							if (val > 0)
							{
								fillFluid.setAmount(val);
								contentCapa.drain(fillFluid, FluidAction.EXECUTE);
								
								inv.setItem(fillSlot, fillCapa.getContainer());
							}
						}
						
						int drainX = x + face.getStepX(),
							drainY = y + face.getStepZ();
						
						if (drainY == 0 && y != 0) drainY = height;
						else if (y == 0 && drainY == 1) drainY = -1;
						else if (y == 0 && drainY == -1) drainY = height - 1;
						else if (drainY == height) drainY = 0;
						
						if (drainX >= 0 && drainX < width &&
							drainY >= 0 && drainY < height)
						{
							int drainSlot = drainX + width * drainY;
							
							ItemStack drainStack = inv.getItem(drainSlot);
							
							drainStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face.getOpposite()).ifPresent(drainCapa ->
							{
								FluidStack drainFluid = drainCapa.drain(FluidAttributes.BUCKET_VOLUME, FluidAction.SIMULATE);
								if (!drainFluid.isEmpty())
								{
									drainFluid.setAmount(contentCapa.fill(drainFluid, FluidAction.SIMULATE));
									if (drainFluid.getAmount() > 0)
									{
										drainFluid.setAmount(fillCapa.fill(drainFluid, FluidAction.SIMULATE));
										if (drainFluid.getAmount() > 0)
										{
											drainCapa.drain(drainFluid, FluidAction.EXECUTE);
											contentCapa.fill(drainFluid, FluidAction.EXECUTE);
											
											inv.setItem(drainSlot, drainCapa.getContainer());
											inv.setItem(fillSlot, fillCapa.getContainer());
										}
									}
								}
							});
						}
					}
					setTransferTime(stack, transferTime);
				}
			});
		}
	}

	@Override
	public List<Icon> getRenderIcons(ItemStack stack, AbstractContainerScreen<?> gui, Slot slot, Inventory inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		Direction face = getFacing(stack);
		
		int itemSlot = slot.getSlotIndex(),
			width = Inventory.getSelectionSize(),
			height = inv.items.size() / width;
		
		if (itemSlot >= width * height) return icons;
		
		int x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getStepX(),
			extractY = y + face.getStepZ(),
			insertX  = x - face.getStepX(),
			insertY  = y - face.getStepZ();
		
		if (extractY == 0 && y != 0)
			extractY = height;
		else if (y == 0 && extractY == 1)
			extractY = -1;
		else if (y == 0 && extractY == -1)
			extractY = height - 1;
		else if (extractY == height)
			extractY = 0;
		
		if (insertY == 0 && y != 0)
			insertY = height;
		else if (y == 0 && insertY == 1)
			insertY = -1;
		else if (y == 0 && insertY == -1)
			insertY = height - 1;
		else if (insertY == height)
			insertY = 0;
		
		if (extractX >= 0 && extractX < width && extractY >= 0 && extractY < height)
		{
			Slot extractSlot = findSlot(gui, slot, extractX + width * extractY);
			icons.add(new Icon(extractSlot, face, 0x0099FF, true, false));
		}
		else icons.add(new Icon(null, face, 0x0099FF, true, true));
		
		if (insertX >= 0 && insertX < width && insertY >= 0 && insertY < height)
		{
			Slot fillSlot = findSlot(gui, slot, insertX + width * insertY);
			icons.add(new Icon(fillSlot, face.getOpposite(), 0xFF7700, false, false));
		}
		else icons.add(new Icon(null, face.getOpposite(), 0xFF7700, false, true));
		
		return icons;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
	{
		return new FluidHandlerItemStack(stack, FluidAttributes.BUCKET_VOLUME);
	}
	
	/*private static final String NBT_TRANSFERRING_FLUID = "TransferringFluid";
	
	public static FluidStack getTransferringFluid(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_TRANSFERRING_FLUID, NBT.TAG_COMPOUND)) return FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(NBT_TRANSFERRING_FLUID));
		}
		return null;
	}
	
	public static void setTransferringFluid(ItemStack stack, FluidStack transferringFluid)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setTag(NBT_TRANSFERRING_FLUID, transferringFluid.writeToNBT(new NBTTagCompound()));
	}*/
	
	
	private static final String NBT_TRANSFER_TIME = "TransferTime";
	
	public static int getTransferTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_TRANSFER_TIME, Tag.TAG_INT)) return tag.getInt(NBT_TRANSFER_TIME);
		}
		return 0;
	}
	
	public static void setTransferTime(ItemStack stack, int transferTime)
	{
		stack.addTagElement(NBT_TRANSFER_TIME, IntTag.valueOf(transferTime));
	}
	
}
