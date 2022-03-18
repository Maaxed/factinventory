package fr.max2.factinventory.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomGuiUtils
{
	public static void drawRectWithSizedTexture(PoseStack ms, int x, int y, float z, int u, int v, int width, int height, int textureWidth, int textureHeight, int r, int g, int b, int a)
	{
		float f = 1.0f / textureWidth;
		float f1 = 1.0f / textureHeight;
		
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder buffer = tessellator.getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		
		Matrix4f mat = ms.last().pose();
		buffer.vertex(mat, x        , y + height, z).uv( u * f         , (v + height) * f1).color(r, g, b, a).endVertex();
		buffer.vertex(mat, x + width, y + height, z).uv((u + width) * f, (v + height) * f1).color(r, g, b, a).endVertex();
		buffer.vertex(mat, x + width, y         , z).uv((u + width) * f,  v * f1          ).color(r, g, b, a).endVertex();
		buffer.vertex(mat, x        , y         , z).uv( u * f         ,  v * f1          ).color(r, g, b, a).endVertex();
		
		tessellator.end();
	}
}
