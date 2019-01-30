package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.capability.StackItemHandlerProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
			ItemStack droppingItem = getContentStack(stack);
			if (droppingItem.isEmpty())
			{
				tooltip.add(I18n.format("tooltip.not_dropping.desc"));
			}
			else
			{
				tooltip.add(I18n.format("tooltip.dropping_item.desc", droppingItem.getDisplayName(), droppingItem.getCount()));
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
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getMetadata() != oldStack.getMetadata() || newStack.getCount() != oldStack.getCount() || !ItemStack.areItemStacksEqual(getContentStack(newStack), getContentStack(oldStack)));
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (!world.isRemote && stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
		{
			int dropTime = getDropTime(stack);
			
			dropTime++;
			
			if (dropTime >= 8)
			{
				IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
				ItemStack dropping = inventory.extractItem(0, 1, true);
				
				if (!dropping.isEmpty())
				{
					if (doDispense(world, dropping, entity))
					{
						inventory.extractItem(0, 1, false);
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
		return new StackItemHandlerProvider();
	}
	
	public static ItemStack getContentStack(ItemStack stack)
	{
		if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
		{
			IItemHandler handler = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			return handler.getStackInSlot(0);
		}
		return ItemStack.EMPTY;
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
     * Play the dispense sound from the specified entity.
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
            
            float speed = worldIn.rand.nextFloat() * 0.5F;
            float angle = worldIn.rand.nextFloat() * ((float)Math.PI * 2F);
            entityitem.motionX = -MathHelper.sin(angle) * speed;
            entityitem.motionZ = MathHelper.cos(angle) * speed;
            entityitem.motionY = 0.20000000298023224D;
            
            return worldIn.spawnEntity(entityitem);
    	}
    }
	
	
}
