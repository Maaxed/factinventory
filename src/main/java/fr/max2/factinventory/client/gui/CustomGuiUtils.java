package fr.max2.factinventory.client.gui;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomGuiUtils
{
	public static void drawRectWithSizedTexture(int x, int y, double z, int u, int v, int width, int height, int textureWidth, int textureHeight)
    {
		double f = 1.0D / textureWidth;
		double f1 = 1.0D / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        
        buffer.pos(x		, y + height, z).tex( u * f			, (v + height) * f1).endVertex();
        buffer.pos(x + width, y + height, z).tex((u + width) * f, (v + height) * f1).endVertex();
        buffer.pos(x + width, y			, z).tex((u + width) * f,  v * f1		   ).endVertex();
        buffer.pos(x		, y			, z).tex( u * f			,  v * f1		   ).endVertex();
        
        tessellator.draw();
    }
}
