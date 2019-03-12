package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Airstrike;

@SuppressWarnings("deprecation")
public class AirstrikeRunnable_pre_1_13 extends BukkitRunnable {

	private UUID shooter;
	private Block center;
	private Location centerLoc;
	private Airstrike strike;
	
	//private static int pingTaskID;
	
	public AirstrikeRunnable_pre_1_13(UUID id, Block center, Airstrike astrike) {
		this.shooter = id;
		this.center = center;
		this.centerLoc = center.getLocation();
		this.strike = astrike;
	}
	
	@Override
	public void run() {
		AirstrikeRunnable_pre_1_13 ref = this;
		this.center.setType(Material.valueOf("REDSTONE_TORCH_ON"));
		final int pingTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				Integer diffY = 255 - ref.centerLoc.getBlockY();
				Integer counts = diffY / 2;
				for(int i = 0; i < counts +1; i++) {
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() -0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) + 0.25, ref.centerLoc.getBlockZ() +0.5, 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i), ref.centerLoc.getBlockZ() +0.5, 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) - 0.25, ref.centerLoc.getBlockZ() +0.5, 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() + 0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
				}
				ref.center.getWorld().playSound(ref.centerLoc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 1.5f);
				if(ref.center.getType().equals(Material.valueOf("REDSTONE_TORCH_OFF"))) {
					ref.center.setType(Material.valueOf("REDSTONE_TORCH_ON"));
				} else if(ref.center.getType().equals(Material.valueOf("REDSTONE_TORCH_ON"))) {
					ref.center.setType(Material.valueOf("REDSTONE_TORCH_OFF"));
				}
				
			}
		}, 0, 20);
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(pingTaskID);
				ref.center.getWorld().playSound(ref.centerLoc, Sound.valueOf("ENTITY_ENDERMEN_STARE"), Float.MAX_VALUE, 2.0f);
				spawnBombs(0);
				Integer diffY = 255 - ref.centerLoc.getBlockY();
				Integer counts = diffY / 2;
				for(int i = 0; i < counts +1; i++) {
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() -0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) + 0.25, ref.centerLoc.getBlockZ() +0.5, 0, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i), ref.centerLoc.getBlockZ() +0.5, 0, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) - 0.25, ref.centerLoc.getBlockZ() +0.5, 0, Float.MIN_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, 1.0);
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() + 0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
				}
				ref.center.getWorld().playSound(ref.centerLoc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 1.25f);
				
				ref.center.setType(Material.AIR);
			}
		}, this.strike.getFuse());
	}
	
	private void spawnBombs(Integer count) {
		
		Vector v = new Vector(1.0, 0.0, 0.0);
		//Random rdm = new Random();
		//Integer degreeRotate = rdm.nextInt(360);
		//v = rotateVectorCC(v, new Vector(0.0, 1.0, 0.0), degreeRotate.doubleValue());
		Double x = -0.5 + Util.getRandomDouble();
		Double z = -0.5 + Util.getRandomDouble();
		v.setX(x);
		v.setZ(z);
		v.normalize();
		v = v.normalize();
		Integer length = Util.getRandomNumber(this.strike.getDropRadius());
		v = v.multiply(length);
		Location dropLoc = new Location(this.center.getWorld(), this.center.getX() + v.getX(), 255, this.center.getZ() + v.getZ());
		
		FallingBlock bomb = dropLoc.getWorld().spawnFallingBlock(dropLoc, new MaterialData(Material.valueOf("NETHER_FENCE")));
		bomb.setDropItem(false);
		bomb.setGravity(true);
		bomb.setHurtEntities(true);
		bomb.setInvulnerable(true);
		bomb.setVelocity(new Vector(0.0, -2.0, 0.0));
		bomb.setMetadata("GG_Airstrike_Bomb", new FixedMetadataValue(GunGamePlugin.instance, true));
		bomb.setMetadata("GG_Airstrike_Name", new FixedMetadataValue(GunGamePlugin.instance, this.strike.name));
		bomb.setMetadata("GG_Airstrike_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooter.toString()));
		
		if(count < this.strike.getBombCount()) {
			Integer t = Util.getRandomNumber(4);
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					spawnBombs(count +1);
				}
			}, t);
		}
	}
}