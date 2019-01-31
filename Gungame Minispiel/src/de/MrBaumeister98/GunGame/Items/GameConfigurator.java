package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class GameConfigurator implements Listener {
	
	public static ItemStack configurator() {
		ItemStack itm = null;
		if(GunGamePlugin.instance.serverPre113) {
			itm = new ItemStack(Material.valueOf("COMMAND"), 1);
		} else {
			itm = new ItemStack(Material.COMMAND_BLOCK, 1);
		}
		ItemMeta meta = itm.getItemMeta();
		
		meta.setDisplayName(LangUtil.buildItemName("GameConfigurator"));
		List<String> lore = new ArrayList<String>();
		for(String s : LangUtil.buildItemLore("GameConfigurator")) {
			lore.add(s);
		}
		meta.setLore(lore);
		
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

		itm.setItemMeta(meta);
		
		itm = ItemUtil.addTags(itm, "GunGame_Item", "Configurator");
		itm = ItemUtil.setGunGameItem(itm);
		
		return itm;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(ItemUtil.isGunGameItem(event.getItem()) && ItemUtil.hasTag(event.getItem(), "GunGame_Item", "Configurator")) {
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				if(GunGamePlugin.instance.arenaManager.isIngame(event.getPlayer())) {
					Arena a = GunGamePlugin.instance.arenaManager.getArena(event.getPlayer());
					a.getConfiguratorUtil().openMenu(event.getPlayer());
				}
			}
		}
	}
	
}
