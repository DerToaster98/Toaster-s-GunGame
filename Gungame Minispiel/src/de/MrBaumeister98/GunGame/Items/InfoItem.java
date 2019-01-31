package de.MrBaumeister98.GunGame.Items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
import net.md_5.bungee.api.ChatColor;

public class InfoItem implements Listener {
	
	public static ItemStack infoItemStack() {
		ItemStack item = new ItemStack(Material.BOOK);
		
		ItemMeta meta = item.getItemMeta();
		
		String name = LangUtil.buildItemName("LobbyItems.InfoItem");
		meta.setDisplayName(name);
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		item.setItemMeta(meta);
		
		item = ItemUtil.addTags(item, "GunGame_Item", "InfoBook");
		item = ItemUtil.setGunGameItem(item);
		
		return item;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Arena a = GunGamePlugin.instance.arenaManager.getArena(p);
				if(a != null) {
					if(ItemUtil.isGunGameItem(event.getItem())) {
						if(ItemUtil.hasTag(event.getItem(), "GunGame_Item", "InfoBook")) {
							p.sendMessage("");
							p.sendMessage("");
							p.sendMessage("");
							p.sendMessage("");
							p.sendMessage("");
							p.sendMessage("");
							p.sendMessage(ChatColor.GRAY + "Arena: " + ChatColor.RED + a.getName());
							p.sendMessage(ChatColor.GRAY + "GameMode: " + ChatColor.RED + a.getArenaMode().toString().toUpperCase());
							p.sendMessage(ChatColor.GRAY + "Players: " + ChatColor.GRAY + a.getPlayers().size() + "/" + a.getMaxPlayers());
							p.sendMessage(ChatColor.GRAY + "Status: " + ChatColor.RED + a.getGameState());
							p.sendMessage("");
							if(a.getArenaWorld() != null) {
								p.sendMessage(ChatColor.GRAY + "Map: " + ChatColor.YELLOW + a.getArenaWorld().getName());
								String builders = "";
								for(String s : a.getArenaWorld().getBuilders()) {
									builders = builders + s + ", ";
								}
								p.sendMessage(ChatColor.GRAY + "Builders: " + ChatColor.YELLOW + builders);
							} else {
								p.sendMessage(ChatColor.GRAY + "Map: " + ChatColor.YELLOW + "???");
							}
							p.sendMessage("");
							p.sendMessage("");
						}
					}
						
				}
			}
		}
	}

}
