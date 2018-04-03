package fr.max2.factinventory.item;

import java.util.List;

import com.google.common.collect.ImmutableList;

import fr.max2.factinventory.client.gui.GuiRenderHandler.Icon;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryDropper extends Item
{
	
	public InventoryDropper()
	{
		super();
		this.maxStackSize = 1;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (!world.isRemote)
		{
			ItemStack drop = getDroppingStack(stack);
			
			if (!drop.isEmpty())
			{
				this.doDispense(world, drop, entity);
		        this.playDispenseSound(world, entity);
			}
			
		}
		
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	{
		return super.initCapabilities(stack, nbt);
	}
	
	private static final String NBT_DROPPING_ITEM = "DroppingItem";
	
	public static ItemStack getDroppingStack(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_DROPPING_ITEM, NBT.TAG_COMPOUND)) return new ItemStack(tag.getCompoundTag(NBT_DROPPING_ITEM));
		}
		return ItemStack.EMPTY;
	}
	
	public static void setDroppingStack(ItemStack stack, ItemStack transferringStck)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setTag(NBT_DROPPING_ITEM, transferringStck.serializeNBT());
	}
	
	
	private static final String NBT_DROP_TIME = "DropTime";
	
	public static int getDropTime(ItemStack stack)
	{
		if (stack.hasTagCompound())
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(NBT_DROP_TIME, NBT.TAG_INT)) return tag.getInteger(NBT_DROP_TIME);
		}
		return 0;
	}
	
	public static void setDropTime(ItemStack stack, int transferTime)
	{
		if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
		
		stack.getTagCompound().setInteger(NBT_DROP_TIME, transferTime);
	}

    /**
     * Play the dispense sound from the specified block.
     */
    protected static void playDispenseSound(World worldIn, Entity entity)
    {
    	worldIn.playEvent(1000, entity.getPosition(), 0);
    }

    protected static void doDispense(World worldIn, ItemStack stack, Entity entity)
    {
        Vec3d pos = entity.getPositionEyes(1.0F);

        EntityItem entityitem = new EntityItem(worldIn, pos.x, pos.y, pos.z, stack);
        double d3 = worldIn.rand.nextDouble() * 0.1D + 0.2D;
        
        entityitem.motionX = Math.cos(entity.rotationYaw) * d3;
        entityitem.motionY = 0.20000000298023224D;
        entityitem.motionZ = Math.sin(entity.rotationYaw) * d3;
        entityitem.motionX += worldIn.rand.nextGaussian() * 0.007499999832361937D * 6;
        entityitem.motionY += worldIn.rand.nextGaussian() * 0.007499999832361937D * 6;
        entityitem.motionZ += worldIn.rand.nextGaussian() * 0.007499999832361937D * 6;
        worldIn.spawnEntity(entityitem);
    }
	
	
}
