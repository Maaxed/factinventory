package fr.max2.factinventory.client.gui;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryItem;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
		if (event.getGui() instanceof AbstractContainerScreen<?>)
		{
			AbstractContainerScreen<?> gui = (AbstractContainerScreen<?>) event.getGui();
			
			Slot slot = gui.getSlotUnderMouse();
			
			if (slot != null && slot.container instanceof Inventory && slot.getItem().getItem() instanceof InventoryItem)
			{
				Lighting.setupForFlatItems();
				RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				RenderSystem.enableDepthTest();
				RenderSystem.depthMask(false);
				
				ItemStack stack = slot.getItem();
				Inventory inv = (Inventory) slot.container;
				PoseStack ms = event.getMatrixStack();
				
				List<Icon> icons = ((InventoryItem)stack.getItem()).getRenderIcons(stack, gui, slot, inv);
				
				for (Icon icon : icons)
				{
					if (icon.slot == null)
					{
						drawIOIcon(gui, ms, slot.x + 18 * icon.face.getStepX(), slot.y + 18 * icon.face.getStepZ(), icon.face, icon.color, icon.extract, icon.missing);
					}
					else drawIOIcon(gui, ms, icon.slot.x, icon.slot.y, icon.face, icon.color, icon.extract, icon.missing);
				}
				
				Lighting.setupFor3DItems();
			}
		}
	}
	
	public static final ResourceLocation ICONS = new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png");
	
	public static void drawIOIcon(AbstractContainerScreen<?> gui, PoseStack ms, int x, int y, Direction face, int color, boolean extract, boolean missing)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, ICONS);
		
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = (color & 255);
		
		CustomGuiUtils.drawRectWithSizedTexture(ms, x + gui.getGuiLeft(), y + gui.getGuiTop(), 500.0f, 16 * ((1 - face.get2DDataValue()) % 3),
			(extract ? 0 : 16) + (missing ? 32 : 0), 16, 16, 64, 64, r, g, b, 255);
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
