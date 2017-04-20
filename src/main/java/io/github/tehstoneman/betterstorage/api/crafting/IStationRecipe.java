package io.github.tehstoneman.betterstorage.api.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;

public interface IStationRecipe
{
	/**
	 * If the input matches this recipe, returns a new station crafting
	 * instance, specifically for this input, or null if it didn't match.
	 */
	StationCrafting checkMatch( ItemStack[] craftingIn, RecipeBounds bounds );

	// NEI related functions

	/**
	 * Returns a list of sample inputs that will match the recipe.
	 * Used to display and cycle recipes for the NEI addon.
	 * Return null if the recipe should not be displayed in NEI.
	 */
	@SideOnly( Side.CLIENT )
	List< IRecipeInput[] > getSampleInputs();

	/**
	 * Returns a list of input items that may be used in this recipe.
	 * Used to show recipes using these inputs for the NEI addon.
	 * Return null if the inputs should be grabbed from getSampleInputs.
	 */
	@SideOnly( Side.CLIENT )
	List< IRecipeInput > getPossibleInputs();

	/**
	 * Returns a list of output items that can result from this recipe.
	 * Used to show recipes matching these outputs for the NEI addon.
	 * Return null if the output made from getSampleInputs should be used.
	 */
	@SideOnly( Side.CLIENT )
	List< ItemStack > getPossibleOutputs();
}
