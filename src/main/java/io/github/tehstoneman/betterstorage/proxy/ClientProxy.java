package io.github.tehstoneman.betterstorage.proxy;

import io.github.tehstoneman.betterstorage.client.gui.GuiCardboardBox;
import io.github.tehstoneman.betterstorage.client.gui.GuiCrate;
import io.github.tehstoneman.betterstorage.client.gui.GuiKeyring;
import io.github.tehstoneman.betterstorage.client.gui.GuiLocker;
import io.github.tehstoneman.betterstorage.client.gui.GuiReinforcedChest;
import io.github.tehstoneman.betterstorage.client.gui.GuiReinforcedLocker;
import io.github.tehstoneman.betterstorage.client.renderer.TileEntityLockerRenderer;
import io.github.tehstoneman.betterstorage.client.renderer.TileEntityReinforcedChestRenderer;
import io.github.tehstoneman.betterstorage.common.block.BetterStorageBlocks;
import io.github.tehstoneman.betterstorage.common.inventory.BetterStorageContainerTypes;
import io.github.tehstoneman.betterstorage.common.item.BetterStorageItems;
import io.github.tehstoneman.betterstorage.common.item.cardboard.CardboardColor;
import io.github.tehstoneman.betterstorage.common.item.locking.KeyColor;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityLocker;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityReinforcedChest;
import io.github.tehstoneman.betterstorage.common.tileentity.TileEntityReinforcedLocker;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ClientProxy implements IProxy
{
	// public static final Map< Class< ? extends TileEntity >, BetterStorageRenderingHandler > renderingHandlers = new HashMap<>();

	@Override
	public void setup( FMLCommonSetupEvent event )
	{
		// MinecraftForge.EVENT_BUS.register( new ClientEventHandler() );

		// OBJLoader.INSTANCE.addDomain( ModInfo.modId );
		DeferredWorkQueue.runLater( () ->
		{
			ClientRegistry.bindTileEntitySpecialRenderer( TileEntityReinforcedChest.class, new TileEntityReinforcedChestRenderer() );
			ClientRegistry.bindTileEntitySpecialRenderer( TileEntityLocker.class, new TileEntityLockerRenderer() );
			ClientRegistry.bindTileEntitySpecialRenderer( TileEntityReinforcedLocker.class, new TileEntityLockerRenderer() );
			ClientRegistry.bindTileEntitySpecialRenderer( TileEntityReinforcedChest.class, new TileEntityReinforcedChestRenderer() );
		} );

		DeferredWorkQueue.runLater( () ->
		{
			ScreenManager.registerFactory( BetterStorageContainerTypes.LOCKER, GuiLocker::new );
			ScreenManager.registerFactory( BetterStorageContainerTypes.CRATE, GuiCrate::new );
			ScreenManager.registerFactory( BetterStorageContainerTypes.REINFORCED_CHEST, GuiReinforcedChest::new );
			ScreenManager.registerFactory( BetterStorageContainerTypes.REINFORCED_LOCKER, GuiReinforcedLocker::new );
			ScreenManager.registerFactory( BetterStorageContainerTypes.KEYRING, GuiKeyring::new );
			ScreenManager.registerFactory( BetterStorageContainerTypes.CARDBOARD_BOX, GuiCardboardBox::new );
		} );

		DeferredWorkQueue.runLater( () ->
		{
			Minecraft.getInstance().getItemColors().register( new KeyColor(), BetterStorageItems.KEY, BetterStorageItems.LOCK );
			Minecraft.getInstance().getItemColors().register( new CardboardColor(), BetterStorageItems.CARDBOARD_AXE,
					BetterStorageItems.CARDBOARD_BOOTS, BetterStorageItems.CARDBOARD_CHESTPLATE, BetterStorageItems.CARDBOARD_HELMET,
					BetterStorageItems.CARDBOARD_HOE, BetterStorageItems.CARDBOARD_LEGGINGS, BetterStorageItems.CARDBOARD_PICKAXE,
					BetterStorageItems.CARDBOARD_SHOVEL, BetterStorageItems.CARDBOARD_SWORD, BetterStorageBlocks.CARDBOARD_BOX );
			Minecraft.getInstance().getBlockColors().register( new CardboardColor(), BetterStorageBlocks.CARDBOARD_BOX );
		} );
	}

	@Override
	public World getClientWorld()
	{
		return Minecraft.getInstance().world;
	}
	/*
	 * @Override
	 * public void initialize()
	 * {
	 * super.initialize();
	 *
	 * // new KeyBindingHandler();
	 *
	 * registerRenderers();
	 * BetterStorageColorHandler.registerColorHandlers();
	 * }
	 */

	/*@Override
	public void postInit()
	{
		super.postInit();

		//@formatter:off
		Minecraft.getInstance().getItemColors().registerItemColorHandler( new KeyColor(),
				BetterStorageItems.KEY,
				BetterStorageItems.LOCK );
		Minecraft.getInstance().getItemColors().registerItemColorHandler( new CardboardColor(),
				Item.getItemFromBlock( BetterStorageBlocks.CARDBOARD_BOX ),
				BetterStorageItems.CARDBOARD_AXE,
				BetterStorageItems.CARDBOARD_BOOTS,
				BetterStorageItems.CARDBOARD_CHESTPLATE,
				BetterStorageItems.CARDBOARD_HELMET,
				BetterStorageItems.CARDBOARD_HOE,
				BetterStorageItems.CARDBOARD_LEGGINGS,
				BetterStorageItems.CARDBOARD_PICKAXE,
				BetterStorageItems.CARDBOARD_SHOVEL,
				BetterStorageItems.CARDBOARD_SWORD );
		//@formatter:on
	}*/

	/*
	 * private void registerRenderers()
	 * {
	 * // RenderingRegistry.registerEntityRenderingHandler(EntityFrienderman.class, new RenderFrienderman());
	 * // RenderingRegistry.registerEntityRenderingHandler(EntityCluckington.class, new RenderChicken(new ModelCluckington(), 0.4F));
	 *
	 * // ClientRegistry.bindTileEntitySpecialRenderer( TileEntityReinforcedChest.class, new TileEntityReinforcedChestRenderer() );
	 * // ClientRegistry.bindTileEntitySpecialRenderer( TileEntityReinforcedLocker.class, new TileEntityLockerRenderer() );
	 * // ClientRegistry.bindTileEntitySpecialRenderer( TileEntityLockableDoor.class, new TileEntityLockableDoorRenderer() );
	 * // ClientRegistry.bindTileEntitySpecialRenderer( TileEntityPresent.class, new TileEntityPresentRenderer() );
	 * // Addon.registerRenderersAll();
	 * }
	 */
}
