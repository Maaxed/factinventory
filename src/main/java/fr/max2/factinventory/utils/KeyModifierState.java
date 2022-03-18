package fr.max2.factinventory.utils;

public class KeyModifierState
{
	public static final KeyModifierState DEFAULT = new KeyModifierState(false, false);
	
	public final boolean shift;
	public final boolean control;

	public KeyModifierState(boolean shift, boolean control)
	{
		this.shift = shift;
		this.control = control;
	}
}
