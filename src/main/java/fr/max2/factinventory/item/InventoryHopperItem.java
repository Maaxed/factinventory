package fr.max2.factinventory.item;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class InventoryHopperItem extends RotatableInventoryItem
{
	
	public InventoryHopperItem()
	{
		super();
	}
	
	@Override
	protected void update(ItemStack stack, InventoryPlayer inv, int itemSlot)
	{
		EnumFacing face = getFacing(stack);
		
		int width = inv.getHotbarSize(),
			height = inv.mainInventory.size() / width,
			x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getFrontOffsetX(),
			extractY = y + face.getFrontOffsetZ(),
			insertX  = x - face.getFrontOffsetX(),
			insertY  = y - face.getFrontOffsetZ();
		
		if (extractY == 0 && y != 0) extractY = height;
		else if (y == 0 && extractY == 1) extractY = -1;
		else if (y == 0 && extractY == -1) extractY = height - 1;
		else if (extractY == height) extractY = 0;

		if (insertY == 0 && y != 0) insertY = height;
		else if (y == 0 && insertY == 1) insertY = -1;
		else if (y == 0 && insertY == -1) insertY = height - 1;
		else if (insertY == height) insertY = 0;
		
		
		//1
		//2
		//3
		//0
		
		if (extractX >= 0 && extractX < width &&
			extractY >= 0 && extractY < height &&
			insertX >= 0 && insertX < width &&
			insertY >= 0 && insertY < height)
		{
			int extractSlot = extractX + width * extractY,
		        insertSlot = insertX + width * insertY;
			
			ItemStack extractStack = inv.getStackInSlot(extractSlot);
			ItemStack insertStack = inv.getStackInSlot(insertSlot);
			
			if (insertStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face))
			{
				if (extractStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()))
				{
					IItemHandler extractCapa = extractStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
					IItemHandler insertCapa = insertStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
					
					int extractSlots = extractCapa.getSlots();
					int insertSlots = insertCapa.getSlots();
					for (int extractIndex = 0; extractIndex < extractSlots; extractIndex++)
					{
						ItemStack extractedStack = extractCapa.extractItem(extractIndex, 1, true);
						if(!extractedStack.isEmpty())
						{
							for (int insertIndex = 0; insertIndex < insertSlots; insertIndex++)
							{
								ItemStack remainder = insertCapa.insertItem(insertIndex, extractedStack, false);
								if (remainder.isEmpty())
								{
									extractCapa.extractItem(extractIndex, 1, false);
									
									return;											//   <-- Here is a return !
								}
							}
						}
					}
				}
				else if (!extractStack.isEmpty())
				{
					IItemHandler insertCapa = insertStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
					
					ItemStack extractedStack = extractStack.copy();
					extractedStack.setCount(1);
					
					int insertSlots = insertCapa.getSlots();
					for (int insertIndex = 0; insertIndex < insertSlots; insertIndex++)
					{
						ItemStack remainder = insertCapa.insertItem(insertIndex, extractedStack, false);
						if (remainder.isEmpty())
						{
							extractStack.shrink(1);
							if (extractStack.isEmpty())
							{
								inv.setInventorySlotContents(extractSlot, ItemStack.EMPTY);
							}
							
							return;											//   <-- Here is a return !
						}
					}
				}
			}
			else if (insertStack.isEmpty() || (insertStack.getCount() < insertStack.getMaxStackSize() && insertStack.getCount() < inv.getInventoryStackLimit()))
			{
				if (extractStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()))
				{
					IItemHandler extractCapa = extractStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
					
					int extractSlots = extractCapa.getSlots();
					for (int extractIndex = 0; extractIndex < extractSlots; extractIndex++)
					{
						ItemStack extractedStack = extractCapa.extractItem(extractIndex, 1, true);
						if (!extractedStack.isEmpty() && (insertStack.isEmpty() || InventoryUtils.canCombine(insertStack, extractedStack)))
						{
							if (insertStack.isEmpty())
							{
								inv.setInventorySlotContents(insertSlot, extractedStack);
							}
							else insertStack.grow(1);
							
							extractCapa.extractItem(extractIndex, 1, false);
							
							return;											//   <-- Here is a return !
							
						}
					}
				}
				else if (!extractStack.isEmpty())
				{
					if (insertStack.isEmpty() || InventoryUtils.canCombine(insertStack, extractStack))
					{
						if (insertStack.isEmpty())
						{
							ItemStack extractedStack = extractStack.copy();
							extractedStack.setCount(1);
							inv.setInventorySlotContents(insertSlot, extractedStack);
						}
						else insertStack.grow(1);
						
						extractStack.shrink(1);
						if (extractStack.isEmpty())
						{
							inv.setInventorySlotContents(extractSlot, ItemStack.EMPTY);
						}
						
						return;											//   <-- Here is a return !
					}
				}
			}
		}
	}
	
}
