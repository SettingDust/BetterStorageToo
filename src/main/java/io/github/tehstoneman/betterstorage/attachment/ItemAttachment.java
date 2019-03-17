package io.github.tehstoneman.betterstorage.attachment;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class ItemAttachment extends Attachment
{

	protected ItemStack	item		= ItemStack.EMPTY;
	public float		scale		= 1.0F;
	public float		scaleDepth	= 1.0F;

	public ItemStack getItem()
	{
		return item;
	}

	public void setItem( ItemStack item )
	{
		this.item = item;
	}

	public ItemAttachment( TileEntity tileEntity, int subId )
	{
		super( tileEntity, subId );
	}

	public void setScale( float scale, float scaleDepth )
	{
		this.scale = scale;
		this.scaleDepth = scaleDepth;
	}

	@Override
	// @SideOnly( Side.CLIENT )
	public IAttachmentRenderer getRenderer()
	{
		return ItemAttachmentRenderer.instance;
	}

}
