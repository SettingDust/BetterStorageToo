package io.github.tehstoneman.betterstorage.item;

import net.minecraft.item.ItemStack;

public interface IDyeableItem
{
	/** Returns if the item can be dyed. */
	boolean canDye( ItemStack stack );
}
