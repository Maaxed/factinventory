package fr.max2.factinventory.item;

import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.capability.StackItemHandlerProvider;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;

public class InventoryDropperItem extends Item
{
	
	public InventoryDropperItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		if (Screen.hasControlDown())
		{
			ItemStack droppingItem = getContentStack(stack);
			if (droppingItem.isEmpty())
			{
				tooltip.add(new TranslationTextComponent("tooltip.not_dropping.desc"));
			}
			else
			{
				tooltip.add(new TranslationTextComponent("tooltip.dropping_item.desc", droppingItem.getDisplayName(), droppingItem.getCount()));
			}
			
			tooltip.add(new TranslationTextComponent("tooltip.drop_time.desc", getDropTime(stack)));
		}
		else
		{
			tooltip.add(new TranslationTextComponent("tooltip.drop_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getCount() != oldStack.getCount() || !ItemStack.matches(getContentStack(newStack), getContentStack(oldStack)));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (!world.isClientSide)
		{
			stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(inventory ->
			{
				int dropTime = getDropTime(stack);
				
				dropTime++;
				
				if (dropTime >= 8)
				{
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
			});
		}
		
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
	{
		return new StackItemHandlerProvider();
	}
	
	public static ItemStack getContentStack(ItemStack stack)
	{
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
			.map(handler -> handler.getStackInSlot(0))
			.orElse(ItemStack.EMPTY);
	}
	
	
	private static final String NBT_DROP_TIME = "DropTime";
	
	public static int getDropTime(ItemStack stack)
	{
		if (stack.hasTag())
		{
			CompoundNBT tag = stack.getTag();
			if (tag.contains(NBT_DROP_TIME, NBT.TAG_INT)) return tag.getInt(NBT_DROP_TIME);
		}
		return 0;
	}
	
	public static void setDropTime(ItemStack stack, int transferTime)
	{
		stack.addTagElement(NBT_DROP_TIME, IntNBT.valueOf(transferTime));
	}

    /**
     * Play the dispense sound from the specified entity.
     */
    protected static void playDispenseSound(World worldIn, Entity entity)
    {
    	worldIn.levelEvent(1000, entity.blockPosition(), 0);
    }
    
    protected static boolean doDispense(World worldIn, ItemStack stack, Entity entity)
    {
    	if (entity instanceof PlayerEntity)
    	{
    		((PlayerEntity)entity).drop(stack, true, false);
    		return true;
    	}
    	else
    	{
    		Vector3d pos = entity.getEyePosition(1.0F);

    		ItemEntity entityitem = new ItemEntity(worldIn, pos.x, pos.y, pos.z, stack);
            entityitem.setDefaultPickUpDelay();
            
            float speed = worldIn.random.nextFloat() * 0.5F;
            float angle = worldIn.random.nextFloat() * ((float)Math.PI * 2F);
            entityitem.setDeltaMovement(-MathHelper.sin(angle) * speed, 0.20000000298023224D, MathHelper.cos(angle) * speed);
            
            return worldIn.addFreshEntity(entityitem);
    	}
    }
	
	
}
