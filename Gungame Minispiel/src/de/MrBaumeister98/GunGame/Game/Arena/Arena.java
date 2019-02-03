package de.MrBaumeister98.GunGame.Game.Arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaCancelEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaChangeStateEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaEndEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaLeaveEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ArenaStartEvent;
import de.MrBaumeister98.GunGame.API.ArenaEvents.ELeaveReason;
import de.MrBaumeister98.GunGame.Achievements.Achievements.StatManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Mechanics.GameConfiguratorUtil;
import de.MrBaumeister98.GunGame.Game.Mechanics.GameModeSelector;
import de.MrBaumeister98.GunGame.Game.Mechanics.TDMMode;
import de.MrBaumeister98.GunGame.Game.Mechanics.TDMMode.TDMTeam;
import de.MrBaumeister98.GunGame.Game.Util.CountdownUtil;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.RadarUtil;
import de.MrBaumeister98.GunGame.Game.Util.ScoreboardUtil;
import de.MrBaumeister98.GunGame.Game.Util.TabListUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.Game.Util.VoteUtil;
import de.MrBaumeister98.GunGame.Items.Crowbar_pre_1_13;
import de.MrBaumeister98.GunGame.Items.Crowbar_v1_13_up;
import de.MrBaumeister98.GunGame.Items.GameConfigurator;

public class Arena {

	public ArenaManager manager;

	private ArenaWorld arenaWorld;
	private GameWorld gameWorld;

	public Player arenaMaster;

	public StatManager statManager;

	private EGameState state;

	private CountdownUtil countdownHelper;
	private ScoreboardUtil scoreboardutil;
	private RadarUtil radUtil;
	private GameConfiguratorUtil configuratorUtil;
	private GameModeSelector gamemodeManager;

	private boolean enabled;
	private boolean full;
	private boolean cancelled;

	private Location lobby;

	public Boolean explosionsBreakBlocks;
	public Boolean fireSpred;
	public Boolean regenerateHealth;
	public Boolean physics;
	public Boolean protectBodiesOfWater;

	private String arenaName;

	private List<Player> players = new ArrayList<Player>();
	private List<Location> signs = new ArrayList<Location>();

	private Integer playerCount;
	private int minPlayers;
	private int maxPlayers;
	private int killsToWin;
	// private static int refreshRadarTaskID;
	private static int taskID;

	public HashMap<String, Integer> voteMap = new HashMap<String, Integer>();
	public HashMap<Player, String> kills = new HashMap<Player, String>();
	public HashMap<UUID, Boolean> canVote = new HashMap<UUID, Boolean>();
	private HashMap<Player, Integer> playerKills = new HashMap<Player, Integer>();

	// GAMEMODES
	private EArenaGameMode arenaMode;
	private boolean gamemodeConfirmed;
	private int forceVotingTaskID;
	private Player modeChooser;
	private List<Player> dyingOrderLMS = new ArrayList<Player>();
	private TDMMode tdmMode;

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public Arena(ArenaManager main, String Name, int minPlayer, int maxPlayer, int killsToWin) {
		this.manager = main;
		this.setArenaMode(EArenaGameMode.ALL_VS_ALL);
		this.arenaMaster = null;
		this.setGameWorld(new GameWorld(this));
		this.countdownHelper = new CountdownUtil(this);
		this.radUtil = new RadarUtil(this);
		this.setConfiguratorUtil(new GameConfiguratorUtil(this));

		this.minPlayers = minPlayer;
		this.maxPlayers = maxPlayer;

		// this.arenaID = ID;
		this.arenaName = Name;
		this.killsToWin = killsToWin;

		this.state = EGameState.LOBBY;
		this.playerCount = 0;
		this.arenaWorld = null;
		this.full = false;
		this.cancelled = false;
		if (GunGamePlugin.instance.serverPre113) {
			Crowbar_pre_1_13.initializeMap(this);
		} else {
			Crowbar_v1_13_up.initializeMap(this);
		}
		this.setScoreboardutil(new ScoreboardUtil(this));
		this.gamemodeManager = new GameModeSelector(this);
		this.gamemodeConfirmed = false;
		this.tdmMode = new TDMMode(this);

		GunGamePlugin.instance.getServer().getPluginManager().registerEvents(this.radUtil, GunGamePlugin.instance);
		GunGamePlugin.instance.getServer().getPluginManager().registerEvents(this.configuratorUtil,
				GunGamePlugin.instance);
		GunGamePlugin.instance.getServer().getPluginManager().registerEvents(this.gamemodeManager,
				GunGamePlugin.instance);
	}

