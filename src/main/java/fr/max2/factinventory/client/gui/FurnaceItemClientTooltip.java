package fr.max2.factinventory.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import fr.max2.factinventory.FactinventoryMod;
import fr.max2.factinventory.item.InventoryFurnaceItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(bus = Bus.MOD, modid = FactinventoryMod.MOD_ID, value = Dist.CLIENT)
public class FurnaceItemClientTooltip extends CustomClientTooltip<InventoryFurnaceItem.Tooltip>
{
	private static final int MARGIN_BOTTOM = 3;
	private static final int GRID_SIZE_X = 1;
	private static final int GRID_SIZE_Y = 2;

	private static final int FLAME_OFFSET_X = 1;
	private static final int FLAME_OFFSET_Y = 2;

	public FurnaceItemClientTooltip(InventoryFurnaceItem.Tooltip data)
	{
		super(data);
	}
	
	@Override
	public int getWidth(Font pFont)
	{
		return GRID_SIZE_X * SLOT_SIZE_X + BORDER_LEFT + BORDER_RIGHT;
	}
	
	@Override
	public int getHeight()
	{
		return GRID_SIZE_Y * SLOT_SIZE_Y + BORDER_TOP + BORDER_BOTTOM + MARGIN_BOTTOM;
	}
	
	@Override
	public void renderImage(Font pFont, int pMouseX, int pMouseY, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset)
	{
		int posX = pMouseX + BORDER_LEFT;
		int posY = pMouseY + BORDER_TOP;
		this.renderSlot(posX, posY, pFont, pPoseStack, pItemRenderer, pBlitOffset);
		
		// Render flames
		blit(pPoseStack, posX, posY + SLOT_SIZE_Y, pBlitOffset, Texture.FURNACE);
		if (this.data.getRemainingBurnTime() > 0)
		{
			int h = Texture.FURNACE_LIT.h;
			if (this.data.getRemainingBurnTime() <= this.data.getTotalBurnTime())
			{
				h = 1 + this.data.getRemainingBurnTime() * Texture.FURNACE_LIT.h / (this.data.getTotalBurnTime() + 1);
			}
			
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
			GuiComponent.blit(pPoseStack, posX + FLAME_OFFSET_X, posY + SLOT_SIZE_Y + FLAME_OFFSET_Y + Texture.FURNACE_LIT.h - h, pBlitOffset, Texture.FURNACE_LIT.x, Texture.FURNACE_LIT.y + Texture.FURNACE_LIT.h - h, Texture.FURNACE_LIT.w, h, TEX_SIZE, TEX_SIZE);
		}
		
		drawBorder(pMouseX, pMouseY, GRID_SIZE_X, GRID_SIZE_Y, pPoseStack, pBlitOffset);
	}
	
	private void renderSlot(int pX, int pY, Font pFont, PoseStack pPoseStack, ItemRenderer pItemRenderer, int pBlitOffset)
	{
		ItemStack itemstack = this.data.getSmeltingStack();
		blit(pPoseStack, pX, pY, pBlitOffset, Texture.SLOT);
		pItemRenderer.renderAndDecorateItem(itemstack, pX + 1, pY + 1, 0);
		pItemRenderer.renderGuiItemDecorations(pFont, itemstack, pX + 1, pY + 1);
	}
	
	@SubscribeEvent
	public static void registerTooltipType(FMLClientSetupEvent event)
	{
		MinecraftForgeClient.registerTooltipComponentFactory(InventoryFurnaceItem.Tooltip.class, FurnaceItemClientTooltip::new);
	}
}
