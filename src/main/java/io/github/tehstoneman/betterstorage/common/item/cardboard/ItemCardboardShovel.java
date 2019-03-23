package io.github.tehstoneman.betterstorage.common.item.cardboard;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.common.item.ItemBetterStorage;
import net.minecraft.item.Item;

public class ItemCardboardShovel extends ItemBetterStorage // ItemSpade implements ICardboardItem
{

	public ItemCardboardShovel()
	{
		super( "cardboard_shovel", new Item.Properties().group( BetterStorage.ITEM_GROUP ) );
	}

	private String name;

	/*
	 * public ItemCardboardShovel()
	 * {
	 * super( ItemCardboardSheet.toolMaterial );
	 * this.name = "cardboard_shovel";
	 * }
	 */

	/*
	 * public void register()
	 * {
	 * setUnlocalizedName( ModInfo.modId + "." + name );
	 * setRegistryName( name );
	 * //GameRegistry.register( this );
	 * }
	 */

	/*
	 * @SideOnly( Side.CLIENT )
	 * public void registerItemModels()
	 * {
	 * ModelLoader.setCustomModelResourceLocation( this, 0, new ModelResourceLocation( getRegistryName(), "inventory" ) );
	 * }
	 */

	// Makes sure cardboard tools don't get destroyed,
	// and are ineffective when durability is at 0.
	/*
	 * @Override
	 * public boolean canHarvestBlock( IBlockState block, ItemStack stack )
	 * {
	 * return ItemCardboardSheet.canHarvestBlock( stack, super.canHarvestBlock( block, stack ) );
	 * }
	 */

	/*
	 * @Override
	 * public boolean onLeftClickEntity( ItemStack stack, EntityPlayer player, Entity entity )
	 * {
	 * return !ItemCardboardSheet.isEffective( stack );
	 * }
	 */

	/*
	 * @Override
	 * public boolean onBlockDestroyed( ItemStack stack, World world, IBlockState block, BlockPos pos, EntityLivingBase player )
	 * {
	 * //return ItemCardboardSheet.onBlockDestroyed( stack, world, block, pos, player );
	 * return block.getBlockHardness( world, pos ) > 0 ? ItemCardboardSheet.damageItem( stack, 1, player ) : true;
	 * }
	 */

	/*
	 * @Override
	 * public boolean hitEntity( ItemStack stack, EntityLivingBase target, EntityLivingBase player )
	 * {
	 * return ItemCardboardSheet.damageItem( stack, 1, player );
	 * }
	 */

	// Cardboard items
	/*
	 * @Override
	 * public boolean canDye( ItemStack stack )
	 * {
	 * return true;
	 * }
	 */

	/*
	 * @Override
	 * public int getColor( ItemStack itemstack )
	 * {
	 * if( hasColor( itemstack ) )
	 * {
	 * final NBTTagCompound compound = itemstack.getTagCompound();
	 * return compound.getInteger( "color" );
	 * }
	 * return 0x705030;
	 * }
	 */

	/*
	 * @Override
	 * public boolean hasColor( ItemStack itemstack )
	 * {
	 * if( itemstack.hasTagCompound() )
	 * {
	 * final NBTTagCompound compound = itemstack.getTagCompound();
	 * return compound.hasKey( "color" );
	 * }
	 * return false;
	 * }
	 */

	/*
	 * @Override
	 * public void setColor( ItemStack itemstack, int colorRGB )
	 * {
	 * NBTTagCompound compound;
	 * if( itemstack.hasTagCompound() )
	 * compound = itemstack.getTagCompound();
	 * else
	 * compound = new NBTTagCompound();
	 * compound.setInteger( "color", colorRGB );
	 * itemstack.setTagCompound( compound );
	 * }
	 */
}
