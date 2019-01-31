package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Landmine;
import de.MrBaumeister98.GunGame.GunEngine.Enums.ELandmineType;

public class LandmineExplodeRunnable extends BukkitRunnable {
	
	private Boolean breakBlocks;
	private Boolean isIncendiary;
	private Boolean noDamage;
	private Boolean physics;
	
	private UUID placer;
	
	private Block block;
	
	private Integer smokeDuration;
	private Integer smokeDensity;
	private Integer radius;
	private Integer fireDuration;
	
	private float power;
	
	private Double bearTrapDamage;
	
	private PotionEffect poisonEffect;
	private PotionEffect bearTrapEffect;
	
	private Landmine mine;
	private ELandmineType type;

	public LandmineExplodeRunnable(Landmine mine, Block block, UUID placerID) {
		this.setPlacer(placerID);
		this.mine = mine;
		this.block = block;
		this.type = this.mine.getType();
		
		switch(this.type) {
			default:
				break;
			case BEARTRAP:
				this.setBearTrapDamage(this.mine.getBearTrapDamage());
				this.setBearTrapEffect(new PotionEffect(PotionEffectType.SLOW, this.mine.getBearTrapEffectDuration(), this.mine.getBearTrapEffectAmplifier()));
				break;
			case EXPLOSIVE:
				this.radius = this.mine.getRadius();
				this.setPower(this.mine.getStrength());
				this.setBreakBlocks(this.mine.getBreakBlocks());
				this.setIsIncendiary(this.mine.getExplosionIncendiary());
				this.setNoDamage(this.mine.getExplosionNoDamage());
				this.setPhysics(this.mine.getUsePhysics());
				break;
			case FIRE:
				this.radius = this.mine.getRadius();
				this.setFireDuration(this.mine.getFireDuration());
				break;
			case POISON:
				this.radius = this.mine.getRadius();
				this.poisonEffect = this.mine.getPotionEffect();
				break;
			case SMOKE:
				this.radius = this.mine.getRadius();
				this.setSmokeDensity(this.mine.getSmokeDensity());
				this.setSmokeDuration(this.mine.getSmokeDuration());
				break;
		}
		
	}
	
