package io.github.tehstoneman.betterstorage.client.gui;

import io.github.tehstoneman.betterstorage.container.ContainerBetterStorage;
import io.github.tehstoneman.betterstorage.misc.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class GuiBetterStorage extends GuiContainer
{
	public final ContainerBetterStorage	container;
	public final String					title;

	private final int					columns;
	private final int					rows;

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return rows;
	}

	public GuiBetterStorage( ContainerBetterStorage container )
	{
		super( container );

		this.container = container;
		final IInventory inv = container.inventory;
		title = inv.getName();
		columns = container.getColumns();
		rows = container.getRows();

		xSize = 14 + columns * 18;
		ySize = container.getHeight();

		container.setUpdateGui( this );
	}

	public GuiBetterStorage( EntityPlayer player, int columns, int rows, IInventory inventory )
	{
		this( new ContainerBetterStorage( player, inventory, columns, rows ) );
	}

	public GuiBetterStorage( EntityPlayer player, int columns, int rows, String title, boolean localized )
	{
		this( player, columns, rows, new InventoryBasic( title, localized, columns * rows ) );
	}

	public GuiBetterStorage( EntityPlayer player, int columns, int rows, String title )
	{
		this( player, columns, rows, title, false );
	}

	protected ResourceLocation getResource()
	{
		if( columns <= 9 )
			return new ResourceLocation( "textures/gui/container/generic_54.png" );
		else
			return Resources.containerReinforcedChest;
	}

	protected int getHeight()
	{
		return 223;
	}

	protected int getTextureWidth()
	{
		return 256;
	}

	protected int getTextureHeight()
	{
		return 256;
	}

	public void update( int par1, int par2 )
	{}

	@Override
	protected void drawGuiContainerForegroundLayer( int par1, int par2 )
	{
		fontRendererObj.drawString( I18n.format( title ), 8, 6, 0x404040 );
		fontRendererObj.drawString( I18n.format( "container.inventory" ), 8 + ( xSize - 176 ) / 2, ySize - 94, 0x404040 );
	}

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTicks, int x, int y )
	{
		mc.renderEngine.bindTexture( getResource() );
		GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );

		final int m = 107;
		final int m1 = ySize - m;
		final int m2 = getHeight() - m;

		drawTexturedModalRect( guiLeft, guiTop, 0, 0, xSize, m1 );
		drawTexturedModalRect( guiLeft, guiTop + m1, 0, m2, xSize, m );
	}
}
