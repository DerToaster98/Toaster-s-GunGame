package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.API.GrenadeExplodeEvent;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Enums.EGrenadeType;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;

public class Grenade {
	
	private EGrenadeType type;
	private WeaponManager manager;
	private String GrenadeName;
	private FileConfiguration weaponFile;
	private WeaponSoundSet soundSet;
	private Integer smokeDuration;
	private Integer fuse;
	private Integer radius;
	private Integer smokeRadius;
	private float strength;
	private WeaponFileUtil wfu;
	private Integer smokeDensity;
	private Double smokeAffectionRange;
	private PotionEffect potionEffect;
	private Particle smokeParticle;
	private Boolean potionEnabled;
	private Boolean breakBlocks;
	private Integer clusterCount;
	private Boolean smokeEnabled;
	private Boolean fireEnabled;
	private Boolean explodeOnImpact;
	private Boolean explosionNoDamage;
	private ItemStack grenadeItem;
	public static int taskID;
	public static int taskID2;
	private Boolean standardWeapon;
	
	public Grenade(WeaponManager manager, String name, FileConfiguration weaponConfig) {
		this.setManager(manager);
		this.wfu = this.manager.wfu;
		this.setWeaponFile(weaponConfig);
		
		this.setStandardWeapon(this.wfu.isStandardWeapon(this.weaponFile));
		setGrenadeName(name);
		setType(this.wfu.getGrenadeType(this.weaponFile));
		createItem();
		setSoundSet(this.wfu.getSoundSet(this.weaponFile));
		setupExplosion();
	}
	public void createLore() {
		ItemStack item = this.getGrenadeItem();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(LangUtil.getWeaponItemLore(this));
		item.setItemMeta(meta);
		
		setGrenadeItem(item);
	}
	public void createItem() {
		ItemStack grenade;
		
		grenade = this.wfu.getGrenadeItem(this.weaponFile);
		
		//return grenade;
		setGrenadeItem(grenade);
	}
	private void setupSmoke() {
		setSmokeParticle(this.wfu.getGrenadeSmokeParticle(this.weaponFile));
		setPotionEffect(this.wfu.getGrenadePotionEffect(this.weaponFile));
		setSmokedensity(this.wfu.getSmokeDensity(this.weaponFile));
		setSmokeAffectionRange(this.wfu.getSmokeAffectionRange(this.weaponFile));
		setSmokeDuration(this.wfu.getSmokeDuration(this.weaponFile));
		setSmokeRadius(this.wfu.getSmokeRadius(this.weaponFile));
		setPotionEnabled(this.wfu.getPotionEnabled(this.weaponFile));
	}
	private void setupExplosion() {	
		setFuse(this.wfu.getGrenadeFuse(this.weaponFile));
		setRadius(this.wfu.getRadius(this.weaponFile));
		setStrength(this.wfu.getPower(this.weaponFile));		
		setBreakBlocks(this.wfu.getBreakBlocks(this.weaponFile));
		setExplosionNoDamage(this.wfu.getExplosionNoDamage(this.weaponFile));
		setExplodeOnImpact(this.wfu.getExplodeOnImpact(this.weaponFile));
		
		setSmokeEnabled(this.wfu.getSmokeEnabled(this.weaponFile));
		if(getSmokeEnabled() || getGrenadeType().equals(EGrenadeType.SMOKE)) {
			setupSmoke();
		}
		if(getGrenadeType().equals(EGrenadeType.CLUSTER)) {
			setClusterCount(this.wfu.getClusterCount(this.weaponFile));
		}
		if(getGrenadeType().equals(EGrenadeType.INCENDIARY)) {
			setFireEnabled(true);
		} else {
			setFireEnabled(this.wfu.getFireEnabled(this.weaponFile));
		}
	}
	public void setupSoundSet() {
		WeaponSoundSet temp = this.wfu.getSoundSet(this.weaponFile);
		
		setSoundSet(temp);
	}
	
