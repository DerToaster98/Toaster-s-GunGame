package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;

public class C4 implements Listener {
	
	public static Integer power = GunGamePlugin.instance.getConfig().getInt("Config.Items.C4.Power");
	public static Integer fuse = GunGamePlugin.instance.getConfig().getInt("Config.Items.C4.Fuse");
	public static Boolean createFire = GunGamePlugin.instance.getConfig().getBoolean("Config.Items.C4.Fire");	
	public static HashMap<UUID, List<Location>> BombsOfPlayer = new HashMap<UUID, List<Location>>();
	public static HashMap<UUID, List<ArmorStand>> ArmorStandsOfPlayer = new HashMap<UUID, List<ArmorStand>>();
	
	public static ItemStack c4Throwable(Integer stacksize) {
		ItemStack C4 = null;
		if(GunGamePlugin.instance.serverPre113) {
			C4 = new ItemStack(Material.valueOf("SULPHUR"), stacksize);
		} else {
			C4 = new ItemStack(Material.GUNPOWDER, stacksize);
		}
		
		ItemMeta meta = C4.getItemMeta();
		
		meta.setDisplayName(LangUtil.buildItemName("C4"));
		List<String> lore = new ArrayList<String>();
		for(String s : LangUtil.buildItemLore("C4")) {
			lore.add(s);
		}
		meta.setLore(lore);
		
		C4.setItemMeta(meta);
		
		C4 = ItemUtil.setGunGameItem(C4);
		C4 = ItemUtil.addTags(C4, "GunGame_Item", "C4");
		return C4;
	}
	
	public static ItemStack c4Remote() {
		ItemStack remote = new ItemStack(Material.FLINT);
		
		ItemMeta meta = remote.getItemMeta();
		
		meta.setDisplayName(LangUtil.buildItemName("C4.Remote"));
		List<String> lore = new ArrayList<String>();
		for(String s: LangUtil.buildItemLore("C4.Remote")) {
			lore.add(s);
		}
		meta.setLore(lore);
		
		remote.setItemMeta(meta);
		
		remote = ItemUtil.setGunGameItem(remote);
		remote = ItemUtil.addTags(remote, "GunGame_Item", "C4_Primer");
		return remote;
	}
	
	public void kaboom(Player p) {
		kaboom(p.getUniqueId(), 1.0, null, 0);	
	}
	
