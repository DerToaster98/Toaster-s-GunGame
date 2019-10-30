package de.MrBaumeister98.GunGame.Game.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class RadarUtil implements Listener {
	
	private int taskID;
	
	private HashMap</*UUID,UUID*/Player,Player> targetOfPlayer = new HashMap</*UUID,UUID*/Player,Player>();
	private List<Player> trackers = new ArrayList<Player>();
	
	private Arena owner;
	
	public RadarUtil(Arena owner) {
		this.owner = owner;
		
		this.trackers = new ArrayList<Player>(this.owner.getPlayers());
	}
	public RadarUtil(GunGamePlugin main) {
		
	}
	public void setTarget(Player target, Player sender) {
		if(target != null && sender != null ) {
			sender.setCompassTarget(target.getLocation());
		}
	}
	
	public Player getRadarTarget(/*UUID*/Player tracker) {

		Player target = null;
		//UUID targetUUID = targetOfPlayer.get(tracker);
		//if(target/*UUID*/ != null) {
			//target = Bukkit.getPlayer(targetUUID);
		//}
		if(targetOfPlayer.containsKey(tracker) && targetOfPlayer.get(tracker) != null) {
			target = targetOfPlayer.get(tracker);
		}

		return target;
	}
	public boolean isRadar(ItemStack stack) {
		/*if(stack != null && stack.getType().equals(Material.COMPASS)) {
			if(stack.hasItemMeta() == true && stack.getItemMeta() != null && stack.getItemMeta().getDisplayName().equals(LangUtil.buildItemName("Radar"))) {
				return true;
			} else { 
				return false; 
			}
		} else { 
			return false; 
		}*/
		if(ItemUtil.isGunGameItem(stack)) {
			if(ItemUtil.hasTag(stack, "GunGame_Item", "Radar")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	@SuppressWarnings("deprecation")
	private Inventory openRadarGUI(Player p) {
		Integer size = 0;
		//Arena tempArena = Main.plugin.arenaManager.getArena(p);
		if(/*Main.plugin.arenaManager.getPlayers(tempArena)*/this.owner.manager.getPlayers(this.owner) <= 27) {
			size = 27;
		} else if(this.owner.manager.getPlayers(this.owner) <= 54 && this.owner.manager.getPlayers(this.owner) > 27) {
			size = 54;
		}
		if(size != 0) {
			Inventory radGUI = Bukkit.createInventory(null, size, LangUtil.buildGUIString("RadarMenu"));
			
			List<Player> tempTargetList = this.owner.manager.getPlayerList(this.owner);
			tempTargetList.remove(p);
			
			for(Player target : tempTargetList) {
				if(GunGamePlugin.instance.serverPre113) {
					ItemStack item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.YELLOW + target.getUniqueId().toString());
					meta.setOwner(target.getName());
					meta.setLore(lore);
					meta.setDisplayName(ChatColor.RED + target.getName());
					item.setItemMeta(meta);
					radGUI.addItem(item);
				} else {
					ItemStack item = new ItemStack(Material.valueOf("PLAYER_HEAD"), 1/*, (short) 3*/);
					SkullMeta meta = (SkullMeta) item.getItemMeta();
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(ChatColor.YELLOW + target.getUniqueId().toString());
					//meta.setOwner(target.getName());
					meta.setOwningPlayer(Bukkit.getOfflinePlayer(target.getUniqueId()));
					//meta.setOwningPlayer(target.getPlayer());
					//meta.setOwningPlayer(target.getPlayer());
					meta.setLore(lore);
					meta.setDisplayName(ChatColor.RED + target.getName());
					item.setItemMeta(meta);
					radGUI.addItem(item);
				}
			}
			
			return radGUI;
			
		} else {
			return null;
		}
	}
	
	@EventHandler
	public void openTargetMenu(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(this.owner != null) {
			if(!event.getAction().equals(Action.PHYSICAL) && this.owner.manager.isIngame(p) && this.owner.manager.getArena(p).equals(this.owner) && !event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				if(!this.trackers.contains(p/*.getUniqueId()*/)) {
					this.trackers.add(p/*.getUniqueId()*/);
				}
				if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(event.getItem() != null && event.getItem().getType() != null && event.getItem().getType().equals(Material.COMPASS)) {
						event.setCancelled(true);
						ItemStack comp = event.getItem();
						if(isRadar(comp) == true) {
							if(openRadarGUI(p) != null) {
								p.openInventory(openRadarGUI(p));
							}
							
						}
					}
					
				} else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
					//UPDATE COMPASS LOC TO TARGET LOCATION
					if(event.getItem() != null && event.getItem().getType() != null && event.getItem().getType().equals(Material.COMPASS)) {
						event.setCancelled(true);
						ItemStack comp = event.getItem();
						if(isRadar(comp) == true && getRadarTarget(p/*.getUniqueId()*/) != null) {
							setTarget(getRadarTarget(p/*.getUniqueId()*/), p);
							
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void chooseTarget(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if(this.owner != null) {
			if(this.owner.manager.isIngame(p) && this.owner.manager.getArena(p).equals(this.owner)) {
				if(event.getView()/*getInventory()*/.getTitle().equals(LangUtil.buildGUIString("RadarMenu"))) {
					if(
							GunGamePlugin.instance.serverPre113 && event.getCurrentItem().getType().equals(Material.valueOf("SKULL_ITEM"))
							|| 
							!GunGamePlugin.instance.serverPre113 && event.getCurrentItem().getType().equals(Material.valueOf("PLAYER_HEAD"))
						) {
						UUID pUUID = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)));
						
						if(pUUID != null) {
							setTarget(Bukkit.getPlayer(pUUID), p);
						} else {
							return;
						}
						
						if(p.getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {
							//p.getInventory().getItemInMainHand().getItemMeta().getLore().add(LangStrings.RadarLore.size(), ChatColor.BLUE + Bukkit.getPlayer(pUUID).getName() + "|" + pUUID.toString());
							targetOfPlayer.put(p/*.getUniqueId()*/, Bukkit.getPlayer(pUUID));
							event.setCancelled(true);
							
							p.closeInventory();
						} else if(p.getInventory().getItemInOffHand().getType().equals(Material.COMPASS)) {
							//p.getInventory().getItemInOffHand().getItemMeta().getLore().add(LangStrings.RadarLore.size(), ChatColor.BLUE + Bukkit.getPlayer(pUUID).getName() + "|" + pUUID.toString());
							targetOfPlayer.put(p/*.getUniqueId()*/, Bukkit.getPlayer(pUUID));
							event.setCancelled(true);
							
							p.closeInventory();
						}				
					} 
				}
			}
		}
	}
	
	public void autoRefresh() {	
		RadarUtil temp = this;
		
		this.taskID = GunGamePlugin.instance.getServer().getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {		
			@Override
			public void run() {
				if(temp.trackers.size() >= 0) {
					for(Player tracker : temp.trackers) {
						if(getRadarTarget(tracker) != null) {
							setTarget(getRadarTarget(tracker/*UUID*/), /*Bukkit.getPlayer(trackerUUID)*/tracker);
						} else {
							//trackers.remove(tracker);
							targetOfPlayer.remove(tracker);
						}
						
					}
				}
			}		
		}, 2L, 2L);
		
	}
	
	public void killRefreshTask() {
		Bukkit.getScheduler().cancelTask(this.taskID);
		this.trackers.clear();
	}
	public void removePlayer(Player toRemove) {
		try {
			targetOfPlayer.remove(toRemove);
			trackers.remove(toRemove);
			for(Player p : trackers) {
				if(getRadarTarget(p) == toRemove) {
					targetOfPlayer.remove(p);
					trackers.remove(p);
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	public void updateTrackerList() {
		this.trackers.clear();
		this.trackers = new ArrayList<Player>(this.owner.getPlayers());
	}

}
