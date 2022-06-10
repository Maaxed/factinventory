package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.init.ModTexts;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

public abstract class InventoryHopperItem extends RotatableInventoryItem
{

	public InventoryHopperItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		if (FactinventoryMod.proxy.getKeyModifierState().shift)
		{
			tooltip.add(Component.translatable(ModTexts.Tooltip.INPUT).setStyle(INPUT_STYLE));
			tooltip.add(Component.translatable(ModTexts.Tooltip.OUTPUT).setStyle(OUTPUT_STYLE));
		}
		else
		{
			tooltip.add(Component.translatable(ModTexts.Tooltip.INTERACTION_INFO));
		}
	}

	@Override
	public List<Icon> getRenderIcons(ItemStack stack, AbstractContainerMenu container, Slot slot, Inventory inv)
	{
		List<Icon> icons = new ArrayList<>();

		Direction face = getFacing(stack);

		int itemSlot = slot.getSlotIndex(),
			width = Inventory.getSelectionSize(),
			height = inv.items.size() / width;

		if (itemSlot >= width * height) return icons;

		int x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getStepX(),
			extractY = y + face.getStepZ(),
			insertX  = x - face.getStepX(),
			insertY  = y - face.getStepZ();

		if (extractY == 0 && y != 0)
			extractY = height;
		else if (y == 0 && extractY == 1)
			extractY = -1;
		else if (y == 0 && extractY == -1)
			extractY = height - 1;
		else if (extractY == height)
			extractY = 0;

		if (insertY == 0 && y != 0)
			insertY = height;
		else if (y == 0 && insertY == 1)
			insertY = -1;
		else if (y == 0 && insertY == -1)
			insertY = height - 1;
		else if (insertY == height)
			insertY = 0;

		if (extractX >= 0 && extractX < width && extractY >= 0 && extractY < height)
		{
			Slot extractSlot = findSlot(container, slot, extractX + width * extractY);
			icons.add(new Icon(extractSlot, face, true, false));
		}
		else icons.add(new Icon(null, face, true, false));

		if (insertX >= 0 && insertX < width && insertY >= 0 && insertY < height)
		{
			Slot fillSlot = findSlot(container, slot, insertX + width * insertY);
			icons.add(new Icon(fillSlot, face.getOpposite(), false, false));
		}
		else icons.add(new Icon(null, face.getOpposite(), false, false));

		return icons;
	}

}
