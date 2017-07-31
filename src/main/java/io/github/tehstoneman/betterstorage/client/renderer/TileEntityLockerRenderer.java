package io.github.tehstoneman.betterstorage.client.renderer;

import org.lwjgl.opengl.GL11;

import io.github.tehstoneman.betterstorage.common.block.BlockLockable;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLocker;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class TileEntityLockerRenderer extends TileEntitySpecialRenderer< TileEntityLocker >
{
	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityAt( TileEntityLocker locker, double x, double y, double z, float partialTicks, int destroyStage )
	{
		if( !locker.isMain() )
			return;
		GlStateManager.pushAttrib();
		GlStateManager.pushMatrix();

		GlStateManager.translate( x, y, z );
		GlStateManager.disableRescaleNormal();

		final BlockPos pos = locker.getPos();
		final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache( locker.getWorld(), pos );
		IBlockState state = world.getBlockState( pos );
		if( state.getPropertyKeys().contains( BlockLockable.MATERIAL ) )
			state = state.withProperty( BlockLockable.MATERIAL, locker.getMaterial() );
		state = state.withProperty( BlockLockable.CONNECTED, locker.isConnected() );

		if( blockRenderer == null )
			blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

		GlStateManager.translate( 0.5, 0.0, 0.5 );
		final EnumFacing facing = state.getValue( BlockHorizontal.FACING );
		GlStateManager.rotate( 180 - facing.getHorizontalAngle(), 0, 1, 0 );
		GlStateManager.translate( -0.5, 0.0, -0.5 );

		renderBase( locker, partialTicks, destroyStage, state );
		renderDoor( locker, partialTicks, destroyStage, state );
		// renderLock( chest );

		GlStateManager.popMatrix();
		GlStateManager.popAttrib();
	}

	private void renderBase( TileEntityLocker locker, float partialTicks, int destroyStage, IBlockState state )
	{
		GlStateManager.pushMatrix();

		RenderHelper.disableStandardItemLighting();
		bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
		if( Minecraft.isAmbientOcclusionEnabled() )
			GlStateManager.shadeModel( GL11.GL_SMOOTH );
		else
			GlStateManager.shadeModel( GL11.GL_FLAT );

		final World world = locker.getWorld();
		GlStateManager.translate( -locker.getPos().getX(), -locker.getPos().getY(), -locker.getPos().getZ() );

		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
		final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState( state.withProperty( Properties.StaticProperty, true ) );
		blockRenderer.getBlockModelRenderer().renderModel( world, model, state, locker.getPos(), buffer, false );
		tessellator.draw();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	private void renderDoor( TileEntityLocker locker, float partialTicks, int destroyStage, IBlockState state )
	{
		GlStateManager.pushMatrix();

		RenderHelper.disableStandardItemLighting();
		bindTexture( TextureMap.LOCATION_BLOCKS_TEXTURE );
		if( Minecraft.isAmbientOcclusionEnabled() )
			GlStateManager.shadeModel( GL11.GL_SMOOTH );
		else
			GlStateManager.shadeModel( GL11.GL_FLAT );

		float openAngle = locker.prevLidAngle + ( locker.lidAngle - locker.prevLidAngle ) * partialTicks;
		openAngle = 1.0F - openAngle;
		openAngle = 1.0F - openAngle * openAngle * openAngle;
		openAngle = openAngle * 90;

		final boolean left = state.getValue( BlockDoor.HINGE ) == EnumHingePosition.LEFT;

		GlStateManager.translate( left ? 15F / 16F : 1F / 16F, 0, 1.0 / 16.0 );
		GlStateManager.rotate( left ? -openAngle : openAngle, 0, 1, 0 );
		GlStateManager.translate( left ? -15F / 16F : -1F / 16F, 0, -1.0 / 16.0 );

		final World world = locker.getWorld();
		GlStateManager.translate( -locker.getPos().getX(), -locker.getPos().getY(), -locker.getPos().getZ() );

		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer VertexBuffer = tessellator.getBuffer();
		VertexBuffer.begin( GL11.GL_QUADS, DefaultVertexFormats.BLOCK );
		final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState( state.withProperty( Properties.StaticProperty, false ) );
		blockRenderer.getBlockModelRenderer().renderModel( world, model, state, locker.getPos(), tessellator.getBuffer(), false );
		tessellator.draw();

		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	/** Renders attached lock on chest. Adapted from vanilla item frame **/
	private void renderItem( TileEntityLocker locker )
	{
		final ItemStack itemstack = locker.getLock();

		if( itemstack != null )
		{
			final EntityItem entityitem = new EntityItem( locker.getWorld(), 0.0D, 0.0D, 0.0D, itemstack );
			final Item item = entityitem.getEntityItem().getItem();
			// entityitem.getEntityItem().stackSize = 1;
			entityitem.hoverStart = 0.0F;
			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();

			final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

			GlStateManager.rotate( 180.0F, 0.0F, 1.0F, 0.0F );
			final double x = 1.0 / 16.0 * -3.5;
			final double y = 1.0 / 16.0 * ( locker.isConnected() ? 13.0 : 6.0 );
			final double z = 1.0 / 16.0 * -0.5;
			GlStateManager.translate( x, y, z );
			GlStateManager.scale( 0.5, 0.5, 0.5 );

			GlStateManager.pushAttrib();
			RenderHelper.enableStandardItemLighting();
			itemRenderer.renderItem( entityitem.getEntityItem(), ItemCameraTransforms.TransformType.FIXED );
			RenderHelper.disableStandardItemLighting();
			GlStateManager.popAttrib();

			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}
}
