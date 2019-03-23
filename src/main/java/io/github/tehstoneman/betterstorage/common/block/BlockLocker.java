package io.github.tehstoneman.betterstorage.common.block;

import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityConnectable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLocker extends BlockLockable
{
	public BlockLocker( Material material )
	{
		super( Block.Properties.create( Material.WOOD ).hardnessAndResistance( 5.0F, 6.0F ).sound( SoundType.WOOD ) );

		// setHardness( 2.5f );

		// setHarvestLevel( "axe", 0 );
		// setSoundType( SoundType.WOOD );

		//@formatter:off
		/*setDefaultState( blockState.getBaseState().withProperty( BlockHorizontal.FACING, EnumFacing.NORTH )
												  .withProperty( Properties.StaticProperty, true )
												  .withProperty( BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT )
												  .withProperty( BlockLockable.CONNECTED, false ) );*/
		//@formatter:on
	}

	public BlockLocker()
	{
		this( Material.WOOD );
	}

	/*
	 * @Override
	 * public boolean isOpaqueCube( IBlockState state )
	 * {
	 * return true;
	 * }
	 */

	/*
	 * @Override
	 * public boolean isFullCube( IBlockState state )
	 * {
	 * return true;
	 * }
	 */

	/*
	 * @Override
	 * // @SideOnly( Side.CLIENT )
	 * public EnumBlockRenderType getRenderType( IBlockState state )
	 * {
	 * return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	 * }
	 */

	/*
	 * @Override
	 * public BlockStateContainer createBlockState()
	 * {
	 * return new BlockStateContainer( this, new IProperty[] { BlockHorizontal.FACING, Properties.StaticProperty, BlockDoor.HINGE, CONNECTED } );
	 * }
	 */

	/*
	 * @Override
	 * public IBlockState getStateFromMeta( int meta )
	 * {
	 * EnumFacing enumfacing = EnumFacing.getFront( meta & 7 );
	 *
	 * if( enumfacing.getAxis() == EnumFacing.Axis.Y )
	 * enumfacing = EnumFacing.NORTH;
	 *
	 * Boolean mirrored = (meta & 8) > 0;
	 *
	 *
	 * return getDefaultState().withProperty( BlockHorizontal.FACING, enumfacing ).withProperty(BlockDoor.HINGE, mirrored ? BlockDoor.EnumHingePosition.RIGHT :
	 * BlockDoor.EnumHingePosition.LEFT);
	 * }
	 */

	/*
	 * @Override
	 * public int getMetaFromState( IBlockState state )
	 * {
	 * int meta = state.getValue( BlockHorizontal.FACING ).getIndex();
	 * if (state.getValue(BlockDoor.HINGE) == BlockDoor.EnumHingePosition.RIGHT)
	 * {
	 * meta |= 8;
	 * }
	 * return meta;
	 * }
	 */

	/*
	 * @Override
	 * public IBlockState getActualState( IBlockState state, IBlockAccess worldIn, BlockPos pos )
	 * {
	 * final TileEntity tileentity = worldIn instanceof ChunkCache ? ( (ChunkCache)worldIn ).getTileEntity( pos, Chunk.EnumCreateEntityType.CHECK )
	 * : worldIn.getTileEntity( pos );
	 *
	 * if( tileentity instanceof TileEntityLocker )
	 * {
	 * final TileEntityLocker locker = (TileEntityLocker)tileentity;
	 * }
	 *
	 * return state.withProperty( Properties.StaticProperty, true );
	 * }
	 */

	@Override
	public void onBlockPlacedBy( World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack )
	{
		// worldIn.setBlockState( pos, state.withProperty( BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite() ), 2 );

		final TileEntity tileentity = worldIn.getTileEntity( pos );

		/*
		 * if( tileentity instanceof TileEntityLocker )
		 * {
		 * final TileEntityLocker locker = (TileEntityLocker)tileentity;
		 * if( stack.hasDisplayName() ) locker.setCustomInventoryName( stack.getDisplayName() );
		 * }
		 */

		if( tileentity instanceof TileEntityConnectable )
			( (TileEntityConnectable)tileentity ).onBlockPlaced( placer, stack );
		else
			super.onBlockPlacedBy( worldIn, pos, state, placer, stack );
	}

	/*
	 * @Override
	 * public TileEntity createNewTileEntity( World world, int metadata )
	 * {
	 * return new TileEntityLocker();
	 * }
	 */

	/*
	 * @Override
	 * public boolean onBlockActivated( World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX,
	 * float hitY, float hitZ )
	 * {
	 * EnumFacing facing = state.getValue( BlockHorizontal.FACING );
	 * if( facing == side)
	 * {
	 * if( !world.isRemote )
	 * {
	 * final TileEntity tileentity = world.getTileEntity( pos );
	 * if( tileentity instanceof TileEntityLocker )
	 * {
	 * final TileEntityLocker locker = (TileEntityLocker)tileentity;
	 * return locker.onBlockActivated( pos, state, player, hand, side, hitX, hitY, hitZ );
	 * }
	 * }
	 * return true;
	 * }
	 * return false;
	 * }
	 */

	@Override
	public boolean hasComparatorInputOverride( IBlockState state )
	{
		return true;
	}

	/*
	 * @Override
	 * protected ItemBlock getItemBlock()
	 * {
	 * return new ItemBlockLocker( this );
	 * }
	 */
}
