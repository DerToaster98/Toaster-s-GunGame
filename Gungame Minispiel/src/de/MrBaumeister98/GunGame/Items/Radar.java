package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class Radar {
	
	public static ItemStack radar() {
		ItemStack radarItem = new ItemStack(Material.COMPASS);
		ItemMeta meta = radarItem.getItemMeta();
		
		meta.setDisplayName(LangUtil.buildItemName("Radar"));
		List<String> lore = new ArrayList<String>();
		for(String s : LangUtil.buildItemLore("Radar")) {
			lore.add(s);
		}
				
		meta.setLore(lore);
		
		radarItem.setItemMeta(meta);
		
		radarItem = ItemUtil.addTags(radarItem, "GunGame_Item", "Radar");
		radarItem = ItemUtil.setGunGameItem(radarItem);
		
		return radarItem;
	}

}
