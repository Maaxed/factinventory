package fr.max2.factinventory.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.factinventory.FactinventoryMod;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public abstract class CustomClientTooltip<D extends TooltipComponent> implements ClientTooltipComponent
{
	protected static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(FactinventoryMod.MOD_ID, "textures/gui/item_tooltip.png");
	
	protected static final int TEX_SIZE = 128;
	protected static final int SLOT_SIZE_X = 18;
	protected static final int SLOT_SIZE_Y = 18;
	
	protected static final int
		BORDER_TOP = 1,
		BORDER_RIGHT = 1,
		BORDER_BOTTOM = 2,
		BORDER_LEFT = 1;
	
	protected final D data;

	public CustomClientTooltip(D data)
	{
		this.data = data;
	}
	
	protected static void drawBorder(int pX, int pY, int pSlotWidth, int pSlotHeight, PoseStack pPoseStack, int pBlitOffset)
	{
		blit(pPoseStack, pX											, pY, pBlitOffset, Texture.BORDER_CORNER_TOP_LEFT);
		blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_LEFT, pY, pBlitOffset, Texture.BORDER_CORNER_TOP_RIGHT);
		
		for (int i = 0; i < pSlotWidth; ++i)
		{
			blit(pPoseStack, pX + BORDER_LEFT + i * SLOT_SIZE_X, pY											, pBlitOffset, Texture.BORDER_HORIZONTAL_TOP);
			blit(pPoseStack, pX + BORDER_LEFT + i * SLOT_SIZE_X, pY + pSlotHeight * SLOT_SIZE_Y + BORDER_TOP, pBlitOffset, Texture.BORDER_HORIZONTAL_BOTTOM);
		}
		
		for (int j = 0; j < pSlotHeight; ++j)
		{
			blit(pPoseStack, pX											, pY + j * SLOT_SIZE_Y + BORDER_TOP, pBlitOffset, Texture.BORDER_VERTICAL_LEFT);
			blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_LEFT, pY + j * SLOT_SIZE_Y + BORDER_TOP, pBlitOffset, Texture.BORDER_VERTICAL_RIGHT);
		}
		
		blit(pPoseStack, pX											, pY + pSlotHeight * SLOT_SIZE_Y + BORDER_TOP, pBlitOffset, Texture.BORDER_CORNER_BOTTOM_LEFT);
		blit(pPoseStack, pX + pSlotWidth * SLOT_SIZE_X + BORDER_LEFT, pY + pSlotHeight * SLOT_SIZE_Y + BORDER_TOP, pBlitOffset, Texture.BORDER_CORNER_BOTTOM_RIGHT);
	}
	
	protected static void blit(PoseStack pPoseStack, int pX, int pY, int pBlitOffset, Texture pTexture)
	{
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
		GuiComponent.blit(pPoseStack, pX, pY, pBlitOffset, pTexture.x, pTexture.y, pTexture.w, pTexture.h, TEX_SIZE, TEX_SIZE);
	}
	
	protected static enum Texture
	{
		BORDER_HORIZONTAL_TOP		(BORDER_LEFT, 0, SLOT_SIZE_X, BORDER_TOP),
		BORDER_CORNER_TOP_RIGHT		(BORDER_LEFT + SLOT_SIZE_X, 0, BORDER_RIGHT, BORDER_TOP),
		BORDER_VERTICAL_RIGHT		(BORDER_LEFT + SLOT_SIZE_X, BORDER_TOP, BORDER_RIGHT, SLOT_SIZE_Y),
		BORDER_CORNER_BOTTOM_RIGHT	(BORDER_LEFT + SLOT_SIZE_X, BORDER_TOP + SLOT_SIZE_Y, BORDER_RIGHT, BORDER_BOTTOM),
		BORDER_HORIZONTAL_BOTTOM	(BORDER_LEFT, BORDER_TOP + SLOT_SIZE_Y, SLOT_SIZE_X, BORDER_BOTTOM),
		BORDER_CORNER_BOTTOM_LEFT	(0, BORDER_TOP + SLOT_SIZE_Y, BORDER_LEFT, BORDER_BOTTOM),
		BORDER_VERTICAL_LEFT		(0, BORDER_TOP, BORDER_LEFT, SLOT_SIZE_Y),
		BORDER_CORNER_TOP_LEFT		(0, 0, BORDER_LEFT, BORDER_TOP),
		
		SLOT(0, 64, SLOT_SIZE_X, SLOT_SIZE_Y),
		BACKGROUND(18, 64, SLOT_SIZE_X, SLOT_SIZE_Y),
		FURNACE(0, 82, SLOT_SIZE_X, SLOT_SIZE_Y),
		FURNACE_LIT(19, 84, 14, 13);
		
		public final int x;
		public final int y;
		public final int w;
		public final int h;
		
		private Texture(int x, int y, int w, int h)
		{
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}
}
