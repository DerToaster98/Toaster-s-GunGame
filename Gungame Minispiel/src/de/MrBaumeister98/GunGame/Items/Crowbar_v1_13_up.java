package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Door;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class Crowbar_v1_13_up implements Listener {
	
	private static HashMap<Arena, HashMap<Player,List<Block>>> breakMap = new HashMap<Arena, HashMap<Player,List<Block>>>();
	
	//@SuppressWarnings("deprecation")
	public static ItemStack CrowBar() {
		ItemStack crowbar = new ItemStack(Material.IRON_HOE);
		
		ItemMeta meta = crowbar.getItemMeta();
		
		meta.setDisplayName(LangUtil.buildItemName("Crowbar"));
		
		List<String> lore = new ArrayList<String>();
		for(String s : LangUtil.buildItemLore("Crowbar")) {
			lore.add(s);
		}
		meta.setLore(lore);
		
		//if(Util.isCrowBarUnbreakable) {
			meta.setUnbreakable(true);
		//} else {
			//meta.setUnbreakable(false);
			//crowbar.setDurability(durability);
		//}
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		crowbar.setItemMeta(meta);
		
		//crowbar = ItemUtil.addTags(crowbar, "GunGame_isGGAItem", true);
		List<String> blocks = new ArrayList<String>();
		blocks.add("minecraft:tnt");
		blocks.add("minecraft:glass");
		blocks.add("minecraft:glass_pane");
		String[] colors = {"black_","blue_","brown_","cyan_","gray_","green_","light_blue_","light_gray_","lime_","magenta_","orange_","pink_","purple_","red_","white_","yellow_"};
		for(String cl : colors) {
			blocks.add("minecraft:" + cl + "stained_glass");
			blocks.add("minecraft:" + cl + "stained_glass_pane");
		}
		crowbar = ItemUtil.addTags(crowbar, "CanDestroy", blocks);
		
		crowbar = ItemUtil.setGunGameItem(crowbar);
		crowbar = ItemUtil.addTags(crowbar, "GunGame_Item", "Crowbar");
		
		return crowbar;
	}
	public static void clearMap(Arena a) {
		breakMap.get(a).clear();
	}
	public static void initializeMap(Arena a) {
		//for(Arena a : aManager.arenas) {
			HashMap<Player,List<org.bukkit.block.Block>> temp = new HashMap<Player,List<org.bukkit.block.Block>>();
			breakMap.put(a, temp);
		//}
	}
	private boolean isCrowbar(ItemStack item) {
		if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.hasTag(item, "GunGame_Item", "Crowbar")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
			if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
				if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {		
					if(isCrowbar(event.getItem()) == true) {
						Block clicked = event.getClickedBlock();
						if(clicked.getType().equals(Material.IRON_DOOR) ||
						   clicked.getType().equals(Material.IRON_TRAPDOOR)) {
							
							Block toAdd;
							if(clicked.getType().equals(Material.IRON_DOOR)) {
								BlockState state = clicked.getState();
								if(((state.getData() instanceof Door)) && (((Door)state.getData()).isTopHalf())) {
									state = clicked.getRelative(org.bukkit.block.BlockFace.DOWN).getState();
									toAdd = clicked.getRelative(org.bukkit.block.BlockFace.DOWN);
								} else {
									toAdd = clicked;
								}
								
								try {
									org.bukkit.block.data.Openable door = (org.bukkit.block.data.Openable)state.getBlockData();
									
									p.playSound(clicked.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 2.0F, 0.5F);
									//p.sendMessage("Du hast die Tuer aufgebrochen");
									if(door.isOpen()) {
										door.setOpen(false);
										//state.setData((MaterialData)door);
										state.setBlockData((org.bukkit.block.data.BlockData)door);
										state.update();
									} else {
										door.setOpen(true);
										//state.setData((MaterialData)door);
										state.setBlockData((org.bukkit.block.data.BlockData)door);
										state.update();
									}
									if(breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).get(p) == null || !breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).containsKey(p)) {
										List<Block> temp = new ArrayList<Block>();
										breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).put(p, temp);
										
										//STAT
										if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
											GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementLocksPicked();
										}
									}
									if(!(breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).get(p).contains(toAdd))) {
										List<Block> temp = new ArrayList<Block>(breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).get(p));
										temp.add(toAdd);
										breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).put(p, temp);
										
										//STAT
										if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
											GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementLocksPicked();
										}
										//STAT STUFF
									}
										
								} catch(ClassCastException ex) {
									//LOG ERROR TO LOG
								}
							}
							if(clicked.getType().equals(Material.IRON_TRAPDOOR)) {
								toAdd = clicked;
								BlockState state = clicked.getState();
								org.bukkit.block.data.type.TrapDoor door = (org.bukkit.block.data.type.TrapDoor)state.getBlockData();
								p.playSound(clicked.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 2.0F, 0.5F);
								//p.sendMessage("Du hast die Falltuer aufgebrochen");
								if(door.isOpen()) {
									door.setOpen(false);
									state.setBlockData((org.bukkit.block.data.BlockData)door);
									state.update();
								} else {
									door.setOpen(true);
									//state.setData((MaterialData)door);
									state.setBlockData((org.bukkit.block.data.BlockData)door);
									state.update();
								}
								if(breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).get(p) == null || !breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).containsKey(p)) {
									List<Block> temp = new ArrayList<Block>();
									breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).put(p, temp);
									
									//STAT
									if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
										GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementLocksPicked();
									}
								}
								if(!(breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).get(p).contains(toAdd))) {
									List<Block> temp = new ArrayList<Block>(breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).get(p));
									temp.add(toAdd);
									breakMap.get(GunGamePlugin.instance.arenaManager.getArena(p)).put(p, temp);
									
									//STAT
									if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
										GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementLocksPicked();
									}
									//STAT STUFF
								}
							}
						}
					}
				}
			}
	}

}
