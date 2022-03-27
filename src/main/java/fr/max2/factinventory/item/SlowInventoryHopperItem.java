package fr.max2.factinventory.item;

import java.util.Optional;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class SlowInventoryHopperItem extends InventoryHopperItem
{
	
	public SlowInventoryHopperItem(Properties properties)
	{
		super(properties); //1 item = 8 ticks
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack pStack)
	{
	      NonNullList<ItemStack> items = NonNullList.create();
	      items.add(getTransferringStack(pStack));
	      return Optional.of(new SimpleItemTooltip(items));
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack pStack, Slot targetSlot, ClickAction pAction, Player pPlayer)
	{
		if (pAction != ClickAction.SECONDARY)
			return false;
		
		ItemStack targetItem = targetSlot.getItem();
		if (targetItem.isEmpty())
		{
			ItemStack content = getTransferringStack(pStack);
			if (content.isEmpty())
				return true;
			playRemoveOneSound(pPlayer);
			setTransferringStack(pStack, targetSlot.safeInsert(content));
		}
		else if (targetItem.getItem().canFitInsideContainerItems())
		{
			int maxCount = 1;
			ItemStack content = getTransferringStack(pStack);
			if (!content.isEmpty())
				return true;
			ItemStack toInsert = targetSlot.safeTake(targetItem.getCount(), maxCount, pPlayer);
			if (toInsert.isEmpty())
				return true;
			setTransferringStack(pStack, toInsert);
			playInsertSound(pPlayer);
		}
		
		return true;
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack carriedStack, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess)
	{
		if (pAction != ClickAction.SECONDARY || !pSlot.allowModification(pPlayer))
			return false;
		
		if (carriedStack.isEmpty())
		{
			ItemStack content = getTransferringStack(pStack);
			if (content.isEmpty())
			{
				return super.overrideOtherStackedOnMe(pStack, content, pSlot, pAction, pPlayer, pAccess);
			}
			if (pAccess.set(content))
			{
				setTransferringStack(pStack, ItemStack.EMPTY);
				playRemoveOneSound(pPlayer);
			}
		}
		else
		{
			ItemStack content = getTransferringStack(pStack);
			if (!content.isEmpty())
				return true;

			setTransferringStack(pStack, carriedStack.split(1));
			playInsertSound(pPlayer);
		}
		
		return true;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getCount() != oldStack.getCount() || !ItemStack.matches(getTransferringStack(newStack), getTransferringStack(oldStack)));
	}
	
	@Override
	protected void update(ItemStack stack, Inventory inv, Player player, int itemSlot)
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
	
	protected void updateHopper(ItemStack stack, Inventory inv, int itemSlot)
	{
		Direction face = getFacing(stack);
		
		int width = Inventory.getSelectionSize(),
			height = inv.items.size() / width,
			x = itemSlot % width,
			y = itemSlot / width;
		
		int insertX  = x - face.getStepX(),
			insertY  = y - face.getStepZ();
		
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
			ItemStack insertStack = inv.getItem(insertSlot);
			
			if (!contentStack.isEmpty())
			{
				LazyOptional<IItemHandler> insertCapaOptional = insertStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face);
				if (!insertStack.isEmpty() && insertCapaOptional.isPresent())
				{
					IItemHandler insertCapa = insertCapaOptional.orElse(null);
					
					int insertSlots = insertCapa.getSlots();
					for (int insertIndex = 0; insertIndex < insertSlots && !contentStack.isEmpty(); insertIndex++)
					{
						contentStack = insertCapa.insertItem(insertIndex, contentStack, false);
					}
				}
				else if (insertStack.isEmpty() || (insertStack.getCount() < insertStack.getMaxStackSize() && insertStack.getCount() < inv.getMaxStackSize()))
				{
					if ((insertStack.isEmpty() || InventoryUtils.canCombine(insertStack, contentStack)))
					{
						if (insertStack.isEmpty())
						{
							inv.setItem(insertSlot, contentStack);
						}
						else insertStack.grow(1);
						
						contentStack = ItemStack.EMPTY;
					}
				}
			}
			
			if (contentStack.isEmpty())
			{
				int extractX = x + face.getStepX(),
					extractY = y + face.getStepZ();
				
				if (extractY == 0 && y != 0) extractY = height;
				else if (y == 0 && extractY == 1) extractY = -1;
				else if (y == 0 && extractY == -1) extractY = height - 1;
				else if (extractY == height) extractY = 0;
				
				if (extractX >= 0 && extractX < width &&
					extractY >= 0 && extractY < height)
				{
					int extractSlot = extractX + width * extractY;
					
					ItemStack extractStack = inv.getItem(extractSlot);
					
					if (!extractStack.isEmpty())
					{
						LazyOptional<IItemHandler> extractCapaOptional = extractStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
						if (extractCapaOptional.isPresent())
						{
							IItemHandler extractCapa = extractCapaOptional.orElse(null);
							
							int extractSlots = extractCapa.getSlots();
							for (int extractIndex = 0; extractIndex < extractSlots && contentStack.isEmpty(); extractIndex++)
							{
								ItemStack extractedStack = extractCapa.extractItem(extractIndex, 1, true);
								if (!extractedStack.isEmpty() && canPush(extractedStack, insertStack, Direction.NORTH))
								{
									contentStack = extractedStack;
									
									extractCapa.extractItem(extractIndex, 1, false);
								}
							}
						}
						else if (canPush(extractStack, insertStack, Direction.NORTH))
						{
							contentStack = extractStack.copy();
							contentStack.setCount(1);
							
							extractStack.shrink(1);
							if (extractStack.isEmpty())
							{
								inv.setItem(extractSlot, ItemStack.EMPTY);
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
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_TRANSFERRING_ITEM, Tag.TAG_COMPOUND)) return ItemStack.of(tag.getCompound(NBT_TRANSFERRING_ITEM));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setTransferringStack(ItemStack stack, ItemStack transferringStack)
	{
		stack.addTagElement(NBT_TRANSFERRING_ITEM, transferringStack.serializeNBT());
	}
	
	
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