	@Override
	public void run() {
		this.block.setType(Material.AIR);
		Location loc = this.block.getLocation();
		switch(this.type) {
		default:
			break;
		case BEARTRAP:
			EvokerFangs trap = (EvokerFangs) loc.getWorld().spawnEntity(loc.getBlock().getLocation().add(0.5, 0.0, 0.5), EntityType.EVOKER_FANGS);
			//trap.setInvulnerable(true);
			trap.setMetadata("GG_BearTrap", new FixedMetadataValue(GunGamePlugin.instance, true));
			trap.setMetadata("GG_BearTrap_Placer", new FixedMetadataValue(GunGamePlugin.instance, getPlacer().toString()));
			trap.setMetadata("GG_BearTrap_Parent", new FixedMetadataValue(GunGamePlugin.instance, this.mine.getName()));
			for(Entity ent : trap.getNearbyEntities(1, 1, 1)) {
				if(ent instanceof LivingEntity) {
					((LivingEntity)ent).addPotionEffect(bearTrapEffect, true);
				}
			}
			break;
		case EXPLOSIVE:
			Util.createExplosion(loc, getIsIncendiary(), getBreakBlocks(), getNoDamage(), getPhysics(), getPower(), getPlacer(), this.radius, false);
			break;
		case FIRE:
			Util.placeFire(this.radius, loc, getBreakBlocks(), getFireDuration());
			break;
		case POISON:
			Util.createExplosion(loc, false, false, true, false, 0, null, 0, false);
			AreaEffectCloud aec = (AreaEffectCloud) loc.getWorld().spawnEntity(loc, EntityType.AREA_EFFECT_CLOUD);
			aec.addCustomEffect(this.poisonEffect, true);
			aec.setDuration(this.poisonEffect.getDuration() * 2);
			aec.setDurationOnUse(-aec.getDuration() / 4);
			aec.setRadius(this.radius.floatValue());
			aec.setRadiusOnUse( -1 * (this.radius.floatValue() / (float)4));
			break;
		case SMOKE:
			Util.createExplosion(loc, false, false, true, false, 0, null, 0, false);
			createSmokeCloud(loc);
			break;
		}
	}
	
	
	
	
	
	
	private void createSmokeCloud(Location center) {
		//if(owner.smokeEnabled == true) {
			List<Location> temp = new ArrayList<Location>();
			for(int i = 0; i <= this.radius; i++) {
				for(int x = 0; x <= this.radius; x++) {
					Location temp1 = new Location(center.getWorld(), center.getX() + i, center.getY(), center.getZ() + x);
					if(temp.isEmpty() || !temp.contains(temp1)) {
						temp.add(temp1);
					}
					Location temp2 = new Location(center.getWorld(), center.getX() - i, center.getY(), center.getZ() - x);
					if(temp.isEmpty() || !temp.contains(temp2)) {
						temp.add(temp2);
					}
					Location temp5 = new Location(center.getWorld(), center.getX() + i, center.getY(), center.getZ() - x);
					if(temp.isEmpty() || !temp.contains(temp5)) {
						temp.add(temp5);
					}
					Location temp6 = new Location(center.getWorld(), center.getX() - i, center.getY(), center.getZ() + x);
					if(temp.isEmpty() || !temp.contains(temp6)) {
						temp.add(temp6);
					}
				}
			}
			List<Location> locs = new ArrayList<Location>(temp);
			for(Location loc : temp) {
				for(int i = 1; i<= this.radius; i++) {
					Location t1 = new Location(loc.getWorld(), loc.getX(), loc.getY() + i, loc.getZ());
					locs.add(t1);
					Location t2 = new Location(loc.getWorld(), loc.getX(), loc.getY() - i, loc.getZ());
					locs.add(t2);
				}
			}
			List<Location> smokeList = new ArrayList<Location>();
			for(Location loc : locs) {
				Material m = loc.getBlock().getType();
				if(canSmokePass(m) == true) {
				//if(canBeAccessed(loc, center)) {
					smokeList.add(loc);
				}
			}
			
			///////////////////////////////////////////////////////
			class smokeThread extends Thread {
				
				private List<Location> toSmoke = new ArrayList<Location>();
				private LandmineExplodeRunnable owner;
				
				public smokeThread(LandmineExplodeRunnable owner, List<Location> toSmoke) {
					this.toSmoke = toSmoke;
					this.owner = owner;
				}

				
				@Override
				public void run() {
					
					while(1 != 0) {
						try {
							for(Location loc : toSmoke) {
								Location loc2 = new Location(loc.getWorld(), loc.getX() - 0.125D, loc.getY() - 0.125D, loc.getZ() - 0.125D);
								loc.getWorld().spawnParticle(Particle.CLOUD, loc2, owner.getSmokeDensity(),  0.25D, 0.25D, 0.25D, 0.0001D);
							}
						} catch(Exception ex) {
							ex.printStackTrace();
						}
						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
				
			}
			///////////////////////////////////////////////////////
			
			smokeThread sThread = new smokeThread(this, smokeList);
			
			sThread.start();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					try {
						sThread.stop();
						//sThread.destroy();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
					
				}
			}, this.smokeDuration);
		//}
	}
	private Boolean canSmokePass(Material m) {
		if(/*m.equals(Material.AIR) |
				m.equals(Material.STATIONARY_WATER) |
				m.equals(Material.WATER) |
				m.equals(Material.WATER_LILY) |
				m.equals(Material.SUGAR_CANE_BLOCK) |
				m.equals(Material.CROPS) |
				m.equals(Material.IRON_TRAPDOOR) |
				m.equals(Material.TORCH) |
				m.equals(Material.REDSTONE) |
				m.equals(Material.REDSTONE_COMPARATOR_OFF) |
				m.equals(Material.REDSTONE_COMPARATOR_ON) |
				m.equals(Material.REDSTONE_WIRE) |
				m.equals(Material.REDSTONE_TORCH_ON) |
				m.equals(Material.REDSTONE_TORCH_OFF) |
				m.equals(Material.TRIPWIRE) |
				m.equals(Material.STONE_BUTTON) |
				m.equals(Material.WOOD_BUTTON) |
				m.equals(Material.LEVER) |
				m.equals(Material.TRIPWIRE_HOOK)|
				m.equals(Material.FLOWER_POT) |
				m.equals(Material.RAILS) |
				m.equals(Material.ACTIVATOR_RAIL) |
				m.equals(Material.DETECTOR_RAIL) |
				m.equals(Material.POWERED_RAIL) |
				m.equals(Material.END_ROD) |
				m.equals(Material.BED_BLOCK) |
				m.equals(Material.STAINED_GLASS_PANE) |
				m.equals(Material.THIN_GLASS) |
				m.equals(Material.FENCE) |
				m.equals(Material.FENCE_GATE) |
				m.equals(Material.VINE) |
				m.equals(Material.BANNER) |
				m.equals(Material.WALL_BANNER) |
				m.equals(Material.SIGN_POST) |
				m.equals(Material.WALL_SIGN) |
				m.equals(Material.COBBLE_WALL) |
				m.equals(Material.TRAP_DOOR) |
				m.equals(Material.ACACIA_DOOR) |
				m.equals(Material.BIRCH_DOOR) |
				m.equals(Material.DARK_OAK_DOOR) |
				m.equals(Material.IRON_DOOR) |
				m.equals(Material.JUNGLE_DOOR) |
				m.equals(Material.SPRUCE_DOOR) |
				m.equals(Material.WOOD_DOOR) |
				m.equals(Material.DOUBLE_PLANT) |
				m.equals(Material.LONG_GRASS) |
				m.equals(Material.GRASS) |
				m.equals(Material.DOUBLE_PLANT) |
				m.equals(Material.RED_ROSE) |
				m.equals(Material.YELLOW_FLOWER) |
				m.equals(Material.LADDER) |
				m.equals(Material.IRON_FENCE) |
				m.equals(Material.DEAD_BUSH) |
				m.equals(Material.BROWN_MUSHROOM) |
				m.equals(Material.RED_MUSHROOM)*/!Util.isFullBlock(m)) {
			return true;
		} else {
			return false;
		}
	}
	public Integer getSmokeDuration() {
		return smokeDuration;
	}
	public void setSmokeDuration(Integer smokeDuration) {
		this.smokeDuration = smokeDuration;
	}
	public Integer getSmokeDensity() {
		return smokeDensity;
	}
	public void setSmokeDensity(Integer smokeDensity) {
		this.smokeDensity = smokeDensity;
	}

