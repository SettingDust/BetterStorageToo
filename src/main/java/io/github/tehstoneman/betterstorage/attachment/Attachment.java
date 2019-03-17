package io.github.tehstoneman.betterstorage.attachment;

import io.github.tehstoneman.betterstorage.utils.DirectionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class Attachment
{

	public final TileEntity	tileEntity;
	public final int		subId;

	private double			x, y, z;
	private double			width, height, depth;
	private AxisAlignedBB	box			= new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );
	private EnumFacing		direction	= EnumFacing.NORTH;

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	public double getWidth()
	{
		return width;
	}

	public double getHeight()
	{
		return height;
	}

	public double getDepth()
	{
		return depth;
	}

	public float getRotation()
	{
		return DirectionUtils.getRotation( direction );
	}

	public AxisAlignedBB getHighlightBox()
	{
		return box.offset( tileEntity.getPos() );
	}

	public Attachment( TileEntity tileEntity, int subId )
	{
		this.tileEntity = tileEntity;
		this.subId = subId;
	}

	public void setBox( double x, double y, double z, double width, double height, double depth, double scale )
	{
		this.x = x * scale;
		this.y = y * scale;
		this.z = z * scale;
		this.width = width * scale;
		this.height = height * scale;
		this.depth = depth * scale;
		updateBox();
	}

	public void setBox( double x, double y, double z, double width, double height, double depth )
	{
		setBox( x, y, z, width, height, depth, 1 / 16.0F );
	}

	public void setDirection( EnumFacing direction )
	{
		this.direction = direction;
		updateBox();
	}

	private void updateBox()
	{
		double minX;
		final double minY = 1 - ( y + height / 2 );
		double minZ;
		double maxX;
		final double maxY = 1 - ( y - height / 2 );
		double maxZ;
		switch( direction )
		{
		case EAST:
			minX = 1 - ( z + depth / 2 );
			minZ = x - width / 2;
			maxX = 1 - ( z - depth / 2 );
			maxZ = x + width / 2;
			break;
		case SOUTH:
			minX = 1 - ( x + width / 2 );
			minZ = 1 - ( z + depth / 2 );
			maxX = 1 - ( x - width / 2 );
			maxZ = 1 - ( z - depth / 2 );
			break;
		case WEST:
			minX = z - depth / 2;
			minZ = 1 - ( x + width / 2 );
			maxX = z + depth / 2;
			maxZ = 1 - ( x - width / 2 );
			break;
		default:
			minX = x - width / 2;
			minZ = z - depth / 2;
			maxX = x + width / 2;
			maxZ = z + depth / 2;
			break;
		}
		box = new AxisAlignedBB( minX, minY, minZ, maxX, maxY, maxZ );
	}

	public void update()
	{}

	public boolean interact( EntityPlayer player, EnumAttachmentInteraction type )
	{
		return false;
	}

	public ItemStack pick()
	{
		return null;
	}

	public boolean boxVisible( EntityPlayer player )
	{
		return true;
	}

	// @SideOnly( Side.CLIENT )
	public abstract IAttachmentRenderer getRenderer();

}
