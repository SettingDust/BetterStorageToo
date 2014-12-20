package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.lock.EnumLockInteraction;
import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.mcft.copy.betterstorage.api.lock.ILockable;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.mcft.copy.betterstorage.network.packet.PacketLockHit;
import net.mcft.copy.betterstorage.utils.MathUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LockAttachment extends ItemAttachment {
	
	public int hit = 0;
	public int breakProgress = 0;
	
	public float wiggle = 0;
	public float wiggleStrength = 0.0F;
	
	public LockAttachment(TileEntity tileEntity, int subId) {
		super(tileEntity, subId);
		if (!(tileEntity instanceof ILockable))
			throw new IllegalArgumentException("tileEntity must be ILockable.");
	}
	
	@Override
	public boolean boxVisible(EntityPlayer player) {
		ItemStack holding = ((player != null) ? player.getCurrentEquippedItem() : null);
		return ((item != null) || StackUtils.isLock(holding));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IAttachmentRenderer getRenderer() { return LockAttachmentRenderer.instance; }
	
	@Override
	public void update() {
		hit = Math.max(-20, hit - 1);
		if (hit <= -20) breakProgress = Math.max(0, breakProgress - 1);
		if (tileEntity.getWorld().isRemote) {
			wiggle++;
			wiggleStrength = Math.max(0.0F, wiggleStrength * 0.9F - 0.1F);
		}
	}
	
	@Override
	public boolean interact(EntityPlayer player, EnumAttachmentInteraction type) {
		ItemStack holding = player.getCurrentEquippedItem();
		return ((type == EnumAttachmentInteraction.attack)
				? attack(player, holding)
				: use(player, holding));
	}
	
	@Override
	public ItemStack pick() {
		if (item == null) return null;
		ItemStack key = new ItemStack(BetterStorageItems.key);
		ItemBetterStorage.setID(key, ItemBetterStorage.getID(item));
		int color = ItemBetterStorage.getColor(item);
		if (color >= 0) ItemBetterStorage.setColor(key, color);
		int fullColor = ItemBetterStorage.getFullColor(item);
		if (fullColor >= 0) ItemBetterStorage.setFullColor(key, fullColor);
		return key;
	}
	
	private boolean attack(EntityPlayer player, ItemStack holding) {
		ILockable lockable = (ILockable)tileEntity;
		ItemStack lock = lockable.getLock();
		if (lock == null) return false;
		
		boolean canHurt = ((hit <= 0) && canHurtLock(holding)); 
		if (canHurt) {
			holding.damageItem(2, player);
			if (holding.stackSize <= 0)
				player.destroyCurrentEquippedItem();
		}
		if (!player.worldObj.isRemote) {
			if (canHurt) {
				hit = 10;
				
				int damage = (int)((AttributeModifier)holding.getAttributeModifiers().get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName()).iterator().next()).getAmount();
				int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantment.field_180314_l.effectId, holding); //sharpness
				int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, holding);
				breakProgress += Math.min(damage, 10) / 2 + Math.min(Math.max(sharpness, efficiency), 5);
				
				int persistance = BetterStorageEnchantment.getLevel(lock, "persistance");
				if (breakProgress > 100 * (1 + persistance)) {
					int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, lock);
					lock.setItemDamage(lock.getItemDamage() + 10 / (1 + unbreaking));
					if (lock.getItemDamage() < lock.getMaxDamage()) {
						EntityItem item = WorldUtils.spawnItem(tileEntity.getWorld(), MathUtils.getCenter(getHighlightBox()), lock);
					}
					lockable.setLock(null);
					breakProgress = 0;
				}
				
				((ILock)lock.getItem()).applyEffects(lock, lockable, player, EnumLockInteraction.ATTACK);
			}
			BetterStorage.networkChannel.sendToAllAround(
					new PacketLockHit(tileEntity.getPos(), canHurt),
					tileEntity.getWorld(), tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), 32);
		} else hit(canHurt);
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	public void hit(boolean damage) {
		wiggleStrength = Math.min(20.0F, wiggleStrength + 12.0F);
		if (damage) {
			hit = 10;
			WorldUtils.playSound(tileEntity.getWorld(), MathUtils.getCenter(getHighlightBox()), "random.break", 0.5F, 2.5F, false);
		}
	}
	
	private boolean use(EntityPlayer player, ItemStack holding) {
		if (player.worldObj.isRemote) return false;
		
		ILockable lockable = (ILockable)tileEntity;
		ItemStack lock = lockable.getLock();
		
		if (lock == null) {
			if (StackUtils.isLock(holding) && lockable.isLockValid(holding)) {
				lockable.setLock(holding);
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				return true;
			}
		} else if (StackUtils.isKey(holding)) {
			IKey keyType = (IKey)holding.getItem();
			ILock lockType = (ILock)lock.getItem();
			
			boolean success = keyType.unlock(holding, lock, true);
			lockType.onUnlock(lock, holding, lockable, player, success);
			if (!success) return true;
			
			if (player.isSneaking()) {
				EntityItem item = WorldUtils.spawnItem(player.worldObj, MathUtils.getCenter(getHighlightBox()), lock);
				lockable.setLock(null);
			} else lockable.useUnlocked(player);
			
			return true;
		}
		
		return false;
	}
	
	private boolean canHurtLock(ItemStack stack) {
		if ((stack == null) || !BetterStorage.globalConfig.getBoolean(
				GlobalConfig.lockBreakable)) return false;
		Item item = stack.getItem();
		return ((item instanceof ItemSword) ||
		        (item instanceof ItemPickaxe) ||
		        (item instanceof ItemAxe));
	}
	
}
