package io.github.tehstoneman.betterstorage.network;

public class ChannelHandler // extends SimpleNetworkWrapper
{

	public ChannelHandler()
	{
		// super( ModInfo.modId );
		// register(1, Side.CLIENT, PacketBackpackTeleport.class);
		// register(2, Side.CLIENT, PacketBackpackHasItems.class);
		// register(3, Side.CLIENT, PacketBackpackIsOpen.class);
		// register(4, Side.SERVER, PacketBackpackOpen.class);
		// register(5, Side.CLIENT, PacketBackpackStack.class);
		// register( 6, Side.SERVER, PacketDrinkingHelmetUse.class );
		// register( 7, Side.SERVER, PacketLockHit.class );
		// register( 8, Side.CLIENT, PacketSyncSetting.class );
		// register( 9, Side.CLIENT, PacketPresentOpen.class );
	}

	/*
	 * public <T extends IMessage & IMessageHandler< T, IMessage >> void register( int id, Side receivingSide, Class< T > messageClass )
	 * {
	 * registerMessage( messageClass, messageClass, id, receivingSide );
	 * }
	 */

	// Sending packets

	/*
	 * public void sendTo( IMessage message, EntityPlayer player )
	 * {
	 * sendTo( message, (EntityPlayerMP)player );
	 * }
	 */

	/*
	 * public void sendToAllAround(IMessage message, World world, double x, double y, double z, double distance) {
	 * sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, distance));
	 * }
	 */

	/*
	 * public void sendToAllAround( IMessage message, World world, double x, double y, double z, double distance, EntityPlayer except )
	 * {
	 * for( final EntityPlayer player : world.playerEntities )
	 * {
	 * if( player == except )
	 * continue;
	 * final double dx = x - player.posX;
	 * final double dy = y - player.posY;
	 * final double dz = z - player.posZ;
	 * if( dx * dx + dy * dy + dz * dz < distance * distance )
	 * sendTo( message, player );
	 * }
	 * }
	 */

	/** Sends a packet to everyone tracking an entity. */
	/*
	 * public void sendToAllTracking(IMessage message, Entity entity) {
	 * ((WorldServer)entity.worldObj).getEntityTracker().func_151247_a(entity, getPacketFrom(message));
	 * }
	 */

	/**
	 * Sends a packet to everyone tracking an entity,
	 * including the entity itself if it's a player.
	 */
	/*
	 * public void sendToAndAllTracking(IMessage message, Entity entity) {
	 * sendToAllTracking(message, entity);
	 * if (entity instanceof EntityPlayer)
	 * sendTo(message, (EntityPlayer)entity);
	 * }
	 */
}