	public void createSmokeCloud(Location center) {
		createSmokeCloud(center, this);
	}
	public void createExplosion(UUID thrower, Grenade gren, Item grenade) {
		Bukkit.getScheduler().cancelTask(taskID2);
		Location center = grenade.getLocation();
		
		GrenadeExplodeEvent explodevent = new GrenadeExplodeEvent(gren, thrower, center);
		Bukkit.getServer().getPluginManager().callEvent(explodevent);
		if(!explodevent.isCancelled()) {
			switch(gren.getGrenadeType()) {
			default:
				createExplosion(grenade, center, gren, thrower, false, gren.getFireEnabled(), gren.getSmokeEnabled());
			break;
			case CLUSTER:
				createExplosion(grenade, center, gren, thrower, true, gren.getFireEnabled(), gren.getSmokeEnabled());
			break;
			case FRAG:
				createExplosion(grenade, center, gren, thrower, false, gren.getFireEnabled(), gren.getSmokeEnabled());
			break;
			case INCENDIARY:
				createExplosion(grenade, center, gren, thrower, false, true, gren.getSmokeEnabled());
			break;
			case SMOKE:
				createExplosion(grenade, center, gren, thrower, false, false, true);
			break;
		}
		}
		
	}
	
	
	
	
	public void throwIt(Player thrower, Double force) {
		Item grenade = thrower.getWorld().dropItem(thrower.getEyeLocation(), this.getGrenadeItem());
		grenade.setVelocity(thrower.getLocation().add(thrower.getLocation().getDirection().normalize().multiply(1.25D)).getDirection().multiply(force));
		throwIt(grenade, thrower, thrower.getLocation());
	}

