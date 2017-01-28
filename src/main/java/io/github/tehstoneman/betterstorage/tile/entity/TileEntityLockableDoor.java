package io.github.tehstoneman.betterstorage.tile.entity;

import io.github.tehstoneman.betterstorage.api.lock.EnumLockInteraction;
import io.github.tehstoneman.betterstorage.api.lock.ILock;
import io.github.tehstoneman.betterstorage.api.lock.ILockable;
import io.github.tehstoneman.betterstorage.attachment.Attachments;
import io.github.tehstoneman.betterstorage.attachment.IHasAttachments;
import io.github.tehstoneman.betterstorage.attachment.LockAttachment;
import io.github.tehstoneman.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityLockableDoor extends TileEntity implements ILockable, IHasAttachments
{

	private final Attachments	attachments	= new Attachments( this );
	public LockAttachment		lockAttachment;
	public EnumFacing			orientation	= EnumFacing.NORTH;

	private boolean				powered		= false;
	private boolean				swing		= false;

	public boolean				isOpen		= false;
	public boolean				isMirrored	= false;

	public TileEntityLockableDoor()
	{

		lockAttachment = attachments.add( LockAttachment.class );
		lockAttachment.setScale( 0.5F, 1.5F );
	}

	@Override
	@SideOnly( Side.CLIENT )
	public AxisAlignedBB getRenderBoundingBox()
	{
		return WorldUtils.getAABB( this, 0, 0, 0, 0, 1, 0 );
	}

	private void updateLockPosition()
	{
		// Maybe we should use the orientation that the attachment has by itself.
		switch( orientation )
		{
		case WEST:
			if( isOpen )
				lockAttachment.setBox( 12.5, -1.5, 1.5, 5, 6, 3 );
			else
				lockAttachment.setBox( 1.5, -1.5, 12.5, 3, 6, 5 );
			break;
		case EAST:
			if( isOpen )
				lockAttachment.setBox( 3.5, -1.5, 14.5, 5, 6, 3 );
			else
				lockAttachment.setBox( 14.5, -1.5, 3.5, 3, 6, 5 );
			break;
		case SOUTH:
			if( isOpen )
				lockAttachment.setBox( 1.5, -1.5, 3.5, 3, 6, 5 );
			else
				lockAttachment.setBox( 12.5, -1.5, 14.5, 5, 6, 3 );
			break;
		default:
			if( isOpen )
				lockAttachment.setBox( 14.5, -1.5, 12.5, 3, 6, 5 );
			else
				lockAttachment.setBox( 3.5, -1.5, 1.5, 5, 6, 3 );
			break;
		}
	}

	@Override
	public Attachments getAttachments()
	{
		return attachments;
	}

	@Override
	public ItemStack getLock()
	{
		return lockAttachment.getItem();
	}

	@Override
	public boolean isLockValid( ItemStack lock )
	{
		return lock == null || lock.getItem() instanceof ILock;
	}

	@Override
	public void setLock( ItemStack lock )
	{
		// Turn it back into a normal iron door
		/*
		 * if(lock == null) {
		 * lockAttachment.setItem(null);
		 * int rotation = orientation == EnumFacing.WEST ? 0 : orientation == EnumFacing.NORTH ? 1 : orientation == EnumFacing.EAST ? 2 : 3;
		 * rotation = isMirrored ? (rotation == 0 ? 1 : rotation == 1 ? 2 : rotation == 2 ? 3 : 0) : rotation;
		 * worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.iron_door, rotation, SetBlockFlag.SEND_TO_CLIENT);
		 * worldObj.setBlock(xCoord, yCoord + 1, zCoord, Blocks.iron_door, isMirrored ? 9 : 8, SetBlockFlag.SEND_TO_CLIENT);
		 * worldObj.notifyBlockChange(xCoord, yCoord, zCoord, Blocks.iron_door);
		 * worldObj.notifyBlockChange(xCoord, yCoord + 1, zCoord, Blocks.iron_door);
		 * } else setLockWithUpdate(lock);
		 */
	}

	public void setLockWithUpdate( ItemStack lock )
	{
		lockAttachment.setItem( lock );
		updateLockPosition();
		// worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	@Override
	public boolean canUse( EntityPlayer player )
	{
		return false;
	}

	@Override
	public void useUnlocked( EntityPlayer player )
	{
		isOpen = !isOpen;
		// worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 0, isOpen ? 1 : 0);
		updateLockPosition();
	}

	@Override
	public void applyTrigger()
	{
		setPowered( true );
	}

	public boolean onBlockActivated( World world, int x, int y, int z, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ )
	{
		if( world.isRemote )
			return true;
		if( canUse( player ) )
			useUnlocked( player );
		else
			( (ILock)getLock().getItem() ).applyEffects( getLock(), this, player, EnumLockInteraction.OPEN );
		return true;
	}

	@Override
	public boolean receiveClientEvent( int eventID, int par )
	{
		// worldObj.playAuxSFX(1003, xCoord, yCoord, zCoord, 0);
		swing = true;
		isOpen = par == 1;
		updateLockPosition();
		worldObj.markBlockRangeForRenderUpdate( pos, pos );
		return true;
	}

	/*
	 * @Override
	 * public void updateEntity()
	 * {
	 * attachments.update();
	 * }
	 */

	@Override
	public void readFromNBT( NBTTagCompound compound )
	{
		super.readFromNBT( compound );
		isOpen = compound.getBoolean( "isOpen" );
		isMirrored = compound.getBoolean( "isMirrored" );
		orientation = EnumFacing.getFront( compound.getByte( "orientation" ) );
		if( compound.hasKey( "lock" ) )
			lockAttachment.setItem( ItemStack.loadItemStackFromNBT( compound.getCompoundTag( "lock" ) ) );
		updateLockPosition();
	}

	@Override
	public NBTTagCompound writeToNBT( NBTTagCompound compound )
	{
		super.writeToNBT( compound );
		compound.setBoolean( "isOpen", isOpen );
		compound.setBoolean( "isMirrored", isMirrored );
		compound.setByte( "orientation", (byte)orientation.ordinal() );
		if( lockAttachment.getItem() != null )
			compound.setTag( "lock", lockAttachment.getItem().writeToNBT( new NBTTagCompound() ) );
		return compound;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		final NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean( "isOpen", isOpen );
		compound.setBoolean( "isMirrored", isMirrored );
		compound.setByte( "orientation", (byte)orientation.ordinal() );
		if( lockAttachment.getItem() != null )
			compound.setTag( "lock", lockAttachment.getItem().writeToNBT( new NBTTagCompound() ) );
		return new SPacketUpdateTileEntity( pos, 0, compound );
	}

	@Override
	public void onDataPacket( NetworkManager net, SPacketUpdateTileEntity pkt )
	{
		super.onDataPacket( net, pkt );
		final NBTTagCompound compound = pkt.getNbtCompound();
		if( !compound.hasKey( "lock" ) )
			lockAttachment.setItem( null );
		else
			lockAttachment.setItem( ItemStack.loadItemStackFromNBT( compound.getCompoundTag( "lock" ) ) );
		orientation = EnumFacing.getFront( compound.getByte( "orientation" ) );
		isOpen = compound.getBoolean( "isOpen" );
		isMirrored = compound.getBoolean( "isMirrored" );
		updateLockPosition();
	}

	public boolean isPowered()
	{
		return powered;
	}

	public void setPowered( boolean powered )
	{

		if( this.powered == powered )
			return;
		this.powered = powered;

		// if (powered) worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), 10);
		// WorldUtils.notifyBlocksAround(worldObj, xCoord, yCoord, zCoord);
		// WorldUtils.notifyBlocksAround(worldObj, xCoord, yCoord + 1, zCoord);
	}

}
