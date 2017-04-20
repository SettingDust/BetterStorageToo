package io.github.tehstoneman.betterstorage.attachment;

import io.github.tehstoneman.betterstorage.BetterStorage;
import io.github.tehstoneman.betterstorage.api.BetterStorageEnchantment;
import io.github.tehstoneman.betterstorage.api.lock.EnumLockInteraction;
import io.github.tehstoneman.betterstorage.api.lock.IKey;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.ILockable;
import io.github.tehstoneman.betterstorage.common.item.BetterStorageItems;
import io.github.tehstoneman.betterstorage.common.item.ItemBetterStorage;
import io.github.tehstoneman.betterstorage.config.GlobalConfig;
import io.github.tehstoneman.betterstorage.utils.StackUtils;
import io.github.tehstoneman.betterstorage.utils.WorldUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockAttachment extends ItemAttachment
{
	public int		hit				= 0;
	public int		breakProgress	= 0;

	public float	wiggle			= 0;
	public float	wiggleStrength	= 0.0F;

	public LockAttachment( TileEntity tileEntity, int subId )
	{
		super( tileEntity, subId );
		if( !( tileEntity instanceof ILockable ) )
			throw new IllegalArgumentException( "tileEntity must be ILockable." );
	}

	@Override
	public boolean boxVisible( EntityPlayer player )
	{
		final ItemStack holding = player != null ? player.getHeldItemMainhand() : null;
		return item != null || StackUtils.isLock( holding );
	}

	@Override
	@SideOnly( Side.CLIENT )
	public IAttachmentRenderer getRenderer()
	{
		return LockAttachmentRenderer.instance;
	}

	@Override
	public void update()
	{
		hit = Math.max( -20, hit - 1 );
		if( hit <= -20 )
			breakProgress = Math.max( 0, breakProgress - 1 );
		if( tileEntity.getWorld().isRemote )
		{
			wiggle++;
			wiggleStrength = Math.max( 0.0F, wiggleStrength * 0.9F - 0.1F );
		}
	}

	@Override
	public boolean interact( EntityPlayer player, EnumAttachmentInteraction type )
	{
		final ItemStack holding = player.getHeldItemMainhand();
		return type == EnumAttachmentInteraction.attack ? attack( player, holding ) : use( player, holding );
	}

	@Override
	public ItemStack pick()
	{
		if( item == null )
			return null;
		final ItemStack key = new ItemStack( BetterStorageItems.KEY );
		ItemBetterStorage.setID( key, ItemBetterStorage.getID( item ) );
		final int color = ItemBetterStorage.getKeyColor1( item );
		if( color >= 0 )
			ItemBetterStorage.setKeyColor1( key, color );
		final int fullColor = ItemBetterStorage.getKeyColor2( item );
		if( fullColor >= 0 )
			ItemBetterStorage.setKeyColor2( key, fullColor );
		return key;
	}

	private boolean attack( EntityPlayer player, ItemStack holding )
	{
		final ILockable lockable = (ILockable)tileEntity;
		final ItemStack lock = lockable.getLock();
		if( lock == null )
			return false;

		final boolean canHurt = hit <= 0 && canHurtLock( holding );
		if( canHurt )
			holding.damageItem( 2, player );
		/*
		 * if (holding.stackSize <= 0)
		 * player.destroyCurrentEquippedItem();
		 */
		if( !player.worldObj.isRemote )
		{
			if( canHurt )
			{
				hit = 10;

				// int damage =
				// (int)((AttributeModifier)holding.getAttributeModifiers().get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName()).iterator().next()).getAmount();
				// int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, holding);
				// int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, holding);
				// breakProgress += Math.min(damage, 10) / 2 + Math.min(Math.max(sharpness, efficiency), 5);

				final int persistance = BetterStorageEnchantment.getLevel( lock, "persistance" );
				if( breakProgress > 100 * ( 1 + persistance ) )
				{
					// int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, lock);
					// lock.setItemDamage(lock.getItemDamage() + 10 / (1 + unbreaking));
					if( lock.getItemDamage() < lock.getMaxDamage() )
					{
						final AxisAlignedBB box = getHighlightBox();
						final double x = ( box.minX + box.maxX ) / 2;
						final double y = ( box.minY + box.maxY ) / 2;
						final double z = ( box.minZ + box.maxZ ) / 2;
						final EntityItem item = WorldUtils.spawnItem( tileEntity.getWorld(), x, y, z, lock );
					}
					lockable.setLock( null );
					breakProgress = 0;
				}

				( (ILock)lock.getItem() ).applyEffects( lock, lockable, player, EnumLockInteraction.ATTACK );
			}
			/*
			 * BetterStorage.networkChannel.sendToAllAround(
			 * new PacketLockHit(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), canHurt),
			 * tileEntity.getWorld(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), 32);
			 */
		}
		else
			hit( canHurt );
		return true;
	}

	@SideOnly( Side.CLIENT )
	public void hit( boolean damage )
	{
		wiggleStrength = Math.min( 20.0F, wiggleStrength + 12.0F );
		if( damage )
		{
			hit = 10;
			final AxisAlignedBB box = getHighlightBox();
			final double x = ( box.minX + box.maxX ) / 2;
			final double y = ( box.minY + box.maxY ) / 2;
			final double z = ( box.minZ + box.maxZ ) / 2;
			// tileEntity.getWorld().playSound(x, y, z, "random.break", 0.5F, 2.5F, false);
		}
	}

	private boolean use( EntityPlayer player, ItemStack holding )
	{
		if( player.worldObj.isRemote )
			return false;

		final ILockable lockable = (ILockable)tileEntity;
		final ItemStack lock = lockable.getLock();

		if( lock == null )
		{
			if( StackUtils.isLock( holding ) && lockable.isLockValid( holding ) )
			{
				lockable.setLock( holding );
				player.inventory.setInventorySlotContents( player.inventory.currentItem, null );
				return true;
			}
		}
		else
			if( StackUtils.isKey( holding ) )
			{
				final IKey keyType = (IKey)holding.getItem();
				final ILock lockType = (ILock)lock.getItem();

				final boolean success = keyType.unlock( holding, lock, true );
				lockType.onUnlock( lock, holding, lockable, player, success );
				if( !success )
					return true;

				if( player.isSneaking() )
				{
					final AxisAlignedBB box = getHighlightBox();
					final double x = ( box.minX + box.maxX ) / 2;
					final double y = ( box.minY + box.maxY ) / 2;
					final double z = ( box.minZ + box.maxZ ) / 2;
					final EntityItem item = WorldUtils.spawnItem( player.worldObj, x, y, z, lock );
					lockable.setLock( null );
				}
				else
					lockable.useUnlocked( player );

				return true;
			}

		return false;
	}

	private boolean canHurtLock( ItemStack stack )
	{
		if( stack == null || !BetterStorage.globalConfig.getBoolean( GlobalConfig.lockBreakable ) )
			return false;
		final Item item = stack.getItem();
		return item instanceof ItemSword || item instanceof ItemPickaxe || item instanceof ItemAxe;
	}
}
