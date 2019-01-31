package de.MrBaumeister98.GunGame.Game.Mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.Grenade;
import de.MrBaumeister98.GunGame.GunEngine.Gun;
import de.MrBaumeister98.GunGame.Items.C4;
import de.tr7zw.itemnbtapi.NBTTileEntity;
import net.md_5.bungee.api.ChatColor;

public class LootChests implements Listener {
	
	private static HashMap<Block, Inventory> invMap = new HashMap<Block, Inventory>();
	private static List<Inventory> invs = new ArrayList<Inventory>();
	
	@EventHandler
	public void openShopClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (GunGamePlugin.instance.arenaManager.isIngame(p) == true && GunGamePlugin.instance.arenaManager.getArena(p).getGameState().equals(EGameState.GAME)) {
				if(event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
					event.setCancelled(true);
					event.getPlayer().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 0.75f);
					NBTTileEntity nbtile = new NBTTileEntity(event.getClickedBlock().getState());
					if((nbtile.hasKey("GG_Opened") && nbtile.getBoolean("GG_Opened")) || (invMap.containsKey(event.getClickedBlock()))) {
						Inventory inv = invMap.get(event.getClickedBlock());
						event.getPlayer().openInventory(inv);
					} else {
						nbtile.setBoolean("GG_Opened", true);
						Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, ChatColor.YELLOW + "Supplies");
						List<Integer> freeSlots = new ArrayList<Integer>();
						for(int i = 0; i<27; i++) {
							freeSlots.add(i);
						}
						//Random rdm = new Random();
						if(Util.getRandomBoolean()) {
							Integer slot = freeSlots.get(Util.getRandomNumber(freeSlots.size()));
							freeSlots.remove(slot);
							Gun g = GunGamePlugin.instance.weaponManager.guns.get(Util.getRandomNumber(GunGamePlugin.instance.weaponManager.guns.size()));
							
							while(g.hasUsePermission() == true) {
								g = GunGamePlugin.instance.weaponManager.guns.get(Util.getRandomNumber(GunGamePlugin.instance.weaponManager.guns.size()));
							}
							
							inv.setItem(slot, g.getItem().clone());
						}
						if(Util.getRandomBoolean()) {
							Integer slot = freeSlots.get(Util.getRandomNumber(freeSlots.size()));
							freeSlots.remove(slot);
							ItemStack item = C4.c4Throwable(Util.getRandomNumber(8) +1);
							inv.setItem(slot, item);
						}
						Integer grenades = Util.getRandomNumber(4);
						if(grenades > 0) {
							for(int i = 0; i < grenades; i++) {
								Integer slot = freeSlots.get(Util.getRandomNumber(freeSlots.size()));
								freeSlots.remove(slot);
								Grenade g = GunGamePlugin.instance.weaponManager.standardGrenades.get(Util.getRandomNumber(GunGamePlugin.instance.weaponManager.standardGrenades.size()));
								ItemStack gren = g.getGrenadeItem().clone();
								gren.setAmount(Util.getRandomNumber(4) +1);
								inv.setItem(slot, gren);
							}
						}
						Integer ammos = Util.getRandomNumber(6) +4;
						for(int i = 0; i < ammos; i++) {
							Integer slot = freeSlots.get(Util.getRandomNumber(freeSlots.size()));
							freeSlots.remove(slot);
							Ammo a = GunGamePlugin.instance.weaponManager.ammos.get(Util.getRandomNumber(GunGamePlugin.instance.weaponManager.ammos.size()));
							ItemStack amm = a.getItem().clone();
							amm.setAmount(Util.getRandomNumber(5) +8);
							inv.setItem(slot, amm);
						}
						saveInv(event.getClickedBlock(), inv);
						event.getPlayer().openInventory(inv);
					}
				} else {
					return;
				}
			}
		}
	}
	@EventHandler
	public void onCloseInv(InventoryCloseEvent event) {
		if(GunGamePlugin.instance.arenaManager.isIngame(event.getPlayer().getUniqueId())) {
			if(invs.contains(event.getInventory())) {
				for(Block b : invMap.keySet()) {
					if(invMap.get(b).equals(event.getInventory())) {
						invMap.put(b, event.getInventory());
						event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0f, 0.75f);
					}
				}
			}
		}
	}
	private static void saveInv(Block b, Inventory inv) {
		invMap.put(b, inv);
		invs.add(inv);
	}
	public static void resetChestsinWorld(World world) {
		List<Block> blocksToRemove = new ArrayList<Block>();
		for(Block b : invMap.keySet()) {
			if(b.getLocation().getWorld().equals(world)) {
				Inventory inv = invMap.get(b);
				invs.remove(inv);
				blocksToRemove.add(b);
			}
		}
		for(Block b : blocksToRemove) {
			invMap.remove(b);
		}
		blocksToRemove.clear();
	}

}
