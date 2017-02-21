package io.github.tehstoneman.betterstorage.utils;

import java.util.List;

import io.github.tehstoneman.betterstorage.attachment.Attachments;
import io.github.tehstoneman.betterstorage.common.inventory.ContainerBetterStorage;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityContainer;
import io.github.tehstoneman.betterstorage.inventory.InventoryTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class WorldUtils
{
	private WorldUtils()
	{}

	@SideOnly( Side.CLIENT )
	public static World getLocalWorld()
	{
		return Minecraft.getMinecraft().theWorld;
	}

	public static AxisAlignedBB getAABB( TileEntity entity, double minX, double minY, double minZ, double maxX, double maxY, double maxZ )
	{
		final double x = entity.getPos().getX();
		final double y = entity.getPos().getY();
		final double z = entity.getPos().getZ();
		return new AxisAlignedBB( x - minX, y - minY, z - minZ, x + maxX + 1, y + maxY + 1, z + maxZ + 1 );
	}

	public static AxisAlignedBB getAABB( TileEntity entity, double radius )
	{
		return getAABB( entity, radius, radius, radius, radius, radius, radius );
	}

	// Item spawning related functions

	/** Spawns an ItemStack in the world. */
	public static EntityItem spawnItem( World world, double x, double y, double z, ItemStack stack )
	{
		if( stack == null || stack.stackSize <= 0 )
			return null;
		final EntityItem item = new EntityItem( world, x, y, z, stack );
		world.spawnEntityInWorld( item );
		return item;
	}

	/** Spawns an ItemStack in the world with random motion. */
	public static EntityItem spawnItemWithMotion( World world, double x, double y, double z, ItemStack stack )
	{
		final EntityItem item = spawnItem( world, x, y, z, stack );
		if( item != null )
		{
			item.motionX = RandomUtils.getGaussian() * 0.05F;
			item.motionY = RandomUtils.getGaussian() * 0.05F + 0.2F;
			item.motionZ = RandomUtils.getGaussian() * 0.05F;
		}
		return item;
	}

	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock( World world, int x, int y, int z, ItemStack stack )
	{
		final float itemX = x + RandomUtils.getFloat( 0.1F, 0.9F );
		final float itemY = y + RandomUtils.getFloat( 0.1F, 0.9F );
		final float itemZ = z + RandomUtils.getFloat( 0.1F, 0.9F );
		return spawnItemWithMotion( world, itemX, itemY, itemZ, stack );
	}

	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock( TileEntity te, ItemStack stack )
	{
		return dropStackFromBlock( te.getWorld(), te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), stack );
	}

	/** Spawns an ItemStack as if it was dropped from an entity on death. */
	public static EntityItem dropStackFromEntity( Entity entity, ItemStack stack, float speed )
	{
		final EntityPlayer player = entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
		EntityItem item;
		if( player == null )
		{
			final double y = entity.posY + entity.getEyeHeight() - 0.3;
			item = spawnItem( entity.worldObj, entity.posX, y, entity.posZ, stack );
			if( item == null )
				return null;
			// item.delayBeforeCanPickup = 40;
			final float f1 = RandomUtils.getFloat( 0.5F );
			final float f2 = RandomUtils.getFloat( (float)Math.PI * 2.0F );
			item.motionX = -MathHelper.sin( f2 ) * f1;
			item.motionY = 0.2;
			item.motionZ = MathHelper.cos( f2 ) * f1;
			return item;
		}
		else
			item = player.dropItem( stack, true );
		if( item != null )
		{
			item.motionX *= speed / 4;
			item.motionZ *= speed / 4;
		}
		return item;
	}

	// TileEntity related functions

	/** Returns whether the TileEntity at the position is an instance of tileClass. */
	public static <T> boolean is( IBlockAccess world, int x, int y, int z, Class< T > tileClass )
	{
		return tileClass.isInstance( world.getTileEntity( new BlockPos( x, y, z ) ) );
	}

	/** Returns the TileEntity at the position if it's an instance of tileClass, null if not. */
	public static <T> T get( IBlockAccess world, int x, int y, int z, Class< T > tileClass )
	{
		final TileEntity t = world.getTileEntity( new BlockPos( x, y, z ) );
		return tileClass.isInstance( t ) ? (T)t : null;
	}

	/** Returns if the TileEntity can be used by this player. */
	public static boolean isTileEntityUsableByPlayer( TileEntity entity, EntityPlayer player )
	{
		return entity.getWorld().getTileEntity( entity.getPos() ) == entity
				&& player.getDistanceSq( entity.getPos().getX() + 0.5, entity.getPos().getY() + 0.5, entity.getPos().getZ() + 0.5 ) <= 64.0;
	}

	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing( TileEntity te, int playersUsing, IInventory playerInventory )
	{
		if( !te.getWorld().isRemote && playersUsing != 0 )
		{
			playersUsing = 0;
			final List< EntityPlayer > players = te.getWorld().getEntitiesWithinAABB( EntityPlayer.class, getAABB( te, 5 ) );
			for( final EntityPlayer player : players )
				if( player.openContainer instanceof ContainerBetterStorage )
				{
					final IInventory inventory = ( (ContainerBetterStorage)player.openContainer ).inventory;
					if( inventory == playerInventory )
						playersUsing++;
					else
						if( inventory instanceof InventoryTileEntity )
							if( ( (InventoryTileEntity)inventory ).mainTileEntity == te )
								playersUsing++;
				}
		}
		return playersUsing;
	}

	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing( TileEntityContainer te, int numUsingPlayers )
	{
		return syncPlayersUsing( te, numUsingPlayers, te.getPlayerInventory() );
	}

	/** This will perform a {@link World#notifyBlockOfNeighborChange()} on every adjacent block including the block at x|y|z. */

	public static void notifyBlocksAround( World world, int x, int y, int z )
	{
		final Block block = world.getBlockState( new BlockPos( x, y, z ) ).getBlock();
		// world.notifyNeighborsOfStateChange( pos, blockType );
		world.notifyNeighborsOfStateChange( new BlockPos( x, y, z ), block );
		world.notifyNeighborsOfStateChange( new BlockPos( x + 1, y, z ), block );
		world.notifyNeighborsOfStateChange( new BlockPos( x - 1, y, z ), block );
		world.notifyNeighborsOfStateChange( new BlockPos( x, y + 1, z ), block );
		world.notifyNeighborsOfStateChange( new BlockPos( x, y - 1, z ), block );
		world.notifyNeighborsOfStateChange( new BlockPos( x, y, z + 1 ), block );
		world.notifyNeighborsOfStateChange( new BlockPos( x, y, z - 1 ), block );
	}

	// Misc functions

	public static RayTraceResult rayTrace( EntityPlayer player, float partialTicks )
	{
		Attachments.playerLocal.set( player );
		// final double range = player.worldObj.isRemote ? Minecraft.getMinecraft().playerController.getBlockReachDistance()
		// : ( (EntityPlayerMP)player ).theItemInWorldManager.getBlockReachDistance();
		final double range = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		final Vec3d start = new Vec3d( player.posX, player.posY + 1.62 - player.getYOffset(), player.posZ );
		final Vec3d look = player.getLook( 1.0F );
		final Vec3d end = start.addVector( look.xCoord * range, look.yCoord * range, look.zCoord * range );
		final RayTraceResult target = player.worldObj.rayTraceBlocks( start, end );
		Attachments.playerLocal.remove();
		return target;
	}
}
