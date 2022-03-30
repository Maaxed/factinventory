package fr.max2.factinventory.client.gui;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryItem;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ContainerScreenEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = FactinventoryMod.MOD_ID, value = Dist.CLIENT)
public class GuiRenderHandler
{
	
	@SubscribeEvent
	public static void onRenderScreenBackground(ContainerScreenEvent.DrawBackground event)
	{
		AbstractContainerScreen<?> screen = event.getContainerScreen();
		Slot slot = screen.getSlotUnderMouse();
		
		if (slot == null || !(slot.container instanceof Inventory) || !(slot.getItem().getItem() instanceof InventoryItem))
			return;
		
		Lighting.setupForFlatItems();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(false);
		
		ItemStack stack = slot.getItem();
		Inventory inv = (Inventory)slot.container;
		PoseStack ms = event.getPoseStack();
		
		List<InventoryItem.Icon> icons = ((InventoryItem)stack.getItem()).getRenderIcons(stack, screen.getMenu(), slot, inv);
		
		for (InventoryItem.Icon icon : icons)
		{
			int slotX;
			int slotY;
			if (icon.slot == null)
			{
				slotX = slot.x + 18 * icon.face.getStepX();
				slotY = slot.y + 18 * icon.face.getStepZ();
			}
			else
			{
				slotX = icon.slot.x;
				slotY = icon.slot.y;
			}
			drawIOSlotBackground(screen, ms, slotX, slotY, screen.getBlitOffset() + 1, icon.face, icon.extract, icon.missing, icon.altColor);
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableDepthTest();
		RenderSystem.disableBlend();
		Lighting.setupFor3DItems();
	}
	
	
	@SubscribeEvent
	public static void onRenderScreenForground(ScreenEvent.DrawScreenEvent.Post event)
	{
		if (!Screen.hasShiftDown())
			return;
		
		if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen))
			return;
		
		Slot slot = screen.getSlotUnderMouse();

		if (slot == null || !(slot.container instanceof Inventory) || !(slot.getItem().getItem() instanceof InventoryItem))
			return;
		
		Lighting.setupForFlatItems();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(false);
		
		ItemStack stack = slot.getItem();
		Inventory inv = (Inventory)slot.container;
		PoseStack ms = event.getPoseStack();
		
		List<InventoryItem.Icon> icons = ((InventoryItem)stack.getItem()).getRenderIcons(stack, screen.getMenu(), slot, inv);
		
		for (InventoryItem.Icon icon : icons)
		{
			int slotX;
			int slotY;
			if (icon.slot == null)
			{
				slotX = slot.x + 9 * icon.face.getStepX();
				slotY = slot.y + 9 * icon.face.getStepZ();
			}
			else
			{
				slotX = (icon.slot.x + slot.x) / 2;
				slotY = (icon.slot.y + slot.y) / 2;
			}
			drawIOSlotOverlay(screen, ms, slotX, slotY, screen.getBlitOffset() + 500, icon.face, icon.extract, icon.missing, icon.altColor);
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		Lighting.setupFor3DItems();
	}
	
	public static final ResourceLocation ICONS = new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/io_icons.png");
	
	public static void drawIOSlotBackground(AbstractContainerScreen<?> gui, PoseStack ms, int x, int y, int z, Direction face, boolean extract, boolean missing, boolean alt)
	{
		RenderSystem.setShaderTexture(0, ICONS);
		
		GuiComponent.blit(ms,
			x - 1 + gui.getGuiLeft(), y - 1 + gui.getGuiTop(), z, // X Y Z
			18 * ((face.get2DDataValue() + (extract ? 2 : 0)) % 4) + (extract ? 0 : 72), (missing ? 18 : 0) + (alt ? 36 : 0), // U V
			18, 18, 144, 144); // W H TW TH
	}
	
	public static void drawIOSlotOverlay(AbstractContainerScreen<?> gui, PoseStack ms, int x, int y, int z, Direction face, boolean extract, boolean missing, boolean alt)
	{
		RenderSystem.setShaderTexture(0, ICONS);
		
		GuiComponent.blit(ms,
			x - 1 + gui.getGuiLeft(), y - 1 + gui.getGuiTop(), z, // X Y Z
			18 * ((face.get2DDataValue() + (extract ? 2 : 0)) % 4) + (extract ? 0 : 72), 72 + (missing ? 18 : 0) + (alt ? 36 : 0), // U V
			18, 18, 144, 144); // W H TW TH
	}
	
}
