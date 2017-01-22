package io.github.tehstoneman.betterstorage.item.cardboard;

import io.github.tehstoneman.betterstorage.item.ItemBetterStorage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

public class ItemCardboardSheet extends ItemBetterStorage
{
	//public static final ToolMaterial toolMaterial = EnumHelper.addToolMaterial( "cardboard", 0, 64, 2.0F, -0.5F, 0 );
	// public static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("cardboard", 5, new int[]{ 1, 2, 2, 1 }, 0);

	public ItemCardboardSheet()
	{
		setMaxStackSize( 8 );
	}

	public static boolean isEffective( ItemStack stack )
	{
		return stack.getItemDamage() < stack.getMaxDamage();
	}

	public static boolean canHarvestBlock( ItemStack stack, boolean canHarvestDefault )
	{
		return isEffective( stack ) ? canHarvestDefault : false;
	}

	public static boolean damageItem( ItemStack stack, int damage, EntityLivingBase entity )
	{
		if( !isEffective( stack ) )
			return true;

		/*
		 * Map<Integer, Integer> enchants = (EnchantmentHelper.getEnchantments(stack));
		 * int numEnchants = enchants.size();
		 * int numLevelTotal = 0;
		 * for (int enchLevel : enchants.values())
		 * numLevelTotal += enchLevel;
		 */

		/*
		 * double changeForNoDamage = - 1 / Math.pow((numLevelTotal / 10 + 1), 2) + 1;
		 * changeForNoDamage = (numEnchants / 10) + (changeForNoDamage * (1 - (numEnchants / 10)));
		 */

		/*
		 * if (entity.worldObj.rand.nextDouble() >= changeForNoDamage)
		 * stack.damageItem(1, entity);
		 */

		if( !isEffective( stack ) )
		{
			entity.renderBrokenItemStack( stack );
			stack.setItemDamage( stack.getMaxDamage() );
			stack.stackSize = 1;
		}

		return true;
	}

	/*
	 * public static boolean onBlockDestroyed(World world, Block block, int x, int y, int z,
	 * ItemStack stack, EntityLivingBase entity) {
	 * return ((block.getBlockHardness(world, x, y, z) > 0)
	 * ? ItemCardboardSheet.damageItem(stack, 1, entity) : true);
	 * }
	 */
}
