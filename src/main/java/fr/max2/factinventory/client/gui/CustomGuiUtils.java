package fr.max2.factinventory.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomGuiUtils
{
	public static void drawRectWithSizedTexture(MatrixStack ms, int x, int y, float z, int u, int v, int width, int height, int textureWidth, int textureHeight, int r, int g, int b, int a)
    {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		
		float f = 1.0f / textureWidth;
		float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        
        Matrix4f mat = ms.getLast().getMatrix();
        buffer.pos(mat, x        , y + height, z).color(r, g, b, a).tex( u * f         , (v + height) * f1).endVertex();
        buffer.pos(mat, x + width, y + height, z).color(r, g, b, a).tex((u + width) * f, (v + height) * f1).endVertex();
        buffer.pos(mat, x + width, y         , z).color(r, g, b, a).tex((u + width) * f,  v * f1          ).endVertex();
        buffer.pos(mat, x        , y         , z).color(r, g, b, a).tex( u * f         ,  v * f1          ).endVertex();
        RenderSystem.enableAlphaTest();
        
        tessellator.draw();
    }
}