	public Boolean getBreakBlocks() {
		return breakBlocks;
	}

	public void setBreakBlocks(Boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}

	public Boolean getIsIncendiary() {
		return isIncendiary;
	}

	public void setIsIncendiary(Boolean isIncendiary) {
		this.isIncendiary = isIncendiary;
	}

	public Boolean getNoDamage() {
		return noDamage;
	}

	public void setNoDamage(Boolean noDamage) {
		this.noDamage = noDamage;
	}

	public UUID getPlacer() {
		return placer;
	}

	public void setPlacer(UUID placer) {
		this.placer = placer;
	}

	public Integer getFireDuration() {
		return fireDuration;
	}

	public void setFireDuration(Integer fireDuration) {
		this.fireDuration = fireDuration;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	public PotionEffect getBearTrapEffect() {
		return bearTrapEffect;
	}

	public void setBearTrapEffect(PotionEffect bearTrapEffect) {
		this.bearTrapEffect = bearTrapEffect;
	}

	public Double getBearTrapDamage() {
		return bearTrapDamage;
	}

	public void setBearTrapDamage(Double bearTrapDamage) {
		this.bearTrapDamage = bearTrapDamage;
	}

	public Boolean getPhysics() {
		return physics;
	}

	public void setPhysics(Boolean physics) {
		this.physics = physics;
	}

}
