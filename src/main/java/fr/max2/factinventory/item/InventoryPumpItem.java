package fr.max2.factinventory.item;

import java.util.ArrayList;
import java.util.List;

import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import fr.max2.factinventory.item.mesh.IStateMesh;
import fr.max2.factinventory.item.mesh.IStateMesh.Property;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InventoryPumpItem extends RotatableInventoryItem
{
	@SideOnly(Side.CLIENT)
	private static final Property[] PROPERTIES = {
			new Property("facing", "north", "south", "west", "east")
			{
				@Override
				protected String getValue(ItemStack stack)
				{
					return getFacing(stack).getName2();
				}
			}
		};
	@SideOnly(Side.CLIENT)
	public static final IStateMesh MESH = new IStateMesh(PROPERTIES);
	
	public InventoryPumpItem()
	{
		super();
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (GuiScreen.isShiftKeyDown())
		{
			tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.input.desc"));
			tooltip.add(TextFormatting.GOLD + I18n.format("tooltip.output.desc"));
		}
		else
		{
			tooltip.add(I18n.format("tooltip.interaction_info_on_shift.desc"));
		}
	}

	@Override
	protected void update(ItemStack stack, InventoryPlayer inv, EntityPlayer player, int itemSlot)
	{
		EnumFacing face = getFacing(stack);
		
		int width = inv.getHotbarSize(),
			height = inv.mainInventory.size() / width,
			x = itemSlot % width,
			y = itemSlot / width,
			drainX = x + face.getFrontOffsetX(),
			drainY = y + face.getFrontOffsetZ(),
			fillX = x - face.getFrontOffsetX(),
			fillY = y - face.getFrontOffsetZ();
		
		if (drainY == 0 && y != 0) drainY = height;
		else if (y == 0 && drainY == 1) drainY = -1;
		else if (y == 0 && drainY == -1) drainY = height - 1;
		else if (drainY == height) drainY = 0;

		if (fillY == 0 && y != 0) fillY = height;
		else if (y == 0 && fillY ==  1) fillY = -1;
		else if (y == 0 && fillY == -1) fillY = height - 1;
		else if (fillY == height) fillY = 0;
		
		if (drainX >= 0 && drainX < width &&
			drainY >= 0 && drainY < height &&
			fillX >= 0 && fillX < width &&
			fillY >= 0 && fillY < height)
		{
			int drainSlot = drainX + width * drainY,
		        fillSlot = fillX + width * fillY;
			
			ItemStack drainStack = inv.getStackInSlot(drainSlot);
			ItemStack fillStack = inv.getStackInSlot(fillSlot);
			
			if (drainStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face.getOpposite()) &&
				fillStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face))
			{
				IFluidHandlerItem drainCapa = drainStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face.getOpposite());
				IFluidHandlerItem fillCapa = fillStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, face);
				
				FluidStack fluid = drainCapa.drain(Fluid.BUCKET_VOLUME, false);
				int val = fillCapa.fill(fluid, true);
				if (val > 0)
				{
					fluid.amount = val;
					drainCapa.drain(fluid, true);
					
					inv.setInventorySlotContents(drainSlot, drainCapa.getContainer());
					inv.setInventorySlotContents(fillSlot, fillCapa.getContainer());
				}
			}
		}
	}

	@Override
	public List<Icon> getRenderIcons(ItemStack stack, GuiContainer gui, Slot slot, InventoryPlayer inv)
	{
		List<Icon> icons = new ArrayList<>();
		
		EnumFacing face = getFacing(stack);
		
		int itemSlot = slot.getSlotIndex(),
			width = inv.getHotbarSize(),
			height = inv.mainInventory.size() / width;
		
		if (itemSlot >= width * height) return icons;
		
		int x = itemSlot % width,
			y = itemSlot / width,
			extractX = x + face.getFrontOffsetX(),
			extractY = y + face.getFrontOffsetZ(),
			insertX  = x - face.getFrontOffsetX(),
			insertY  = y - face.getFrontOffsetZ();
		
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
			Slot extractSlot = gui.inventorySlots.getSlotFromInventory(inv, extractX + width * extractY);
			icons.add(new Icon(extractSlot, face, 0x0099FF, true, false));
		}
		else icons.add(new Icon(null, face, 0x0099FF, true, true));
		
		if (insertX >= 0 && insertX < width && insertY >= 0 && insertY < height)
		{
			Slot fillSlot = gui.inventorySlots.getSlotFromInventory(inv, insertX + width * insertY);
			icons.add(new Icon(fillSlot, face.getOpposite(), 0xFF7700, false, false));
		}
		else icons.add(new Icon(null, face.getOpposite(), 0xFF7700, false, true));
		
		return icons;
	}
	
}
