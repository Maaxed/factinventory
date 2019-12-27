package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import fr.max2.factinventory.utils.StringUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
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
	private static final IItemPropertyGetter FILL_GETTER = (stack, world, entity) ->
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
		this.addPropertyOverride(FILL_GETTER_LOC, FILL_GETTER);
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if (Screen.hasShiftDown())
		{
			tooltip.add(new TranslationTextComponent("tooltip.input.desc").applyTextStyle(TextFormatting.BLUE));
			tooltip.add(new TranslationTextComponent("tooltip.output.desc").applyTextStyle(TextFormatting.GOLD));
		}
		else
		{
			tooltip.add(new TranslationTextComponent("tooltip.interaction_info_on_shift.desc"));
		}
		
		if (Screen.hasControlDown())
		{
			FluidStack transferringItem = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
			if (transferringItem.isEmpty())
			{
				tooltip.add(new TranslationTextComponent("tooltip.not_transferring.desc"));
			}
			else
			{
				tooltip.add(new TranslationTextComponent("tooltip.transferring_item.desc", transferringItem.getDisplayName()));
			}
			
			
			tooltip.add(new TranslationTextComponent("tooltip.transfer_progress.desc", StringUtils.progress(8 - getTransferTime(stack), 8)));
		}
		else
		{
			tooltip.add(new TranslationTextComponent("tooltip.transfer_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getCount() != oldStack.getCount());
	}
	
	@Override
	protected void update(ItemStack stack, PlayerInventory inv, PlayerEntity player, int itemSlot)
	{
		Direction face = getFacing(stack);
		
		int width = PlayerInventory.getHotbarSize(),
			height = inv.mainInventory.size() / width,
			x = itemSlot % width,
			y = itemSlot / width,
			fillX = x - face.getXOffset(),
			fillY = y - face.getZOffset();

		if (fillY == 0 && y != 0) fillY = height;
		else if (y == 0 && fillY ==  1) fillY = -1;
		else if (y == 0 && fillY == -1) fillY = height - 1;
		else if (fillY == height) fillY = 0;
		
		
		if (fillX >= 0 && fillX < width &&
			fillY >= 0 && fillY < height)
		{
			int fillSlot = fillX + width * fillY;
			ItemStack fillStack = inv.getStackInSlot(fillSlot);
			
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
								
								inv.setInventorySlotContents(fillSlot, fillCapa.getContainer());
							}
						}
						
						int drainX = x + face.getXOffset(),
							drainY = y + face.getZOffset();
						
						if (drainY == 0 && y != 0) drainY = height;
						else if (y == 0 && drainY == 1) drainY = -1;
						else if (y == 0 && drainY == -1) drainY = height - 1;
						else if (drainY == height) drainY = 0;
						
						if (drainX >= 0 && drainX < width &&
							drainY >= 0 && drainY < height)
						{
							int drainSlot = drainX + width * drainY;
							
							ItemStack drainStack = inv.getStackInSlot(drainSlot);
							
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
											
											inv.setInventorySlotContents(drainSlot, drainCapa.getContainer());
											inv.setInventorySlotContents(fillSlot, fillCapa.getContainer());
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
	public List<Icon> getRenderIcons(ItemStack stack, ContainerScreen<?> gui, Slot slot, PlayerInventory inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		Direction face = getFacing(stack);
		
		int itemSlot = slot.getSlotIndex(),
			width = PlayerInventory.getHotbarSize(),
			height = inv.mainInventory.size() / width;
		
		if (itemSlot >= width * height) return icons;
		
		int x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getXOffset(),
			extractY = y + face.getZOffset(),
			insertX  = x - face.getXOffset(),
			insertY  = y - face.getZOffset();
		
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
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
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
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_TRANSFER_TIME, NBT.TAG_INT)) return tag.getInt(NBT_TRANSFER_TIME);
		}
		return 0;
	}
	
	public static void setTransferTime(ItemStack stack, int transferTime)
	{
		stack.setTagInfo(NBT_TRANSFER_TIME, new IntNBT(transferTime));
	}
	
}
