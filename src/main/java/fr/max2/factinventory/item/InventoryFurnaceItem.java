package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
		this.addPropertyOverride(new ResourceLocation(FactinventoryMod.MOD_ID, "burn_time"), BURN_TIME_GETTER);
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
	protected void update(ItemStack stack, InventoryPlayer inv, EntityPlayer player, int itemSlot)
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
	            	if (newInputStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH))
	            	{
	            		IItemHandler inputCapa = newInputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
	            		int slots = inputCapa.getSlots();
	            		for (int i = 0; i < slots && inputStack.isEmpty(); i++)
	            		{
	            			ItemStack currentSlot = inputCapa.extractItem(i, 1, true);
	            			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(currentSlot);
	            			if(!result.isEmpty() && canPush(result, outputStack, EnumFacing.NORTH))
	            			{
	            				inputStack = inputCapa.extractItem(i, 1, false);
	            			}
	            		}
	            	}
	            	else
	            	{
	            		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(newInputStack);
	        			if(!result.isEmpty() && canPush(result, outputStack, EnumFacing.NORTH))
		            	{
		            		inputStack = newInputStack.copy();
		            		inputStack.setCount(1);
		            		
		            		newInputStack.shrink(1);
		            		if (newInputStack.isEmpty()) inv.setInventorySlotContents(inputSlot, ItemStack.EMPTY);
		            	}
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
						for (int i = 0; i < slots && burnTime == 0; i++)
						{
							ItemStack testStack = fuelCapa.extractItem(i, 1, true);
							
							if (TileEntityFurnace.isItemFuel(testStack) || SlotFurnaceFuel.isBucket(testStack) && testStack.getItem() != Items.BUCKET)
							{
								burnTime = TileEntityFurnace.getItemBurnTime(testStack);
								
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

        			if (outputY == 0 && y != 0) outputY = height;
        			else if (y == 0 && outputY == 1) outputY = -1;
        			else if (y == 0 && outputY == -1) outputY = height - 1;
        			else if (outputY == height) outputY = 0;
        			
        			if (outputY >= 0 && outputY < height)
        			{
    					int outputSlot = outputX + width * outputY;
    	            	ItemStack outputStack = inv.getStackInSlot(outputSlot);
    	            	
	            		ItemStack result = FurnaceRecipes.instance().getSmeltingResult(inputStack).copy();
    	            	
    	            	if (outputStack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH))
    	        		{
    	            		IItemHandler outputHandler = outputStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.SOUTH);
    	            		if (canPush(result, outputHandler))
    	            		{
    	            			result.onCrafting(player.world, player, result.getCount());
        		                FMLCommonHandler.instance().firePlayerSmeltedEvent(player, result);
    	            			push(result, outputHandler);
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
    	        				outputStack.onCrafting(player.world, player, result.getCount());
    	        				FMLCommonHandler.instance().firePlayerSmeltedEvent(player, outputStack);
    	            			inputStack = ItemStack.EMPTY;
    	        			}
    	        		}
    	            	
    		            if (inputStack.isEmpty())
    		            {
    		            	if (!player.world.isRemote)
    		                {
    		                    int i = result.getCount();
    		                    float f = FurnaceRecipes.instance().getSmeltingExperience(stack);

    		                    if (f == 0.0F)
    		                    {
    		                        i = 0;
    		                    }
    		                    else if (f < 1.0F)
    		                    {
    		                        int j = MathHelper.floor((float)i * f);

    		                        if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j))
    		                        {
    		                            ++j;
    		                        }

    		                        i = j;
    		                    }
    		                    
    		                    while (i > 0)
    		                    {
    		                        int k = EntityXPOrb.getXPSplit(i);
    		                        i -= k;
    		                        player.world.spawnEntity(new EntityXPOrb(player.world, player.posX, player.posY + 0.5D, player.posZ + 0.5D, k));
    		                    }
    		                }
    		            	
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

	@Override
	public List<Icon> getRenderIcons(ItemStack stack, GuiContainer gui, Slot slot, InventoryPlayer inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		int itemSlot = slot.getSlotIndex(),
			width = inv.getHotbarSize(),
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
			Slot extractSlot = gui.inventorySlots.getSlotFromInventory(inv, x + width * pullY);
			icons.add(new Icon(extractSlot, EnumFacing.NORTH, 0x4995FF, true, false));
		}
		else icons.add(new Icon(null, EnumFacing.NORTH, 0x4995FF, true, true));
		
		if (pushY >= 0 && pushY < height)
		{
			Slot fillSlot = gui.inventorySlots.getSlotFromInventory(inv, x + width * pushY);
			icons.add(new Icon(fillSlot, EnumFacing.SOUTH, 0xFF7716, false, false));
		}
		else icons.add(new Icon(null, EnumFacing.SOUTH, 0xFF7716, false, true));
		
		int fuelX1 = x - 1,
			fuelX2 = x + 1;
		
		if (fuelX1 >= 0 && fuelX1 < width)
		{
			Slot fuelSlot = gui.inventorySlots.getSlotFromInventory(inv, fuelX1 + width * y);
			icons.add(new Icon(fuelSlot, EnumFacing.WEST, 0xAA00FF, true, false));
		}
		else icons.add(new Icon(null, EnumFacing.WEST, 0xAA00FF, true, true));
		
		if (fuelX2 >= 0 && fuelX2 < width)
		{
			Slot fuelSlot = gui.inventorySlots.getSlotFromInventory(inv, fuelX2 + width * y);
			icons.add(new Icon(fuelSlot, EnumFacing.EAST, 0xAA00FF, true, false));
		}
		else icons.add(new Icon(null, EnumFacing.EAST, 0xAA00FF, true, true));
		
		
		return icons;
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
