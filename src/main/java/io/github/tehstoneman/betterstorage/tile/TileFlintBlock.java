package io.github.tehstoneman.betterstorage.tile;

import net.minecraft.block.material.Material;

public class TileFlintBlock extends TileBetterStorage
{
	public TileFlintBlock()
	{
		super( Material.ROCK );

		setHardness( 3.0F );
		setResistance( 6.0F );
		// setStepSound(soundTypeStone);
	}

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT)
	 * public void registerBlockIcons(IIconRegister iconRegister) {
	 * blockIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName());
	 * }
	 */
}
