package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Enums.WeaponType;
import de.tr7zw.itemnbtapi.NBTItem;

public abstract class GunItemUtil {
	
	//private static WeaponManager manager = Main.plugin.weaponManager;
	
	public static Boolean isGrenade(ItemStack item) {
		if(ItemUtil.isGGWeapon(item)) {
			if(ItemUtil.getWeaponType(item).equals(WeaponType.GRENADE)) {
				return true;
			}
		}
		return false;
	}
	
	public static Integer getRemainingShots(ItemStack gunItem) {
		Integer i = 0;
		if(ItemUtil.isGGWeapon(gunItem) && ItemUtil.getWeaponType(gunItem).equals(WeaponType.GUN)) {
			if(ItemUtil.hasKey(gunItem, "GG_RemainingShots")) {
				return ItemUtil.getInteger(gunItem, "GG_RemainingShots");
			}
		}
		return i;
	}
	public static Boolean isMatchingAmmo(Ammo ammo, ItemStack toCheck) {
		if(ItemUtil.isGGAmmo(toCheck)) {
			Ammo ammo2 = GunGamePlugin.instance.weaponManager.getAmmo(toCheck);
			if(ammo2.equals(ammo)) {
				return true;
			}
		}
		return false;
	}
	public static Boolean isMatchingGrenade(Grenade grenade, ItemStack toCheck) {
		if(ItemUtil.isGGWeapon(toCheck)) {
			if(ItemUtil.getWeaponType(toCheck).equals(WeaponType.GRENADE)) {
				Grenade gren = GunGamePlugin.instance.weaponManager.getGrenade(toCheck);
				if(gren.equals(grenade)) {
					return true;
				}
			}
		}
		return false;
	}
	public static Boolean hasPlayerAmmo(Player p, Ammo toSearch) {
		for(ItemStack item : p.getInventory()/*.getContents()*/) {
			if(isMatchingAmmo(toSearch, item)) {
				return true;
			}
		}
		/*for(ItemStack item : p.getInventory().getExtraContents()) {
			if(isMatchingAmmo(toSearch, item)) {
				return true;
			}
		}*/
		return false;
	}
	public static Integer getSlotOfAmmoStack(Player p, Ammo ammo) {
		for(int i = 0; i < p.getInventory().getContents().length; i++) {
			if(isMatchingAmmo(ammo, p.getInventory().getItem(i))) {
				return i;
			}
		}
		return null;
	}
	public static Integer getMaxAmmo(ItemStack gunItem) {
		Integer i = 0;
		Gun g = GunGamePlugin.instance.weaponManager.getGun(gunItem);
		if(g != null) {
			return g.getMaxAmmo();
		}
		return i;
	}
	public static Boolean isCoolingDown(ItemStack gunItem) {
		if(ItemUtil.isGGWeapon(gunItem) && ItemUtil.getWeaponType(gunItem).equals(WeaponType.GUN)) {
			if(ItemUtil.hasKey(gunItem, "GG_System_CoolingDown")) {
				return ItemUtil.getBoolean(gunItem, "GG_System_CoolingDown");
			}
		}
		return false;
	}
	public static Boolean isReloading(ItemStack gunItem) {
		if(ItemUtil.isGGWeapon(gunItem) && ItemUtil.getWeaponType(gunItem).equals(WeaponType.GUN)) {
			if(ItemUtil.hasKey(gunItem, "GG_Gun_Reloading")) {
				return ItemUtil.getBoolean(gunItem, "GG_Gun_Reloading");
			}
		}
		return false;
	}
	public static ItemStack setCoolingDown(ItemStack gunItem, Boolean coolingDown) {
		NBTItem nbti = new NBTItem(gunItem);
		nbti.setBoolean("GG_System_CoolingDown", coolingDown);
		
		return nbti.getItem();
	}
	public static ItemStack updateRemainingShots(ItemStack gunItem, Integer shots) {
		NBTItem nbti = new NBTItem(gunItem);
		nbti.setInteger("GG_RemainingShots", shots);
		return nbti.getItem();
		//return ItemUtil.addTags(gunItem, "GG_RemainingShots", shots);
	}
	public static ItemStack setReloading(ItemStack gunItem, Boolean reloading) {
		NBTItem nbti = new NBTItem(gunItem);
		nbti.setBoolean("GG_Gun_Reloading", reloading);
		
		return nbti.getItem();
		//return ItemUtil.addTags(gunItem, "GG_Gun_Reloading", reloading);
	}
	public static Boolean isOutOfAmmo(ItemStack gunItem) {
		if(getRemainingShots(gunItem) > 0) {
			return false;
		}
		return true;
	}
	public static Boolean isOutOfAmmoWithNextShot(ItemStack gunItem) {
		if((getRemainingShots(gunItem) -1) > 0) {
			return false;
		}
		return true;
	}
	public static Boolean readyToShoot(ItemStack gunItem) {
		/*if(isCoolingDown(gunItem) | isOutOfAmmo(gunItem) | isReloading(gunItem)) {
			return false;
		} else {
			return true;
		}*/
		if(isCoolingDown(gunItem)) {
			return false;
		}
		if(isOutOfAmmo(gunItem)) {
			return false;
		}
		if(isReloading(gunItem)) {
			return false;
		}
		return true;
	}
	public static Grenade getLoadedGrenade(ItemStack gunItem) {
		Grenade g = null;
		String s = ItemUtil.getString(gunItem, "GG_GrenadeThrower_LoadedGrenade");
		if(s != null && !s.equalsIgnoreCase("NONE")) {
			g = GunGamePlugin.instance.weaponManager.getGrenade(s);
			
		}
		return g;
	}
	public static ItemStack setLoadedGrenade(ItemStack gunItem, String grenadeName) {
		return ItemUtil.addTags(gunItem, "GG_GrenadeThrower_LoadedGrenade", grenadeName);
	}
	public static ItemStack setLoadedGrenade(ItemStack gunItem, Grenade grenadeToLoad) {
		return setLoadedGrenade(gunItem, grenadeToLoad.getGrenadeName());
	}

}
