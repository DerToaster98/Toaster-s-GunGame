package de.MrBaumeister98.GunGame.GunEngine;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Items.SuicideArmor;

public class DamageSet {
	
	private Double defaultDamge;
	
	public DamageSet(Double damage0) {
		this.defaultDamge = damage0;
	}
	
	public void damage(LivingEntity damaged, Location where, UUID who) {
		Player p = Bukkit.getPlayer(who);
		if(damaged.getUniqueId() != who && (damaged instanceof Player && SuicideArmor.hasVest((Player)damaged))) {
			SuicideArmor.kaboom((Player)damaged);
		} else if(isHeadShot(damaged.getLocation(), where) && !(damaged instanceof ArmorStand)) {
			Location fw = damaged.getEyeLocation();
			if(damaged instanceof Player) {
				spawnFireWork(fw);
				damaged.damage(this.defaultDamge *2, Bukkit.getEntity(who));
					
			} else {
				spawnFireWork(fw);
				damaged.damage(this.defaultDamge *2, Bukkit.getEntity(who));
					
			}
			//STAT
			if(p != null) {
				if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
					GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementHeadShotsDealt();
				}
			}
			//STATEND
		} else {
			if(damaged instanceof Player) {
				damaged.damage(this.defaultDamge, Bukkit.getEntity(who));
			} else {
				damaged.damage(this.defaultDamge, Bukkit.getEntity(who));
			}
		}
	}
	
	private Boolean isHeadShot(Location head, Location bullet) {
		/*if(((head.getX() -0.25D) <= bullet.getX()) && ((head.getX() +0.25D) >= bullet.getBlockX())) {
			if(((head.getY() -0.25D) <= bullet.getY()) && ((head.getY() +0.25D) >= bullet.getBlockY())) {
				if(((head.getZ() -0.25D) <= bullet.getZ()) && ((head.getZ() +0.25D) >= bullet.getBlockZ())) {
					return true;
				}
			}
		}*/
		double bulletY = bullet.getY();
		double headY = head.getY();
		
		boolean yComparison = bulletY - headY > 1.35d;
		
		boolean distanceComparison = head.distance(bullet) <= 0.26d;
		
		return yComparison || distanceComparison;
	}
	private void spawnFireWork(Location where) {
		Firework fw;
		fw = (Firework)where.getWorld().spawn(where.add(0.0, 0.25, 0.0), Firework.class);
		FireworkMeta meta = fw.getFireworkMeta();
		FireworkEffect effect = FireworkEffect.builder().
				trail(false)
				.flicker(true)
				.withColor(Color.RED)
				.with(FireworkEffect.Type.BURST)
				.build();
		meta.addEffects(new FireworkEffect[] { effect });
		fw.setFireworkMeta(meta);
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				fw.detonate();
			}
		}, 1);
	}
}
