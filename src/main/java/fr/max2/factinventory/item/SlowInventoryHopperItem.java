package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SlowInventoryHopperItem extends InventoryHopperItem
{
	
	public SlowInventoryHopperItem()
	{
		super(); //1 item = 8 ticks
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		if (GuiScreen.isCtrlKeyDown())
		{
			ItemStack transferringItem = getTransferringStack(stack);
			if (transferringItem.isEmpty())
			{
				tooltip.add(I18n.format("tooltip.not_transferring.desc"));
			}
			else
			{
				tooltip.add(I18n.format("tooltip.transferring_item.desc", transferringItem.getDisplayName()));
			}
			
			tooltip.add(I18n.format("tooltip.transfer_time.desc", getTransferTime(stack)));
		}
		else
		{
			tooltip.add(I18n.format("tooltip.transfer_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getMetadata() != oldStack.getMetadata() || newStack.getCount() != oldStack.getCount() || !ItemStack.areItemStacksEqual(getTransferringStack(newStack), getTransferringStack(oldStack)));
	}
	
	@Override
	protected void update(ItemStack stack, InventoryPlayer inv, EntityPlayer player, int itemSlot)
	{
		int transferTime = getTransferTime(stack);
		
		transferTime--;
		if (transferTime <= 0)
		{
			transferTime = 8;
			
			updateHopper(stack, inv, itemSlot);
		}
		setTransferTime(stack, transferTime);
	}
	
	protected void updateHopper(ItemStack stack, InventoryPlayer inv, int itemSlot)
	{
		EnumFacing face = getFacing(stack);
		
		int width = InventoryPlayer.getHotbarSize(),
			height = inv.mainInventory.size() / width,
			x = itemSlot % width,
			y = itemSlot / width;
		
		int insertX  = x - face.getFrontOffsetX(),
			insertY  = y - face.getFrontOffsetZ();
		
		if (insertY == 0 && y != 0) insertY = height;
		else if (y == 0 && insertY ==  1) insertY = -1;
		else if (y == 0 && insertY == -1) insertY = height - 1;
		else if (insertY == height) insertY = 0;
		
		if (insertX >= 0 && insertX < width &&
			insertY >= 0 && insertY < height)
		{
			int insertSlot = insertX + width * insertY;
			
			ItemStack contentStack = getTransferringStack(stack);
			ItemStack originalContent = contentStack;
			ItemStack insertStack = inv.getStackInSlot(insertSlot);
			
			if (!contentStack.isEmpty())
			{
				
				if (!insertStack.isEmpty() && insertStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face))
				{
					IItemHandler insertCapa = insertStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
					
					int insertSlots = insertCapa.getSlots();
					for (int insertIndex = 0; insertIndex < insertSlots && !contentStack.isEmpty(); insertIndex++)
					{
						contentStack = insertCapa.insertItem(insertIndex, contentStack, false);
					}
				}
				else if (insertStack.isEmpty() || (insertStack.getCount() < insertStack.getMaxStackSize() && insertStack.getCount() < inv.getInventoryStackLimit()))
				{
					if ((insertStack.isEmpty() || InventoryUtils.canCombine(insertStack, contentStack)))
					{
						if (insertStack.isEmpty())
						{
							inv.setInventorySlotContents(insertSlot, contentStack);
						}
						else insertStack.grow(1);
						
						contentStack = ItemStack.EMPTY;
					}
				}
			}
			
			if (contentStack.isEmpty())
			{
				int extractX = x + face.getFrontOffsetX(),
					extractY = y + face.getFrontOffsetZ();
				
				if (extractY == 0 && y != 0) extractY = height;
				else if (y == 0 && extractY == 1) extractY = -1;
				else if (y == 0 && extractY == -1) extractY = height - 1;
				else if (extractY == height) extractY = 0;
				
				if (extractX >= 0 && extractX < width &&
					extractY >= 0 && extractY < height)
				{
					int extractSlot = extractX + width * extractY;
					
					ItemStack extractStack = inv.getStackInSlot(extractSlot);
					
					if (!extractStack.isEmpty())
					{
						if (extractStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()))
						{
							IItemHandler extractCapa = extractStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
							
							int extractSlots = extractCapa.getSlots();
							for (int extractIndex = 0; extractIndex < extractSlots && contentStack.isEmpty(); extractIndex++)
							{
								ItemStack extractedStack = extractCapa.extractItem(extractIndex, 1, true);
								if (!extractedStack.isEmpty() && canPush(extractedStack, insertStack, EnumFacing.NORTH))
								{
									contentStack = extractedStack;
									
									extractCapa.extractItem(extractIndex, 1, false);
								}
							}
						}
						else if (canPush(extractStack, insertStack, EnumFacing.NORTH))
						{
							contentStack = extractStack.copy();
							contentStack.setCount(1);
							
							extractStack.shrink(1);
							if (extractStack.isEmpty())
							{
								inv.setInventorySlotContents(extractSlot, ItemStack.EMPTY);
							}
						}
					}
				}
			}
			
			if (contentStack != originalContent)
			{
				setTransferringStack(stack, contentStack);
			}
		}
	}
	
	
	private static final String NBT_TRANSFERRING_ITEM = "TransferringItem";
	
	public static ItemStack getTransferringStack(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_TRANSFERRING_ITEM, NBT.TAG_COMPOUND)) return new ItemStack(tag.getCompoundTag(NBT_TRANSFERRING_ITEM));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setTransferringStack(ItemStack stack, ItemStack transferringStack)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setTag(NBT_TRANSFERRING_ITEM, transferringStack.serializeNBT());
	}
	
	
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
