package fr.max2.factinventory.item.mesh;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Cartesian;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IStateMesh implements IVarientMesh
{
	
	private final Property[] properties;
	
	public IStateMesh(Property... properties)
	{
		this.properties = properties;
	}

	@Override
	public String[] varients()
	{
		Iterable<String[]> values = Cartesian.cartesianProduct(String.class, Arrays.asList(this.properties));
		
		return Streams.stream(values).map(v -> Stream.of(v).collect(Collectors.joining(","))).toArray(String[]::new);
	}
	
	@Override
	public String getVarient(ItemStack stack)
	{
		return Arrays.stream(properties).map(p -> p.name + "=" + p.getValue(stack)).collect(Collectors.joining(","));
	}

	@SideOnly(Side.CLIENT)
	public static abstract class Property implements Iterable<String>
	{
		public final String name;
		public final String[] values;
		
		public Property(String name, String... values)
		{
			this.name = name;
			this.values = values;
			for (int i = 0; i < values.length; i++)
			{
				values[i] = name + "=" + values[i];
			}
		}
		
		protected abstract String getValue(ItemStack stack);
		
		@Override
		public Iterator<String> iterator()
		{
			return Iterators.forArray(this.values);
		}
	}
	
}
