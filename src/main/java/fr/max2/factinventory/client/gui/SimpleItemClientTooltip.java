package fr.max2.factinventory.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.SimpleItemTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = Bus.MOD, modid = FactinventoryMod.MOD_ID, value = Dist.CLIENT)
public class SimpleItemClientTooltip extends CustomClientTooltip<SimpleItemTooltip>
{
	private static final int MARGIN_BOTTOM = 3;
	
	public SimpleItemClientTooltip(SimpleItemTooltip tooltipData)
	{
		super(tooltipData);
	}
	
	@Override
	public int getWidth(Font pFont)
	{
		return this.gridSizeX() * SLOT_SIZE_X + BORDER_LEFT + BORDER_RIGHT;
	}
	
	@Override
	public int getHeight()
	{
		return this.gridSizeY() * SLOT_SIZE_Y + BORDER_TOP + BORDER_BOTTOM + MARGIN_BOTTOM;
	}
	
	@Override
	public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset)
	{
		int width = this.gridSizeX();
		int height = this.gridSizeY();
		int slotIndex = 0;
		
		for (int iy = 0; iy < height; ++iy)
		{
			for (int ix = 0; ix < width; ++ix)
			{
				int posX = pMouseX + ix * SLOT_SIZE_X + BORDER_LEFT;
				int posY = pMouseY + iy * SLOT_SIZE_Y + BORDER_TOP;
				this.renderSlot(posX, posY, slotIndex, pFont, pPoseStack, pItemRenderer, pBlitOffset);
				slotIndex++;
			}
		}
		
		drawBorder(pMouseX, pMouseY, width, height, pPoseStack, pBlitOffset);
	}
	
	private void renderSlot(int pX, int pY, int pItemIndex, Font pFont, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset)
	{
		if (pItemIndex >= this.data.getItems().size())
		{
			blit(pPoseStack, pX, pY, pBlitOffset, Texture.BACKGROUND);
			return;
		}
		
		ItemStack itemstack = this.data.getItems().get(pItemIndex);
		blit(pPoseStack, pX, pY, pBlitOffset, Texture.SLOT);
		pItemRenderer.renderAndDecorateItem(itemstack, pX + 1, pY + 1, pItemIndex);
		pItemRenderer.renderGuiItemDecorations(pFont, itemstack, pX + 1, pY + 1);
		if (pItemIndex == this.data.getSelectedIndex())
		{
			AbstractContainerScreen.renderSlotHighlight(pPoseStack, pX + 1, pY + 1, pBlitOffset);
		}
	}
	
	private int gridSizeX()
	{
		return Math.max(1, (int)Math.ceil(Math.sqrt(this.data.getItems().size())));
	}
	
	private int gridSizeY()
	{
		int sx = this.gridSizeX();
		return (this.data.getItems().size() + sx - 1) / sx;
	}
	
	@SubscribeEvent
	public static void registerTooltipType(FMLClientSetupEvent event)
	{
		MinecraftForgeClient.registerTooltipComponentFactory(SimpleItemTooltip.class, SimpleItemClientTooltip::new);
	}
}