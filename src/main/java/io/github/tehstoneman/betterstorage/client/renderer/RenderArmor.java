package io.github.tehstoneman.betterstorage.client.renderer;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class RenderArmor extends RenderPlayer
{
	public RenderArmor( RenderManager renderManager )
	{
		super( renderManager );
		// setRenderManager(renderManager);
	}

	/*
	 * @Override
	 * protected float handleRotationFloat(EntityLivingBase entity, float partialTicks) { return partialTicks; }
	 */
}