	public void throwIt(Item grenade, Player thrower, Location soundLoc) {
		grenade.getItemStack().setAmount(1);
		grenade.setInvulnerable(true);
		grenade.setPickupDelay(999999999);
		grenade.setMetadata("GG_Grenade", new FixedMetadataValue(GunGamePlugin.instance, true));
		grenade.setMetadata("GG_Grenade_Thrower", new FixedMetadataValue(GunGamePlugin.instance, thrower.getUniqueId().toString()));
		
		this.getSoundSet().throwSound.play(soundLoc.getWorld(), soundLoc);
		Grenade gren = this;
		if(gren.getExplodeOnImpact() == true) {
			UUID throwerID = null;
			if(thrower != null) {
				throwerID = thrower.getUniqueId();
			}
			
			flyThread fThread = new flyThread(throwerID, grenade, gren, soundLoc);		
			fThread.start();
			
		} else {
			UUID uuidThrower = null;
			//if(thrower != null) {
				uuidThrower = thrower.getUniqueId();
			//}
			flyThread fThread = new flyThread(uuidThrower, grenade, gren, soundLoc);
			fThread.addTaskIDToKill(taskID);
			fThread.start();
			taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					fThread.stop();
					UUID uuidThrower = null;
					if(thrower != null) {
						uuidThrower = thrower.getUniqueId();
					}
					createExplosion(uuidThrower, gren, grenade);			
				}
			}, this.getFuse());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void createExplosion(Item grenade, Location center, Grenade owner, UUID thrower, Boolean splitIntoCluster, Boolean fire, Boolean smoke) {
		owner.getSoundSet().explodeSound.play(center.getWorld(), center);
		//Bukkit.getPlayer(thrower).sendMessage(splitIntoCluster.toString());
		if(owner.getGrenadeType().equals(EGrenadeType.INCENDIARY)) {
			
			Util.createExplosion(center, true, owner.getBreakBlocks(), owner.getExplosionNoDamage(), true, owner.getStrength(), thrower, owner.getRadius(), true);
			if(smoke == true) {
				createSmokeCloud(grenade.getLocation(), owner);
			}
			grenade.remove();
			
		} else if(splitIntoCluster == false) {		
			if(owner.getStrength() >= 0) {
				//Bukkit.getPlayer(thrower).sendMessage("BOOM");
				Util.createExplosion(center, fire, owner.getBreakBlocks(), owner.getExplosionNoDamage(), true, owner.getStrength(), thrower, owner.getRadius(), true);
			}			
			if(smoke == true) {
				//Bukkit.getPlayer(thrower).sendMessage("A");
				createSmokeCloud(grenade.getLocation(), owner);
			}
			grenade.remove();
		} else /*if(splitIntoCluster != null && splitIntoCluster == true)*/ {
			List<Item> droplets = new ArrayList<Item>();
			for(int i = 0; i < owner.getClusterCount(); i++) {
				Item temp = grenade.getWorld().dropItem(grenade.getLocation(), new ItemStack(owner.getGrenadeItem()));
				temp.getItemStack().setAmount(1);
				//Random rdm = new Random();
				Double x = 0.5D - Util.getRandomDouble();
				Double y = 0.75D;
				Double z =  0.5D - Util.getRandomDouble();
				Vector v = new Vector(x, y, z);
				temp.setVelocity(v);
				temp.setPickupDelay(999999999);
				droplets.add(temp);
			}
			grenade.remove();
			Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {

					for(Item itm : droplets) {
						owner.getSoundSet().explodeSound.play(itm.getWorld(), itm.getLocation());
						//Bukkit.getPlayer(thrower).sendMessage("(.Y.)");
						Util.createExplosion(itm.getLocation(), fire, owner.getBreakBlocks(), owner.getExplosionNoDamage(), true, owner.getStrength(), thrower, owner.getRadius(), true);
						if(smoke == true) {
							//Bukkit.getPlayer(thrower).sendMessage("B");
							createSmokeCloud(itm.getLocation(), owner);
						}
						itm.remove();
					}
				}
			}, owner.getFuse() - (owner.getFuse() /2));
		}
		
	}
	/*private Boolean canPass(Location l) {
		Block b = l.getBlock();
		Material m = b.getType();
		return canPass(m);
	}*/
	private Boolean canSmokePass(Material m) {
		if(!Util.isFullBlock(m)) {
			return true;
		} else {
			return false;
		}
	}
	private Boolean canPass(Material m) {
		if(GunGamePlugin.instance.serverPre113) {
			if(m.equals(Material.AIR) |
					m.equals(Material.valueOf("STATIONARY_WATER")) |
					m.equals(Material.WATER) |
					m.equals(Material.valueOf("SUGAR_CANE_BLOCK")) |
					m.equals(Material.valueOf("CROPS")) |
					m.equals(Material.TORCH) |
					m.equals(Material.REDSTONE) |
					m.equals(Material.valueOf("REDSTONE_COMPARATOR_ON")) |
					m.equals(Material.REDSTONE_WIRE) |
					m.equals(Material.valueOf("REDSTONE_TORCH_ON")) |
					m.equals(Material.valueOf("REDSTONE_TORCH_OFF")) |
					m.equals(Material.TRIPWIRE) |
					m.equals(Material.STONE_BUTTON) |
					m.equals(Material.valueOf("WOOD_BUTTON")) |
					m.equals(Material.LEVER) |
					m.equals(Material.TRIPWIRE_HOOK)|
					m.equals(Material.VINE) |
					m.equals(Material.FIRE) |
					m.equals(Material.valueOf("WALL_BANNER")) |
					m.equals(Material.WALL_SIGN) |
					m.equals(Material.valueOf("DOUBLE_PLANT")) |
					m.equals(Material.valueOf("LONG_GRASS")) |
					m.equals(Material.GRASS) |
					m.equals(Material.valueOf("DOUBLE_PLANT")) |
					m.equals(Material.valueOf("RED_ROSE")) |
					m.equals(Material.valueOf("YELLOW_FLOWER")) |
					m.equals(Material.DEAD_BUSH) |
					m.equals(Material.BROWN_MUSHROOM) |
					m.equals(Material.RED_MUSHROOM)) {
				return true;
			}
		} else {
			if(m.equals(Material.AIR) ||
					m.equals(Material.WATER) ||
					m.equals(Material.WATER) ||
					m.equals(Material.LILY_PAD) ||
					m.equals(Material.SUGAR_CANE) ||
					//m.equals(Material.LEGACY_CROPS) ||
					m.equals(Material.WHEAT) ||
					m.equals(Material.CARROTS) ||
					m.equals(Material.POTATOES) ||
					m.equals(Material.BEETROOTS) ||
					m.equals(Material.SEA_PICKLE) ||
					m.equals(Material.SEAGRASS) ||
					m.equals(Material.BRAIN_CORAL_FAN) ||
					m.equals(Material.BRAIN_CORAL_WALL_FAN) ||
					m.equals(Material.BUBBLE_CORAL_FAN) ||
					m.equals(Material.BUBBLE_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_BRAIN_CORAL_FAN) ||
					m.equals(Material.DEAD_BRAIN_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_BUBBLE_CORAL_FAN) ||
					m.equals(Material.DEAD_BUBBLE_CORAL_WALL_FAN) ||
					m.equals(Material.FIRE_CORAL_FAN) ||
					m.equals(Material.FIRE_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_FIRE_CORAL_FAN) ||
					m.equals(Material.DEAD_FIRE_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_HORN_CORAL_FAN) ||
					m.equals(Material.DEAD_HORN_CORAL_WALL_FAN) ||
					m.equals(Material.DEAD_TUBE_CORAL_FAN) ||
					m.equals(Material.DEAD_TUBE_CORAL_WALL_FAN) ||
					m.equals(Material.HORN_CORAL_FAN) ||
					m.equals(Material.HORN_CORAL_WALL_FAN) ||
					m.equals(Material.TUBE_CORAL_FAN) ||
					m.equals(Material.TUBE_CORAL_WALL_FAN) || 
					m.equals(Material.IRON_TRAPDOOR) ||
					m.equals(Material.TORCH) ||
					m.equals(Material.REDSTONE) ||
					m.equals(Material.COMPARATOR) ||
					//m.equals(Material.REDSTONE_COMPARATOR_ON) ||
					m.equals(Material.REDSTONE_WIRE) ||
					m.equals(Material.REDSTONE_TORCH) ||
					//m.equals(Material.REDSTONE_TORCH_OFF) ||
					m.equals(Material.TRIPWIRE) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.ACACIA_BUTTON) ||
					m.equals(Material.BIRCH_BUTTON) ||
					m.equals(Material.DARK_OAK_BUTTON) ||
					m.equals(Material.JUNGLE_BUTTON) ||
					m.equals(Material.OAK_BUTTON) ||
					m.equals(Material.SPRUCE_BUTTON) ||
					m.equals(Material.LEVER) ||
					m.equals(Material.TRIPWIRE_HOOK)||
					m.equals(Material.FLOWER_POT) ||
					m.equals(Material.RAIL) ||
					m.equals(Material.ACTIVATOR_RAIL) ||
					m.equals(Material.DETECTOR_RAIL) ||
					m.equals(Material.POWERED_RAIL) ||
					m.equals(Material.END_ROD) ||
					m.equals(Material.VINE) ||
					m.equals(Material.BLACK_BANNER) ||
					m.equals(Material.BLACK_WALL_BANNER) ||
					m.equals(Material.BLUE_BANNER) ||
					m.equals(Material.BLUE_WALL_BANNER) ||
					m.equals(Material.BROWN_BANNER) ||
					m.equals(Material.BROWN_WALL_BANNER) ||
					m.equals(Material.CYAN_BANNER) ||
					m.equals(Material.CYAN_WALL_BANNER) ||
					m.equals(Material.GRAY_BANNER) ||
					m.equals(Material.GRAY_WALL_BANNER) ||
					m.equals(Material.GREEN_BANNER) ||
					m.equals(Material.GREEN_WALL_BANNER) ||
					m.equals(Material.LIME_BANNER) ||
					m.equals(Material.LIME_WALL_BANNER) ||
					m.equals(Material.LIGHT_BLUE_BANNER) ||
					m.equals(Material.LIGHT_BLUE_WALL_BANNER) ||
					m.equals(Material.LIGHT_GRAY_BANNER) ||
					m.equals(Material.LIGHT_GRAY_WALL_BANNER) ||
					m.equals(Material.MAGENTA_BANNER) ||
					m.equals(Material.MAGENTA_WALL_BANNER) ||
					m.equals(Material.ORANGE_BANNER) ||
					m.equals(Material.ORANGE_WALL_BANNER) ||
					m.equals(Material.PINK_BANNER) ||
					m.equals(Material.PINK_WALL_BANNER) ||
					m.equals(Material.PURPLE_BANNER) ||
					m.equals(Material.PURPLE_WALL_BANNER) ||
					m.equals(Material.RED_BANNER) ||
					m.equals(Material.RED_WALL_BANNER) ||
					m.equals(Material.WHITE_BANNER) ||
					m.equals(Material.WHITE_WALL_BANNER) ||
					m.equals(Material.YELLOW_BANNER) ||
					m.equals(Material.YELLOW_WALL_BANNER) ||
					
					m.equals(Material.SIGN) ||
					m.equals(Material.WALL_SIGN) ||
					m.equals(Material.SUNFLOWER) ||
					m.equals(Material.LILAC) ||
					m.equals(Material.ROSE_BUSH) ||
					m.equals(Material.PEONY) ||
					m.equals(Material.LARGE_FERN)||
					m.equals(Material.TALL_GRASS) ||
					m.equals(Material.GRASS) ||
					m.equals(Material.ROSE_RED) ||
					m.equals(Material.DANDELION) ||
					m.equals(Material.DANDELION_YELLOW) ||
					m.equals(Material.POPPY) ||
					m.equals(Material.BLUE_ORCHID) ||
					m.equals(Material.ALLIUM) ||
					m.equals(Material.AZURE_BLUET) ||
					m.equals(Material.ORANGE_TULIP) ||
					m.equals(Material.PINK_TULIP) ||
					m.equals(Material.RED_TULIP) ||
					m.equals(Material.WHITE_TULIP) ||
					m.equals(Material.OXEYE_DAISY) ||
					m.equals(Material.FERN) ||
					m.equals(Material.COCOA) ||
					m.equals(Material.LADDER) ||
					m.equals(Material.IRON_BARS) ||
					m.equals(Material.DEAD_BUSH) ||
					m.equals(Material.BROWN_MUSHROOM) ||
					m.equals(Material.RED_MUSHROOM)) {
				return true;
			}
		}
		return false;
		
	}
	/*
	private Boolean canBeAccessed(Location loc, Location center) {
		Vector lc = new Vector(center.getX() - loc.getX(), center.getY() - loc.getY(), center.getZ() - loc.getZ());
		Vector lcOrig = new Vector(lc.getX(), lc.getY(), lc.getZ());
		
		lc = lc.normalize();
		
		lc.setX(lc.getX());
		lc.setY(lc.getY());
		lc.setZ(lc.getZ());
		
		for(double i = 0; i <= lcOrig.length(); ) {
			Location temp = loc.add(lc.multiply(i));
			if(canPass(temp)) {
				i++;
				if(i == lcOrig.length()) {
					return true;
				}
			} else {
				return false;
			}
		}
		return false;
	}
	*/
	private void createSmokeCloud(Location center, Grenade owner) {
		if(owner.smokeEnabled == true) {
			List<Location> temp = new ArrayList<Location>();
			for(int i = 0; i <= owner.smokeRadius; i++) {
				for(int x = 0; x <= owner.smokeRadius; x++) {
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
				for(int i = 1; i<= this.smokeRadius; i++) {
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
				private Grenade owner;
				
				public smokeThread(Grenade owner, List<Location> toSmoke) {
					this.toSmoke = toSmoke;
					this.owner = owner;
				}

				
				@Override
				public void run() {
					
					while(1 != 0) {
						try {
							for(Location loc : toSmoke) {
								Location loc2 = new Location(loc.getWorld(), loc.getX() - 0.125D, loc.getY() - 0.125D, loc.getZ() - 0.125D);
								loc.getWorld().spawnParticle(owner.getSmokeParticle(), loc2, owner.getSmokedensity(),  0.25D, 0.25D, 0.25D, 0.0001D);
								
								if(owner.getPotionEnabled() != null && owner.getPotionEnabled() == true) {
									Collection<Entity> affected = loc.getWorld().getNearbyEntities(loc, owner.getSmokeAffectionRange(), owner.getSmokeAffectionRange(), owner.getSmokeAffectionRange());
									for(Entity ent : affected) {
										if(ent instanceof Player && !((Player) ent).getGameMode().equals(GameMode.SPECTATOR)) {
											Player ent2 = (Player)ent;
											Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
												
												@Override
												public void run() {
													//PotionEffect pe = new PotionEffect(PotionEffectType.CONFUSION, 10, 1);
													PotionEffect pe = owner.getPotionEffect();
													if(!ent2.hasPotionEffect(pe.getType()) || ent2.getPotionEffect(pe.getType()).getDuration() <= 80.0D
															//TODO: CHECK FOR GAS-MASK
															) {
														ent2.addPotionEffect(pe, true);
													}											
												}
											});
											
											
										}
									}
								}
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
			
			smokeThread sThread = new smokeThread(owner, smokeList);
			
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
			}, owner.smokeDuration);
		}
	}
	
	
	
	
	private class flyThread extends Thread {
		
		private UUID thrower;
		private Item grenade;
		private Grenade gren;
		private boolean detonated;
		private boolean explodeOnContact;
		private int taskID;
		private Location throwerLocation;
		private List<Block> blacklistBlocks;
		
		public flyThread(UUID thrower, Item grenade, Grenade gren, Location throwerLoc) {
			this.thrower = thrower;
			this.grenade = grenade;
			this.gren = gren;
			this.detonated = false;
			this.throwerLocation = throwerLoc;
			this.explodeOnContact = this.gren.getExplodeOnImpact();
			this.blacklistBlocks = new ArrayList<Block>();
			this.blacklistBlocks.add(throwerLoc.getBlock());
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.DOWN));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.EAST_NORTH_EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.EAST_SOUTH_EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.NORTH_NORTH_EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.NORTH_NORTH_WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.SOUTH_SOUTH_EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.SOUTH_SOUTH_WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.WEST_NORTH_WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.WEST_SOUTH_WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.NORTH));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.NORTH_EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.NORTH_WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.SOUTH));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.SOUTH_EAST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.SOUTH_WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.WEST));
			this.blacklistBlocks.add(throwerLoc.getBlock().getRelative(BlockFace.UP));
			//this.taskID = null;
		}
		public void addTaskIDToKill(int taskID) {
			this.taskID = taskID;
		}
		
		private Boolean isGlass(Material m, World w) {
			if((GunGamePlugin.instance.griefHelper.isGGWorld(w) || GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.SHOTS_BREAK_GLASS, w)) && Util.isGlass(m)/*(m.equals(Material.GLASS) ||
					m.equals(Material.GLASS_PANE) ||
					m.equals(Material.STAINED_GLASS) ||
					m.equals(Material.STAINED_GLASS_PANE))*/) {
				return true;
			} else {
				return false;
			}
		}
		private Boolean isLocationNotBlacklisted(Block b) {
			if(this.blacklistBlocks.contains(b)) {
				return false;
			} else {
				return true;
			}
		}
		private void breakIt(Block b) {
			Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					b.breakNaturally();
					b.setType(Material.AIR);
					b.getWorld().playSound(b.getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
				}
			});
		}
		private void detonate() {
			//Bukkit.getPlayer(this.thrower).sendMessage("THMP");
			
			Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
				
				//@SuppressWarnings("deprecation")
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					createExplosion(thrower, gren, grenade);
					//Bukkit.getPlayer(thrower).sendMessage("MEH");
					detonated = true;
					killTask();
					stop();
					//destroy();
				}
			});
		}
		
		private Boolean stoppedFlying(Vector v) {
			if(v.getX() == 0.0D || v.getZ() == 0.0D || v.getY() == 0.0D) {
				return true;
			}
			return false;
		}
		

		private void killTask() {
			if(this.explodeOnContact == true) {
				Bukkit.getScheduler().cancelTask(this.taskID);
			}			
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			while(!detonated && grenade != null) {
				try {
					
					if(grenade != null && !grenade.getLocation().getChunk().isLoaded()) {
						try {
							grenade.getLocation().getChunk().load();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
						
					Vector vel = new Vector(grenade.getVelocity().getX(), grenade.getVelocity().getY(), grenade.getVelocity().getZ());
					//Bukkit.getPlayer(this.thrower).sendMessage("VX: " + vel.getX() +"     VZ: " + vel.getZ());
					Material m = grenade.getLocation().add(vel).add(vel).getBlock().getType();
					//Bukkit.getPlayer(this.thrower).sendMessage("START");
					
					Block bAtLoc = grenade.getLocation().getBlock();
					Block bNxtLoc = grenade.getLocation().add(vel).getBlock();
					Block bNxtLocUpper = bNxtLoc.getRelative(BlockFace.UP);
					Block bNxtLocLower = bNxtLoc.getRelative(BlockFace.DOWN);
					World w = bNxtLoc.getWorld();
					//WENN ES NOCH FLIEGT-->Glas kaputt machen
					if(grenade != null && !stoppedFlying(vel)) {
						if(isGlass(m, w)) {
							breakIt(bNxtLoc);
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.DOWN).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.DOWN));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.UP).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.UP));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.NORTH).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.NORTH));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.EAST).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.SOUTH).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.SOUTH));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.WEST).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.WEST));
							grenade.setVelocity(vel);				
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.NORTH_EAST).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.NORTH_EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.NORTH_WEST).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.NORTH_WEST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.SOUTH_EAST).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.SOUTH_EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLoc.getRelative(BlockFace.SOUTH_WEST).getType(), w)) {
							breakIt(bNxtLoc.getRelative(BlockFace.SOUTH_WEST));
							grenade.setVelocity(vel);
							//BASIS GECHECKT
							//JETZT EBENE ÜBER LOCATION CHECKEN
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.NORTH).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.NORTH));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.EAST).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.SOUTH).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.SOUTH));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.WEST).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.WEST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.NORTH_EAST).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.NORTH_EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.NORTH_WEST).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.NORTH_WEST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.SOUTH_EAST).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.SOUTH_EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocUpper.getRelative(BlockFace.SOUTH_WEST).getType(), w)) {
							breakIt(bNxtLocUpper.getRelative(BlockFace.SOUTH_WEST));
							grenade.setVelocity(vel);
							//OBERE EBENE GECHECKT
							//JETZT UNTERE CHECKEN
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.NORTH).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.NORTH));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.EAST).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.SOUTH).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.SOUTH));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.WEST).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.WEST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.NORTH_EAST).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.NORTH_EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.NORTH_WEST).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.NORTH_WEST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.SOUTH_EAST).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.SOUTH_EAST));
							grenade.setVelocity(vel);
						} else if(isGlass(bNxtLocLower.getRelative(BlockFace.SOUTH_WEST).getType(), w)) {
							breakIt(bNxtLocLower.getRelative(BlockFace.SOUTH_WEST));
							grenade.setVelocity(vel);
						}
					} else if(grenade != null && explodeOnContact == true 
							&& (grenade.hasMetadata("GG_HitTank") ||
									(grenade.isOnGround() && !grenade.getLocation().equals(throwerLocation)) || 
									(isLocationNotBlacklisted(bAtLoc) && !grenade.getLocation().equals(throwerLocation) && (grenade.isOnGround() 
							|| !canPass(m)
							|| !canPass(bAtLoc.getRelative(BlockFace.DOWN).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.UP).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.NORTH).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.EAST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.SOUTH).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.WEST).getType())
							/*|| !canPass(bAtLoc.getRelative(BlockFace.EAST_NORTH_EAST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.EAST_SOUTH_EAST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.WEST_NORTH_WEST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.WEST_SOUTH_WEST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.NORTH_NORTH_EAST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.NORTH_NORTH_WEST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.SOUTH_SOUTH_EAST).getType())
							|| !canPass(bAtLoc.getRelative(BlockFace.SOUTH_SOUTH_WEST).getType())*/)))) {
								if(!detonated) {
									this.detonated = true;									
									detonate();
								}
							
					} else if(grenade != null) {
						if(explodeOnContact && 
								GunGamePlugin.instance.tankManager.isPositionInATankHitbox(grenade.getLocation()) != null && 
								GunGamePlugin.instance.tankManager.isPositionInATankHitbox(grenade.getLocation()).getDriverUUID() != null &&
								!GunGamePlugin.instance.tankManager.isPositionInATankHitbox(grenade.getLocation()).getDriverUUID().equals(thrower)) {
							detonated = true;						
							createExplosion(thrower, gren, grenade);
							killTask();
							stop();
						} else {
							Collection<Entity> entitiesInRange = grenade.getNearbyEntities(0.25D, 0.25D, 0.25D);
							if(!entitiesInRange.isEmpty()) {
								for(Entity ent : entitiesInRange) {
									if(ent instanceof LivingEntity) {
										LivingEntity damaged = (LivingEntity)ent;
										if(ent instanceof Player) {
											Player throwerPP = null;
											if(thrower != null) {
												throwerPP = Bukkit.getPlayer(this.thrower);
											}
											if((Player) ent != throwerPP) {
												Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
													
													@Override
													public void run() {
														detonated = true;
														Player throwerP = null;
														if(thrower != null) {
															throwerP = Bukkit.getPlayer(thrower);
														}
														
														if(throwerP != null) {
															damaged.damage(1.5D, (Entity)throwerP);
														} else {
															damaged.damage(1.5D);
														}
														
														if(explodeOnContact == true) {
															createExplosion(thrower, gren, grenade);
														}
														killTask();
														stop();
													}
												});
											}
										} else {
											Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
													
												@Override
												public void run() {
													detonated = true;
													Player throwerP = null;
													if(thrower != null) {
														throwerP = Bukkit.getPlayer(thrower);
													}
													
													if(throwerP != null) {
														damaged.damage(1.5D, (Entity)throwerP);
													} else {
														damaged.damage(1.5D);
													}
													if(explodeOnContact == true) {
														createExplosion(thrower, gren, grenade);
													}
													killTask();
													stop();
												}
											});
										}
									}
								}
							}
						}
						}							
						try {
							sleep(50);
							/*if(!detonated && grenade != null) {
								run();
							}*/
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
				
		}			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public WeaponManager getManager() {
		return manager;
	}
	public WeaponSoundSet getSoundSet() {
		return this.soundSet;
	}
	public void setSoundSet(WeaponSoundSet soundset) {
		this.soundSet = soundset;
	}

	public void setManager(WeaponManager manager) {
		this.manager = manager;
	}

	public String getGrenadeName() {
		return GrenadeName;
	}

	public void setGrenadeName(String grenadeName) {
		GrenadeName = grenadeName;
	}

	public FileConfiguration getWeaponFile() {
		return weaponFile;
	}

	public void setWeaponFile(FileConfiguration weaponFile) {
		this.weaponFile = weaponFile;
	}

	public EGrenadeType getGrenadeType() {
		return type;
	}

	public void setType(EGrenadeType type) {
		this.type = type;
	}

	public Integer getTimer() {
		return smokeDuration;
	}

	public void setSmokeDuration(Integer timer) {
		this.smokeDuration = timer;
	}

	public Integer getRadius() {
		return radius;
	}

	public void setRadius(Integer radius) {
		this.radius = radius;
	}

	public float getStrength() {
		return strength;
	}

	public void setStrength(float strength2) {
		this.strength = strength2;
	}

	public Integer getSmokedensity() {
		return smokeDensity;
	}
	public void setSmokedensity(Integer smokeDEnsity) {
		smokeDensity = smokeDEnsity;
	}
	public Double getSmokeAffectionRange() {
		return this.smokeAffectionRange;
	}
	public void setSmokeAffectionRange(Double range) {
		this.smokeAffectionRange = range;
	}

	public PotionEffect getPotionEffect() {
		return potionEffect;
	}

	public void setPotionEffect(PotionEffect potionEffect) {
		this.potionEffect = potionEffect;
	}

	public Integer getFuse() {
		return fuse;
	}

	public void setFuse(Integer fuse) {
		this.fuse = fuse;
	}

	public Particle getSmokeParticle() {
		return smokeParticle;
	}

	public void setSmokeParticle(Particle smokeParticle) {
		this.smokeParticle = smokeParticle;
	}

	public Boolean getBreakBlocks() {
		return breakBlocks;
	}

	public void setBreakBlocks(Boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}

	public Boolean getSmokeEnabled() {
		return smokeEnabled;
	}

	public void setSmokeEnabled(Boolean smokeEnabled) {
		this.smokeEnabled = smokeEnabled;
	}

	public Integer getClusterCount() {
		return clusterCount;
	}

	public void setClusterCount(Integer clusterCount) {
		this.clusterCount = clusterCount;
	}

	public Boolean getFireEnabled() {
		return fireEnabled;
	}

	public void setFireEnabled(Boolean fireEnabled) {
		this.fireEnabled = fireEnabled;
	}

	public ItemStack getGrenadeItem() {
		return grenadeItem;
	}

	public void setGrenadeItem(ItemStack grenadeItem) {
		this.grenadeItem = grenadeItem;
	}

	public Integer getSmokeRadius() {
		return smokeRadius;
	}

	public void setSmokeRadius(Integer samokeRadius) {
		this.smokeRadius = samokeRadius;
	}

	public Boolean getPotionEnabled() {
		return potionEnabled;
	}

	public void setPotionEnabled(Boolean potionEnabled) {
		this.potionEnabled = potionEnabled;
	}

	public Boolean getExplosionNoDamage() {
		return explosionNoDamage;
	}

	public void setExplosionNoDamage(Boolean explosionDamage) {
		this.explosionNoDamage = explosionDamage;
	}

	public Boolean getExplodeOnImpact() {
		return explodeOnImpact;
	}

	public void setExplodeOnImpact(Boolean explodeOnImpact) {
		this.explodeOnImpact = explodeOnImpact;
	}

	public Boolean isStandardWeapon() {
		return standardWeapon;
	}

	public void setStandardWeapon(Boolean standardWeapon) {
		this.standardWeapon = standardWeapon;
	}

}
