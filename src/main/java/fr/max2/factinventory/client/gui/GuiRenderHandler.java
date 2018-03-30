package fr.max2.factinventory.client.gui;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryHopperItem;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = FactinventoryMod.MOD_ID, value = Side.CLIENT)
public class GuiRenderHandler
{
	
	@SubscribeEvent
	public static void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if (event.getGui() instanceof GuiContainer)
		{
			GuiContainer gui = (GuiContainer) event.getGui();
			
			Slot slot = gui.getSlotUnderMouse();
			
			if (slot != null && slot.inventory instanceof InventoryPlayer && slot.getStack().getItem() instanceof InventoryHopperItem)
			{
				ItemStack stack = slot.getStack();
				InventoryPlayer inv = (InventoryPlayer)slot.inventory;
				EnumFacing face = InventoryHopperItem.getFacing(stack);
				
				int itemSlot = slot.getSlotIndex(),
					width = inv.getHotbarSize(),
					height = inv.mainInventory.size() / width;
				
				if (itemSlot >= width * height) return;
					
				int	x = itemSlot % width,
					y = itemSlot / width,
					extractX = x + face.getFrontOffsetX(),
					extractY = y + face.getFrontOffsetZ(),
					insertX = x - face.getFrontOffsetX(),
					insertY = y - face.getFrontOffsetZ();
				
				if (extractY == 0 && y != 0) extractY = height;
				else if (y == 0 && extractY == 1) extractY = -1;
				else if (y == 0 && extractY == -1) extractY = height - 1;
				else if (extractY == height) extractY = 0;

				if (insertY == 0 && y != 0) insertY = height;
				else if (y == 0 && insertY == 1) insertY = -1;
				else if (y == 0 && insertY == -1) insertY = height - 1;
				else if (insertY == height) insertY = 0;
				
				
				//1
				//2
				//3
				//0
				
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				
				if (extractX >= 0 && extractX < width &&
					extractY >= 0 && extractY < height)
				{
					Slot extractSlot = gui.inventorySlots.getSlotFromInventory(inv, extractX + width * extractY);
					if (extractSlot == null)
					{
						drawIOIcon(gui, slot.xPos + 18 * face.getFrontOffsetX(), slot.yPos + 18 * face.getFrontOffsetZ(), face, true, false);
					}
					else drawIOIcon(gui, extractSlot.xPos, extractSlot.yPos, face, true, false);
				}
				else drawIOIcon(gui, slot.xPos + 18 * face.getFrontOffsetX(), slot.yPos + 18 * face.getFrontOffsetZ(), face, true, true);
				
				if (insertX >= 0 && insertX < width &&
					insertY >= 0 && insertY < height)
				{
					Slot fillSlot = gui.inventorySlots.getSlotFromInventory(inv, insertX + width * insertY);
					if (fillSlot == null)
					{
						drawIOIcon(gui, slot.xPos - 18 * face.getFrontOffsetX(), slot.yPos - 18 * face.getFrontOffsetZ(), face, false, false);
					}
					else drawIOIcon(gui, fillSlot.xPos, fillSlot.yPos, face, false, false);
				}
				else drawIOIcon(gui, slot.xPos - 18 * face.getFrontOffsetX(), slot.yPos - 18 * face.getFrontOffsetZ(), face, false, true);
			}
		}
	}
	
	public static final ResourceLocation ICONS = new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png");
	
	public static void drawIOIcon(GuiContainer gui, int x, int y, EnumFacing face, boolean extract, boolean missing)
	{
		gui.mc.getTextureManager().bindTexture(new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png"));
		CustomGuiUtils.drawRectWithSizedTexture(x + gui.getGuiLeft(), y + gui.getGuiTop(), 400.0D, 16 * ((1 - face.getHorizontalIndex()) % 3), (extract ? 0 : 16) + (missing ? 32 : 0), 16, 16, 64, 64);
	}
	
}
