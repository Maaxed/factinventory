package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.FurnaceFuelSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class InventoryFurnaceItem extends InventoryItem
{
	public static final ResourceLocation BURN_TIME_GETTER_LOC = new ResourceLocation(FactinventoryMod.MOD_ID, "burn_time");
	@OnlyIn(Dist.CLIENT)
	public static final IItemPropertyGetter
		BURN_TIME_GETTER = (stack, worldIn, entityIn) -> getStackBurnTime(stack);
	
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
    @OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if (Screen.hasShiftDown())
		{
			tooltip.add(new TranslationTextComponent("tooltip.ingredient_input.desc").mergeStyle(TextFormatting.BLUE));
			tooltip.add(new TranslationTextComponent("tooltip.fuel_input.desc").mergeStyle(TextFormatting.DARK_PURPLE));
			tooltip.add(new TranslationTextComponent("tooltip.product_output.desc").mergeStyle(TextFormatting.GOLD));
		}
		else
		{
			tooltip.add(new TranslationTextComponent("tooltip.interaction_info_on_shift.desc"));
		}
		
		if (Screen.hasControlDown())
		{
			ItemStack smeltingItem = getSmeltingStack(stack);
			if (smeltingItem.isEmpty())
			{
				tooltip.add(new TranslationTextComponent("tooltip.not_smelting.desc"));
			}
			else
			{
				tooltip.add(new TranslationTextComponent("tooltip.smelting_item.desc", smeltingItem.getDisplayName()));
			}
			
			int burnTime = getStackBurnTime(stack);
			if (burnTime > 0)
			{
				tooltip.add(new TranslationTextComponent("tooltip.burning_time.desc", burnTime));
			}
			else
			{
				tooltip.add(new TranslationTextComponent("tooltip.not_burning.desc"));
			}
		}
		else
		{
			tooltip.add(new TranslationTextComponent("tooltip.smelting_info_on_ctrl.desc"));
		}
	}
	
	@Override
	protected void update(ItemStack stack, PlayerInventory inv, PlayerEntity player, int itemSlot)
	{
		int width = PlayerInventory.getHotbarSize(),
			height = inv.mainInventory.size() / width,
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
	            ItemStack newInputStack = inv.getStackInSlot(inputSlot);
	            
	            if (!newInputStack.isEmpty())
	            {
					int outputSlot = outputX + width * outputY;
	            	ItemStack outputStack = inv.getStackInSlot(outputSlot);
            		IItemHandler inputCapa = newInputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.SOUTH).orElse(null);
	            	if (inputCapa != null)
	            	{
	            		int slots = inputCapa.getSlots();
	            		for (int i = 0; i < slots && smeltingStack.isEmpty(); i++)
	            		{
	            			ItemStack currentSlot = inputCapa.extractItem(i, 1, true);
	                        
	            			IInventory slotInv = new Inventory(currentSlot);
	    	            	
	    	            	FurnaceRecipe recipe = getSmeltingRecipe(player, slotInv);
	    	            	if (recipe != null)
	    	            	{
		            			ItemStack result = recipe.getCraftingResult(slotInv);
		            			if(!result.isEmpty() && canPush(result, outputStack, Direction.NORTH))
		            			{
		            				smeltingStack = inputCapa.extractItem(i, 1, false);
		            				totalCookTime = recipe.getCookTime();
		            			}
	    	            	}
	            		}
	            	}
	            	else
	            	{
	            		IInventory slotInv = new Inventory(newInputStack);
    	            	
    	            	FurnaceRecipe recipe = getSmeltingRecipe(player, slotInv);
    	            	if (recipe != null)
    	            	{
                			ItemStack result = recipe.getCraftingResult(slotInv);
    	        			if(!result.isEmpty() && canPush(result, outputStack, Direction.NORTH))
    		            	{
    		            		smeltingStack = newInputStack.copy();
    		            		smeltingStack.setCount(1);
    		            		
    		            		newInputStack.shrink(1);
    		            		if (newInputStack.isEmpty()) inv.setInventorySlotContents(inputSlot, ItemStack.EMPTY);
                				totalCookTime = recipe.getCookTime();
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
				int fuelX = x + side.getXOffset(), fuelY = y;
				
				if (fuelX >= 0 && fuelX < width)
				{
					int fuelSlot = fuelX + width * fuelY;
					ItemStack fuelItem = inv.getStackInSlot(fuelSlot);

					IItemHandler fuelCapa = fuelItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
					if (fuelCapa != null)
					{
						
						int slots = fuelCapa.getSlots();
						for (int i = 0; i < slots && burnTime == 0; i++)
						{
							ItemStack testStack = fuelCapa.extractItem(i, 1, true);
							
							if (AbstractFurnaceTileEntity.isFuel(testStack) || FurnaceFuelSlot.isBucket(testStack) && testStack.getItem() != Items.BUCKET)
							{
								burnTime = AbstractFurnaceTileEntity.getBurnTimes().get(testStack.getItem());
								
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
					else if (AbstractFurnaceTileEntity.isFuel(fuelItem) || FurnaceFuelSlot.isBucket(fuelItem) && fuelItem.getItem() != Items.BUCKET)
					{
						burnTime = AbstractFurnaceTileEntity.getBurnTimes().get(fuelItem.getItem());
						
						if (burnTime > 0)
						{
							Item item = fuelItem.getItem();
							fuelItem.shrink(1);
							
							if (fuelItem.isEmpty())
							{
								inv.setInventorySlotContents(fuelSlot, item.getContainerItem(fuelItem));
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
    	            	ItemStack outputStack = inv.getStackInSlot(outputSlot);
    	                IInventory slotInv = new Inventory(smeltingStack);
    	            	
    	            	FurnaceRecipe recipe = getSmeltingRecipe(player, slotInv);
    	            	if (recipe != null)
    	            	{
		            		ItemStack result = recipe.getCraftingResult(inv);
	
		            		IItemHandler outputHandler = outputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.SOUTH).orElse(null);
	    	            	if (outputHandler != null)
	    	        		{
	    	            		if (canPush(result, outputHandler))
	    	            		{
	    	            			result.onCrafting(player.world, player, result.getCount());
	    	            			BasicEventHooks.firePlayerSmeltedEvent(player, result);
	    	            			push(result, outputHandler);
	    	            			smeltingStack = ItemStack.EMPTY;
	    	            		}
	    	        		}
	    	        		else
	    	        		{
	    	        			if (outputStack.isEmpty())
	    	        			{
	    	        				inv.setInventorySlotContents(outputSlot, result.copy());
	    	            			smeltingStack = ItemStack.EMPTY;
	    	        			}
	    	        			else if (InventoryUtils.canCombine(result, outputStack) && outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize() && outputStack.getCount() + result.getCount() <= inv.getInventoryStackLimit())
	    	        			{
	    	        				outputStack.grow(result.getCount());
	    	        				outputStack.onCrafting(player.world, player, result.getCount());
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
			                        int j = MathHelper.floor(i * f);
	
			                        if (j < MathHelper.ceil(i * f) && Math.random() < i * f - j)
			                        {
			                            ++j;
			                        }
	
			                        i = j;
			                    }
			                    
			                    while (i > 0)
			                    {
			                        int k = ExperienceOrbEntity.getXPSplit(i);
			                        i -= k;
			                        player.world.addEntity(new ExperienceOrbEntity(player.world, player.getPosX(), player.getPosY() + 0.5D, player.getPosZ() + 0.5D, k));
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
	public List<Icon> getRenderIcons(ItemStack stack, ContainerScreen<?> gui, Slot slot, PlayerInventory inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		int itemSlot = slot.getSlotIndex(),
			width = PlayerInventory.getHotbarSize(),
			height = inv.mainInventory.size() / width;
		
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
			Slot extractSlot = findSlot(gui, slot, x + width * pullY);
			icons.add(new Icon(extractSlot, Direction.NORTH, 0x0099FF, true, false));
		}
		else icons.add(new Icon(null, Direction.NORTH, 0x0099FF, true, true));
		
		if (pushY >= 0 && pushY < height)
		{
			Slot fillSlot = findSlot(gui, slot, x + width * pushY);
			icons.add(new Icon(fillSlot, Direction.SOUTH, 0xFF7700, false, false));
		}
		else icons.add(new Icon(null, Direction.SOUTH, 0xFF7700, false, true));
		
		int fuelX1 = x - 1,
			fuelX2 = x + 1;
		
		if (fuelX1 >= 0 && fuelX1 < width)
		{
			Slot fuelSlot = findSlot(gui, slot, fuelX1 + width * y);
			icons.add(new Icon(fuelSlot, Direction.WEST, 0xAA00FF, true, false));
		}
		else icons.add(new Icon(null, Direction.WEST, 0xAA00FF, true, true));
		
		if (fuelX2 >= 0 && fuelX2 < width)
		{
			Slot fuelSlot = findSlot(gui, slot, fuelX2 + width * y);
			icons.add(new Icon(fuelSlot, Direction.EAST, 0xAA00FF, true, false));
		}
		else icons.add(new Icon(null, Direction.EAST, 0xAA00FF, true, true));
		
		
		return icons;
	}
	
	private static ItemStack getSmeltingResult(PlayerEntity player, ItemStack ingredient)
	{
        IInventory slotInv = new Inventory(ingredient);
		return player.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, slotInv, player.world).map(recipe -> recipe.getCraftingResult(slotInv)).orElse(ItemStack.EMPTY);
	}
	
	private static FurnaceRecipe getSmeltingRecipe(PlayerEntity player, IInventory inv)
	{
		return player.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, inv, player.world).orElse(null);
	}
	
	private static final Direction[] FUEL_SIDE = { Direction.EAST, Direction.WEST };

	private static final String NBT_BURN_TIME = "BurnTime";
	
	public static int getStackBurnTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_BURN_TIME, NBT.TAG_INT)) return tag.getInt(NBT_BURN_TIME);
		}
		return 0;
	}
	
	public static void setBurnTime(ItemStack stack, int burnTime)
	{
		stack.setTagInfo(NBT_BURN_TIME, IntNBT.valueOf(burnTime));
	}
	
	
	private static final String NBT_COOK_TIME = "CookTime";
	
	public static int getCookTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_COOK_TIME, NBT.TAG_INT)) return tag.getInt(NBT_COOK_TIME);
		}
		return 0;
	}
	
	public static void setCookTime(ItemStack stack, int cookTime)
	{
		stack.setTagInfo(NBT_COOK_TIME, IntNBT.valueOf(cookTime));
	}
	
	
	private static final String NBT_TOTAL_COOK_TIME = "TotalCookTime";
	
	public static int getTotalCookTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_TOTAL_COOK_TIME, NBT.TAG_INT)) return tag.getInt(NBT_TOTAL_COOK_TIME);
		}
		return 0;
	}
	
	public static void setTotalCookTime(ItemStack stack, int cookTime)
	{
		stack.setTagInfo(NBT_TOTAL_COOK_TIME, IntNBT.valueOf(cookTime));
	}
	
	
	private static final String NBT_ACTUAL_INPUT = "SmeltingItem";
	
	public static ItemStack getSmeltingStack(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_ACTUAL_INPUT, NBT.TAG_COMPOUND)) return ItemStack.read(tag.getCompound(NBT_ACTUAL_INPUT));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setSmeltingStack(ItemStack stack, ItemStack smeltingStck)
	{
		stack.setTagInfo(NBT_ACTUAL_INPUT, smeltingStck.serializeNBT());
	}
	
}
