package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class SuicideArmor implements Listener {
	
	public static List<UUID> hasIgnited = new ArrayList<UUID>();
	
	public static ItemStack suicideWest() {
		ItemStack vest = null;
			vest = new ItemStack(Material.GOLDEN_CHESTPLATE, 1);
		ItemMeta meta = vest.getItemMeta();
			
		meta.setUnbreakable(true);
		meta.setDisplayName(LangUtil.buildItemName("SuicideVest.Vest"));
		List<String> lore = new ArrayList<String>();
		for (String s : LangUtil.buildItemLore("SuicideVest.Vest")) {
			lore.add(s);
		}
		meta.setLore(lore);
		vest.setItemMeta(meta);
		
		vest = ItemUtil.addTags(vest, "GunGame_Item", "SuicideBomberVest");
		vest = ItemUtil.setGunGameItem(vest);
		
		return vest;
	}
	
	public static ItemStack remote() {
		ItemStack remote = new ItemStack(Material.IRON_INGOT, 1);
		ItemMeta meta = remote.getItemMeta();
		
		meta.setUnbreakable(true);
		meta.setDisplayName(LangUtil.buildItemName("SuicideVest.Remote"));
		List<String> lore = new ArrayList<String>();
		for(String s : LangUtil.buildItemLore("SuicideVest.Remote")) {
			lore.add(s);
		}
		meta.setLore(lore);
		remote.setItemMeta(meta);
		
		remote = ItemUtil.addTags(remote, "GunGame_Item", "SuicideVest_Remote");
		remote = ItemUtil.setGunGameItem(remote);
		
		return remote;
	}
	public static boolean hasVest(Player p) {
		ItemStack item = p.getInventory().getChestplate();
		if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.hasTag(item, "GunGame_Item", "SuicideBomberVest")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	private boolean isPrimer(ItemStack item) {
		if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.hasTag(item, "GunGame_Item", "SuicideVest_Remote")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	public static void kaboom(Player p) {
		p.getWorld().playSound(p.getLocation(), Sound.valueOf(GunGamePlugin.instance.getConfig().getString("Config.Items.SuicideVest.Sound")), 50.0F, 1.0F);									
		p.setInvulnerable(true);
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			@Override
			public void run() {							
				Boolean fire = GunGamePlugin.instance.getConfig().getBoolean("Config.Items.SuicideVest.Fire");
				Integer power = GunGamePlugin.instance.getConfig().getInt("Config.Items.SuicideVest.Power");
				//Util.createExplosion(p.getLocation(),fire,power);
				Util.createExplosion(p.getLocation(), fire, true, false, true, power, p.getUniqueId(), power / 2, false);
				p.getInventory().setChestplate(new ItemStack(Material.AIR));
				p.setInvulnerable(false);
				Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {

					@Override
					public void run() {
						
						//STAT
						GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementSuicideBombings();
						
						GunGamePlugin.instance.arenaManager.getArena(p).respawn(p);
						if(hasIgnited.contains(p.getUniqueId())) {
							hasIgnited.remove(p.getUniqueId());
						}
					}	
				}, 10L);
				
			}
		}, Long.valueOf(GunGamePlugin.instance.getConfig().getString("Config.Items.SuicideVest.Fuse")) *20);
	}
	
	@EventHandler
	public void onIgnite(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(isPrimer(event.getItem())) {		
						
						if(!hasIgnited.contains(p.getUniqueId()) && hasVest(p)) {
							hasIgnited.add(p.getUniqueId());
							p.getWorld().playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 50.0F, 2.0F);
							if(hasVest(p) == true) {
								event.setCancelled(true);
								kaboom(p);
							}
						}
					}
				//}
			}
		}
	}
	
}
