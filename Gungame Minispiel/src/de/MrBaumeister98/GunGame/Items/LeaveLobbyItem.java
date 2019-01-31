package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class LeaveLobbyItem implements Listener {
	
	private List<Player> wantsToQuit = new ArrayList<Player>();
	
	public static ItemStack leaveItem() {
		ItemStack item = new ItemStack(Material.BARRIER);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(LangUtil.buildItemName("LobbyItems.LeaveItem"));
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		item.setItemMeta(meta);
		
		item = ItemUtil.addTags(item, "GunGame_Item", "LeaveGame");
		item = ItemUtil.setGunGameItem(item);
		
		return item;
	}
	private boolean isLeaveItem(ItemStack item) {
		if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.hasTag(item, "GunGame_Item", "LeaveGame")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	@EventHandler
	public void onChatLeave(AsyncPlayerChatEvent event) {
		Player p = Bukkit.getPlayer(event.getPlayer().getUniqueId());
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			if(event.getMessage().equalsIgnoreCase("leave") || event.getMessage().equalsIgnoreCase("quit")) {
				if(wantsToQuit.contains(p)) {
					event.setCancelled(true);
					Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {						
							Arena arena = GunGamePlugin.instance.arenaManager.getArena(p);
							Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Player: " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has left the Arena: " + ChatColor.RED + arena.getName());
							p.sendMessage(LangUtil.createString("lang.Info.leftArena",
									arena,
									(arena.getArenaWorld() == null ? null : arena.getArenaWorld().getName()),
									p,
									null,
									null,
									arena.getMinPlayers(),
									arena.getMaxPlayers(),
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									true,
									false));
							wantsToQuit.remove(p);
							
							if(arena != null) {
								GunGamePlugin.instance.arenaManager.leaveGame(p, arena);
							}
						}
					});
					
				} else {
					event.setCancelled(true);
					wantsToQuit.add(p);
					p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
					p.sendMessage(LangUtil.createString("lang.Items.LobbyItems.LeaveItem.ConfirmQuit",
							GunGamePlugin.instance.arenaManager.getArena(p),
							(GunGamePlugin.instance.arenaManager.getArena(p) == null ? null : GunGamePlugin.instance.arenaManager.getArena(p).getArenaWorld().getName()),
							p,
							5,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							true,
							false));
					Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {

						@Override
						public void run() {
							wantsToQuit.remove(p);
						}
						
					}, 100L);
				}
			}
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			ItemStack item = event.getItem();
			if(isLeaveItem(item)) {		
			
					if(event.getAction() == Action.RIGHT_CLICK_AIR | event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if(wantsToQuit.contains(p)) {
							event.setCancelled(true);
							Arena arena = GunGamePlugin.instance.arenaManager.getArena(p);
							GunGamePlugin.instance.arenaManager.leaveGame(p, arena);
							
							
							p.sendMessage(LangUtil.createString("lang.Info.leftArena",
									arena,
									(arena.getArenaWorld() == null ? null : arena.getArenaWorld().getName()),
									p,
									null,
									null,
									arena.getMinPlayers(),
									arena.getMaxPlayers(),
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									true,
									false));
							wantsToQuit.remove(p);
							for(Player p2 : arena.getPlayers()) {
								p2.sendMessage(LangUtil.createString("lang.Info.playerLeft",
										arena,
										(arena.getArenaWorld() == null ? null : arena.getArenaWorld().getName()),
										p,
										null,
										null,
										arena.getMinPlayers(),
										arena.getMaxPlayers(),
										null,
										null,
										null,
										null,
										null,
										null,
										null,
										true,
										false));
							}
						} else {
							event.setCancelled(true);
							wantsToQuit.add(p);
							p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0F, 1.0F);
							p.sendMessage(LangUtil.createString("lang.Items.LobbyItems.LeaveItem.ConfirmQuit",
									GunGamePlugin.instance.arenaManager.getArena(p),
									(GunGamePlugin.instance.arenaManager.getArena(p).getArenaWorld() == null ? null : GunGamePlugin.instance.arenaManager.getArena(p).getArenaWorld().getName()),
									p,
									5,
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									null,
									true,
									false));
							Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {

								@Override
								public void run() {
									wantsToQuit.remove(p);
								}
								
							}, 100L);
						}
					}
				}
			}
	}

}
