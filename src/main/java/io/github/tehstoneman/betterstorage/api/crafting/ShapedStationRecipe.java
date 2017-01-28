package io.github.tehstoneman.betterstorage.api.crafting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ShapedStationRecipe implements IStationRecipe
{
	public final IRecipeInput[]	recipeInput;
	public final ItemStack[]	recipeOutput;
	public final int			recipeWidth;
	public final int			recipeHeight;
	public final boolean		mirrored;

	public int					requiredExperience	= 0;
	public int					craftingTime		= 0;

	public ShapedStationRecipe( IRecipeInput[] input, int width, int height, boolean mirrored, ItemStack[] output )
	{
		recipeInput = input;
		recipeOutput = output;
		recipeWidth = width;
		recipeHeight = height;
		this.mirrored = mirrored;
	}

	public ShapedStationRecipe( IRecipeInput[] input, int width, int height, ItemStack[] output )
	{
		this( input, width, height, false, output );
	}

	public ShapedStationRecipe( ItemStack[] output, Object... input )
	{
		if( input.length <= 0 )
			throw new IllegalArgumentException( "input has no elements." );

		int width = 0;
		int height = 0;
		mirrored = input[0] instanceof Boolean ? (Boolean)input[0] : false;

		int inputIndex = input[0] instanceof Boolean ? 1 : 0;
		final Map< Character, IRecipeInput > inputMap = new HashMap< >();

		while( inputIndex < input.length && input[inputIndex] instanceof String )
		{
			final String line = (String)input[inputIndex++];
			if( line.isEmpty() )
				throw new IllegalArgumentException( "Empty string isn't valid." );
			if( width <= 0 )
				width = line.length();
			else
				if( width != line.length() )
					throw new IllegalArgumentException( "All strings must have the same length." );
			for( final char chr : line.toCharArray() )
				inputMap.put( chr, null );
			height++;
		}
		if( height <= 0 )
			throw new IllegalArgumentException( "At least one string must be supplied." );

		if( inputIndex >= input.length )
			throw new IllegalArgumentException( "At least one mapping must be supplied." );
		if( ( input.length - inputIndex ) % 2 > 0 )
			throw new IllegalArgumentException( "Mappings have to be in pairs of two." );

		for( ; inputIndex < input.length; inputIndex += 2 )
		{
			if( !( input[inputIndex] instanceof Character ) )
				throw new IllegalArgumentException( "First argument of a mapping needs to be a character." );
			final char chr = (Character)input[inputIndex];
			if( !inputMap.containsKey( chr ) )
				throw new IllegalArgumentException( "Mapping for unused character '" + chr + "'." );
			if( inputMap.get( chr ) != null )
				throw new IllegalArgumentException( "Duplicate mapping for character '" + chr + "'." );
			final IRecipeInput mapping = BetterStorageCrafting.makeInput( input[inputIndex + 1] );
			inputMap.put( chr, mapping );
		}

		for( final Entry< Character, IRecipeInput > entry : inputMap.entrySet() )
			if( entry.getKey() != ' ' && entry.getValue() == null )
				throw new IllegalArgumentException( "No mapping for character '" + entry.getKey() + "'." );

		recipeInput = new IRecipeInput[width * height];
		recipeOutput = output;

		for( int x = 0; x < width; x++ )
			for( int y = 0; y < height; y++ )
				recipeInput[x + y * width] = inputMap.get( ( (String)input[y] ).charAt( x ) );

		recipeWidth = width;
		recipeHeight = height;
	}

	public ShapedStationRecipe( ItemStack output, Object... input )
	{
		this( new ItemStack[] { null, null, null, null, output }, input );
	}

	public ShapedStationRecipe setRequiredExperience( int experience )
	{
		requiredExperience = experience;
		return this;
	}

	public ShapedStationRecipe setCraftingTime( int time )
	{
		craftingTime = time;
		return this;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public List< IRecipeInput[] > getSampleInputs()
	{
		IRecipeInput[] input;
		if( recipeWidth != 3 || recipeHeight != 3 )
		{
			input = new IRecipeInput[9];
			for( int y = 0; y < recipeHeight; y++ )
				System.arraycopy( recipeInput, y * recipeWidth, input, y * 3, recipeWidth );
		}
		else
			input = recipeInput;
		return Arrays.asList( new IRecipeInput[][] { input } );
	}

	@Override
	@SideOnly( Side.CLIENT )
	public List< IRecipeInput > getPossibleInputs()
	{
		return null;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public List< ItemStack > getPossibleOutputs()
	{
		return null;
	}

	// IStationRecipe implementation

	@Override
	public StationCrafting checkMatch( ItemStack[] input, RecipeBounds bounds )
	{
		// Check if boundaries match recipe size.
		if( recipeWidth != bounds.getWidth() || recipeHeight != bounds.getHeight() )
			return null;

		final IRecipeInput[] requiredInput = new IRecipeInput[9];
		if( !checkMatch( input, bounds, requiredInput, false ) && !( mirrored && checkMatch( input, bounds, requiredInput, true ) ) )
			return null;
		return new StationCrafting( recipeOutput, requiredInput, requiredExperience, craftingTime );
	}

	private boolean checkMatch( ItemStack[] input, RecipeBounds bounds, IRecipeInput[] requiredInput, boolean mirror )
	{
		for( int x = 0; x < recipeWidth; x++ )
			for( int y = 0; y < recipeHeight; y++ )
			{
				final int mx = mirror ? recipeWidth - x - 1 : x;
				final int index = bounds.minX + mx + ( bounds.minY + y ) * 3;
				requiredInput[index] = recipeInput[x + y * recipeWidth];

				final int adjustedIndex = bounds.minX + x + ( bounds.minY + y ) * 3;
				final IRecipeInput recipeStack = recipeInput[mx + y * recipeWidth];
				final ItemStack inputStack = input[adjustedIndex];
				if( recipeStack == null ? inputStack != null : !recipeStack.matches( inputStack ) || inputStack.stackSize < recipeStack.getAmount() )
					return false;
			}
		return true;
	}

	// Utility functions

	/**
	 * Returns the bounds of the input items:
	 * An array with minX, minY, width and height.
	 */
	public static int[] calculateRecipeBounds( ItemStack[] input )
	{
		int minX = 0, maxX = 2, minY = 0, maxY = 2;

		for( int x = minX; x <= maxX; x++ )
			for( int y = minY; y <= maxY; y++ )
				if( input[x + y * 3] != null )
				{
					minX = x;
					break;
				}

		for( int x = maxX; x >= minX; x-- )
			for( int y = minY; y <= maxY; y++ )
				if( input[x + y * 3] != null )
				{
					maxX = x;
					break;
				}

		for( int y = minY; y <= maxY; y++ )
			for( int x = minX; x <= maxX; x++ )
				if( input[x + y * 3] != null )
				{
					minY = y;
					break;
				}

		for( int y = maxY; y >= minY; y-- )
			for( int x = minX; x <= maxX; x++ )
				if( input[x + y * 3] != null )
				{
					maxY = y;
					break;
				}

		final int width = maxX - minX + 1;
		final int height = maxY - minY + 1;

		return new int[] { minX, minY, width, height };
	}
}
