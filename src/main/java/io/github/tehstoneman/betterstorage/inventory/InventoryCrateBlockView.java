package io.github.tehstoneman.betterstorage.inventory;

import java.util.List;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.crate.ICrateWatcher;
import io.github.tehstoneman.betterstorage.config.GlobalConfig;
import io.github.tehstoneman.betterstorage.misc.ItemIdentifier;
import io.github.tehstoneman.betterstorage.tile.crate.CratePileData;
import io.github.tehstoneman.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

/** An inventory interface built for machines accessing crate piles. */
public class InventoryCrateBlockView extends InventoryBetterStorage implements ICrateWatcher
{

	private static final int	numStacksStored	= 4;

	private final CratePileData	data;

	private final ItemStack[]	originalStacks	= new ItemStack[numStacksStored];
	private final ItemStack[]	exposedStacks	= new ItemStack[numStacksStored];

	/** If the crate contents were changed outside of this block view. */
	private boolean				changed			= true;
	/** If the block view is currently modifying items, so changed will not be set. */
	private boolean				isModifying		= false;

	/** If the crate was accessed by something. Resets every update. */
	private boolean				accessed		= false;

	public InventoryCrateBlockView( CratePileData data )
	{
		super( ModInfo.containerCrate );
		this.data = data;
		data.addWatcher( this );
	}

	@Override
	public int getSizeInventory()
	{
		return numStacksStored + 1;
	}

	@Override
	public boolean isItemValidForSlot( int slot, ItemStack stack )
	{
		return slot > 0 || data.getSpaceForItem( stack ) >= stack.stackSize;
	}

	@Override
	public ItemStack getStackInSlot( int slot )
	{
		access();
		if( slot <= 0 || slot >= getSizeInventory() )
			return null;
		return exposedStacks[slot - 1];
	}

	@Override
	public void setInventorySlotContents( int slot, ItemStack stack )
	{
		access();
		if( slot < 0 || slot >= getSizeInventory() )
			return;
		ItemStack oldStack = null;
		if( slot > 0 )
		{
			oldStack = originalStacks[slot - 1];
			exposedStacks[slot - 1] = stack;
			originalStacks[slot - 1] = ItemStack.copyItemStack( stack );
		}
		isModifying = true;
		if( oldStack != null )
		{
			// If the two stacks match, just add/remove the difference.
			if( StackUtils.matches( oldStack, stack ) )
			{
				final int count = stack.stackSize - oldStack.stackSize;
				if( count > 0 )
					data.addItems( StackUtils.copyStack( stack, count ) );
				else
					if( count < 0 )
						data.removeItems( StackUtils.copyStack( stack, -count ) );
				isModifying = false;
				return;
			}
			data.removeItems( oldStack );
		}
		data.addItems( stack );
		isModifying = false;
	}

	@Override
	public ItemStack decrStackSize( int slot, int amount )
	{
		final ItemStack stack = getStackInSlot( slot );
		if( stack == null )
			return null;
		amount = Math.min( amount, stack.stackSize );
		if( stack.stackSize <= amount )
			originalStacks[slot - 1] = exposedStacks[slot - 1] = null;
		else
			originalStacks[slot - 1].stackSize = exposedStacks[slot - 1].stackSize -= amount;
		isModifying = true;
		final ItemStack result = data.removeItems( new ItemIdentifier( stack ), amount );
		isModifying = false;
		return result;
	}

	@Override
	public void markDirty()
	{
		for( int i = 0; i < numStacksStored; i++ )
			if( !ItemStack.areItemStacksEqual( originalStacks[i], exposedStacks[i] ) )
				setInventorySlotContents( i + 1, exposedStacks[i] );
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer player )
	{
		return true;
	}

	/*
	 * @Override
	 * public void openInventory() { }
	 * 
	 * @Override
	 * public void closeInventory() { }
	 */

	public void onUpdate()
	{
		if( !GlobalConfig.enableCrateInventoryInterfaceSetting.getValue() || !accessed )
			return;
		accessed = false;
		// Cause new stacks to be picked the
		// next time something is accessed.
		changed = true;
	}

	private void access()
	{
		accessed = true;

		if( changed )
		{
			// Pick a new set of random stacks from the crate.
			final List< ItemStack > picked = data.getContents().getRandomStacks( numStacksStored );
			for( int i = 0; i < numStacksStored; i++ )
			{
				exposedStacks[i] = i < picked.size() ? picked.get( i ) : null;
				originalStacks[i] = ItemStack.copyItemStack( exposedStacks[i] );
			}
			changed = false;
		}
	}

	@Override
	public void onCrateItemsModified( ItemStack stack )
	{
		if( !isModifying )
			changed = true;
	}

	@Override
	public ItemStack removeStackFromSlot( int index )
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openInventory( EntityPlayer player )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void closeInventory( EntityPlayer player )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getField( int id )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField( int id, int value )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getFieldCount()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ITextComponent getDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
