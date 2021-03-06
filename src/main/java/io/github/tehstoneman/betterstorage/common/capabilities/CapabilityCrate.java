package io.github.tehstoneman.betterstorage.common.capabilities;

import io.github.tehstoneman.betterstorage.ModInfo;
import io.github.tehstoneman.betterstorage.api.ICrateStorage;
import io.github.tehstoneman.betterstorage.common.world.storage.CrateStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityCrate
{
	@CapabilityInject( ICrateStorage.class )
	public static final Capability< ICrateStorage >	CRATE_PILE_CAPABILITY	= null;

	public static ResourceLocation					CAPABILITY_RESOURCE		= new ResourceLocation( ModInfo.MOD_ID, "crate_pile" );

	public static void register()
	{
		CapabilityManager.INSTANCE.register( ICrateStorage.class, new Capability.IStorage< ICrateStorage >()
		{
			@Override
			public INBT writeNBT( Capability< ICrateStorage > capability, ICrateStorage instance, Direction side )
			{
				return instance.serializeNBT();
			}

			@Override
			public void readNBT( Capability< ICrateStorage > capability, ICrateStorage instance, Direction side, INBT nbt )
			{
				if( !( instance instanceof CrateStorage ) )
					throw new IllegalArgumentException( "Can not deserialize to an instance that isn't the default implementation" );
				instance.deserializeNBT( (CompoundNBT)nbt );
			}
		}, CrateStorage::new );
	}

	public static class Provider implements ICapabilitySerializable< CompoundNBT >
	{
		private final Capability< ICrateStorage >	capability;
		private final LazyOptional					capabilityHandler;

		public Provider( Capability< ICrateStorage > capability )
		{
			this.capability = capability;
			capabilityHandler = LazyOptional.of( () -> this.capability );
		}

		@Override
		public <T> LazyOptional< T > getCapability( Capability< T > capability, Direction side )
		{
			return CRATE_PILE_CAPABILITY.orEmpty( capability, capabilityHandler );
			// return LazyOptional.empty();
		}

		@Override
		public CompoundNBT serializeNBT()
		{
			return (CompoundNBT)capability.writeNBT( capability.getDefaultInstance(), null );
		}

		@Override
		public void deserializeNBT( CompoundNBT nbt )
		{
			capability.readNBT( capability.getDefaultInstance(), null, nbt );
		}
	}
}