	public EGameState getGameState() {
		return this.state;
	}

	public Integer getKillsToWin() {
		return this.killsToWin;
	}

	public void setKillsToWin(Integer k) {
		this.killsToWin = k;
	}

	public void addPlayer(Player p) {
		this.players.add(p);
		if (!this.canVote.containsKey(p.getUniqueId())) {
			this.canVote.put(p.getUniqueId(), true);
		}
		this.playerCount++;
		p.setInvulnerable(true);
		this.getScoreboardutil().updateScoreBoard();
		p.setSaturation(20);
		updateSigns();
	}

	public void remPlayer(Player p) {
		// p.setScoreboard(null);
		this.players.remove(p);
		if (this.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
			this.getTdmMode().remPlayer(p);
		}

		/*
		 * if(this.dyingOrderLMS.contains(p)) { this.dyingOrderLMS.remove(p); }
		 */
		this.playerCount--;

		for (Player p2 : this.players) {
			p2.sendMessage(LangUtil.createString("lang.Info.playerLeft", this,
					(this.getArenaWorld() == null ? null : this.getArenaWorld().getName()), p, null, null,
					this.getMinPlayers(), this.getMaxPlayers(), null, null, null, null, null, null, null, true, false));
		}
		
		/*if(GunGamePlugin.instance.TabListAPIloaded) {
			de.Herbystar.TTA.TTA_Methods.sendTablist(p, null, null);
		}*/
		try {
			TabListUtil.sendTabTitle(p, null, null);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		// RadarUtil.removePlayer(p);
		this.radUtil.removePlayer(p);
		if (this.arenaMaster != null && this.arenaMaster.getUniqueId().equals(p.getUniqueId())) {
			this.arenaMaster = null;
		}
		updateSigns();
		this.getScoreboardutil().updateScoreBoard();
		if (p.isOnline()) {
			p.setResourcePack(Util.defaultPack);
		}
		if (!this.getGameState().equals(EGameState.ENDGAME)
				&& (this.playerCount < this.minPlayers || this.playerCount < 2) && this.cancelled == false) {
			cancelArena();
		}
	}

	public void joinLobby(Player p) {
		// if(this.isEnabled() == true) {
		if (this.maxPlayers > this.playerCount) {
			for (Player inArena : this.players) {
				inArena.sendMessage(LangUtil.createString("lang.Info.playerJoined", this,
						(this.arenaWorld == null ? null : this.arenaWorld.getName()), p, null, null, this.minPlayers,
						this.maxPlayers, null, null, null, null, null, null, null, true, false));
			}
			Boolean openModeMenu = false;
			if (this.playerCount < 1) {
				this.explosionsBreakBlocks = true;
				this.fireSpred = false;
				this.regenerateHealth = false;
				this.protectBodiesOfWater = true;
				this.physics = true;

				this.gamemodeManager.reset();
				this.configuratorUtil.reset();
				openModeMenu = true;
			}
			addPlayer(p);
			if (this.arenaMaster == null && p.hasPermission("gungame.customgame")) {
				this.arenaMaster = p;
			}

			this.getScoreboardutil().updateScoreBoard();

			this.cancelled = false;
			if (this.playerCount == 1) {
				ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(this, this.state, EGameState.LOBBY);
				Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
				this.state = EGameState.LOBBY;

			}
			updateSigns();
			// Util.saveInventory(p);

			p.teleport(this.lobby);
			if (openModeMenu) {
				this.gamemodeManager.openMenu(p);
				this.modeChooser = p;
			}

			this.getScoreboardutil().updateScoreBoard();

			if (this.state == EGameState.VOTING) {
				Util.giveLobbyItems(p, true);
			} else {
				Util.giveLobbyItems(p, false);
			}

			this.getScoreboardutil().updateScoreBoard();

			if (this.gamemodeConfirmed && this.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
				this.tdmMode.openMenu(p);
			}

			if (this.playerCount >= this.minPlayers && this.state == EGameState.LOBBY && this.gamemodeConfirmed) {
				try {
					Bukkit.getScheduler().cancelTask(this.forceVotingTaskID);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				this.startVotePhase();
			} else if (this.playerCount >= this.minPlayers && this.state == EGameState.LOBBY) {
				this.gamemodeManager.setTimedOut();
				this.modeChooser.closeInventory();
				Arena aref = this;
				this.forceVotingTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance,
						new Runnable() {

							@Override
							public void run() {
								if (aref.playerCount >= aref.minPlayers && aref.state == EGameState.LOBBY) {
									aref.startVotePhase();
								}
							}
						}, Util.votingPhaseDuration * 20);
			}
		} else {
			this.full = true;
			p.sendMessage(LangUtil.createString("lang.Errors.arenaIsFull", this,
					(this.arenaWorld == null ? null : this.arenaWorld.getName()), p, null, null, this.minPlayers,
					this.maxPlayers, null, null, null, null, null, null, null, false, true));
			this.manager.leaveGame(p, this);
			updateSigns();
		}
		// } else {
		// p.sendMessage(ChatColor.translateAlternateColorCodes('&',
		// LangStrings.notEnabled));
		// p.sendMessage(ChatColor.translateAlternateColorCodes('&',
		// LangStrings.arenaFull));
		// this.manager.leaveGame(p, this);
		// }
	}

	private void startVotePhase() {
		try {
			Bukkit.getScheduler().cancelTask(this.forceVotingTaskID);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(this, this.state, EGameState.VOTING);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
		this.state = EGameState.VOTING;
		updateSigns();
		for (Player p2 : players) {
			Util.giveLobbyItems(p2, true);
			if (this.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
				if (this.tdmMode.getTeam(p2) != null) {
					this.tdmMode.openMenu(p2);
				}
			}
		}
		if (this.arenaMaster != null) {
			this.arenaMaster.getInventory().setItem(2, GameConfigurator.configurator());
		}
		VoteUtil.fillVoteMap(this);
		this.getScoreboardutil().updateScoreBoard();
		Arena temp = this;
		taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {

			@Override
			public void run() {
				temp.endVotePhase();

			}

		}, Util.votingPhaseDuration * 20);
	}
	
	public void endVotePhase() {
		try {
			Bukkit.getScheduler().cancelTask(taskID);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		VoteUtil.endVotePhase(this);
		this.getScoreboardutil().updateScoreBoard();

		for (Player p : players) {
			p.playSound(p.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1.0F, 1.0F);
			p.sendMessage(LangUtil.createString("lang.Info.endOfVotePhase", this,
					(this.arenaWorld == null ? null : this.arenaWorld.getName()), p, null, null,
					this.minPlayers, this.maxPlayers, null, null, null, null, null, null, null, true, false));
			p.sendMessage(LangUtil.createString("lang.Info.votedMap", this,
					(this.arenaWorld == null ? null : this.arenaWorld.getName()), p, null, null,
					this.minPlayers, this.maxPlayers, null, null, null, null, null, null, null, true, false));
		}
		this.getScoreboardutil().updateScoreBoard();
		if (this.arenaMaster != null) {
			this.arenaMaster.closeInventory();
			this.arenaMaster.getInventory().setItem(2, new ItemStack(Material.AIR, 1));
			this.configuratorUtil.transferValues();
		} else {
			this.explosionsBreakBlocks = false;
			this.fireSpred = false;
			this.regenerateHealth = false;
			this.physics = true;
			this.protectBodiesOfWater = true;
		}

		this.gameWorld.initialize(this.arenaWorld, this.explosionsBreakBlocks, this.fireSpred,
				this.regenerateHealth, this.physics, this.protectBodiesOfWater);
		ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(this, this.state,
				EGameState.WAITING_FOR_GAMEWORLD);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
		this.state = EGameState.WAITING_FOR_GAMEWORLD;
		updateSigns();
	}

	public void preStartArena() {
		Arena temp = this;
		taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {

			@Override
			public void run() {

				if (arenaWorld != null && temp.gameWorld.isReadyToPlay()) {
					temp.getScoreboardutil().updateScoreBoard();
					ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(temp, temp.state,
							EGameState.STARTING);
					Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
					temp.state = EGameState.STARTING;
					updateSigns();
					for (Player p2 : players) {
						p2.sendMessage(LangUtil.createString("lang.Info.starting", temp,
								(temp.arenaWorld == null ? null : temp.arenaWorld.getName()), p2, Util.lobbyCountDown,
								null, temp.minPlayers, temp.maxPlayers, null, null, null, null, null, null, null, true,
								false));
						// p2.sendTitle(ChatColor.translateAlternateColorCodes('&', LangUtil.Starting),
						// null, 0, 80, 10);
					}
					// CountdownUtil.startCountDown(temp, Util.lobbyCountDown, 5);
					countdownHelper.startCountDown(Util.lobbyCountDown, 5);
					if (temp.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
						temp.tdmMode.onStartGame();
					}
					taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
						@Override
						public void run() {
							for (Player p2 : players) {
								p2.sendMessage(LangUtil.createString("lang.Info.startingGame", temp,
										(temp.arenaWorld == null ? null : temp.arenaWorld.getName()), p2, null, null,
										temp.minPlayers, temp.maxPlayers, null, null, null, null, null, null, null,
										true, false));
								// p2.sendTitle(ChatColor.translateAlternateColorCodes('&', LangUtil.Starting),
								// ChatColor.GREEN + "Map: " + arenaWorld.getName(), 0, 4, 1);

								p2.playSound(p2.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 1);
							}
							Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {

								@Override
								public void run() {

									if (cancelled == true) {
										endArena(false);
									} else if (cancelled == false) {
										startArena();
									}

								}

							}, 20);

						}
					}, Util.lobbyCountDown * 20);
				}

			}

		}, 100);
	}

	public void startArena() {
		if (this.cancelled == false) {

			ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(this, this.state, EGameState.PREGAME);
			Bukkit.getServer().getPluginManager().callEvent(statechangeevent);

			ArenaStartEvent startevent = new ArenaStartEvent(this);
			Bukkit.getServer().getPluginManager().callEvent(startevent);

			this.state = EGameState.PREGAME;
			// start counter, setup scoreboard, give inventories
			fillKillMap();
			this.statManager = new StatManager(this);
			for (Player p : this.players) {

				// teleport the player to a random spawn location
				if (GunGamePlugin.instance.serverPre113) {
					p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ENDERMEN_TELEPORT"), 1, 1);
				} else {
					p.playSound(p.getLocation(), Sound.valueOf("ENTITY_ENDERMAN_TELEPORT"), 1, 1);
				}

				p.teleport(getRandomSpawn());

				this.gameWorld.joinGame(p);
				if (this.arenaWorld.getResourcePackLInk() != null) {
					p.setResourcePack(this.arenaWorld.getResourcePackLInk());
				}

				// RadarUtil.autoRefresh(refreshRadarTaskID);
				this.radUtil.autoRefresh();
				p.setInvulnerable(false);
				p.setCollidable(true);
				p.setHealthScale(20.0);
				p.setHealth(20.0);
				p.setSaturation(20);

				this.statManager.getStatPlayer.get(p.getUniqueId()).incrementJoinedGames();

			}
			Debugger.logInfoWithColoredText(
					ChatColor.YELLOW + "Starting Arena: " + ChatColor.RED + this.arenaName + ChatColor.YELLOW + " with "
							+ ChatColor.GREEN + playerCount.toString() + ChatColor.YELLOW + " Players!");
			updateSigns();
			// this.arenaWorld.startGame();

			if (playerCount <= 0 || playerCount < this.minPlayers) {
				cancelArena();
			}
			this.radUtil.updateTrackerList();
			// start countdown
			for (Player p : this.players) {
				// Util.dealItems(p);
				p.setInvulnerable(true);
				p.getInventory().clear();
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
				p.addPotionEffect(
						new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false));
			}
			Arena ref = this;
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {

				@Override
				public void run() {
					for (Player p : ref.getPlayers()) {
						p.sendMessage(LangUtil.createString("lang.Info.preparingPhase", ref,
								(ref.arenaWorld == null ? null : ref.arenaWorld.getName()), p,
								Util.protectionPhaseDuration, null, ref.minPlayers, ref.maxPlayers, null, null, null,
								null, null, null, null, true, false));
					}
					ref.countdownHelper.startCountDown(Util.protectionPhaseDuration, 5, new Runnable() {

						@Override
						public void run() {
							for (Player p : ref.players) {
								p.setInvulnerable(false);
								p.getInventory().clear();
								p.setExp(0.0f);
								p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
								p.removePotionEffect(PotionEffectType.INVISIBILITY);
								p.removePotionEffect(PotionEffectType.WEAKNESS);
								Util.dealItems(p);
								ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(ref, ref.state,
										EGameState.GAME);
								Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
								ref.setGameState(EGameState.GAME);
							}

						}
					});
				}
			}, 30l);
		} else if (this.cancelled == true) {
			endArena(true);
			this.cancelled = false;
		}

	}

	public void preEndArena() {
		try {

			this.getScoreboardutil().destroy();

			for (Player p : this.players) {
				this.statManager.getStatPlayer.get(p.getUniqueId()).incrementGamesPlayed();
			}

			HashMap<Integer, Player> placemapevent = new HashMap<Integer, Player>();
			if (this.arenaMode.equals(EArenaGameMode.ALL_VS_ALL)) {
				HashMap<Integer, List<Player>> victorMap = new HashMap<Integer, List<Player>>();
				for (Integer i = 0; i <= this.killsToWin; i++) {
					List<Player> tmp = new ArrayList<Player>();
					victorMap.put(i, tmp);
				}
				for (Player p : this.players) {
					p.setInvulnerable(true);
					victorMap.get(getKills(p)).add(p);
				}
				for (Integer i = 0; i <= this.killsToWin; i++) {
					if (victorMap.get(i) == null || victorMap.get(i).isEmpty()) {
						victorMap.remove(i, victorMap.get(i));
					}
				}
				List<Integer> placesByKills = new ArrayList<Integer>(victorMap.keySet());
				// placesByKills = Collections.reverse(placesByKills);.reverse(placesByKills);
				Collections.reverse(placesByKills);
				Player victor = victorMap.get(this.killsToWin).get(0);
				for (Player p : this.players) {

					p.getInventory().clear();
					Integer place = placesByKills.indexOf(getKills(p)) + 1;

					// eventVictorList.add(place, p);
					placemapevent.put(place, p);

					if (place == 1) {
						this.statManager.getStatPlayer.get(p.getUniqueId()).winORlose(true);
					}
					if (place == placesByKills.size()) {
						this.statManager.getStatPlayer.get(p.getUniqueId()).winORlose(false);
					}

					List<String> msgRaw = LangUtil.getStringListByPath("lang.Info.victoryMessage");
					for (Integer i = 0; i < msgRaw.size(); i++) {
						String s = msgRaw.get(i);

						s = s.replace("%victor%", victor.getName());
						s = s.replace("%place%", place.toString());
						s = s.replace("%kills%", getKills(p).toString());

						p.sendMessage(s);
					}

				}
			}
			if (this.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
				List<TDMTeam> victorList = new ArrayList<TDMTeam>(this.tdmMode.getTeams());
				Collections.sort(victorList, new Comparator<TDMTeam>() {

					@Override
					public int compare(TDMTeam team1, TDMTeam team2) {
						Integer scoreT1 = team1.getDistanceToVictory();
						Integer scoreT2 = team2.getDistanceToVictory();
						return scoreT2 - scoreT1;
					}
				});
				Collections.reverse(victorList);
				TDMTeam victor = victorList.get(0);
				TDMTeam loser = victorList.get(victorList.size() - 1);
				for (Player p : this.players) {
					p.setInvulnerable(true);
					if (this.tdmMode.getTeam(p).equals(victor)) {
						this.statManager.getStatPlayer.get(p.getUniqueId()).winORlose(true);
					} else if (this.tdmMode.getTeam(p).equals(loser)) {
						this.statManager.getStatPlayer.get(p.getUniqueId()).winORlose(false);
					}

					Integer place = victorList.indexOf(this.tdmMode.getTeam(p)) + 1;
					placemapevent.put(place, p);

					List<String> msgRaw = LangUtil.getStringListByPath("lang.Info.victoryMessage");
					for (Integer i = 0; i < msgRaw.size(); i++) {
						String s = msgRaw.get(i);

						s = s.replace("%victor%", victor.getDisplayName());
						s = s.replace("%place%", place.toString());
						s = s.replace("%kills%", getKills(p).toString());

						p.sendMessage(s);
					}
				}
				for (TDMTeam tdteam : this.tdmMode.getTeams()) {
					tdteam.onEndOfGame();
				}
			}
			if (this.arenaMode.equals(EArenaGameMode.LAST_MAN_STANDING)) {
				List<Player> victors = new ArrayList<Player>(this.dyingOrderLMS);
				Collections.reverse(victors);
				Player victor = victors.get(0);
				this.statManager.getStatPlayer.get(victor.getUniqueId()).winORlose(true);
				Player loser = victors.get(victors.size() - 1);
				this.statManager.getStatPlayer.get(loser.getUniqueId()).winORlose(false);
				if (this.arenaMode.equals(EArenaGameMode.LAST_MAN_STANDING)) {
					this.manager.remSpectators(this);
				}
				for (Player p : this.players) {
					p.setInvulnerable(true);
					p.getInventory().clear();
					Integer place = victors.indexOf(p) + 1;
					placemapevent.put(place, p);

					List<String> msgRaw = LangUtil.getStringListByPath("lang.Info.victoryMessage");
					for (Integer i = 0; i < msgRaw.size(); i++) {
						String s = msgRaw.get(i);

						s = s.replace("%victor%", victor.getName());
						s = s.replace("%place%", place.toString());
						s = s.replace("%kills%", getKills(p).toString());

						p.sendMessage(s);
					}
				}
			}

			ArenaEndEvent endevent = new ArenaEndEvent(this, placemapevent);
			Bukkit.getServer().getPluginManager().callEvent(endevent);

			this.countdownHelper.startCountDown(30, 5, new Runnable() {

				@Override
				public void run() {
					endArena(true);
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void endArena(boolean refreshWorld) {
		updateSigns();
		this.statManager.calculateAchievements();
		// RadarUtil.killRefreshTask(refreshRadarTaskID);
		this.radUtil.killRefreshTask();
		ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(this, this.state, EGameState.ENDGAME);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
		this.state = EGameState.ENDGAME;
		this.arenaMode = EArenaGameMode.ALL_VS_ALL;
		this.gamemodeConfirmed = false;
		this.gamemodeManager.reset();
		this.dyingOrderLMS.clear();
		// this.manager.remSpectators(this);
		// Declare Victor and give points
		List<Player> tempPlayers = new ArrayList<Player>(this.players);
		// this.getScoreboardutil().destroy();
		for (Player p : tempPlayers) {
			p.teleport(Util.getGlobalLobby());
			/*
			 * try { Util.restoreInventory(p); } catch (IOException ex) { Debugger.
			 * logWarning("An error occured while trying to restore the inventory of player "
			 * + p.getName() + "! Exception: " + ex); }
			 */
			this.manager.leaveGame(p, this);
			ArenaLeaveEvent leaveEvent = new ArenaLeaveEvent(this, p, ELeaveReason.ARENA_END);
			Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
		}
		ArenaChangeStateEvent statechangeevent1 = new ArenaChangeStateEvent(this, this.state, EGameState.RESTORING);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent1);
		this.state = EGameState.RESTORING;
		if (this.gameWorld != null) {
			// this.arenaWorld.endGame();
			this.gameWorld.stopGame();
		}
		this.arenaWorld = null;
		// this.gameWorld.stopGame();
		this.full = false;
		ArenaChangeStateEvent statechangeevent2 = new ArenaChangeStateEvent(this, this.state, EGameState.LOBBY);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent2);
		this.state = EGameState.LOBBY;
		this.playerKills.clear();
		if (GunGamePlugin.instance.serverPre113) {
			Crowbar_pre_1_13.clearMap(this);
		} else {
			Crowbar_v1_13_up.clearMap(this);
		}
		updateSigns();
		this.statManager = null;

		for (Player p2 : tempPlayers) {
			p2.setResourcePack(Util.defaultPack);
		}
	}

	public void cancelArena() {
		if (this.state != EGameState.LOBBY && this.cancelled == false) {
			this.cancelled = true;
		}

		ArenaCancelEvent cancelevent = new ArenaCancelEvent(this);
		Bukkit.getServer().getPluginManager().callEvent(cancelevent);

		this.getScoreboardutil().destroy();
		this.manager.remSpectators(this);
		this.arenaMode = EArenaGameMode.ALL_VS_ALL;
		this.gamemodeConfirmed = false;
		this.gamemodeManager.reset();
		try {
			this.statManager.calculateAchievements();
		} catch (NullPointerException ex) {
			// ex.printStackTrace();
		}
		this.statManager = null;
		this.dyingOrderLMS.clear();
		updateSigns();
		Debugger.logInfoWithColoredText(
				ChatColor.YELLOW + "Cancelling Arena " + ChatColor.RED + this.arenaName + ChatColor.YELLOW + " ...");
		List<Player> tempPlayers = new ArrayList<Player>(this.players);
		try {
			for (Player p : tempPlayers) {
				/*
				 * if(p != null) { p.teleport(Util.getGlobalLobby());
				 * p.setResourcePack(Util.defaultPack); } try { Util.restoreInventory(p); }
				 * catch (IOException ex) { Debugger.
				 * logWarning("An error occured while trying to restore the inventory of player "
				 * + p.getName() + "! Exception: " + ex); }
				 */
				this.manager.leaveGame(p, this);

				ArenaLeaveEvent leaveEvent = new ArenaLeaveEvent(this, p, ELeaveReason.ARENA_CANCEL);
				Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (this.tdmMode != null && this.tdmMode.getTeams() != null && !this.tdmMode.getTeams().isEmpty()) {
			for (TDMTeam tdteam : this.tdmMode.getTeams()) {
				tdteam.onEndOfGame();
			}
		}
		this.radUtil.killRefreshTask();
		countdownHelper.cancelTask();
		Bukkit.getScheduler().cancelTask(taskID);
		this.voteMap.clear();
		this.kills.clear();
		this.canVote.clear();
		this.playerKills.clear();
		if (GunGamePlugin.instance.serverPre113) {
			Crowbar_pre_1_13.clearMap(this);
		} else {
			Crowbar_v1_13_up.clearMap(this);
		}
		this.full = false;
		ArenaChangeStateEvent statechangeevent = new ArenaChangeStateEvent(this, this.state, EGameState.RESTORING);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent);
		this.state = EGameState.RESTORING;
		updateSigns();
		// for(Player p : this.players) {
		// this.manager.leaveGame(p, this);
		// }
		if (this.gameWorld != null) {
			// this.arenaWorld.endGame();
			// this.arenaWorld.refreshWorld();
			this.gameWorld.stopGame();
		}
		this.arenaWorld = null;
		Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Done!");
		ArenaChangeStateEvent statechangeevent1 = new ArenaChangeStateEvent(this, this.state, EGameState.LOBBY);
		Bukkit.getServer().getPluginManager().callEvent(statechangeevent1);
		this.state = EGameState.LOBBY;
		this.state = EGameState.LOBBY;
		this.state = EGameState.LOBBY;
		this.state = EGameState.LOBBY;
		updateSigns();
	}

	public void respawn(Player p) {
		if (!this.arenaMode.equals(EArenaGameMode.LAST_MAN_STANDING)) {
			p.setHealth(20);
			p.setFireTicks(0);
			p.setFallDistance(0.0F);
			p.teleport(getRandomSpawn());
		} else {
			this.dyingOrderLMS.add(p);
			this.manager.addSpectator(p);
			p.setAllowFlight(true);
			p.setFlying(true);
			p.setHealth(20);
			p.setFireTicks(0);
			p.setFallDistance(0.0F);
			p.getInventory().clear();
			p.setInvulnerable(true);
			p.setCollidable(false);
			if (this.dyingOrderLMS.size() >= (this.playerCount - 1)) {
				// END GAME
				for (Player p2 : this.players) {
					if (!this.dyingOrderLMS.contains(p2)) {
						this.dyingOrderLMS.add(p2);
					}
				}
				this.preEndArena();
			}
		}
	}

	public void setLobby(Location loc) {
		ArenaFileStuff.setArenaLobbyToConfig(this.arenaName, loc);

		this.lobby = loc;
		// FileManager.saveArenaConfig();
	}

	public List<String> updateSignText() {
		List<String> text = new ArrayList<String>();
		text.add(LangUtil.prefix);
		text.add(ChatColor.AQUA + this.arenaName);
		switch (this.state) {
		case ENDGAME:
			text.add(" ");
			text.add(ChatColor.RED + "Ending...");
			break;
		case GAME:
			text.add(ChatColor.YELLOW + "Map: " + this.arenaWorld.getName());
			text.add(ChatColor.RED + "Ingame");
			break;
		case LOBBY:
			text.add(ChatColor.GREEN + this.playerCount.toString() + "/" + this.maxPlayers + " Map: ???");
			text.add(ChatColor.GREEN + "Lobby");
			break;
		case PREGAME:
			text.add("Map: " + this.arenaWorld.getName());
			text.add(ChatColor.RED + "Ingame");
			break;
		case RESTORING:
			text.add(" ");
			text.add(ChatColor.RED + "Restarting...");
			break;
		case STARTING:
			text.add(ChatColor.GREEN + this.playerCount.toString() + "/" + this.maxPlayers + " Map: "
					+ this.arenaWorld.getName());
			text.add(ChatColor.RED + "Starting...");
			break;
		case VOTING:
			text.add(ChatColor.GREEN + this.playerCount.toString() + "/" + this.maxPlayers + " Map: ???");
			text.add(ChatColor.GREEN + "Voting Map...");
			break;
		case WAITING_FOR_GAMEWORLD:
			text.add(ChatColor.GREEN + this.playerCount.toString() + "/" + this.maxPlayers + " Map: "
					+ this.arenaWorld.getName());
			text.add(ChatColor.RED + "Preparing Map...");
			break;
		default:
			break;
		}
		return text;
	}

	public void updateSigns() {
		for (Location signL : this.signs) {
			updateSign(signL);
		}
	}

	public void updateSign(Location signL) {
		if (signL != null && (signL.getBlock().getType().equals(Material.WALL_SIGN)
				|| signL.getBlock().getType().equals(Material.SIGN))) {
			Block signB = signL.getBlock();
			Sign sign = (Sign) signB.getState();

			List<String> lines = updateSignText();
			for (int i = 0; i < 4; i++) {
				sign.setLine(i, lines.get(i));
			}

			sign.update();
			sign.update(true);
		}
	}

	public Integer getKills(Player p) {
		return this.playerKills.get(p);
	}

	public void addKill(Player p, Boolean killedPlayer) {
		if (killedPlayer) {
			this.playerKills.put(p, getKills(p) + 1);

			// STAT
			this.statManager.getStatPlayer.get(p.getUniqueId()).incrementKillStreak();
		}
		Util.calcCoins(p);
		if (this.playerKills.get(p) >= this.killsToWin && !this.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
			preEndArena();
		}
	}

	public void confirmGameMode() {
		this.gamemodeConfirmed = true;
		if (this.arenaMode.equals(EArenaGameMode.TEAM_DEATHMATCH)) {
			for (Player p : this.players) {
				this.tdmMode.openMenu(p);
			}
		}
		if (this.playerCount >= this.minPlayers && this.state == EGameState.LOBBY && this.gamemodeConfirmed) {
			try {
				Bukkit.getScheduler().cancelTask(this.forceVotingTaskID);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Arena ref = this;
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {

				@Override
				public void run() {
					ref.startVotePhase();
				}
			}, 100);
		}
	}

	private void fillKillMap() {
		for (Player p : this.players) {
			this.playerKills.put(p, 0);
		}
	}

	public Location getLobby() {
		return this.lobby;
	}

	public String getName() {
		return this.arenaName;
	}

	public Integer getMinPlayers() {
		return this.minPlayers;
	}

	public Integer getMaxPlayers() {
		return this.maxPlayers;
	}

	public void setArenaWorld(ArenaWorld world) {
		this.arenaWorld = world;
	}

	public void setGameState(EGameState state) {
		this.state = state;
	}

	public Location getRandomSpawn() {
		// int rndmNumber = Util.getRandomNumber(this.arenaWorld.getSpawnList().size());
		int rdmNumber = Util.getRandomNumber(this.gameWorld.getSpawns().size());
		// Location rndomSpawn = this.arenaWorld.getSpawn(rndmNumber);
		Location rndomSpawn = this.gameWorld.getSpawn(rdmNumber);
		return rndomSpawn;
	}

	public List<Player> getPlayers() {
		return this.players;
	}

	public boolean isFull() {
		return this.full;
	}

	public List<Location> getSigns() {
		return this.signs;
	}

	public void addSign(Location l) {
		ArenaFileStuff.addJoinSignToConfig(l, this.arenaName);
		updateSign(l);
		this.signs.add(l);
	}

	public void remSign(Location l) {
		ArenaFileStuff.removeJoinSignFromConfig(l, this.arenaName);
		this.signs.remove(l);
	}

	public void setSigns(List<Location> input) {
		this.signs = input;
		updateSigns();
	}

	public void resetSigns() {
		this.signs.clear();
		ArenaFileStuff.resetJoinSignsInConfig(this.arenaName);
	}

	public GameWorld getGameWorld() {
		return gameWorld;
	}

	public void setGameWorld(GameWorld gameWorld) {
		this.gameWorld = gameWorld;
	}

	public ArenaWorld getArenaWorld() {
		return this.arenaWorld;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public GameConfiguratorUtil getConfiguratorUtil() {
		return configuratorUtil;
	}

	public void setConfiguratorUtil(GameConfiguratorUtil configuratorUtil) {
		this.configuratorUtil = configuratorUtil;
	}

	public ScoreboardUtil getScoreBoardUtil() {
		return this.getScoreboardutil();
	}

	public EArenaGameMode getArenaMode() {
		return arenaMode;
	}

	public void setArenaMode(EArenaGameMode arenaMode) {
		this.arenaMode = arenaMode;
	}

	public TDMMode getTdmMode() {
		return tdmMode;
	}

	public void setTdmMode(TDMMode tdmMode) {
		this.tdmMode = tdmMode;
	}

	public ScoreboardUtil getScoreboardutil() {
		return scoreboardutil;
	}

	public void setScoreboardutil(ScoreboardUtil scoreboardutil) {
		this.scoreboardutil = scoreboardutil;
	}

}
