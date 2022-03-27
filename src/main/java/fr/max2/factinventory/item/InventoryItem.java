package fr.max2.factinventory.item;

import java.util.List;

import fr.max2.factinventory.utils.InventoryUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class InventoryItem extends Item
{
	
	public InventoryItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (entity instanceof Player && !world.isClientSide)
		{
			Inventory inv = ((Player)entity).getInventory();
			
			if (inv.getItem(itemSlot) == stack) this.update(stack, inv, (Player)entity, itemSlot);
			
		}
		
	}

	protected abstract void update(ItemStack stack, Inventory inv, Player player, int itemSlot);
	
	public abstract List<Icon> getRenderIcons(ItemStack stack, AbstractContainerMenu container, Slot slot, Inventory inv);
	
	public static boolean canPush(ItemStack stack, ItemStack output, Direction face)
	{
		return output.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face)
			.map(itemHandler -> canPush(stack, itemHandler))
			.orElseGet(() -> output.isEmpty() || InventoryUtils.canCombine(stack, output));
	}
	
	public static boolean canPush(ItemStack stack, IItemHandler output)
	{
		int insertSlots = output.getSlots();
		for (int insertIndex = 0; insertIndex < insertSlots; insertIndex++)
		{
			stack = output.insertItem(insertIndex, stack, true);
			if (stack.isEmpty())
			{
				return true;
			}
		}
		return false;
	}
	
	public static void push(ItemStack stack, IItemHandler output)
	{
		int insertSlots = output.getSlots();
		for (int insertIndex = 0; insertIndex < insertSlots; insertIndex++)
		{
			stack = output.insertItem(insertIndex, stack, false);
			if (stack.isEmpty())
			{
				return;
			}
		}
	}
	
	public static Slot findSlot(AbstractContainerMenu container, Slot original, int newIndex)
	{
		return container.slots.stream().filter(slot -> slot.isSameInventory(original) && slot.getSlotIndex() == newIndex).findAny().orElse(null);
	}

	public static void playRemoveOneSound(Entity target)
	{
		target.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + target.getLevel().getRandom().nextFloat() * 0.4F);
	}
	
	public static void playInsertSound(Entity target)
	{
		target.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + target.getLevel().getRandom().nextFloat() * 0.4F);
	}
	
	public static class Icon
	{
		public final Slot slot;
		public final Direction face;
		public final int color;
		public final boolean extract, missing;
		
		public Icon(Slot slot, Direction face, int color, boolean extract, boolean missing)
		{
			this.slot = slot;
			this.face = face;
			this.color = color;
			this.extract = extract;
			this.missing = missing;
		}
	}
	
}
