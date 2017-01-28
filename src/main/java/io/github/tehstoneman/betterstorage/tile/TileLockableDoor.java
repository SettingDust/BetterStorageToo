package io.github.tehstoneman.betterstorage.tile;

import io.github.tehstoneman.betterstorage.attachment.Attachments;
import io.github.tehstoneman.betterstorage.attachment.EnumAttachmentInteraction;
import io.github.tehstoneman.betterstorage.attachment.IHasAttachments;
import io.github.tehstoneman.betterstorage.tile.entity.TileEntityLockableDoor;
import io.github.tehstoneman.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileLockableDoor extends TileBetterStorage
{
	/*
	 * private IIcon iconUpper;
	 * private IIcon iconLower;
	 * private IIcon iconUpperFlipped;
	 * private IIcon iconLowerFlipped;
	 */

	public TileLockableDoor()
	{
		super( Material.WOOD );

		setCreativeTab( null );
		setHardness( 8.0F );
		setResistance( 20.0F );
		// setStepSound(soundTypeWood);
		setHarvestLevel( "axe", 2 );
	}

	/*
	 * @Override
	 * public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
	 * int metadata = world.getBlockMetadata(x, y, z);
	 * float offset = metadata == 0 ? 0F : -1F;
	 * TileEntityLockableDoor te = WorldUtils.get(world, x, y + (int)offset, z, TileEntityLockableDoor.class);
	 * 
	 * if (te == null) return;
	 * 
	 * switch (te.orientation) {
	 * case WEST:
	 * if (te.isOpen) setBlockBounds(0F, 0F, 0.005F / 16F, 1F, 1F, 2.995F / 16F);
	 * else setBlockBounds(0.005F / 16F, 0F, 0F, 2.995F / 16F, 1F, 1F);
	 * break;
	 * case EAST:
	 * if (te.isOpen) setBlockBounds(0F, 0F, 13.005F / 16F, 1F, 1F, 15.995F / 16F);
	 * else setBlockBounds(13.005F / 16F, 0F, 0F, 15.995F / 16F, 1F, 1F);
	 * break;
	 * case SOUTH:
	 * if (te.isOpen) setBlockBounds(0.005F / 16F, 0F, 0F, 2.995F / 16F, 1F, 1F);
	 * else setBlockBounds(0F, 0F, 13.005F / 16F, 1F, 1F, 15.995F / 16F);
	 * break;
	 * default:
	 * if (te.isOpen) setBlockBounds(13.005F / 16F, 0F, 0F, 15.995F / 16F, 1F, 1F);
	 * else setBlockBounds(0F, 0F, 0.005F / 16F, 1F, 1F, 2.995F / 16F);
	 * break;
	 * }
	 * }
	 */

	/*
	 * @Override
	 * public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
	 * setBlockBoundsBasedOnState(world, x, y, z);
	 * return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	 * }
	 */

	/*
	 * @Override
	 * 
	 * @SideOnly(Side.CLIENT)
	 * public void registerBlockIcons(IIconRegister iconRegister) {
	 * iconUpper = iconRegister.registerIcon("door_iron_upper");
	 * iconLower = iconRegister.registerIcon("door_iron_lower");
	 * iconUpperFlipped = new IconFlipped(iconUpper, true, false);
	 * iconLowerFlipped = new IconFlipped(iconLower, true, false);
	 * blockIcon = iconUpper;
	 * }
	 */

	/*
	 * @Override
	 * public IIcon getIcon(IBlockAccess world, int x, int y, int z, int face) {
	 * int meta = world.getBlockMetadata(x, y, z);
	 * if (meta > 0) y -= 1;
	 * TileEntityLockableDoor lockable = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
	 * 
	 * boolean flip = false;
	 * IIcon icon = iconUpper;
	 * 
	 * if(meta == 0 || face == 1) {
	 * icon = iconLower;
	 * }
	 * 
	 * switch(lockable.orientation) {
	 * case WEST:
	 * if(face == 3 && !lockable.isOpen) flip = true;
	 * else if(face == 2 && lockable.isOpen) flip = true;
	 * break;
	 * case EAST:
	 * if(face == 4 && !lockable.isOpen) flip = true;
	 * else if(face == 3 && lockable.isOpen) flip = true;
	 * break;
	 * case SOUTH:
	 * if(face == 2 && !lockable.isOpen) flip = true;
	 * else if(face == 4 && lockable.isOpen) flip = true;
	 * break;
	 * default:
	 * if(face == 3 && !lockable.isOpen) flip = true;
	 * else if(face == 5 && lockable.isOpen) flip = true;
	 * break;
	 * }
	 * 
	 * icon = flip ? (icon == iconLower ? iconLowerFlipped : iconUpperFlipped) : icon;
	 * return icon;
	 * }
	 */

	@Override
	public float getBlockHardness( IBlockState state, World world, BlockPos pos )
	{
		// if (world.getBlockMetadata(pos.getX(), pos.getY(), pos.getZ()) > 0) y -= 1;
		final TileEntityLockableDoor lockable = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), TileEntityLockableDoor.class );
		if( lockable != null && lockable.getLock() != null )
			return -1;
		else
			return super.getBlockHardness( state, world, pos );
	}

	/*
	 * @Override
	 * public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
	 * //if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
	 * float modifier = 1.0F;
	 * TileEntityLockableDoor lockable = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
	 * if (lockable != null) {
	 * int persistance = BetterStorageEnchantment.getLevel(lockable.getLock(), "persistance");
	 * if (persistance > 0) modifier += Math.pow(2, persistance);
	 * }
	 * return super.getExplosionResistance(entity) * modifier;
	 * }
	 */

	/*
	 * @Override
	 * public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPar) {
	 * TileEntity te = world.getTileEntity(x, y, z);
	 * return ((te != null) ? te.receiveClientEvent(eventId, eventPar) : false);
	 * }
	 */

	@Override
	public boolean onBlockActivated( World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem,
			EnumFacing side, float hitX, float hitY, float hitZ )
	{
		// if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		final TileEntityLockableDoor te = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), TileEntityLockableDoor.class );
		return te.onBlockActivated( world, pos.getX(), pos.getY(), pos.getZ(), player, side, hitX, hitY, hitZ );
	}

	@Override
	public void onBlockClicked( World world, BlockPos pos, EntityPlayer player )
	{
		// if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		final Attachments attachments = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), IHasAttachments.class ).getAttachments();
		final boolean abort = attachments.interact( WorldUtils.rayTrace( player, 1.0F ), player, EnumAttachmentInteraction.attack );
	}

	/*
	 * @Override
	 * public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
	 * //int metadata = world.getBlockMetadata(x, y, z);
	 * IHasAttachments te = WorldUtils.get(world, pos.getX(), pos.getY() - (metadata > 0 ? 1 : 0), pos.getZ(), IHasAttachments.class);
	 * if(te == null) return super.collisionRayTrace(world, x, y, z, start, end);
	 * MovingObjectPosition pos = te.getAttachments().rayTrace(world, x, y - (metadata > 0 ? 1 : 0), z, start, end);
	 * return pos != null ? pos : super.collisionRayTrace(world, x, y, z, start, end);
	 * }
	 */

	@Override
	public ItemStack getPickBlock( IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player )
	{
		return new ItemStack( Items.IRON_DOOR );
	}

	@Override
	public boolean removedByPlayer( IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest )
	{
		return world.setBlockToAir( pos );
	}

	@Override
	public void breakBlock( World world, BlockPos pos, IBlockState state )
	{
		// if (meta > 0) return;
		super.breakBlock( world, pos, state );
	}

	@Override
	public void onNeighborChange( IBlockAccess world, BlockPos pos, BlockPos neighbor )
	{
		/*
		 * int metadata = world.getBlockMetadata(x, y, z);
		 * int targetY = y + ((metadata == 0) ? 1 : -1);
		 * int targetMeta = ((metadata == 0) ? 8 : 0);
		 * if (world.getBlock(x, y - 1, z) == Blocks.air && metadata == 0) world.setBlockToAir(x, y, z);
		 * if ((world.getBlock(x, targetY, z) == this) && (world.getBlockMetadata(x, targetY, z) == targetMeta)) return;
		 * world.setBlockToAir(x, y, z);
		 * if (metadata == 0) WorldUtils.spawnItem(world, x, y, z, new ItemStack(Items.IRON_DOOR));
		 */
	}

	/*
	 * @Override
	 * public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
	 * if(meta == 0) {
	 * TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
	 * WorldUtils.dropStackFromBlock(te, te.getLock());
	 * te.setLockWithUpdate(null);
	 * }
	 * super.onBlockPreDestroy(world, x, y, z, meta);
	 * }
	 */

	@Override
	public boolean isOpaqueCube( IBlockState state )
	{
		return false;
	}

	@Override
	public boolean isNormalCube( IBlockState state )
	{
		return false;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public EnumBlockRenderType getRenderType( IBlockState state )
	{
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	/*
	 * @Override
	 * public int quantityDropped(int meta, int fortune, Random random) {
	 * return ((meta == 0) ? 1 : 0);
	 * }
	 */

	@Override
	public boolean canProvidePower( IBlockState state )
	{
		return true;
	}

	@Override
	public int getWeakPower( IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side )
	{
		// if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		final TileEntityLockableDoor te = WorldUtils.get( world, pos.getX(), pos.getY(), pos.getZ(), TileEntityLockableDoor.class );
		return te == null ? 0 : te.isPowered() ? 15 : 0;
	}

	@Override
	public int getStrongPower( IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side )
	{
		return getWeakPower( state, world, pos, side );
	}

	/*
	 * @Override
	 * public void updateTick(World world, int x, int y, int z, Random random) {
	 * if(world.getBlockMetadata(x, y, z) != 0) return;
	 * WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class).setPowered(false);
	 * }
	 */

	/*
	 * @Override
	 * public TileEntity createTileEntity(World world, IBlockState state) {
	 * return ((metadata == 0) ? new TileEntityLockableDoor() : null);
	 * }
	 */

	@Override
	public boolean hasTileEntity( IBlockState state )
	{
		return true;
	}
}
