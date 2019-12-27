package fr.max2.factinventory.client.gui;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryItem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = FactinventoryMod.MOD_ID, value = Dist.CLIENT)
public class GuiRenderHandler
{
	
	@SubscribeEvent
	public static void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event)
	{
		if (event.getGui() instanceof ContainerScreen<?>)
		{
			ContainerScreen<?> gui = (ContainerScreen<?>) event.getGui();
			
			Slot slot = gui.getSlotUnderMouse();
			
			if (slot != null && slot.inventory instanceof PlayerInventory && slot.getStack().getItem() instanceof InventoryItem)
			{
		        RenderHelper.disableStandardItemLighting();
		        GlStateManager.disableDepthTest();
		        GlStateManager.disableLighting();
				
				ItemStack stack = slot.getStack();
				PlayerInventory inv = (PlayerInventory) slot.inventory;
				
				List<Icon> icons = ((InventoryItem)stack.getItem()).getRenderIcons(stack, gui, slot, inv);
				
				for (Icon icon : icons)
				{
					if (icon.slot == null)
					{
						drawIOIcon(gui, slot.xPos + 18 * icon.face.getXOffset(), slot.yPos + 18 * icon.face.getZOffset(), icon.face, icon.color, icon.extract, icon.missing);
					}
					else drawIOIcon(gui, icon.slot.xPos, icon.slot.yPos, icon.face, icon.color, icon.extract, icon.missing);
				}
				
				GlStateManager.color3f(1.0f, 1.0f, 1.0f);
		        GlStateManager.enableLighting();
		        GlStateManager.enableDepthTest();
		        RenderHelper.enableStandardItemLighting();
			}
		}
	}
	
	public static final ResourceLocation ICONS = new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png");
	
	public static void drawIOIcon(ContainerScreen<?> gui, int x, int y, Direction face, int color, boolean extract, boolean missing)
	{
		gui.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png"));
		
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		GlStateManager.color3f(r, g, b);
		
		CustomGuiUtils.drawRectWithSizedTexture(x + gui.getGuiLeft(), y + gui.getGuiTop(), 400.0D, 16 * ((1 - face.getHorizontalIndex()) % 3),
			(extract ? 0 : 16) + (missing ? 32 : 0), 16, 16, 64, 64);
	}
	
	public static class Icon
	{
		private final Slot slot;
		private final Direction face;
		private final int color;
		private final boolean extract, missing;
		
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
