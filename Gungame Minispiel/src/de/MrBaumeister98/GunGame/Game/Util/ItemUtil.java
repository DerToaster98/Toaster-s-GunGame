package de.MrBaumeister98.GunGame.Game.Util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import de.MrBaumeister98.GunGame.GunEngine.Enums.EWeaponType;
import de.tr7zw.itemnbtapi.NBTItem;
import de.tr7zw.itemnbtapi.NBTList;
import de.tr7zw.itemnbtapi.NBTType;

public class ItemUtil {
	
	public static Boolean isGunGameItem(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GunGame_isGGAItem") != null && nbti.getBoolean("GunGame_isGGAItem") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean isGGWeapon(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GGWeapon") != null && nbti.getBoolean("GGWeapon") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean isGGAirstrike(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GGAirStrike") != null && nbti.getBoolean("GGAirStrike") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean isGGLandmine(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GGLandmine") != null && nbti.getBoolean("GGLandmine") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean isGGTurret(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GG_Turret") != null && nbti.getBoolean("GG_Turret") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean isGGTank(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GG_Tank") != null && nbti.getBoolean("GG_Tank") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean belongsToWeaponMenu(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GGWeaponMenu") != null && nbti.getBoolean("GGWeaponMenu") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static Boolean isGGAmmo(ItemStack item) {
		if(item != null) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getBoolean("GGAmmo") != null && nbti.getBoolean("GGAmmo") == true) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static EWeaponType getWeaponType(ItemStack item) {
		EWeaponType type = null;
		if(isGGWeapon(item)) {
			if(hasKey(item, "GGWeaponType")) {
				String typeS = getString(item, "GGWeaponType");
				type = EWeaponType.valueOf(typeS);
			}
		}
		return type;
	}
	
	public static ItemStack setGunGameItem(ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		nbti.setBoolean("GunGame_isGGAItem", true);
		return nbti.getItem();
	}
	public static ItemStack setWeaponMenuItem(ItemStack item) {
		NBTItem nbti = new NBTItem(item);
		nbti.setBoolean("GunGame_isGGAItem", true);
		nbti.setBoolean("GGWeaponMenu", true);
		return nbti.getItem();
	}
	public static ItemStack addTags(ItemStack item, String[] GGAString, String[] tag) {
		NBTItem nbti = new NBTItem(item);
		if(GGAString.length == tag.length) {
			for(int i = 0; i < GGAString.length; i++) {
				nbti.setString(GGAString[i], tag[i]);
			}
			return nbti.getItem();
		} else {
			return null;
		}
	}
	public static ItemStack addTags(ItemStack item, String GGAString, List<String> tags) {
		NBTItem nbti = new NBTItem(item);
		//if(GGAString.length == tag.length) {
			NBTList destroys = nbti.getList(GGAString, NBTType.NBTTagString);
			for(String s : tags) {
				destroys.addString(s);
			}
			return nbti.getItem();
		/*} else {
			return null;
		}*/
	}
	public static ItemStack addTags(ItemStack item, String GGAString, String tag) {
		NBTItem nbti = new NBTItem(item);
		//if(GGAString.length == tag.length) {
			//for(int i = 0; i < GGAString.length; i++) {
				nbti.setString(GGAString, tag);
			//}
			return nbti.getItem();
		//} else {
			//return null;
		//}
	}
	public static ItemStack addTags(ItemStack item, String GGAString, Integer tag) {
		NBTItem nbti = new NBTItem(item);
		//if(GGAString.length == tag.length) {
			//for(int i = 0; i < GGAString.length; i++) {
				nbti.setInteger(GGAString, tag);
			//}
			return nbti.getItem();
		//} else {
			//return null;
		//}
	}
	public static ItemStack addTags(ItemStack item, String[] GGAString, Boolean[] tag) {
		NBTItem nbti = new NBTItem(item);
		if(GGAString.length == tag.length) {
			for(int i = 0; i < GGAString.length; i++) {
				nbti.setBoolean(GGAString[i], tag[i]);
			}
			return nbti.getItem();
		} else {
			return null;
		}
	}
	public static ItemStack addTags(ItemStack item, String GGAString, Boolean tag) {
		NBTItem nbti = new NBTItem(item);
		//if(GGAString.length == tag.length) {
			//for(int i = 0; i < GGAString.length; i++) {
				nbti.setBoolean(GGAString, tag);
		//	}
			return nbti.getItem();
		//} else {
			//return null;
		//}
	}
	public static Boolean hasKey(ItemStack item, String key) {
		if(item != null && !item.getType().equals(Material.AIR)) {
			NBTItem nbti = new NBTItem(item);
			Boolean has = nbti.hasKey(key); 
			return has;
		}
		return false;
	}
	public static String getString(ItemStack item, String tag) {
		NBTItem nbti = new NBTItem(item);
		return nbti.getString(tag);
	}
	public static Boolean getBoolean(ItemStack item, String tag) {
		NBTItem nbti = new NBTItem(item);
		return nbti.getBoolean(tag);
	}
	public static Integer getInteger(ItemStack item, String tag) {
		NBTItem nbti = new NBTItem(item);
		return nbti.getInteger(tag);
	}
	public static Boolean hasTag(ItemStack item, String key, String tag) {
		if(hasKey(item, key)) {
			NBTItem nbti = new NBTItem(item);
			if(nbti.getString(key).equals(tag)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static ItemStack removeTag(ItemStack bought, String string) {
		NBTItem nbti = new NBTItem(bought);
		nbti.removeKey(string);
		return nbti.getItem();
	}

}
