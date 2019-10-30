package de.MrBaumeister98.GunGame.Game.Listeners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
//import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
//import org.bukkit.World;
//import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
//import org.bukkit.event.world.WorldLoadEvent;
//import org.bukkit.event.world.WorldSaveEvent;
//import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaManager;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.GunEngine.GunItemUtil;
import de.MrBaumeister98.GunGame.GunEngine.Landmine;

public class GameListener implements Listener {
	
	public ArenaManager manager = GunGamePlugin.instance.arenaManager;
	public static List<String> processed = new ArrayList<String>();
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void copyAdvancementFilesToNewWorld(WorldLoadEvent event) {
		//World world = event.getWorld();
		World world = event.getWorld();
		/*if(!processed.contains(world.getName())) {	
			
			processed.add(world.getName());
			Main.plugin.achUtil.copyToWorld(world, Main.plugin.achUtil.getAdvancementFolder());
			
		}*/
		if(!GunGamePlugin.instance.achUtil.areAchievementsUpToDate(world) && !GameListener.processed.contains(world.getName())) {
			GameListener.processed.add(world.getName());
			GunGamePlugin.instance.achUtil.copyToWorld(world, GunGamePlugin.instance.achUtil.getAdvancementFolder());
			if(!GameListener.processed.contains(world.getName())) {
				GameListener.processed.add(world.getName());
			}
		} else if(GameListener.processed.contains(world.getName())) {
			if(!GameListener.processed.contains(world.getName())) {
				GameListener.processed.add(world.getName());
			}
			try {
				GunGamePlugin.instance.tankManager.respawnTanks(world);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			try {
				GunGamePlugin.instance.turretManager.respawnTurrets(world);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} else if(GunGamePlugin.instance.achUtil.areAchievementsUpToDate(world)) {
			if(!GameListener.processed.contains(world.getName())) {
				GameListener.processed.add(world.getName());
			}
			try {
				GunGamePlugin.instance.tankManager.respawnTanks(world);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			try {
				GunGamePlugin.instance.turretManager.respawnTurrets(world);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		//Main.plugin.achUtil.copyToWorld(world, Main.plugin.achUtil.getAdvancementFolder());
	}
	//DONT DELETE!!! IMPORTANT FOR NOT LOADING ANY SPAWNCHUNKS!!!
	@EventHandler(priority=EventPriority.HIGHEST)
	public void stopGeneratingSpawnChunksOnLoad(WorldLoadEvent event) {
		World world = event.getWorld();
		if(world.getName().contains("GunGame-")) {
			world.setKeepSpawnInMemory(false);
		}
	}
	//DONT DELETE!!! IMPORTANT FOR NOT LOADING ANY SPAWNCHUNKS!!!
	@EventHandler(priority=EventPriority.HIGHEST)
	public void stopGeneratingSpawnChunksOnWorldInitialization(WorldInitEvent event) {
		World world = event.getWorld();
		if(world.getName().contains("GunGame-")) {
			world.setKeepSpawnInMemory(false);
		}
	}
	@EventHandler
	public void stopHunger(FoodLevelChangeEvent event) {
		Player p = (Player) event.getEntity();
		if(this.manager.isIngame(p)) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Player killer = p.getKiller();
		try {
			if(killer != null && !killer.equals(p)) {
				if(this.manager.isIngame(killer) && this.manager.isIngame(p)) {
					this.manager.getArena(killer).addKill(killer, true);
					killer.sendMessage(LangUtil.buildKillMessage(p.getDisplayName(),this.manager.getArena(killer).getKills(killer) , this.manager.getArena(killer).statManager.getStatPlayer.get(killer.getUniqueId()).getCurrentKillStreak()));
				}
				if(this.manager.isIngame(p)) {
					p.sendMessage(LangUtil.buildDeathMessage(killer.getDisplayName()));
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		if(this.manager.isIngame(p)) {
				
		//STAT
			GunGamePlugin.instance.arenaManager.getArena(p).statManager.getStatPlayer.get(p.getUniqueId()).incrementDeaths();
			
			e.setKeepInventory(true);
			e.setDroppedExp(0);
			e.getDrops().clear();
			e.setDeathMessage(null);
				
			Arena a = this.manager.getArena(p);
		
			a.respawn(p);
		}
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Player entKiller = event.getEntity().getKiller();
		Entity ent = event.getEntity();
		if(entKiller != null && !(ent instanceof Player) && entKiller instanceof Player) {
			//STAT
			if(this.manager.isIngame(entKiller)) {
				this.manager.getArena(entKiller).addKill(entKiller, false);
				//STAT
				//GunGamePlugin.instance.arenaManager.getArena(entKiller).statManager.getStatPlayer.get(entKiller.getUniqueId()).incrementKillStreak();
			}
		}
	}
	
	@EventHandler
	public void openShop(PlayerInteractEvent e) {
			Player p = e.getPlayer();
			if(this.manager.isIngame(p)) {
				
				Block b = e.getClickedBlock();
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(Util.isShopBlock(b.getType())) {
						//OPEN SHOP
						e.setCancelled(true);
						GunGamePlugin.instance.weaponShop.openShop(p);
					}
				}
			}
		
	}
	
	@EventHandler
	public void onRageQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if(this.manager.isIngame(p.getUniqueId())) {
			this.manager.ragequit(p.getUniqueId());
		}
	}
	@EventHandler
	public void onLostConnection(PlayerKickEvent event) {
		Player p = event.getPlayer();
		if(this.manager.isIngame(p.getUniqueId())) {
			this.manager.ragequit(p.getUniqueId());
		}
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				File f = new File(FileManager.getPlayerDataFolder().getAbsolutePath(), p.getUniqueId().toString() + ".yml");
				if(f.exists() == true) {
					FileConfiguration c = YamlConfiguration.loadConfiguration(f);
					String rageQuit = c.getString("inGame");
					if(rageQuit != null) {
						Boolean quit = Boolean.valueOf(rageQuit);
						if(quit == true) {
							try {
								Util.restoreInventory(p);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}			
			}
			
		}, 1L);
		
		
	}
	
	@EventHandler
	public void onThrowItem(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		if(this.manager.isIngame(p) && !GunItemUtil.isGrenade(event.getItemDrop().getItemStack()) && !ItemUtil.isGGWeapon(event.getItemDrop().getItemStack())) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity boomer = event.getEntity();
		if(boomer instanceof TNTPrimed) {
			TNTPrimed exploder = (TNTPrimed)boomer;
			if(exploder.hasMetadata("GG_Explosive")) {
				if(exploder.hasMetadata("GG_breakNoBlocks") && event.blockList() != null || exploder.hasMetadata("GG_Physics")/* || !(boomer.getWorld().hasMetadata("GG_ExplosionBreakBlocks") && boomer.getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean())*/) {
					if(!(exploder.hasMetadata("GG_Physics") && !exploder.hasMetadata("GG_breakNoBlocks") && event.blockList() != null)) {
						event.blockList().clear();
					} else {
						UUID causer = null;
						if(exploder.hasMetadata("GG_Owner")) {
							causer = UUID.fromString(exploder.getMetadata("GG_Owner").get(0).asString());
						}
						List<Block> blockList = new ArrayList<Block>(event.blockList());
							Util.computeBlockDamage_v1_13_up(blockList, causer);
						event.blockList().clear();
					}
				} else if(!exploder.hasMetadata("GG_breakNoBlocks") /*&& !exploder.hasMetadata("GG_Physics")*/) {
					UUID causer = null;
					if(exploder.hasMetadata("GG_Owner")) {
						causer = UUID.fromString(exploder.getMetadata("GG_Owner").get(0).asString());
					}
					List<Block> blockList = new ArrayList<Block>(event.blockList());
						Util.computeBlockDamage_v1_13_up(blockList, causer);
					event.blockList().clear();
				}
				
			} else if (boomer.getWorld().hasMetadata("GG_ExplosionBreakBlocks") && !boomer.getWorld().getMetadata("GG_ExplosionBreakBlocks").get(0).asBoolean()) {
				event.blockList().clear();
			}
		}
	}
	@EventHandler
	public void onPunchTNT(PlayerInteractEvent event) {
		if(this.manager.isIngame(event.getPlayer())) {
			if(this.manager.getArena(event.getPlayer()).getGameState().equals(EGameState.GAME) && event.getAction().equals(Action.LEFT_CLICK_BLOCK) && !event.getAction().equals(Action.PHYSICAL) && !event.getPlayer().isSneaking()) {
				if(event.getClickedBlock().getType().equals(Material.TNT)) {
					event.getClickedBlock().setType(Material.AIR);
					Util.createExplosion(event.getClickedBlock().getLocation(), Util.getRandomBoolean(), Util.getRandomBoolean(), false, Util.getRandomBoolean(), 3.5f, event.getPlayer().getUniqueId(), 1 + Util.getRandomNumber(4) , false, 50);
				}
			}
		}
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if(entity instanceof LivingEntity) {
			LivingEntity damaged = (LivingEntity)entity;	
			//if(!(entity instanceof Player) || this.manager.isIngame((Player)damaged)) {
				if(event.getCause().equals(DamageCause.BLOCK_EXPLOSION) | event.getCause().equals(DamageCause.ENTITY_EXPLOSION) | event.getCause().equals(DamageCause.FALLING_BLOCK)) {
					Entity damager = event.getDamager();
					
					if(damager instanceof TNTPrimed) {
						if(damager.hasMetadata("GG_Explosive")) {
							//event.setCancelled(true);;
							Double damage = Double.valueOf(damager.getMetadata("GG_strength").get(0).asString());
							UUID damagerID = null;
							if(damager.hasMetadata("GG_Owner")) {
								 damagerID = UUID.fromString(damager.getMetadata("GG_Owner").get(0).asString());
							}							
							
							Boolean noDamage = damager.getMetadata("GG_NoDamage").get(0).asBoolean();
							
							Player p = null;
							if(damagerID != null) {
								p = Bukkit.getPlayer(damagerID);
							}
							//if(this.manager.isIngame(p)) {
								if(!noDamage) {
									if(damage > 0) {
										event.setDamage(damage);
									}
									if(p != null && damaged instanceof Player && !(damaged.getUniqueId().equals(p.getUniqueId()))) {
										damaged.damage(event.getDamage(), p);
									} else if(!(damaged instanceof Player)){
										if(this.manager.isIngame(p)) {
											damaged.damage(event.getDamage(), null);
										} else {
											damaged.damage(event.getDamage(), p);
										}
									}
								} else {
									event.setDamage(0.0D);
									damaged.damage(event.getDamage());
								}
								
							//}							
							
						}
					}
					
					if(damager instanceof FallingBlock) {
						if(damager.hasMetadata("GG_GravBlock")) {
							//event.setCancelled(true);
							Float fallDist = damager.getFallDistance();
							Double damage = null;
							if(fallDist > 0) {
								damage = 0.5D + (fallDist.doubleValue() /9.81D);
							} else {
								damage = 0.5D;
							}
							Player p = null;
							if(damager.hasMetadata("GG_Owner")) {
								p = Bukkit.getPlayer(UUID.fromString(damager.getMetadata("GG_Owner").get(0).asString()));
							}
							//if(this.manager.isIngame(p)) {
								if(damage < 0) {
									event.setDamage(damage);
								}
								if(p != null && damaged instanceof Player && !(damaged.getUniqueId().equals(p.getUniqueId()))) {
									damaged.damage(event.getDamage(), p);
								} else if(!(damaged instanceof Player)){
									damaged.damage(event.getDamage(), null);
								}
							//}
							
							
						}
					}
				}
			//}
				if(event.getDamager() instanceof EvokerFangs) {
					if(event.getDamager().hasMetadata("GG_BearTrap")) {
						event.setCancelled(true);
						
						Landmine mine = GunGamePlugin.instance.weaponManager.getLandmine(event.getDamager().getMetadata("GG_BearTrap_Parent").get(0).asString());
						
						Double dmg = mine.getBearTrapDamage();
						PotionEffect bte = new PotionEffect(PotionEffectType.SLOW, mine.getBearTrapEffectDuration(), mine.getBearTrapEffectAmplifier());
						
						UUID dmgr = UUID.fromString(event.getDamager().getMetadata("GG_BearTrap_Placer").get(0).asString());
						
						event.setDamage(dmg);
						if(event.getEntity() instanceof LivingEntity) {
							LivingEntity ent = (LivingEntity) event.getEntity();
							ent.damage(dmg, Bukkit.getEntity(dmgr));
							ent.addPotionEffect(bte, true);
						}
					}
				}
				//STAT
				/*if(damaged instanceof Player) {
					Player pTemp = (Player)damaged;
					
					if(GunGamePlugin.instance.arenaManager.isIngame(pTemp)) {
						GunGamePlugin.instance.arenaManager.getArena(pTemp).statManager.getStatPlayer.get(pTemp.getUniqueId()).incrementTakenDamage(event.getDamage());
					}
				}*/
				if(event.getDamager() instanceof Player) {
					Player pTemp = (Player)event.getDamager();
					
					//STAT
					if(GunGamePlugin.instance.arenaManager.isIngame(pTemp)) {
						try {
							GunGamePlugin.instance.arenaManager.getArena(pTemp).statManager.getStatPlayer.get(pTemp.getUniqueId()).incrementDealtDamage(event.getDamage());
						} catch(Exception ex) {
							
						}
						
					}
				}
				
				
		}		
	}
	@EventHandler(priority=EventPriority.NORMAL)
	public void onDamagePhysical(EntityDamageEvent event) {
		Entity damaged = event.getEntity();
		Double damage = event.getFinalDamage();
		
		//STAT
		if(damaged instanceof Player) {
			Player pTemp = (Player)damaged;
			
			if(GunGamePlugin.instance.arenaManager.isIngame(pTemp)) {
				GunGamePlugin.instance.arenaManager.getArena(pTemp).statManager.getStatPlayer.get(pTemp.getUniqueId()).incrementTakenDamage(damage);
			}
		}
	}
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(this.manager.isIngame(player)) {
			String cmd = event.getMessage().split(" ")[0];
			if(player.hasPermission("gungame.bypasscmdrestriction")) {
				event.setCancelled(false);
			} else {

				if(Util.allowedCmds.contains(cmd)) {
					event.setCancelled(false);
				} else {
					event.setCancelled(true);
					player.sendMessage(LangUtil.createString("lang.Errors.cmdNotAllowedInArena",
							manager.getArena(player),
							(manager.getArena(player).getArenaWorld() == null ? null : manager.getArena(player).getArenaWorld().getName()),
							player,
							null,
							null,
							null,
							null,
							cmd,
							null,
							null,
							null,
							null,
							null,
							null,
							false,
							true));
				}
			}
		}
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		if(GunGamePlugin.instance.getConfig().getBoolean("Config.SeparatedChat")) {
			if(this.manager.isIngame(player)) {
				
				List<Player> mayRecieveMessage = this.manager.getArena(player).getPlayers();
				Set<Player> recipients = event.getRecipients().stream().collect(Collectors.toSet());
				if(this.manager.isSpectator(player)) {
					for(Player receiver : recipients) {
						if(!(mayRecieveMessage.contains(receiver)) || (!this.manager.isSpectator(receiver) && mayRecieveMessage.contains(receiver))) {
							event.getRecipients().remove(receiver);
						}
					}
				} else {
					for(Player receiver : recipients) {
						if(!(mayRecieveMessage.contains(receiver))) {
							event.getRecipients().remove(receiver);
						}
					}
				}
			} else if(!(this.manager.isIngame(player))) {
				Set<Player> recipients = event.getRecipients().stream().collect(Collectors.toSet());
				if(recipients.isEmpty() == false) {
					for(Player p : recipients) {
						if(this.manager.isIngame(p)) {
							event.getRecipients().remove(p);
						}
					}				
				}
			}
		}
	}
	@EventHandler
	public void onSpecatorTP(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		if(this.manager.isIngame(p) && this.manager.isSpectator(p)) {
			if(event.getCause().equals(TeleportCause.SPECTATE)) {
				Collection<Entity> entitiesAtTarget = null;
				boolean cancel = false;
				try {
					entitiesAtTarget = event.getTo().getWorld().getNearbyEntities(event.getTo(), 0.05, 0.05, 0.05);
					for(Entity ent : entitiesAtTarget) {
						if(ent instanceof Player) {
							if(this.manager.isIngame((Player)ent)) {
								Arena targetArena = this.manager.getArena((Player)ent);
								if(!targetArena.equals(this.manager.getArena(p))) {
									cancel = true;
								}
							} 
							cancel = true;
						}
					}
				} catch(Exception ex) {
					cancel = true;
				}
				if(cancel) {
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		
	}

}
