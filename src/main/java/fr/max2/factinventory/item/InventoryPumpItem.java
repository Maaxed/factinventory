package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import fr.max2.factinventory.item.mesh.StateMesh;
import fr.max2.factinventory.item.mesh.StateMesh.MeshProperty;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryPumpItem extends RotatableInventoryItem
{
	@SideOnly(Side.CLIENT)
	private static final MeshProperty[] PROPERTIES = { PROPERTIE_ROTATION , new MeshProperty("filled", "0", "1", "2", "3", "4", "5", "6", "7", "8")
	{
		@Override
		protected String getValue(ItemStack stack)
		{
			IFluidHandlerItem contentCapa = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			FluidStack fluid = contentCapa.drain(Fluid.BUCKET_VOLUME, false);
			if (fluid == null || fluid.amount <= 0) return "0";
			
			int value = getTransferTime(stack);
			if (value > 8) value = 8;
			if (value < 0) value = 0;
			return Integer.toString(value);
		}
	}};
	
	@SideOnly(Side.CLIENT)
	public static final StateMesh MESH = new StateMesh(PROPERTIES);
	
	public InventoryPumpItem()
	{
		super();
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (GuiScreen.isShiftKeyDown())
		{
			tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.input.desc"));
			tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.output.desc"));
		}
		else
		{
			tooltip.add(I18n.format("tooltip.interaction_info_on_shift.desc"));
		}
		
		if (GuiScreen.isCtrlKeyDown())
		{
			FluidStack transferringItem = FluidUtil.getFluidContained(stack);
			if (transferringItem == null)
			{
				tooltip.add(I18n.format("tooltip.not_transferring.desc"));
			}
			else
			{
				tooltip.add(I18n.format("tooltip.transferring_item.desc", transferringItem.getLocalizedName()));
			}
			
			tooltip.add(I18n.format("tooltip.transfer_time.desc", getTransferTime(stack))); // TODO use the '█' char as a progess bar
		}
		else
		{
			tooltip.add(I18n.format("tooltip.transfer_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getMetadata() != oldStack.getMetadata() || newStack.getCount() != oldStack.getCount());
	}
	
	@Override
	protected void update(ItemStack stack, InventoryPlayer inv, EntityPlayer player, int itemSlot)
	{
		EnumFacing face = getFacing(stack);
		
		int width = inv.getHotbarSize(),
			height = inv.mainInventory.size() / width,
			x = itemSlot % width,
			y = itemSlot / width,
			fillX = x - face.getFrontOffsetX(),
			fillY = y - face.getFrontOffsetZ();

		if (fillY == 0 && y != 0) fillY = height;
		else if (y == 0 && fillY ==  1) fillY = -1;
		else if (y == 0 && fillY == -1) fillY = height - 1;
		else if (fillY == height) fillY = 0;
		
		
		if (fillX >= 0 && fillX < width &&
			fillY >= 0 && fillY < height)
		{
			int fillSlot = fillX + width * fillY;
			ItemStack fillStack = inv.getStackInSlot(fillSlot);
			
			IFluidHandlerItem contentCapa = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			
			if (fillStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face))
			{
				int transferTime = getTransferTime(stack);
				
				IFluidHandlerItem fillCapa = fillStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face);
				
				FluidStack fillFluid = contentCapa.drain(Fluid.BUCKET_VOLUME, false);
				boolean canTransfer = fillFluid == null || transferTime > 4;
				
				if (!canTransfer)
				{
					int val = fillCapa.fill(fillFluid, false);
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
						
						
						if (fillFluid != null)
						{
							int val = fillCapa.fill(fillFluid, true);
							if (val > 0)
							{
								fillFluid.amount = val;
								contentCapa.drain(fillFluid, true);
								
								inv.setInventorySlotContents(fillSlot, fillCapa.getContainer());
							}
						}
						
						int drainX = x + face.getFrontOffsetX(),
							drainY = y + face.getFrontOffsetZ();
						
						if (drainY == 0 && y != 0) drainY = height;
						else if (y == 0 && drainY == 1) drainY = -1;
						else if (y == 0 && drainY == -1) drainY = height - 1;
						else if (drainY == height) drainY = 0;
						
						if (drainX >= 0 && drainX < width &&
							drainY >= 0 && drainY < height)
						{
							int drainSlot = drainX + width * drainY;
							
							ItemStack drainStack = inv.getStackInSlot(drainSlot);
							
							if (drainStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face.getOpposite()))
							{
								IFluidHandlerItem drainCapa = drainStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face.getOpposite());
								
								FluidStack drainFluid = drainCapa.drain(Fluid.BUCKET_VOLUME, false);
								if (drainFluid != null)
								{
									drainFluid.amount = contentCapa.fill(drainFluid, false);
									if (drainFluid.amount > 0)
									{
										drainFluid.amount = fillCapa.fill(drainFluid, false);
										if (drainFluid.amount > 0)
										{
											drainCapa.drain(drainFluid, true);
											contentCapa.fill(drainFluid, true);
											
											inv.setInventorySlotContents(drainSlot, drainCapa.getContainer());
											inv.setInventorySlotContents(fillSlot, fillCapa.getContainer());
										}
									}
								}
							}
						}
					}
					setTransferTime(stack, transferTime);
				}
			}
		}
	}

	@Override
	public List<Icon> getRenderIcons(ItemStack stack, GuiContainer gui, Slot slot, InventoryPlayer inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		EnumFacing face = getFacing(stack);
		
		int itemSlot = slot.getSlotIndex(),
			width = inv.getHotbarSize(),
			height = inv.mainInventory.size() / width;
		
		if (itemSlot >= width * height) return icons;
		
		int x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getFrontOffsetX(),
			extractY = y + face.getFrontOffsetZ(),
			insertX  = x - face.getFrontOffsetX(),
			insertY  = y - face.getFrontOffsetZ();
		
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
			Slot extractSlot = gui.inventorySlots.getSlotFromInventory(inv, extractX + width * extractY);
			icons.add(new Icon(extractSlot, face, 0x0099FF, true, false));
		}
		else icons.add(new Icon(null, face, 0x0099FF, true, true));
		
		if (insertX >= 0 && insertX < width && insertY >= 0 && insertY < height)
		{
			Slot fillSlot = gui.inventorySlots.getSlotFromInventory(inv, insertX + width * insertY);
			icons.add(new Icon(fillSlot, face.getOpposite(), 0xFF7700, false, false));
		}
		else icons.add(new Icon(null, face.getOpposite(), 0xFF7700, false, true));
		
		return icons;
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new FluidHandlerItemStack(stack, Fluid.BUCKET_VOLUME);
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
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_TRANSFER_TIME, NBT.TAG_INT)) return tag.getInteger(NBT_TRANSFER_TIME);
		}
		return 0;
	}
	
	public static void setTransferTime(ItemStack stack, int transferTime)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setInteger(NBT_TRANSFER_TIME, transferTime);
	}
	
}
