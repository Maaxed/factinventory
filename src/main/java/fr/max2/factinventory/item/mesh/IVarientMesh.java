package fr.max2.factinventory.item.mesh;

import net.minecraft.item.ItemStack;

public interface IVarientMesh
{
	
	String[] varients();
	
	String getVarient(ItemStack stack);
	
}
