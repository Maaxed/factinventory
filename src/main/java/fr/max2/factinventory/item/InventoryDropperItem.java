package fr.max2.factinventory.item;

import java.util.List;

import com.google.common.collect.ImmutableList;

import fr.max2.factinventory.capability.SingleStackItemHandler;
import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryDropperItem extends Item
{
	
	public InventoryDropperItem()
	{
		super();
		this.maxStackSize = 1;
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (GuiScreen.isCtrlKeyDown())
		{
			ItemStack droppingItem = getDroppingStack(stack);
			if (droppingItem.isEmpty())
			{
				tooltip.add(I18n.format("tooltip.not_dropping.desc"));
			}
			else
			{
				tooltip.add(I18n.format("tooltip.dropping_item.desc", droppingItem.getDisplayName()));
			}
			
			tooltip.add(I18n.format("tooltip.drop_time.desc", getDropTime(stack)));
		}
		else
		{
			tooltip.add(I18n.format("tooltip.drop_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getMetadata() != oldStack.getMetadata() || newStack.getCount() != oldStack.getCount() || !ItemStack.areItemStacksEqual(getDroppingStack(newStack), getDroppingStack(oldStack)));
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (!world.isRemote)
		{
			int dropTime = getDropTime(stack);
			
			dropTime++;
			
			if (dropTime >= 8)
			{
				ItemStack inventory = getDroppingStack(stack);
				
				if (!inventory.isEmpty())
				{
					ItemStack dropping = inventory.copy();
					dropping.setCount(1);
					if (this.doDispense(world, dropping, entity))
					{
						inventory.shrink(1);
						setDroppingStack(stack, inventory);
					}
			        playDispenseSound(world, entity);
				}
				
				dropTime = 0;
			}
			setDropTime(stack, dropTime);
		}
		
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return new SingleStackItemHandler(stack, NBT_DROPPING_ITEM);
	}
	
	private static final String NBT_DROPPING_ITEM = "DroppingItem";
	
	public static ItemStack getDroppingStack(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_DROPPING_ITEM, NBT.TAG_COMPOUND)) return new ItemStack(tag.getCompoundTag(NBT_DROPPING_ITEM));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setDroppingStack(ItemStack stack, ItemStack transferringStck)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		if (transferringStck.isEmpty()) transferringStck = ItemStack.EMPTY;
		
		stack.getTagCompound().setTag(NBT_DROPPING_ITEM, transferringStck.serializeNBT());
	}
	
	
	private static final String NBT_DROP_TIME = "DropTime";
	
	public static int getDropTime(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_DROP_TIME, NBT.TAG_INT)) return tag.getInteger(NBT_DROP_TIME);
		}
		return 0;
	}
	
	public static void setDropTime(ItemStack stack, int transferTime)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setInteger(NBT_DROP_TIME, transferTime);
	}

    /**
     * Play the dispense sound from the specified block.
     */
    protected static void playDispenseSound(World worldIn, Entity entity)
    {
    	worldIn.playEvent(1000, entity.getPosition(), 0);
    }
    protected static boolean doDispense(World worldIn, ItemStack stack, Entity entity)
    {
    	if (entity instanceof EntityPlayer)
    	{
    		((EntityPlayer)entity).dropItem(stack, true, false);
    		return true;
    	}
    	else
    	{
    		Vec3d pos = entity.getPositionEyes(1.0F);

    		EntityItem entityitem = new EntityItem(worldIn, pos.x, pos.y, pos.z, stack);
            entityitem.setDefaultPickupDelay();
            
            float f = worldIn.rand.nextFloat() * 0.5F;
            float f1 = worldIn.rand.nextFloat() * ((float)Math.PI * 2F);
            entityitem.motionX = (double)(-MathHelper.sin(f1) * f);
            entityitem.motionZ = (double)(MathHelper.cos(f1) * f);
            entityitem.motionY = 0.20000000298023224D;
            
            return worldIn.spawnEntity(entityitem);
    	}
    }
	
	
}