	public void kaboom(UUID c4PlacerID, Double damageModifier, Location startPos, double maxDistance) {
		if(ArmorStandsOfPlayer != null && ArmorStandsOfPlayer.containsKey(c4PlacerID) && ArmorStandsOfPlayer.get(c4PlacerID) != null && ArmorStandsOfPlayer.get(c4PlacerID).size() > 0) {
			List<ArmorStand> sts = new ArrayList<ArmorStand>(ArmorStandsOfPlayer.get(c4PlacerID));
			List<Location> bombs = new ArrayList<Location>(BombsOfPlayer.get(c4PlacerID));
			if(sts != null) {
				for(ArmorStand st : sts) {	
					if((maxDistance <= 0 || startPos == null) || (startPos != null && Math.abs(startPos.distance(st.getLocation())) <= Math.abs(maxDistance))) {
						st.remove();
						ArmorStandsOfPlayer.get(c4PlacerID).remove(st);
					}
				}
			ArmorStandsOfPlayer.remove(c4PlacerID);
			ArmorStandsOfPlayer.put(c4PlacerID, sts);
			}
			if(bombs != null) {
				for(Location bomb : bombs) {					
					if((maxDistance <= 0 || startPos == null) || (startPos != null && Math.abs(startPos.distance(bomb)) <= Math.abs(maxDistance))) {
						if(
								(
										bomb.getWorld().hasMetadata("GG_ExplosionBreakBlocks") && 
										bomb.getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean()
								)
								||
								(
										GunGamePlugin.instance.griefHelper.isGGWorld(bomb.getWorld()) &&
										GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.EXPLOSIONS, bomb.getWorld())
								)
						) {
							bomb.getBlock().setType(Material.AIR);
							bomb.getBlock().breakNaturally();
						}
							
						Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
							
							@Override
							public void run() {
								Util.createExplosion(bomb, createFire, true, true, true, ((Double)(power * damageModifier)).floatValue(), c4PlacerID, ((Double)((power / 2) * damageModifier)).intValue(), false);
							}
						}, 1);
						BombsOfPlayer.get(c4PlacerID).remove(bomb);
					}
				}
				BombsOfPlayer.remove(c4PlacerID);
				BombsOfPlayer.put(c4PlacerID, bombs);
			}
		}
	}
	
	public static void addC4(Location loc, Player p) {
		Location loc2 = loc;
		List<Location> temp = null;
		if(BombsOfPlayer.get(p.getUniqueId()) != null) {
			temp = BombsOfPlayer.get(p.getUniqueId());
		} else {
			temp = new ArrayList<Location>();
		}
		if(loc2 != null) {
			temp.add(loc2);
			BombsOfPlayer.put(p.getUniqueId(), temp);
		}		
	}
	private static boolean placeC4(Material m) {
		if(	Util.isFullBlock(m)) 
		{
			return true;
		} else {
			return false;
		}
	}
	public static void placeArmorStand(EulerAngle angle, Location loc, Player p) {
		//STAT
		if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
			GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementC4Planted();
		}
		//STAT END
		ItemStack temp = null;
		if(GunGamePlugin.instance.serverPre113) {
			temp = new ItemStack(Material.valueOf("SULPHUR"));
		} else {
			temp = new ItemStack(Material.GUNPOWDER);
		}
		temp.setAmount(1);
		ArmorStand st = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		
		st.setAI(false);
		st.setCollidable(false);
		st.setGravity(false);
		st.setInvulnerable(true);
		st.setHelmet(temp);
		st.setVisible(false);
		st.setHeadPose(angle);	
		//NBT Tags f�r entsch�rfen und sp�terer Zuordnung
		st.setMetadata("GG_C4_Entity", new FixedMetadataValue(GunGamePlugin.instance, true));
		st.setMetadata("GG_C4_Placer", new FixedMetadataValue(GunGamePlugin.instance, p.getUniqueId().toString()));
		
		st.teleport(loc);
		
		List<ArmorStand> tempL = null;
		if(ArmorStandsOfPlayer.get(p.getUniqueId()) != null) {
			tempL = ArmorStandsOfPlayer.get(p.getUniqueId());
		} else {
			tempL = new ArrayList<ArmorStand>();
		}
		if(st != null) {
			tempL.add(st);
			ArmorStandsOfPlayer.put(p.getUniqueId(), tempL);
		}		
	}
	public static void placeArmorStand(Location loc, Player p) {		
		//Location spawnArmorStand = new Location(loc.getWorld(), loc.getBlockX(), loc.getY() -1 + 0.05D, loc.getBlockZ());
		Location locUnder = new Location(loc.getWorld(), loc.getX(), loc.getY() -1, loc.getZ());
			Location locXP = new Location (loc.getWorld(), loc.getBlockX() +1, loc.getBlockY(), loc.getBlockZ());
			Location locXN = new Location (loc.getWorld(), loc.getBlockX() -1, loc.getBlockY(), loc.getBlockZ());
			if(placeC4(locXP.getBlock().getType()) == true) {
				//WEST
				Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ()+0.5D);
				placeArmorStand(new EulerAngle(1.5708D, 1.5708D, 0.0D), loc2, p);
			} else if(placeC4(locXN.getBlock().getType()) == true) {
				//EAST
				Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ()+0.5D);
				placeArmorStand(new EulerAngle(1.5708D, 4.71239D, 0.0D), loc2, p);
			} else {
				Location locZP = new Location (loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() +1);
				Location locZN = new Location (loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() -1);
				if(placeC4(locZP.getBlock().getType()) == true) {
					//NORTH
					Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ() +0.5D);
					placeArmorStand(new EulerAngle(1.5708D, 3.14159D, 0.0D), loc2, p);
				} else if(placeC4(locZN.getBlock().getType()) == true) {
					//SOUTH
					Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ() +0.5D);
					placeArmorStand(new EulerAngle(1.5708D, 0.0D, 0.0D), loc2, p);
				} else if(placeC4(locUnder.getBlock().getType()) == true) {
					//UP
					Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ()+0.5D);
					placeArmorStand(new EulerAngle(0.0D, 0.0D, 0.0D), loc2, p);
				}
			}
	}
	public static void placeArmorStand(Location loc, Player p, BlockFace face) {
		if(face == BlockFace.UP) {		
			Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() +0.05D, loc.getZ()+0.5D);
			placeArmorStand(new EulerAngle(0.0D, 0.0D, 0.0D), loc2, p);
		}
		if(face == BlockFace.DOWN) {
			Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -2 +0.1D, loc.getZ()+0.5D);
			placeArmorStand(new EulerAngle(3.14159, 0.0D, 0.0D),loc2, p);
		}
		if(face == BlockFace.NORTH) {
			Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ() -1 +0.5D);
			placeArmorStand(new EulerAngle(1.5708D, 3.14159D, 0.0D), loc2, p);
		}
		
		if(face == BlockFace.EAST) {
			Location loc2 = new Location(loc.getWorld(), loc.getX() +1 +0.5D, loc.getY() -1 +0.05D, loc.getZ()+0.5D);
			placeArmorStand(new EulerAngle(1.5708D, 4.71239D, 0.0D), loc2, p);
		}
		
		if(face == BlockFace.WEST) {
			Location loc2 = new Location(loc.getWorld(), loc.getX() -1 +0.5D, loc.getY() -1 +0.05D, loc.getZ()+0.5D);
			placeArmorStand(new EulerAngle(1.5708D, 1.5708D, 0.0D), loc2, p);
		}
		
		if(face == BlockFace.SOUTH) {
			Location loc2 = new Location(loc.getWorld(), loc.getX() +0.5D, loc.getY() -1 +0.05D, loc.getZ() +1 +0.5D);
			placeArmorStand(new EulerAngle(1.5708D, 0.0D, 0.0D), loc2, p);
		}
	}
	
	public void startExplosion(Player p) {
		p.getInventory().getItem(getC4Remote(p)).setType(Material.GLOWSTONE_DUST);
		Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable(){
			
			@Override
			public void run() {
				p.getInventory().getItem(getC4Remote(p)).setType(Material.FLINT);
				kaboom(p);
			}
			
		}, fuse);
		
	}
	
	public Integer getC4Remote(Player p) {
		Integer slot = null;
		for(int i = 0; i <= p.getInventory().getContents().length; i++) {
			ItemStack item = p.getInventory().getItem(i);	
			if(isC4Remote(item) == true) {
				slot = i;
			}
		}
		return slot;	
	}
	private boolean isC4Remote(ItemStack item) {
		if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.hasTag(item, "GunGame_Item", "C4_Primer")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	private boolean isC4Item(ItemStack item) {
		if(ItemUtil.isGunGameItem(item)) {
			if(ItemUtil.hasTag(item, "GunGame_Item", "C4")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	private Boolean canPass(Material m) {
			if(m.equals(Material.AIR) ||
					m.equals(Material.WATER) ||
					m.equals(Material.SUGAR_CANE) ||
					m.equals(Material.WHEAT) ||
					m.equals(Material.CARROTS) ||
					m.equals(Material.POTATOES) ||
					m.equals(Material.BEETROOTS) ||
					m.equals(Material.SEA_PICKLE) ||
					m.equals(Material.SEAGRASS) ||
					m.equals(Material.TORCH) ||
					m.equals(Material.REDSTONE) ||
					m.equals(Material.COMPARATOR) ||
					m.equals(Material.REDSTONE_WIRE) ||
					m.equals(Material.REDSTONE_TORCH) ||
					m.equals(Material.TRIPWIRE) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.STONE_BUTTON) ||
					m.equals(Material.ACACIA_BUTTON) ||
					m.equals(Material.BIRCH_BUTTON) ||
					m.equals(Material.DARK_OAK_BUTTON) ||
					m.equals(Material.JUNGLE_BUTTON) ||
					m.equals(Material.OAK_BUTTON) ||
					m.equals(Material.SPRUCE_BUTTON) ||
					m.equals(Material.LEVER) ||
					m.equals(Material.TRIPWIRE_HOOK)||
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
					Util.isWallSign(m) ||
					m.equals(Material.TALL_GRASS) ||
					m.equals(Material.GRASS) ||
					m.equals(Material.SUNFLOWER) ||
					m.equals(Material.LILAC) ||
					m.equals(Material.ROSE_BUSH) ||
					m.equals(Material.PEONY) ||
					m.equals(Material.LARGE_FERN)||
					m.equals(Material.TALL_GRASS) ||
					m.equals(Material.GRASS) ||
					m.equals(Material.WITHER_ROSE) ||
					m.equals(Material.DANDELION) ||
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
					m.equals(Material.DEAD_BUSH) ||
					m.equals(Material.BROWN_MUSHROOM) ||
					m.equals(Material.RED_MUSHROOM)) {
				return true;
			}
		return false;
	}
	@EventHandler
	public void interact(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(GunGamePlugin.instance.arenaManager.isIngame(player)) {
				
				/*if(event.getAction() == Action.RIGHT_CLICK_AIR) {
					ItemStack item = event.getItem();
						if(isC4Item(item)) {
								
								event.setCancelled(true);
								Item c4 = player.getWorld().dropItem(player.getEyeLocation(), item);
								c4.setVelocity(player.getLocation().getDirection().multiply(2D));
								c4.getItemStack().setAmount(1);
								c4.setInvulnerable(true);
								flyThread fThread = new flyThread(c4, 0.2D, player);
								fThread.start();

								int amount = player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getAmount();
								if(amount > 1) {
									player.getInventory().getItem(player.getInventory().getHeldItemSlot()).setAmount(amount -1);
								} 
								if(amount == 1) {
									player.getInventory().remove(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
								}
								player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0F, 1.0F);

						}	else if(isC4Remote(item)) {
							if(BombsOfPlayer.get(player.getUniqueId()) != null) {
								
								player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2.0F, 2.0F);
								startExplosion(player);
							} else {
								player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2.0F, 2.0F);
								startExplosion(player);
							}
						}
				} else*/ if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
					ItemStack item = event.getItem();

					if(isC4Item(item) && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							event.setCancelled(true);
							addC4(event.getClickedBlock().getLocation(), player);
							placeArmorStand(event.getClickedBlock().getLocation(), player, event.getBlockFace());
							int amount = player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getAmount();
							if(amount > 1) {
								player.getInventory().getItem(player.getInventory().getHeldItemSlot()).setAmount(amount -1);
							}
							if(amount == 1) {
								player.getInventory().remove(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
							}
							if(GunGamePlugin.instance.serverPre113) {
								player.getWorld().playSound(player.getLocation(), Sound.valueOf("ENTITY_ITEMFRAME_PLACE"), 1.0F, 1.0F);
							} else {
								player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
							}
					} else if(isC4Remote(item)) {
							if(BombsOfPlayer.get(player.getUniqueId()) != null) {
								event.setCancelled(true);
								player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2.0F, 2.0F);						
								startExplosion(player);
							} else {
								event.setCancelled(true);
								player.getWorld().playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2.0F, 2.0F);
								startExplosion(player);
							}
						}
					}
				}

	}
	
	class flyThread extends Thread {
		
		private Item c4;
		private Double radius;
		private Player thrower;
		private Boolean placed;
		
		public flyThread(Item itm, Double rad, Player thrwr) {
			this.c4 = itm;
			this.c4.getItemStack().setAmount(1);
			this.radius = rad;
			this.thrower = thrwr;
			this.placed = false;
			this.c4.setPickupDelay(99999);			
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if(!this.c4.getLocation().getChunk().isLoaded()) {
				try {
					this.c4.getLocation().getChunk().load();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			
			try {
				
				Vector velocity = new Vector(
						this.c4.getVelocity().getX(),
						this.c4.getVelocity().getY(),
						this.c4.getVelocity().getZ());
				velocity = velocity.normalize();
				velocity.setX(velocity.getX() / (velocity.length() * 8));
				velocity.setY(velocity.getY() / (velocity.length() * 8));
				velocity.setZ(velocity.getZ() / (velocity.length() * 8));
				
				Block flyingTo = c4.getLocation().add(velocity).getBlock();
				Block bAtLoc = c4.getLocation().getBlock();

				Material m = flyingTo.getType();
				
				if(this.c4.isOnGround() 
						|| !canPass(m)
						|| !canPass(bAtLoc.getRelative(BlockFace.DOWN).getType())
						|| !canPass(bAtLoc.getRelative(BlockFace.UP).getType())
						|| !canPass(bAtLoc.getRelative(BlockFace.NORTH).getType())
						|| !canPass(bAtLoc.getRelative(BlockFace.EAST).getType())
						|| !canPass(bAtLoc.getRelative(BlockFace.SOUTH).getType())
						|| !canPass(bAtLoc.getRelative(BlockFace.WEST).getType())) {
					
					this.placed = true;
				} else {
					Collection<Entity> possibleTargets = this.c4.getNearbyEntities(this.radius, this.radius, this.radius);
					if(!possibleTargets.isEmpty()) {
						for(Entity entity : possibleTargets) {
							if(entity instanceof LivingEntity) {

								if(entity instanceof Player) {
									Player target2 = (Player) entity;
									if(!target2.getUniqueId().equals(this.thrower.getUniqueId())) {
										Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
											
											@Override
											public void run() {
												target2.damage(1.5D, thrower);
											}
										});
										
										this.placed = true;
									}
									
								} else {
									LivingEntity damaged = (LivingEntity)entity;
									Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
										
										@Override
										public void run() {
											damaged.damage(1.5D, thrower);
										}
									});
									
									this.placed = true;
								}				
							}
						}
					}
		
				}
				
				if(this.placed == true) {
					Location targetLoc = this.c4.getLocation().getBlock().getLocation();
					addC4(targetLoc, this.thrower);
					Bukkit.getScheduler().runTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							placeArmorStand(targetLoc, thrower);
						}
					});
					
					if(GunGamePlugin.instance.serverPre113) {
						this.c4.getWorld().playSound(targetLoc, Sound.valueOf("ENTITY_ITEMFRAME_PLACE"), 1.0F, 1.0F);
					} else {
						this.c4.getWorld().playSound(targetLoc, Sound.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
					}
					this.c4.remove();
					
					stop();
				}
				
				try {
					sleep(10);
					if(!this.placed) {
						run();						
					}

				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	

}
