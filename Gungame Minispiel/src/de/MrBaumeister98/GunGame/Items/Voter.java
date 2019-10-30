package de.MrBaumeister98.GunGame.Items;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.VoteUtil;
import net.md_5.bungee.api.ChatColor;

public class Voter implements Listener {

	public static ItemStack votePaper() {
		ItemStack voter = new ItemStack(Material.NAME_TAG);
		ItemMeta meta = voter.getItemMeta();
		meta.setDisplayName(LangUtil.buildItemName("LobbyItems.VotePaper"));
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		voter.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		voter.setItemMeta(meta);
		
		voter = ItemUtil.addTags(voter, "GunGame_Item", "Voter");
		voter = ItemUtil.setGunGameItem(voter);
		
		return voter;
	}
	
	private void fillVoteMenu(Player p) {
		Inventory GUI = Bukkit.createInventory(null, InventoryType.HOPPER, LangUtil.buildGUIString("VoteMenu"));
		Arena arena = GunGamePlugin.instance.arenaManager.getArena(p);
		Set<String> worlds = arena.voteMap.keySet();
		for(String map : worlds) {
			if(map != ChatColor.stripColor("???")) {
				ItemStack mapItem = new ItemStack(Material.MAP);
				ItemMeta meta = mapItem.getItemMeta();
				meta.setDisplayName(ChatColor.RED + map);
				if(arena.kills.get(p) != null && arena.kills.containsKey(p) == true) {
					if(arena.kills.get(p).equalsIgnoreCase(map)) {
						mapItem.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					}
				}
				Integer count = arena.voteMap.get(map);
				if(count != null && count != 0) {
					mapItem.setAmount(count);
				}
				mapItem.setItemMeta(meta);
				GUI.addItem(mapItem);
			}			
		}
		ItemStack rndmMap = new ItemStack(Material.MAP);
		ItemMeta meta = rndmMap.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "???");
		if(arena.kills.get(p) != null && arena.kills.containsKey(p) == true) {
			if(arena.kills.get(p).equalsIgnoreCase("???")) {
				rndmMap.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		}
		Integer count = arena.voteMap.get("???");
		if(count != null && count != 0) {
			rndmMap.setAmount(count);
		}
		rndmMap.setItemMeta(meta);
		GUI.addItem(rndmMap);
		
		p.openInventory(GUI);
	}
	
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			ItemStack item = event.getItem();
					if(ItemUtil.isGunGameItem(item)) {
						if(ItemUtil.hasTag(item, "GunGame_Item", "Voter")) {
							if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								fillVoteMenu(p);
							}
						}
					}
				}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			if(event./*getInventory().getName()*/getView().getTitle().equalsIgnoreCase(LangUtil.buildGUIString("VoteMenu"))) {
				if(event.getInventory().getType().equals(InventoryType.HOPPER)) {				
					ItemStack clicked = event.getCurrentItem();
					if(clicked != null ) {
						event.setCancelled(true);
							if(clicked.getType().equals(Material.MAP) || clicked.getType().equals(Material.FILLED_MAP)) {
								String map = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
								VoteUtil.castVote(p, map);
								p.closeInventory();
							}
					}
				}
			}
		}
	}

}
