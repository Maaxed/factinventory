package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.utils.InventoryUtils;
import fr.max2.factinventory.utils.KeyModifierState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fmllegacy.hooks.BasicEventHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class InventoryFurnaceItem extends InventoryItem
{
	public static final ResourceLocation BURN_TIME_GETTER_LOC = new ResourceLocation(FactinventoryMod.MOD_ID, "burn_time");
	
	public InventoryFurnaceItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1 - (getCookTime(stack) / (double)getTotalCookTime(stack));
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return 0xFF0000 | (int)(getDurabilityForDisplay(stack) * 0xFF) << 8;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		KeyModifierState keyModifiers = FactinventoryMod.proxy.getKeyModifierState();
		if (keyModifiers.shift)
		{
			tooltip.add(new TranslatableComponent("tooltip.ingredient_input.desc").withStyle(ChatFormatting.BLUE));
			tooltip.add(new TranslatableComponent("tooltip.fuel_input.desc").withStyle(ChatFormatting.DARK_PURPLE));
			tooltip.add(new TranslatableComponent("tooltip.product_output.desc").withStyle(ChatFormatting.GOLD));
		}
		else
		{
			tooltip.add(new TranslatableComponent("tooltip.interaction_info_on_shift.desc"));
		}
		
		if (keyModifiers.control)
		{
			ItemStack smeltingItem = getSmeltingStack(stack);
			if (smeltingItem.isEmpty())
			{
				tooltip.add(new TranslatableComponent("tooltip.not_smelting.desc"));
			}
			else
			{
				tooltip.add(new TranslatableComponent("tooltip.smelting_item.desc", smeltingItem.getDisplayName()));
			}
			
			int burnTime = getStackBurnTime(stack);
			if (burnTime > 0)
			{
				tooltip.add(new TranslatableComponent("tooltip.burning_time.desc", burnTime));
			}
			else
			{
				tooltip.add(new TranslatableComponent("tooltip.not_burning.desc"));
			}
		}
		else
		{
			tooltip.add(new TranslatableComponent("tooltip.smelting_info_on_ctrl.desc"));
		}
	}
	
	@Override
	protected void update(ItemStack stack, Inventory inv, Player player, int itemSlot)
	{
		int width = Inventory.getSelectionSize(),
			height = inv.items.size() / width,
			x = itemSlot % width,
			y = itemSlot / width;
		
		int burnTime = getStackBurnTime(stack);
		int cookTime = getCookTime(stack);
		
    	int totalCookTime = getTotalCookTime(stack);
    	ItemStack smeltingStack = getSmeltingStack(stack);
		
		if (burnTime > 0)
		{
			burnTime--;
			setBurnTime(stack, burnTime);
		}
        
		if (smeltingStack.isEmpty())
        {
			// Try pull a new item
			
			int inputX = x, inputY = y - 1;
			int outputX = x, outputY = y + 1;

			if (inputY == 0 && y != 0) inputY = height;
			else if (y == 0 && inputY == 1) inputY = -1;
			else if (y == 0 && inputY == -1) inputY = height - 1;
			else if (inputY == height) inputY = 0;

			if (outputY == 0 && y != 0) outputY = height;
			else if (y == 0 && outputY == 1) outputY = -1;
			else if (y == 0 && outputY == -1) outputY = height - 1;
			else if (outputY == height) outputY = 0;
			
			if (inputY >= 0 && inputY < height && outputY >= 0 && outputY < height)
			{
				int inputSlot = inputX + width * inputY;
	            ItemStack newInputStack = inv.getItem(inputSlot);
	            
	            if (!newInputStack.isEmpty())
	            {
					int outputSlot = outputX + width * outputY;
	            	ItemStack outputStack = inv.getItem(outputSlot);
            		IItemHandler inputCapa = newInputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.SOUTH).orElse(null);
	            	if (inputCapa != null)
	            	{
	            		int slots = inputCapa.getSlots();
	            		for (int i = 0; i < slots && smeltingStack.isEmpty(); i++)
	            		{
	            			ItemStack currentSlot = inputCapa.extractItem(i, 1, true);
	                        
	            			Container slotInv = new SimpleContainer(currentSlot);
	    	            	
	    	            	SmeltingRecipe recipe = getSmeltingRecipe(player, slotInv);
	    	            	if (recipe != null)
	    	            	{
		            			ItemStack result = recipe.assemble(slotInv);
		            			if(!result.isEmpty() && canPush(result, outputStack, Direction.NORTH))
		            			{
		            				smeltingStack = inputCapa.extractItem(i, 1, false);
		            				totalCookTime = recipe.getCookingTime();
		            			}
	    	            	}
	            		}
	            	}
	            	else
	            	{
	            		Container slotInv = new SimpleContainer(newInputStack);
    	            	
    	            	SmeltingRecipe recipe = getSmeltingRecipe(player, slotInv);
    	            	if (recipe != null)
    	            	{
                			ItemStack result = recipe.assemble(slotInv);
    	        			if(!result.isEmpty() && canPush(result, outputStack, Direction.NORTH))
    		            	{
    		            		smeltingStack = newInputStack.copy();
    		            		smeltingStack.setCount(1);
    		            		
    		            		newInputStack.shrink(1);
    		            		if (newInputStack.isEmpty()) inv.setItem(inputSlot, ItemStack.EMPTY);
                				totalCookTime = recipe.getCookingTime();
    		            	}
    	            	}
	            	}
	            	
		            if (!smeltingStack.isEmpty())
		            {
		            	setSmeltingStack(stack, smeltingStack);
		            	setTotalCookTime(stack, totalCookTime);
		            }
	            }
			}
        }
		
		if (burnTime == 0 && !smeltingStack.isEmpty())
        {
			// Try fill with fuel
			for (Direction side : FUEL_SIDE)
			{
				int fuelX = x + side.getStepX(), fuelY = y;
				
				if (fuelX >= 0 && fuelX < width)
				{
					int fuelSlot = fuelX + width * fuelY;
					ItemStack fuelItem = inv.getItem(fuelSlot);

					IItemHandler fuelCapa = fuelItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
					if (fuelCapa != null)
					{
						
						int slots = fuelCapa.getSlots();
						for (int i = 0; i < slots && burnTime == 0; i++)
						{
							ItemStack testStack = fuelCapa.extractItem(i, 1, true);
							
							if (AbstractFurnaceBlockEntity.isFuel(testStack) || FurnaceFuelSlot.isBucket(testStack) && testStack.getItem() != Items.BUCKET)
							{
								burnTime = ForgeHooks.getBurnTime(testStack, RecipeType.SMELTING);
								
								if (burnTime > 0)
								{
									Item item = fuelCapa.extractItem(i, 1, false).getItem();
									
									if (fuelCapa.getStackInSlot(i).isEmpty())
									{
										fuelCapa.insertItem(i, item.getContainerItem(testStack), false);
									}
									
									setBurnTime(stack, burnTime);
								}
							}
						}
						if (burnTime != 0) break;
					}
					else if (AbstractFurnaceBlockEntity.isFuel(fuelItem) || FurnaceFuelSlot.isBucket(fuelItem) && fuelItem.getItem() != Items.BUCKET)
					{
						burnTime = ForgeHooks.getBurnTime(fuelItem, RecipeType.SMELTING);
						
						if (burnTime > 0)
						{
							Item item = fuelItem.getItem();
							fuelItem.shrink(1);
							
							if (fuelItem.isEmpty())
							{
								inv.setItem(fuelSlot, item.getContainerItem(fuelItem));
							}
							setBurnTime(stack, burnTime);
						}
					}
				}
			}
        }
        
        if (burnTime > 0)
        {
        	if (!smeltingStack.isEmpty())
            {
        		// Smelt
        		
                cookTime++;
                if (cookTime < totalCookTime)
                {
                	setCookTime(stack, cookTime);
                }
                else
                {
                	// Smelt item
                    
                	int outputX = x, outputY = y + 1;

        			if (outputY == 0 && y != 0) outputY = height;
        			else if (y == 0 && outputY == 1) outputY = -1;
        			else if (y == 0 && outputY == -1) outputY = height - 1;
        			else if (outputY == height) outputY = 0;
        			
        			if (outputY >= 0 && outputY < height)
        			{
    					int outputSlot = outputX + width * outputY;
    	            	ItemStack outputStack = inv.getItem(outputSlot);
    	                Container slotInv = new SimpleContainer(smeltingStack);
    	            	
    	            	SmeltingRecipe recipe = getSmeltingRecipe(player, slotInv);
    	            	if (recipe != null)
    	            	{
		            		ItemStack result = recipe.assemble(inv);
	
		            		IItemHandler outputHandler = outputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.SOUTH).orElse(null);
	    	            	if (outputHandler != null)
	    	        		{
	    	            		if (canPush(result, outputHandler))
	    	            		{
	    	            			result.onCraftedBy(player.level, player, result.getCount());
	    	            			BasicEventHooks.firePlayerSmeltedEvent(player, result);
	    	            			push(result, outputHandler);
	    	            			smeltingStack = ItemStack.EMPTY;
	    	            		}
	    	        		}
	    	        		else
	    	        		{
	    	        			if (outputStack.isEmpty())
	    	        			{
	    	        				inv.setItem(outputSlot, result.copy());
	    	            			smeltingStack = ItemStack.EMPTY;
	    	        			}
	    	        			else if (InventoryUtils.canCombine(result, outputStack) && outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize() && outputStack.getCount() + result.getCount() <= inv.getMaxStackSize())
	    	        			{
	    	        				outputStack.grow(result.getCount());
	    	        				outputStack.onCraftedBy(player.level, player, result.getCount());
	    	        				BasicEventHooks.firePlayerSmeltedEvent(player, outputStack);
	    	            			smeltingStack = ItemStack.EMPTY;
	    	        			}
	    	        		}
	    	            	
	    		            if (smeltingStack.isEmpty())
	    		            {
			                    int i = result.getCount();
			                    float f = recipe.getExperience();
	
			                    if (f == 0.0F)
			                    {
			                        i = 0;
			                    }
			                    else if (f < 1.0F)
			                    {
			                        int j = Mth.floor(i * f);
	
			                        if (j < Mth.ceil(i * f) && Math.random() < i * f - j)
			                        {
			                            ++j;
			                        }
	
			                        i = j;
			                    }
			                    
			                    while (i > 0)
			                    {
			                        int k = ExperienceOrb.getExperienceValue(i);
			                        i -= k;
			                        player.level.addFreshEntity(new ExperienceOrb(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, k));
			                    }
	    		            	
	    	                	setCookTime(stack, 0);
	    		            	setSmeltingStack(stack, ItemStack.EMPTY);
	    		            	setTotalCookTime(stack, 200);
	    		            }
    	            	}
    	            }
                	
                }
            }
        }
        else
        {
        	if (cookTime > 0)
	        {
	        	if (cookTime > totalCookTime) cookTime = totalCookTime;
	        	cookTime = Math.min(0, cookTime - 2);
	        	setCookTime(stack, cookTime);
	        }
        }
        
	}

	@Override
	public List<Icon> getRenderIcons(ItemStack stack, AbstractContainerMenu container, Slot slot, Inventory inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		int itemSlot = slot.getSlotIndex(),
			width = Inventory.getSelectionSize(),
			height = inv.items.size() / width;
		
		if (itemSlot >= width * height) return icons;
		
		int x = itemSlot % width,
			y = itemSlot / width,
			pullY = y - 1,
			pushY = y + 1;
		
		if (pullY == 0 && y != 0)
			pullY = height;
		else if (y == 0 && pullY == 1)
			pullY = -1;
		else if (y == 0 && pullY == -1)
			pullY = height - 1;
		else if (pullY == height)
			pullY = 0;
		
		if (pushY == 0 && y != 0)
			pushY = height;
		else if (y == 0 && pushY == 1)
			pushY = -1;
		else if (y == 0 && pushY == -1)
			pushY = height - 1;
		else if (pushY == height)
			pushY = 0;
		
		if (pullY >= 0 && pullY < height)
		{
			Slot extractSlot = findSlot(container, slot, x + width * pullY);
			icons.add(new Icon(extractSlot, Direction.NORTH, 0x0099FF, true, false));
		}
		else icons.add(new Icon(null, Direction.NORTH, 0x0099FF, true, true));
		
		if (pushY >= 0 && pushY < height)
		{
			Slot fillSlot = findSlot(container, slot, x + width * pushY);
			icons.add(new Icon(fillSlot, Direction.SOUTH, 0xFF7700, false, false));
		}
		else icons.add(new Icon(null, Direction.SOUTH, 0xFF7700, false, true));
		
		int fuelX1 = x - 1,
			fuelX2 = x + 1;
		
		if (fuelX1 >= 0 && fuelX1 < width)
		{
			Slot fuelSlot = findSlot(container, slot, fuelX1 + width * y);
			icons.add(new Icon(fuelSlot, Direction.WEST, 0xAA00FF, true, false));
		}
		else icons.add(new Icon(null, Direction.WEST, 0xAA00FF, true, true));
		
		if (fuelX2 >= 0 && fuelX2 < width)
		{
			Slot fuelSlot = findSlot(container, slot, fuelX2 + width * y);
			icons.add(new Icon(fuelSlot, Direction.EAST, 0xAA00FF, true, false));
		}
		else icons.add(new Icon(null, Direction.EAST, 0xAA00FF, true, true));
		
		
		return icons;
	}
	
	private static SmeltingRecipe getSmeltingRecipe(Player player, Container inv)
	{
		return player.level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inv, player.level).orElse(null);
	}
	
	private static final Direction[] FUEL_SIDE = { Direction.EAST, Direction.WEST };

	private static final String NBT_BURN_TIME = "BurnTime";
	
	public static int getStackBurnTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_BURN_TIME, Tag.TAG_INT)) return tag.getInt(NBT_BURN_TIME);
		}
		return 0;
	}
	
	public static void setBurnTime(ItemStack stack, int burnTime)
	{
		stack.addTagElement(NBT_BURN_TIME, IntTag.valueOf(burnTime));
	}
	
	
	private static final String NBT_COOK_TIME = "CookTime";
	
	public static int getCookTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_COOK_TIME, Tag.TAG_INT)) return tag.getInt(NBT_COOK_TIME);
		}
		return 0;
	}
	
	public static void setCookTime(ItemStack stack, int cookTime)
	{
		stack.addTagElement(NBT_COOK_TIME, IntTag.valueOf(cookTime));
	}
	
	
	private static final String NBT_TOTAL_COOK_TIME = "TotalCookTime";
	
	public static int getTotalCookTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_TOTAL_COOK_TIME, Tag.TAG_INT)) return tag.getInt(NBT_TOTAL_COOK_TIME);
		}
		return 0;
	}
	
	public static void setTotalCookTime(ItemStack stack, int cookTime)
	{
		stack.addTagElement(NBT_TOTAL_COOK_TIME, IntTag.valueOf(cookTime));
	}
	
	
	private static final String NBT_ACTUAL_INPUT = "SmeltingItem";
	
	public static ItemStack getSmeltingStack(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_ACTUAL_INPUT, Tag.TAG_COMPOUND)) return ItemStack.of(tag.getCompound(NBT_ACTUAL_INPUT));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setSmeltingStack(ItemStack stack, ItemStack smeltingStck)
	{
		stack.addTagElement(NBT_ACTUAL_INPUT, smeltingStck.serializeNBT());
	}
	
}
