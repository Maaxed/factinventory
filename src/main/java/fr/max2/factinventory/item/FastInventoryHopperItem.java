package fr.max2.factinventory.item;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class FastInventoryHopperItem extends InventoryHopperItem
{
	
	public FastInventoryHopperItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	protected void update(ItemStack stack, Inventory inv, Player player, int itemSlot)
	{
		Direction face = getFacing(stack);
		
		int width = Inventory.getSelectionSize(),
			height = inv.items.size() / width,
			x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getStepX(),
			extractY = y + face.getStepZ(),
			insertX  = x - face.getStepX(),
			insertY  = y - face.getStepZ();
		
		if (extractY == 0 && y != 0) extractY = height;
		else if (y == 0 && extractY == 1) extractY = -1;
		else if (y == 0 && extractY == -1) extractY = height - 1;
		else if (extractY == height) extractY = 0;

		if (insertY == 0 && y != 0) insertY = height;
		else if (y == 0 && insertY == 1) insertY = -1;
		else if (y == 0 && insertY == -1) insertY = height - 1;
		else if (insertY == height) insertY = 0;
		
		if (extractX >= 0 && extractX < width &&
			extractY >= 0 && extractY < height &&
			insertX >= 0 && insertX < width &&
			insertY >= 0 && insertY < height)
		{
			int extractSlot = extractX + width * extractY,
		        insertSlot = insertX + width * insertY;
			
			ItemStack extractStack = inv.getItem(extractSlot);
			ItemStack insertStack = inv.getItem(insertSlot);

			LazyOptional<IItemHandler> insertCapaOptional = insertStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
			
			if (!insertStack.isEmpty() && insertCapaOptional.isPresent())
			{
				if (!extractStack.isEmpty())
				{
					IItemHandler insertCapa = insertCapaOptional.orElse(null);
					IItemHandler extractCapa = extractStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()).orElse(null);
					
					if (extractCapa != null)
					{
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
					else
					{
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
									inv.setItem(extractSlot, ItemStack.EMPTY);
								}
								
								return;											//   <-- Here is a return !
							}
						}
					}
				}
			}
			else if (insertStack.isEmpty() || (insertStack.getCount() < insertStack.getMaxStackSize() && insertStack.getCount() < inv.getMaxStackSize()))
			{
				IItemHandler extractCapa = extractStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()).orElse(null);
				
				if (extractCapa != null)
				{
					int extractSlots = extractCapa.getSlots();
					for (int extractIndex = 0; extractIndex < extractSlots; extractIndex++)
					{
						ItemStack extractedStack = extractCapa.extractItem(extractIndex, 1, true);
						if (!extractedStack.isEmpty() && (insertStack.isEmpty() || InventoryUtils.canCombine(insertStack, extractedStack)))
						{
							if (insertStack.isEmpty())
							{
								inv.setItem(insertSlot, extractedStack);
							}
							else insertStack.grow(1);
							
							extractCapa.extractItem(extractIndex, 1, false);
							
							return;											//   <-- Here is a return !
							
						}
					}
				}
				else if (!extractStack.isEmpty() && (insertStack.isEmpty() || InventoryUtils.canCombine(insertStack, extractStack)))
				{
					if (insertStack.isEmpty())
					{
						ItemStack extractedStack = extractStack.copy();
						extractedStack.setCount(1);
						inv.setItem(insertSlot, extractedStack);
					}
					else insertStack.grow(1);
					
					extractStack.shrink(1);
					if (extractStack.isEmpty())
					{
						inv.setItem(extractSlot, ItemStack.EMPTY);
					}
					
					return;											//   <-- Here is a return !
				}
			}
		}
	}
	
}
