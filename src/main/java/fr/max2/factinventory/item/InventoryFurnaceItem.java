package fr.max2.factinventory.item;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class InventoryFurnaceItem extends InventoryItem
{

	private static final IItemPropertyGetter BURN_TIME_GETTER = new IItemPropertyGetter()
	{
		@Override
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
		{
			return getBurnTime(stack);
		}
	};
	
	private final int totalCookTime = 200;
	
	public InventoryFurnaceItem()
	{
		super();
		this.addPropertyOverride(new ResourceLocation(FactinventoryMod.MOD_ID, "burnTime"), BURN_TIME_GETTER);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		return 1 - (getCookTime(stack) / (double)this.totalCookTime);
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		return 0xFF0000 | (int)(getDurabilityForDisplay(stack) * 0xFF) << 8;
	}
	
	@Override
	protected void update(ItemStack stack, InventoryPlayer inv, int itemSlot)
	{
		int width = inv.getHotbarSize(),
			height = inv.mainInventory.size() / width,
			x = itemSlot % width,
			y = itemSlot / width;
		
		int burnTime = getBurnTime(stack);
		int cookTime = getCookTime(stack);
		
    	ItemStack inputStack = getActualInput(stack);
		
		if (burnTime > 0)
		{
			burnTime--;
			setBurnTime(stack, burnTime);
		}
        
		if (inputStack.isEmpty())
        {
			// Try pull a new item
			
			int inputX = x, inputY = y - 1;
			int outputX = x, outputY = y + 1;

			if (inputX == 0 && y != 0) inputX = height;
			else if (y == 0 && inputX == 1) inputX = -1;
			else if (y == 0 && inputX == -1) inputX = height - 1;
			else if (inputX == height) inputX = 0;

			if (outputX == 0 && y != 0) outputX = height;
			else if (y == 0 && outputX == 1) outputX = -1;
			else if (y == 0 && outputX == -1) outputX = height - 1;
			else if (outputX == height) outputX = 0;
			
			if (inputY >= 0 && inputY < height && outputY >= 0 && outputY < height)
			{
				int inputSlot = inputX + width * inputY;
	            ItemStack newInputStack = inv.getStackInSlot(inputSlot);
	            
	            if (!newInputStack.isEmpty())
	            {
					int outputSlot = outputX + width * outputY;
	            	ItemStack outputStack = inv.getStackInSlot(outputSlot);
	            	if (newInputStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH))
	            	{
	            		IItemHandler inputCapa = newInputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
	            		int slots = inputCapa.getSlots();
	            		for (int i = 0; i < slots; i++)
	            		{
	            			ItemStack currentSlot = inputCapa.extractItem(i, 1, true);
	            			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(currentSlot);
	            			if(!result.isEmpty() && canPush(result, outputStack, EnumFacing.NORTH))
	            			{
	            				inputStack = inputCapa.extractItem(i, 1, false);
	            			}
	            		}
	            	}
	            	else if (canPush(FurnaceRecipes.instance().getSmeltingResult(newInputStack), outputStack, EnumFacing.NORTH))
	            	{
	            		inputStack = newInputStack.copy();
	            		inputStack.setCount(1);
	            		
	            		newInputStack.shrink(1);
	            		if (newInputStack.isEmpty()) inv.setInventorySlotContents(inputSlot, ItemStack.EMPTY);
	            	}
	            	
		            if (!inputStack.isEmpty()) setActualInput(stack, inputStack);
	            }
			}
        }
		
		if (burnTime == 0 && !inputStack.isEmpty())
        {
			// Try fill with fuel
			for (EnumFacing side : FUEL_SIDE)
			{
				int fuelX = x + side.getFrontOffsetX(), fuelY = y;
				
				if (fuelX >= 0 && fuelX < width)
				{
					int fuelSlot = fuelX + width * fuelY;
					ItemStack fuelItem = inv.getStackInSlot(fuelSlot);
					
					if (fuelItem.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side))
					{
						IItemHandler fuelCapa = fuelItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
						
						int slots = fuelCapa.getSlots();
						for (int i = 0; i < slots; i++)
						{
							ItemStack testStack = fuelCapa.extractItem(i, 1, true);
							
							if (TileEntityFurnace.isItemFuel(testStack) || SlotFurnaceFuel.isBucket(testStack) && testStack.getItem() != Items.BUCKET)
							{
								burnTime = TileEntityFurnace.getItemBurnTime(testStack);
								
								if (burnTime > 0)
								{
									Item item = fuelCapa.extractItem(i, 1, false).getItem();
									
									if (fuelCapa.getStackInSlot(fuelSlot).isEmpty())
									{
										fuelCapa.insertItem(fuelSlot, item.getContainerItem(testStack), false);
									}
									
									setBurnTime(stack, burnTime);
								}
							}
						}
					}
					else if (TileEntityFurnace.isItemFuel(fuelItem) || SlotFurnaceFuel.isBucket(fuelItem) && fuelItem.getItem() != Items.BUCKET)
					{
						burnTime = TileEntityFurnace.getItemBurnTime(fuelItem);
						
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
        	if (!inputStack.isEmpty())
            {
        		// Cook

                if (cookTime < this.totalCookTime)
                {
                    cookTime++;
                	setCookTime(stack, cookTime);
                }
                else
                {
                	// Smelt item
                    
                	int outputX = x, outputY = y + 1;

        			if (outputX == 0 && y != 0) outputX = height;
        			else if (y == 0 && outputX == 1) outputX = -1;
        			else if (y == 0 && outputX == -1) outputX = height - 1;
        			else if (outputX == height) outputX = 0;
        			
        			if (outputY >= 0 && outputY < height)
        			{
    					int outputSlot = outputX + width * outputY;
    	            	ItemStack outputStack = inv.getStackInSlot(outputSlot);
    	            	
	            		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(inputStack);
    	            	
    	            	if (outputStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH))
    	        		{
    	            		IItemHandler outputHandler = outputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
    	            		if (canPush(result, outputHandler))
    	            		{
    	            			push(result.copy(), outputHandler);
    	            			inputStack = ItemStack.EMPTY;
    	            		}
    	        		}
    	        		else
    	        		{
    	        			if (outputStack.isEmpty())
    	        			{
    	        				inv.setInventorySlotContents(outputSlot, result.copy());
    	            			inputStack = ItemStack.EMPTY;
    	        			}
    	        			else if (InventoryUtils.canCombine(result, outputStack) && outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize() && outputStack.getCount() + result.getCount() <= inv.getInventoryStackLimit())
    	        			{
    	        				outputStack.grow(result.getCount());
    	            			inputStack = ItemStack.EMPTY;
    	        			}
    	        		}
    	            	
    		            if (inputStack.isEmpty())
    		            {
    	                	setCookTime(stack, 0);
    		            	setActualInput(stack, ItemStack.EMPTY);
    		            }
    	            }
                	
                }
            }
        }
        else
        {
        	setCookTime(stack, 0);
        	if (cookTime > 0)
	        {
	        	if (cookTime > this.totalCookTime) cookTime = this.totalCookTime;
	        	cookTime = Math.min(0, cookTime - 2);
	        	setCookTime(stack, cookTime);
	        }
        }
        
	}

	private int getInventoryStackLimit()
	{
		return 64;
	}
	
	private static final EnumFacing[] FUEL_SIDE = { EnumFacing.EAST, EnumFacing.WEST };

	private static final String NBT_BURN_TIME = "burn_time";
	
	public static int getBurnTime(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_BURN_TIME, NBT.TAG_INT)) return tag.getInteger(NBT_BURN_TIME);
		}
		return 0;
	}
	
	public static void setBurnTime(ItemStack stack, int burnTime)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setInteger(NBT_BURN_TIME, burnTime);
	}
	
	
	private static final String NBT_COOK_TIME = "cook_time";
	
	public static int getCookTime(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_COOK_TIME, NBT.TAG_INT)) return tag.getInteger(NBT_COOK_TIME);
		}
		return 0;
	}
	
	public static void setCookTime(ItemStack stack, int cookTime)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setInteger(NBT_COOK_TIME, cookTime);
	}
	
	
	private static final String NBT_ACTUAL_INPUT = "actual_input";
	
	public static ItemStack getActualInput(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_ACTUAL_INPUT, NBT.TAG_COMPOUND)) return new ItemStack(tag.getCompoundTag(NBT_ACTUAL_INPUT));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setActualInput(ItemStack stack, ItemStack actualInput)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setTag(NBT_ACTUAL_INPUT, actualInput.serializeNBT());
	}
	
}
