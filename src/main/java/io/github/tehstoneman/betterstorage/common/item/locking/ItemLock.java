package io.github.tehstoneman.betterstorage.common.item.locking;

import java.util.UUID;

import io.github.tehstoneman.betterstorage.api.lock.EnumLockInteraction;
import io.github.tehstoneman.betterstorage.api.lock.IKeyLockable;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.common.enchantment.EnchantmentBetterStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLock extends ItemKeyLock implements ILock
{
	public ItemLock()
	{
		super( "lock" );
		// setMaxDamage( 64 );
		// setMaxStackSize( 1 );
	}

	@Override
	public boolean getIsRepairable( ItemStack stack, ItemStack material )
	{
		return material.getItem() == Items.GOLD_INGOT;
	}

	@Override
	public boolean isDamageable()
	{
		return true;
	}

	@Override
	public void onCreated( ItemStack stack, World world, PlayerEntity player )
	{
		if( !world.isRemote )
			ensureHasID( stack );
	}

	@Override
	public ActionResultType onItemUse( ItemUseContext context )
	{
		final PlayerEntity playerIn = context.getPlayer();
		final Hand hand = context.getHand();
		final World worldIn = context.getWorld();
		final BlockPos pos = context.getPos();
		final ItemStack stack = context.getItem();

		if( hand == Hand.MAIN_HAND )
		{
			final BlockState blockState = worldIn.getBlockState( pos );
			final Block block = blockState.getBlock();

			/*if( block == Blocks.IRON_DOOR )
			{
				if( blockState.get( DoorBlock.HALF ) == DoubleBlockHalf.UPPER )
				{
					pos = pos.down();
					blockState = worldIn.getBlockState( pos );
					block = blockState.getBlock();
				}

				//@formatter:off
				worldIn.setBlockState( pos, BetterStorageBlocks.LOCKABLE_DOOR.getDefaultState()
						.withProperty( BlockDoor.FACING, blockState.getValue( BlockDoor.FACING ) )
						.withProperty( BlockDoor.OPEN, blockState.getValue( BlockDoor.OPEN ) )
						.withProperty( BlockDoor.HINGE, blockState.getValue( BlockDoor.HINGE ) )
						.withProperty( BlockDoor.HALF, EnumDoorHalf.LOWER ) );
				worldIn.setBlockState( pos.up(), BetterStorageBlocks.LOCKABLE_DOOR.getDefaultState()
						.withProperty( BlockDoor.FACING, blockState.getValue( BlockDoor.FACING ) )
						.withProperty( BlockDoor.OPEN, blockState.getValue( BlockDoor.OPEN ) )
						.withProperty( BlockDoor.HINGE, blockState.getValue( BlockDoor.HINGE ) )
						.withProperty( BlockDoor.HALF, EnumDoorHalf.UPPER ) );
				//@formatter:on

				final TileEntity tileEntity = worldIn.getTileEntity( pos );
				if( tileEntity instanceof TileEntityLockableDoor )
				{
					final TileEntityLockableDoor lockable = (TileEntityLockableDoor)tileEntity;
					if( lockable.isLockValid( stack ) )
					{
						lockable.setLock( stack.copy() );
						if( !playerIn.isCreative() )
							playerIn.setHeldItem( hand, ItemStack.EMPTY );
						return EnumActionResult.SUCCESS;
					}
				}
			}*/

			final TileEntity tileEntity = worldIn.getTileEntity( pos );
			if( tileEntity instanceof IKeyLockable )
			{
				final IKeyLockable lockable = (IKeyLockable)tileEntity;
				if( lockable.isLockValid( stack ) && lockable.getLock().isEmpty() )
				{
					lockable.setLock( stack.copy() );
					if( !playerIn.isCreative() )
						playerIn.setHeldItem( hand, ItemStack.EMPTY );
					return ActionResultType.SUCCESS;
				}
			}
		}
		return super.onItemUse( context );
	}

	/**
	 * Gives the lock a random ID if it doesn't have one. <br>
	 * This is usually only when the lock is taken out of creative.
	 */
	public static void ensureHasID( ItemStack stack )
	{
		CompoundNBT tag = stack.getTag();
		if( tag == null )
			tag = new CompoundNBT();
		if( !tag.hasUniqueId( TAG_KEYLOCK_ID ) )
		{
			tag.putUniqueId( TAG_KEYLOCK_ID, UUID.randomUUID() );
			stack.setTag( tag );
		}
	}

	// ILock implementation

	@Override
	public String getLockType()
	{
		return "normal";
	}

	@Override
	public void onUnlock( ItemStack lock, ItemStack key, IKeyLockable lockable, PlayerEntity player, boolean success )
	{
		if( success )
			return;
		// Power is 2 when a key was used to open the lock, 1 otherwise.
		applyEffects( lock, lockable, player, key.isEmpty() ? EnumLockInteraction.OPEN : EnumLockInteraction.PICK );
	}

	@Override
	public void applyEffects( ItemStack lock, IKeyLockable lockable, PlayerEntity player, EnumLockInteraction interaction )
	{
		final int shock = EnchantmentHelper.getEnchantmentLevel( EnchantmentBetterStorage.SHOCK, lock );

		if( shock > 0 )
		{
			int damage = shock;
			if( interaction == EnumLockInteraction.PICK )
				damage *= 3;
			player.attackEntityFrom( DamageSource.MAGIC, damage );
			final World world = player.getEntityWorld();
			world.playSound( (PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS, 0.5f,
					world.rand.nextFloat() * 0.1F + 0.9F );
			if( shock >= 3 && interaction != EnumLockInteraction.OPEN )
				player.setFire( 3 );
		}
	}

	@Override
	public boolean canApplyEnchantment( ItemStack key, Enchantment enchantment )
	{
		return true;
	}
}
