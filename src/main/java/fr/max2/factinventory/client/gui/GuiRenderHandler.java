package fr.max2.factinventory.client.gui;

import java.util.List;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryItem;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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
			
			if (slot != null && slot.inventory instanceof InventoryPlayer && slot.getStack().getItem() instanceof InventoryItem)
			{
		        RenderHelper.disableStandardItemLighting();
		        GlStateManager.disableDepth();
		        GlStateManager.disableLighting();
				
				ItemStack stack = slot.getStack();
				InventoryPlayer inv = (InventoryPlayer) slot.inventory;
				
				List<Icon> icons = ((InventoryItem)stack.getItem()).getRenderIcons(stack, gui, slot, inv);
				
				for (Icon icon : icons)
				{
					if (icon.slot == null)
					{
						drawIOIcon(gui, slot.xPos + 18 * icon.face.getFrontOffsetX(), slot.yPos + 18 * icon.face.getFrontOffsetZ(), icon.face, icon.color, icon.extract, icon.missing);
					}
					else drawIOIcon(gui, icon.slot.xPos, icon.slot.yPos, icon.face, icon.color, icon.extract, icon.missing);
				}
				
				GlStateManager.color(1.0f, 1.0f, 1.0f);
		        GlStateManager.enableLighting();
		        GlStateManager.enableDepth();
		        RenderHelper.enableStandardItemLighting();
			}
		}
	}
	
	public static final ResourceLocation ICONS = new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png");
	
	public static void drawIOIcon(GuiContainer gui, int x, int y, EnumFacing face, int color, boolean extract, boolean missing)
	{
		gui.mc.getTextureManager().bindTexture(new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png"));
		
	//	float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		GlStateManager.color(r, g, b);
		
		CustomGuiUtils.drawRectWithSizedTexture(x + gui.getGuiLeft(), y + gui.getGuiTop(), 400.0D, 16 * ((1 - face.getHorizontalIndex()) % 3),
			(extract ? 0 : 16) + (missing ? 32 : 0), 16, 16, 64, 64);
	}
	
	public static class Icon
	{
		private final Slot slot;
		private final EnumFacing face;
		private final int color;
		private final boolean extract, missing;
		
		public Icon(Slot slot, EnumFacing face, int color, boolean extract, boolean missing)
		{
			this.slot = slot;
			this.face = face;
			this.color = color;
			this.extract = extract;
			this.missing = missing;
		}
		
	}
	
}
