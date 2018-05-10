package fr.max2.factinventory.item.mesh;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IVarientMesh
{
	
	String[] varients();
	
	String getVarient(ItemStack stack);
	
}
