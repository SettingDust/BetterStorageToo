package io.github.tehstoneman.betterstorage.client.model;

import org.lwjgl.opengl.GL11;

import io.github.tehstoneman.betterstorage.utils.RenderUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelRendererPotion extends ModelRenderer
{
	public ItemStack stack = null;

	public ModelRendererPotion( ModelBase modelBase, boolean right )
	{
		super( modelBase );
		addChild( new Potion( modelBase ) );

		setRotationPoint( right ? -5.5F : 5.5F, -7, 1 );
		rotateAngleX = (float)Math.PI;
		rotateAngleY = (float)Math.PI / ( right ? -2 : 2 );
	}

	private class Potion extends ModelRenderer
	{
		public Potion( ModelBase modelBase )
		{
			super( modelBase );
		}

		@Override
		@SideOnly( Side.CLIENT )
		public void render( float par1 )
		{
			if( stack == null )
				return;
			GL11.glPushMatrix();
			GL11.glPushAttrib( GL11.GL_ALL_ATTRIB_BITS );
			GL11.glScalef( 0.5F, 0.5F, 0.5F );
			RenderUtils.renderItemIn3d( stack );
			GL11.glPopAttrib();
			GL11.glPopMatrix();
		}
	}
}
