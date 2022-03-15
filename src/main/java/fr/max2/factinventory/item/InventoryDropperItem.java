package fr.max2.factinventory.item;

import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.capability.StackItemHandlerProvider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;

import net.minecraft.world.item.Item.Properties;

public class InventoryDropperItem extends Item
{
	
	public InventoryDropperItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		if (Screen.hasControlDown())
		{
			ItemStack droppingItem = getContentStack(stack);
			if (droppingItem.isEmpty())
			{
				tooltip.add(new TranslatableComponent("tooltip.not_dropping.desc"));
			}
			else
			{
				tooltip.add(new TranslatableComponent("tooltip.dropping_item.desc", droppingItem.getDisplayName(), droppingItem.getCount()));
			}
			
			tooltip.add(new TranslatableComponent("tooltip.drop_time.desc", getDropTime(stack)));
		}
		else
		{
			tooltip.add(new TranslatableComponent("tooltip.drop_info_on_ctrl.desc"));
		}
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && (slotChanged || newStack.getItem() != oldStack.getItem() || newStack.getCount() != oldStack.getCount() || !ItemStack.matches(getContentStack(newStack), getContentStack(oldStack)));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected)
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
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
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
			CompoundTag tag = stack.getTag();
			if (tag.contains(NBT_DROP_TIME, NBT.TAG_INT)) return tag.getInt(NBT_DROP_TIME);
		}
		return 0;
	}
	
	public static void setDropTime(ItemStack stack, int transferTime)
	{
		stack.addTagElement(NBT_DROP_TIME, IntTag.valueOf(transferTime));
	}

    /**
     * Play the dispense sound from the specified entity.
     */
    protected static void playDispenseSound(Level worldIn, Entity entity)
    {
    	worldIn.levelEvent(1000, entity.blockPosition(), 0);
    }
    
    protected static boolean doDispense(Level worldIn, ItemStack stack, Entity entity)
    {
    	if (entity instanceof Player)
    	{
    		((Player)entity).drop(stack, true, false);
    		return true;
    	}
    	else
    	{
    		Vec3 pos = entity.getEyePosition(1.0F);

    		ItemEntity entityitem = new ItemEntity(worldIn, pos.x, pos.y, pos.z, stack);
            entityitem.setDefaultPickUpDelay();
            
            float speed = worldIn.random.nextFloat() * 0.5F;
            float angle = worldIn.random.nextFloat() * ((float)Math.PI * 2F);
            entityitem.setDeltaMovement(-Mth.sin(angle) * speed, 0.20000000298023224D, Mth.cos(angle) * speed);
            
            return worldIn.addFreshEntity(entityitem);
    	}
    }
	
	
}
