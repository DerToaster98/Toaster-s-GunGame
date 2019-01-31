package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.FallingBlock;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Airstrike;

public class AirstrikeRunnable_v1_13_up extends BukkitRunnable {

	private UUID shooter;
	private Block center;
	private Location centerLoc;
	private Airstrike strike;
	
	//private static int pingTaskID;
	
	public AirstrikeRunnable_v1_13_up(UUID id, Block center, Airstrike astrike) {
		this.shooter = id;
		this.center = center;
		this.centerLoc = center.getLocation();
		this.strike = astrike;
	}
	
	@Override
	public void run() {
		AirstrikeRunnable_v1_13_up ref = this;
		this.center.setType(Material.REDSTONE_TORCH);
		final int pingTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				Integer diffY = 255 - ref.centerLoc.getBlockY();
				Integer counts = diffY / 2;
				for(int i = 0; i < counts +1; i++) {
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() -0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) + 0.25, ref.centerLoc.getBlockZ() +0.5, 1, new Particle.DustOptions(Color.RED, 1.0f));
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i), ref.centerLoc.getBlockZ() +0.5, 1, new Particle.DustOptions(Color.RED, 1.0f));
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) - 0.25, ref.centerLoc.getBlockZ() +0.5, 1, new Particle.DustOptions(Color.RED, 1.0f));
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() + 0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
				}
				ref.center.getWorld().playSound(ref.centerLoc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 1.5f);
				if(ref.center.getType().equals(Material.REDSTONE_TORCH) && ((Lightable)center.getBlockData()).isLit()) {
					//ref.center.setType(Material.REDSTONE_TORCH_ON);
					Lightable data = (Lightable)center.getBlockData();
					data.setLit(false);
					center.setBlockData(data);
				} else if(ref.center.getType().equals(Material.REDSTONE_TORCH)) {
					//ref.center.setType(Material.REDSTONE_TORCH_OFF);
					Lightable data = (Lightable)center.getBlockData();
					data.setLit(true);
					center.setBlockData(data);
				}
				
			}
		}, 0, 20);
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(pingTaskID);
				ref.center.getWorld().playSound(ref.centerLoc, Sound.ENTITY_ENDERMAN_STARE, Float.MAX_VALUE, 2.0f);
				spawnBombs(0);
				Integer diffY = 255 - ref.centerLoc.getBlockY();
				Integer counts = diffY / 2;
				for(int i = 0; i < counts +1; i++) {
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() -0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) + 0.25, ref.centerLoc.getBlockZ() +0.5, 1, new Particle.DustOptions(Color.LIME, 1.0f));
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i), ref.centerLoc.getBlockZ() +0.5, 1, new Particle.DustOptions(Color.LIME, 1.0f));
					ref.center.getWorld().spawnParticle(Particle.REDSTONE, ref.centerLoc.getBlockX() +0.5, ref.center.getY() + (2*i) - 0.25, ref.centerLoc.getBlockZ() +0.5, 1, new Particle.DustOptions(Color.LIME, 1.0f));
					//tmp.getWorld().spawnParticle(Particle.REDSTONE, tmp.getX(), tmp.getY() + 0.5, tmp.getZ(), 0, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, 1.0);
				}
				ref.center.getWorld().playSound(ref.centerLoc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0f, 1.25f);
				
				ref.center.setType(Material.AIR);
			}
		}, this.strike.getFuse());
	}
	
	private void spawnBombs(Integer count) {
		
		Vector v = new Vector(1.0, 0.0, 0.0);
		Random rdm = new Random();
		//Integer degreeRotate = rdm.nextInt(360);
		//v = rotateVectorCC(v, new Vector(0.0, 1.0, 0.0), degreeRotate.doubleValue());
		Double x = -0.5 + rdm.nextDouble();
		Double z = -0.5 + rdm.nextDouble();
		v.setX(x);
		v.setZ(z);
		v.normalize();
		v = v.normalize();
		Integer length = rdm.nextInt(this.strike.getDropRadius());
		v = v.multiply(length);
		Location dropLoc = new Location(this.center.getWorld(), this.center.getX() + v.getX(), 255, this.center.getZ() + v.getZ());
		/*Block tmp = dropLoc.getBlock();
		tmp.setType(Material.NETHER_BRICK_FENCE);*/
		BlockData data = Material.NETHER_BRICK_FENCE.createBlockData();
		FallingBlock bomb = dropLoc.getWorld().spawnFallingBlock(dropLoc, data);
		//tmp.setType(Material.AIR);
		bomb.setDropItem(false);
		bomb.setGravity(true);
		bomb.setHurtEntities(true);
		bomb.setInvulnerable(true);
		bomb.setVelocity(new Vector(0.0, -2.0, 0.0));
		bomb.setMetadata("GG_Airstrike_Bomb", new FixedMetadataValue(GunGamePlugin.instance, true));
		bomb.setMetadata("GG_Airstrike_Name", new FixedMetadataValue(GunGamePlugin.instance, this.strike.name));
		bomb.setMetadata("GG_Airstrike_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooter.toString()));
		
		if(count < this.strike.getBombCount()) {
			Integer t = rdm.nextInt(4);
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					spawnBombs(count +1);
				}
			}, t);
		}
	}
}
