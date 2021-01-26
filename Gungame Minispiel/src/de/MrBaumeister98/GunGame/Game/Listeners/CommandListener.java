package de.MrBaumeister98.GunGame.Game.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaFileStuff;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaWorld;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.Items.Crowbar_pre_1_13;
import de.MrBaumeister98.GunGame.Items.Crowbar_v1_13_up;
import de.MrBaumeister98.GunGame.Items.FlareGun;
import de.MrBaumeister98.GunGame.Items.Radar;
import de.MrBaumeister98.GunGame.Items.SuicideArmor;

public class CommandListener implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player) sender;

			/*if(cmd.getName().equals("ggtrackmap") || cmd.getName().equals("ggtrackpack")) {
				TrackPadCreator.renderMapView(p.getLocation(), p.getUniqueId());
			}*/
			
			if(cmd.getName().equalsIgnoreCase("gg") | cmd.getName().equalsIgnoreCase("gungame") | cmd.getName().equalsIgnoreCase("gga") | cmd.getName().equalsIgnoreCase("ggame")| cmd.getName().equalsIgnoreCase("gung")) {
				if(args.length > 0 ) {
					List<String> userHelp = LangUtil.createStringList("lang.Commands.Help",
							GunGamePlugin.instance.arenaManager.getArena(p),
							null,
							p,
							null,
							null,
							null,
							null,
							"gg help",
							null,
							null,
							null,
							null,
							null,
							null,
							true,
							false);
					List<String> adminHelp = LangUtil.createStringList("lang.Commands.HelpAdmin",
							GunGamePlugin.instance.arenaManager.getArena(p),
							null,
							p,
							null,
							null,
							null,
							null,
							"gg help",
							null,
							null,
							null,
							null,
							null,
							null,
							true,
							false);
					
					switch(args[0]) {
					
					default:
						
						if(!p.hasPermission("gungame.admin")) {
							for (int i = 0; i <= userHelp.size() -1; i++) {
								String msg = userHelp.get(i);
								p.sendMessage(msg);
							}
						} else {
							
							for (int i = 0; i <= userHelp.size() -1; i++) {
								String msg = userHelp.get(i);
								p.sendMessage(msg);
							}
							
							for (int i = 0; i <= adminHelp.size() -1; i++) {
								String msg = adminHelp.get(i);
								p.sendMessage(msg);
							}
						}
						
						return true;
						
					case "help":
						if(!p.hasPermission("gungame.admin")) {
							for (int i = 0; i <= userHelp.size() -1; i++) {
								String msg = userHelp.get(i);
								p.sendMessage(msg);
							}
						} else {
							for (int i = 0; i <= userHelp.size() -1; i++) {
								String msg = userHelp.get(i);
								p.sendMessage(msg);
							}
							for (int i = 0; i <= adminHelp.size() -1; i++) {
								String msg = adminHelp.get(i);
								p.sendMessage(msg);
							}
						}
						
						return true;
					case "?":
						if(!p.hasPermission("gungame.admin")) {
							for (int i = 0; i <= userHelp.size() -1; i++) {
								String msg = userHelp.get(i);
								p.sendMessage(msg);
							}
						} else {
							for (int i = 0; i <= userHelp.size() -1; i++) {
								String msg = userHelp.get(i);
								p.sendMessage(msg);
							}
							for (int i = 0; i <= adminHelp.size() -1; i++) {
								String msg = adminHelp.get(i);
								p.sendMessage(msg);
							}
						}
						
						return true;
					case "join":
						if(p.hasPermission("gungame.user") || p.hasPermission("gungame.admin")) {
							if(!GunGamePlugin.instance.arenaManager.isIngame(p)) {
								if(args.length == 1) {
									p.sendMessage(LangUtil.buildHelpString("Commands.Join"));
								} else {
									String arenaID = String.valueOf(args[1]);
									if(GunGamePlugin.instance.arenaManager.isNameValid(arenaID)) {
										Arena a = GunGamePlugin.instance.arenaManager.getArena(arenaID);
										
										GunGamePlugin.instance.arenaManager.tryJoin(p, a);
										
									} else {
										p.sendMessage(LangUtil.createString("lang.Errors.noSuchArenaName",
												GunGamePlugin.instance.arenaManager.getArena(p),
												null,
												p,
												null,
												null,
												null,
												null,
												"gg join",
												null,
												null,
												null,
												null,
												null,
												null,
												true,
												true));
									}
								}
							}
						} else {
							p.sendMessage(LangUtil.noPermission);
							return true;
						}
						return true;
					case "leave":
						if(p.hasPermission("gungame.user") || p.hasPermission("gungame.admin")) {
							if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
								Arena a = GunGamePlugin.instance.arenaManager.getArena(p);
								
								
								Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Player: " + ChatColor.GREEN + p.getName() + ChatColor.YELLOW + " has left the Arena: " + ChatColor.RED + a.getName());
								p.sendMessage(LangUtil.createString2("lang.Info.leftArena",
										a,
										(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
										p,
										null,
										null,
										null,
										null,
										"gg leave",
										p.getLocation(),
										null,
										null,
										null,
										null,
										null,
										true,
										false));
								GunGamePlugin.instance.arenaManager.leaveGame(p, a);
							}
						} else {
							p.sendMessage(LangUtil.noPermission);
							return true;
						}
						return true;
					case "setgloballobby":
						if(p.hasPermission("gungame.admin")) {
							Util.setGlobalLobby(p.getLocation());
							Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Global Lobby was set to: " + ChatColor.LIGHT_PURPLE + Util.locToString(p.getLocation()));
							
							GunGamePlugin.instance.saveConfig();
							p.sendMessage(LangUtil.createString2("lang.Info.setGlobalLobby",
									GunGamePlugin.instance.arenaManager.getArena(p),
									null,
									p,
									null,
									null,
									null,
									null,
									"gg setgloballobby",
									p.getLocation(),
									null,
									null,
									null,
									null,
									null,
									true,
									false));
						} else {
							p.sendMessage(LangUtil.noPermission);
							return true;
						}
						return true;
					case "list":
						if(p.hasPermission("gungame.user") || p.hasPermission("gungame.admin")) {
							List<String> aNames = new ArrayList<String>();
							p.sendMessage(LangUtil.prefix + ChatColor.RED + "Arenas: ");
							for(int i = 0; i < GunGamePlugin.instance.arenaManager.arenas.size(); i++) {
								String aName = GunGamePlugin.instance.arenaManager.arenas.get(i).getName();
									
								aNames.add(aName);
								p.sendMessage(ChatColor.YELLOW + " - Arena #" + (i+1) + ": " + ChatColor.GREEN + aNames.get(i));
							}
						} else {
							p.sendMessage(LangUtil.noPermission);
							return true;
						}			
						return true;

						
						
						
						
						
						
						
						
						
						
						
						
						
					case "arena":
						if(p.hasPermission("gungame.admin")) {
							List<String> helpList = LangUtil.createStringList("lang.Commands.HelpArena",
									GunGamePlugin.instance.arenaManager.getArena(p),
									null,
									p,
									null,
									null,
									null,
									null,
									"gg arena ?",
									null,
									null,
									null,
									null,
									null,
									null,
									true,
									false);
							if(args.length < 2) {
								//SEND ARENA HELP								
								for(String s : helpList) {
									p.sendMessage(s);
									p.sendMessage(" ");
								}
							} else if(args.length > 1) {
								switch(args[1]) {
								
									default:
										for(String s : helpList) {
											p.sendMessage(s);
										}
										return true;
								
									case "enable":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.Arena.enable"));
										} else {
											String arenaID = String.valueOf(args[2]);
											String enabled = String.valueOf(args[3]);
											Arena a = GunGamePlugin.instance.arenaManager.getArena(arenaID);
											
											if(enabled == "1" | enabled == "0" | enabled == "true" | enabled == "false" | enabled == null) {
												if(enabled == "1" | enabled == "true") {
													GunGamePlugin.instance.arenaManager.enableArena(a, true);
													p.sendMessage(LangUtil.createString("lang.Commands.Arena.enabled",
															a,
															(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
															p,
															null,
															null,
															a.getMinPlayers(),
															a.getMaxPlayers(),
															"gg arena enable",
															null,
															null,
															null,
															null,
															null,
															null,
															true,
															false));												
													} else {
													if(enabled == "0" | enabled == "false") {
														GunGamePlugin.instance.arenaManager.enableArena(a, false);
														p.sendMessage(LangUtil.createString("lang.Commands.Arena.disabled",
																a,
																(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
																p,
																null,
																null,
																a.getMinPlayers(),
																a.getMaxPlayers(),
																"gg arena enable",
																null,
																null,
																null,
																null,
																null,
																null,
																true,
																false));												
													} else {
														if(enabled == null) {
															GunGamePlugin.instance.arenaManager.enableArena(a);
															if(a.isEnabled()) {
																p.sendMessage(LangUtil.createString("lang.Commands.Arena.enabled",
																		a,
																		(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
																		p,
																		null,
																		null,
																		a.getMinPlayers(),
																		a.getMaxPlayers(),
																		"gg arena enable",
																		null,
																		null,
																		null,
																		null,
																		null,
																		null,
																		true,
																		false));												
															} else {
																p.sendMessage(LangUtil.createString("lang.Commands.Arena.disabled",
																		a,
																		(a.getArenaWorld() == null ? null : a.getArenaWorld().getName()),
																		p,
																		null,
																		null,
																		a.getMinPlayers(),
																		a.getMaxPlayers(),
																		"gg arena enable",
																		null,
																		null,
																		null,
																		null,
																		null,
																		null,
																		true,
																		false));												
															}
														} else {
															p.sendMessage(LangUtil.buildHelpString("Commands.Arena.enableArena"));														}
														}
													}												
											}
										} 
										return true;
									
									case "add":
										if(args.length < 6) {
											p.sendMessage(LangUtil.buildHelpString("Commands.Arena.addArena"));
										} else {
											String arenaID = String.valueOf(args[2]);
											if(Util.isNameValid(arenaID) == true) {
												Integer minP = Integer.valueOf(args[3]);
												Integer maxP = Integer.valueOf(args[4]);
												Integer kills = Integer.valueOf(args[5]);
												
												ArenaFileStuff.addArenaToConfig(arenaID, minP, maxP, kills, p.getLocation());
												GunGamePlugin.instance.arenaManager.createArena(arenaID, minP, maxP, kills);
												GunGamePlugin.instance.arenaManager.getArena(arenaID).setLobby(p.getLocation());
												FileManager.saveArenaConfig();
												p.sendMessage(LangUtil.createString("lang.Commands.Arena.added",
														GunGamePlugin.instance.arenaManager.getArena(arenaID),
														null,
														p,
														null,
														null,
														minP,
														maxP,
														"gg arena add",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.createString("lang.Errors.arenaNameInUse",
														GunGamePlugin.instance.arenaManager.getArena(arenaID),
														null,
														p,
														null,
														null,
														null,
														null,
														"gg arena add",
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
										return true;
									
									case "list":
										List<String> aNames2 = new ArrayList<String>();
										p.sendMessage(ChatColor.RED + "[GunGame] Arenas: ");
										for(int i = 0; i < GunGamePlugin.instance.arenaManager.arenas.size(); i++) {
											String aName = GunGamePlugin.instance.arenaManager.arenas.get(i).getName();
												
											aNames2.add(aName);
											p.sendMessage(ChatColor.YELLOW + " - Arena #" + (i+1) + ": " + ChatColor.GREEN + aNames2.get(i));
										}
									
										return true;
									
									case "setlobby":
											if(args.length < 3) {
												p.sendMessage(LangUtil.buildHelpString("Commands.Arena.setLobby"));
											} else {
												String arenaID = String.valueOf(args[2]);
												Arena arena = GunGamePlugin.instance.arenaManager.getArena(arenaID);						
												arena.setLobby(p.getLocation());
												FileManager.saveArenaConfig();
												p.sendMessage(LangUtil.createString2("lang.Commands.Arena.lobbyset",
														GunGamePlugin.instance.arenaManager.getArena(arenaID),
														null,
														p,
														null,
														null,
														null,
														null,
														"gg arena setlobby",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											}
											return true;
									case "?":
										for(String s : helpList) {
											p.sendMessage(s);
											p.sendMessage(" ");
										}
										return true;
									case "help":
										for(String s : helpList) {
											p.sendMessage(s);
											p.sendMessage(" ");
										}
										return true;
			
								}
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
							return true;
						}
						
						
						
						
						
						
						
						
						
						
					case "world":
						if(p.hasPermission("gungame.admin")) {
							List<String> helpList = LangUtil.createStringList("lang.Commands.HelpWorld",
									GunGamePlugin.instance.arenaManager.getArena(p),
									null,
									p,
									null,
									null,
									null,
									null,
									"gg arena ?",
									null,
									null,
									null,
									null,
									null,
									null,
									true,
									false);
							if(args.length < 2) {
								//SEND WORLD HELP
								for(String s : helpList) {
									p.sendMessage(s);
									p.sendMessage(" ");
								}
							} else {
								switch(args[1]) {
									
									default:
										for(String s : helpList) {
											p.sendMessage(s);
											p.sendMessage(" ");
										}
										return true;
									
									case "add":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.addWorld"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											if(Util.isWorldNameValid(arenaWorldID) == true) {
												GunGamePlugin.instance.arenaManager.createArenaWorld(arenaWorldID);
												p.sendMessage(LangUtil.createString("lang.Commands.World.added",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world add",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											}
										}
										return true;
									
									case "list":
										p.sendMessage(ChatColor.RED + "[GunGame] Maps: ");
										for(int i = 0; i < GunGamePlugin.instance.arenaManager.arenaWorlds.size(); i++) {
											ArenaWorld aMap = GunGamePlugin.instance.arenaManager.arenaWorlds.get(i);
											String aName = aMap.getName();
											
											//if(Main.plugin.arenaManager.usedWorlds.contains(aMap)) {
												//p.sendMessage(ChatColor.YELLOW + " - Map #" + (i+1) + ": " + ChatColor.RED + aName);
											//} else {
												p.sendMessage(ChatColor.YELLOW + " - Map #" + (i+1) + ": " + ChatColor.GREEN + aName);
											//}
											
										}
										return true;
									
									case "addspawn":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.addSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.addSpawn(p.getLocation());
												p.sendMessage(LangUtil.createString2("lang.Commands.World.addedSpawn",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addSpawn",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addspawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();										}
										return true;
									
									case "tpspawn":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.tpSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											Integer spawnNmbr = Integer.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												Location tpTo = a.getSpawn(spawnNmbr);
												p.teleport(tpTo);
												p.sendMessage(LangUtil.createString2("lang.Commands.World.tpToSpawn",
														null,
														arenaWorldID,
														p,
														null,
														spawnNmbr,
														null,
														null,
														"gg world tpSpawn",
														tpTo,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world tpspawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
										}
										return true;
									
									case "setspawn":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.setSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											Integer spawnNmbr = Integer.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.setSpawn(p.getLocation(), spawnNmbr);
												p.sendMessage(LangUtil.createString2("lang.Commands.World.spawnSet",
														null,
														arenaWorldID,
														p,
														null,
														spawnNmbr,
														null,
														null,
														"gg world setSpawn",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world setSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();	
										}
										return true;
									
									case "resetspawns":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.resetSpawns"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.delSpawns();
												p.sendMessage(LangUtil.createString("lang.Commands.World.resetedSpawns",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world ressetSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world resetSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}
										return true;
									
									case "delspawn":
										if(p.hasPermission("gungame.admin")) {
											if(args.length < 4) {
												p.sendMessage(LangUtil.buildHelpString("Commands.World.deleteSpawn"));
											} else {
												String arenaWorldID = String.valueOf(args[2]);
												int spawn = Integer.valueOf(args[3]);
												ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
												if(a != null) {
													a.delSpawn(spawn);
													p.sendMessage(LangUtil.createString2("lang.Commands.World.deletedSpawn",
															null,
															arenaWorldID,
															p,
															null,
															spawn,
															null,
															null,
															"gg world deleteSpawn",
															a.getSpawn(spawn),
															null,
															null,
															null,
															null,
															null,
															true,
															false));
												} else {
													p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
															null,
															arenaWorldID,
															p,
															null,
															null,
															null,
															null,
															"gg world deleteSpawn",
															null,
															null,
															null,
															null,
															null,
															null,
															true,
															true));
												}
												FileManager.saveArenaConfig();
											}
										} else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
											return true;
										}
										return true;
									
									case "listspawns":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.listSpawns"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												List<String> spawns = a.getSpawnList();
												p.sendMessage(ChatColor.AQUA + a.getName());
												p.sendMessage(ChatColor.BLUE + a.getWorld());
												for(int i = 0; i <= spawns.size() -1; i++) {
													//int j = i +1;
													p.sendMessage(ChatColor.YELLOW + " - Spawn #" + (i+1) + ": " + ChatColor.GREEN + spawns.get(i));
												}
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world listspawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
										}
										return true;
										
										
										
										
										
										
										
										
								
										
										
										
									case "addtankspawn":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.addTankSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.addTankSpawn(p.getLocation());
												p.sendMessage(LangUtil.createString2("lang.Commands.World.addedTankSpawn",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addTankSpawn",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addTankSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();										}
										return true;
									
									case "tptankspawn":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.tpTankSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											Integer spawnNmbr = Integer.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												Location tpTo = a.getTankSpawn(spawnNmbr);
												p.teleport(tpTo);
												p.sendMessage(LangUtil.createString2("lang.Commands.World.tpToTankSpawn",
														null,
														arenaWorldID,
														p,
														null,
														spawnNmbr,
														null,
														null,
														"gg world tpTankSpawn",
														tpTo,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world tpTankSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
										}
										return true;
									
									case "settankspawn":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.setTankSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											Integer spawnNmbr = Integer.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.setTankSpawn(p.getLocation(), spawnNmbr);
												p.sendMessage(LangUtil.createString2("lang.Commands.World.tankSpawnSet",
														null,
														arenaWorldID,
														p,
														null,
														spawnNmbr,
														null,
														null,
														"gg world setTankSpawn",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world setTankSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();	
										}
										return true;
									
									case "resettankspawns":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.resetTankSpawns"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.delTankSpawns();
												p.sendMessage(LangUtil.createString("lang.Commands.World.resetedTankSpawns",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world ressetTankSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world resetTankSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}
										return true;
									
									case "deltankspawn":
										if(p.hasPermission("gungame.admin")) {
											if(args.length < 4) {
												p.sendMessage(LangUtil.buildHelpString("Commands.World.deleteTankSpawn"));
											} else {
												String arenaWorldID = String.valueOf(args[2]);
												int spawn = Integer.valueOf(args[3]);
												ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
												if(a != null) {
													a.delTankSpawn(spawn);
													p.sendMessage(LangUtil.createString2("lang.Commands.World.deletedTankSpawn",
															null,
															arenaWorldID,
															p,
															null,
															spawn,
															null,
															null,
															"gg world delTankSpawn",
															a.getSpawn(spawn),
															null,
															null,
															null,
															null,
															null,
															true,
															false));
												} else {
													p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
															null,
															arenaWorldID,
															p,
															null,
															null,
															null,
															null,
															"gg world delTankSpawn",
															null,
															null,
															null,
															null,
															null,
															null,
															true,
															true));
												}
												FileManager.saveArenaConfig();
											}
										} else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
											return true;
										}
										return true;
										
										case "listtankspawns":
											if(args.length < 3) {
												p.sendMessage(LangUtil.buildHelpString("Commands.World.listTankSpawns"));
											} else {
												String arenaWorldID = String.valueOf(args[2]);
												ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
												if(a != null) {
													List<String> spawns = a.getTankSpawnList();
													p.sendMessage(ChatColor.AQUA + a.getName());
													p.sendMessage(ChatColor.BLUE + a.getWorld());
													for(int i = 0; i <= spawns.size() -1; i++) {
														//int j = i +1;
														p.sendMessage(ChatColor.YELLOW + " - Tank-Spawn #" + (i+1) + ": " + ChatColor.GREEN + spawns.get(i));
													}
												} else {
													p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
															null,
															arenaWorldID,
															p,
															null,
															null,
															null,
															null,
															"gg world listtankspawns",
															null,
															null,
															null,
															null,
															null,
															null,
															true,
															true));
												}
											}
											return true;
										
										
								
										
										
										
									case "addturretspawn":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.addTurretSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.addTurretSpawn(p.getLocation().getBlock().getLocation());
												p.sendMessage(LangUtil.createString2("lang.Commands.World.addedTurretSpawn",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addTurretSpawn",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addTurretSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();										}
										return true;
									
									case "tpturretspawn":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.tpTurretSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											Integer spawnNmbr = Integer.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												Location tpTo = a.getTurretSpawn(spawnNmbr);
												p.teleport(tpTo);
												p.sendMessage(LangUtil.createString2("lang.Commands.World.tpToTurretSpawn",
														null,
														arenaWorldID,
														p,
														null,
														spawnNmbr,
														null,
														null,
														"gg world tpTurretSpawn",
														tpTo,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world tpTurretSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
										}
										return true;
									
									case "setturretspawn":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.setTurretSpawn"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											Integer spawnNmbr = Integer.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.setTurretSpawn(p.getLocation().getBlock().getLocation(), spawnNmbr);
												p.sendMessage(LangUtil.createString2("lang.Commands.World.turretSpawnSet",
														null,
														arenaWorldID,
														p,
														null,
														spawnNmbr,
														null,
														null,
														"gg world setTurretSpawn",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world setTurretSpawn",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();	
										}
										return true;
									
									case "resetturretspawns":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.resetTurretSpawns"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.delTurretSpawns();
												p.sendMessage(LangUtil.createString("lang.Commands.World.resetedTurretSpawns",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world resetTurretSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world resetTurretSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}
										return true;
									
									case "delturretspawn":
										if(p.hasPermission("gungame.admin")) {
											if(args.length < 4) {
												p.sendMessage(LangUtil.buildHelpString("Commands.World.deleteTurretSpawn"));
											} else {
												String arenaWorldID = String.valueOf(args[2]);
												int spawn = Integer.valueOf(args[3]);
												ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
												if(a != null) {
													a.delTurretSpawn(spawn);
													p.sendMessage(LangUtil.createString2("lang.Commands.World.deletedTurretSpawn",
															null,
															arenaWorldID,
															p,
															null,
															spawn,
															null,
															null,
															"gg world delTurretSpawn",
															a.getSpawn(spawn),
															null,
															null,
															null,
															null,
															null,
															true,
															false));
												} else {
													p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
															null,
															arenaWorldID,
															p,
															null,
															null,
															null,
															null,
															"gg world delTurretSpawn",
															null,
															null,
															null,
															null,
															null,
															null,
															true,
															true));
												}
												FileManager.saveArenaConfig();
											}
										} else {
											p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
											return true;
										}
										return true;
									
									case "listturretspawns":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.listTurretSpawns"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												List<String> spawns = a.getTurretSpawnList();
												p.sendMessage(ChatColor.AQUA + a.getName());
												p.sendMessage(ChatColor.BLUE + a.getWorld());
												for(int i = 0; i <= spawns.size() -1; i++) {
													//int j = i +1;
													p.sendMessage(ChatColor.YELLOW + " - Turret-Spawn #" + (i+1) + ": " + ChatColor.GREEN + spawns.get(i));
												}
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world listTurretSpawns",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
										}
										return true;
										
										
										
										
										
										
									
										
									
									case "set":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.setWorld"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											String world = String.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.setWorld(world);
												p.sendMessage(LangUtil.createString("lang.Commands.World.worldSet",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world set",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world setworld",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}
				
									return true;
									
									case "?":
										for(String s : helpList) {
											p.sendMessage(s);
											p.sendMessage(" ");
										}
										return true;
									case "help":
										for(String s : helpList) {
											p.sendMessage(s);
											p.sendMessage(" ");
										}
										return true;
										
									case "save":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.saveWorld"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.saveArenaWorld();
												p.sendMessage(LangUtil.createString("lang.Commands.World.savedWorld",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world save",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world save",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}								
									return true;
									
									case "addBuilder":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.addBuilder"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											String builderID = String.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.addBuilder(builderID);
												p.sendMessage(LangUtil.createString("lang.Commands.World.addedBuilder",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addbuilder",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world addBuilder",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}								
									return true;
									
									case "remBuilder":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.removeBuilder"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											String builderID = String.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.remBuilder(builderID);
												p.sendMessage(LangUtil.createString("lang.Commands.World.removedBuilder",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world rembuilder",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world remBuilder",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}								
									return true;
									
									case "resetBuilders":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.resetBuilders"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											//String builderID = String.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.resetBuilders();
												p.sendMessage(LangUtil.createString("lang.Commands.World.resettedBuilder",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world resetbuilders",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world resetBuilders",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}								
									return true;
									
									case "listBuilders":
										if(args.length < 3) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.listBuilders"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											//String builderID = String.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												List<String> builders = a.getBuilders();
												p.sendMessage(ChatColor.AQUA + a.getName());
												for(String b : builders) {
													//int j = i +1;
													p.sendMessage(ChatColor.YELLOW + " - " + ChatColor.GREEN + b);
												}
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world listBuilders",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
										}								
									return true;
									
									case "setResourcepack":
										if(args.length < 4) {
											p.sendMessage(LangUtil.buildHelpString("Commands.World.addResourcepack"));
										} else {
											String arenaWorldID = String.valueOf(args[2]);
											String RP = String.valueOf(args[3]);
											ArenaWorld a = GunGamePlugin.instance.arenaManager.getArenaWorld(arenaWorldID);
											if(a != null) {
												a.setResourcePackLInk(RP);
												p.sendMessage(LangUtil.createString("lang.Commands.World.setResourcepack",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world setResourcepack",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														false));
											} else {
												p.sendMessage(LangUtil.prefixErr + LangUtil.createString("lang.Errors.worldDoesNotExist",
														null,
														arenaWorldID,
														p,
														null,
														null,
														null,
														null,
														"gg world setResourcepack",
														null,
														null,
														null,
														null,
														null,
														null,
														true,
														true));
											}
											FileManager.saveArenaConfig();
										}								
									return true;
								
								}
							}
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
							return true;
						}
						return true;
						
					
					
					
					
					
					
					
					case "check":
						if(p.hasPermission("gungame.admin")) {
							if(args.length <= 1) {
								p.sendMessage(LangUtil.buildHelpString("Commands.checkArena"));
							} else {
								String arenaID = String.valueOf(args[1]);
								Arena a = GunGamePlugin.instance.arenaManager.getArena(arenaID);
								if(ArenaFileStuff.isArenaValid(a.getName())) {
									p.sendMessage(LangUtil.createString("lang.Info.arenaValid",
											a,
											null,
											p,
											null,
											null,
											null,
											null,
											"gg check arena",
											null,
											null,
											null,
											null,
											null,
											null,
											true,
											false));
								} else {
									p.sendMessage(LangUtil.createString("lang.Errors.arenaInvalid",
											a,
											null,
											p,
											null,
											null,
											null,
											null,
											"gg check arena",
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
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
							return true;
						}
						return true;
					
					
					
						
						
						
						
						
						
						
						
						
						
					case "items":
						if(p.hasPermission("gungame.admin")) {
							if(args.length > 1) {
								List<String> items = LangUtil.createStringList("lang.Commands.ItemList",
										null,
										null,
										p,
										null,
										null,
										null,
										null,
										"gg items list",
										null,
										null,
										null,
										null,
										null,
										null,
										true,
										false);
								List<String> itemsHelp = LangUtil.createStringList("lang.Commands.ItemsHelp",
										null,
										null,
										p,
										null,
										null,
										null,
										null,
										"gg items help",
										null,
										null,
										null,
										null,
										null,
										null,
										true,
										false);
								switch(args[1]) {
								
								default:
									
									for(String m : itemsHelp) {
										p.sendMessage(m);
									}
									return true;
								
								case "give":
									if(args.length > 2) {
										switch(args[2]) {
										default:
											for(String m : items) {
												p.sendMessage(m);
											}
											p.sendMessage(ChatColor.GREEN + "- " + ChatColor.YELLOW + "crowbar");
											p.sendMessage(ChatColor.GREEN + "- " + ChatColor.YELLOW + "c4");
											p.sendMessage(ChatColor.GREEN + "- " + ChatColor.YELLOW + "radar");
											p.sendMessage(ChatColor.GREEN + "- " + ChatColor.YELLOW + "suicide_jacket");
											//p.sendMessage(ChatColor.GREEN + "- " + ChatColor.YELLOW + "gravity_grenade");
											return true;
											
										case "crowbar":
											if(GunGamePlugin.instance.serverPre113) {
												p.getInventory().addItem(Crowbar_pre_1_13.CrowBar());
											} else {
												p.getInventory().addItem(Crowbar_v1_13_up.CrowBar());
											}
											return true;
										case "flaregun":
											if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
												p.getInventory().addItem(FlareGun.getFlareGun());
											} else {
												p.sendMessage(LangUtil.createString2("lang.Errors.notInArena",
														null,
														null,
														p,
														null,
														null,
														null,
														null,
														"gg items flaregun",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														false,
														true));
											}
											return true;
										case "radar":
											if(GunGamePlugin.instance.arenaManager.isIngame(p)) {
												p.getInventory().addItem(Radar.radar());
											} else {
												p.sendMessage(LangUtil.createString2("lang.Errors.notInArena",
														null,
														null,
														p,
														null,
														null,
														null,
														null,
														"gg items radar",
														p.getLocation(),
														null,
														null,
														null,
														null,
														null,
														false,
														true));
											}
											return true;
										/*case "c4":
											p.getInventory().addItem(C4.c4Remote());
											p.getInventory().addItem(C4.c4Throwable(16));
											return true;*/
										case "suicide_jacket":
											p.getInventory().addItem(SuicideArmor.suicideWest());
											p.getInventory().addItem(SuicideArmor.remote());
											break;

										}
									} else {
										p.sendMessage(LangUtil.buildHelpString("Commands.Items.Give"));
									}
									return true;
									
								case "list":
									
									for(String m : items) {
										p.sendMessage(m);
									}
									p.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + "crowbar");
									p.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + "c4");
									p.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + "radar");
									p.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + "suicide_jacket");
									p.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + "flaregun");
									//p.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + "gravity_grenade");
									
									return true;
								
								case "help":
									for(String m : itemsHelp) {
										p.sendMessage(m);
									}
									return true;
									
								}
							} else {
								List<String> itemsHelp = LangUtil.createStringList("lang.Commands.ItemsHelp",
										null,
										null,
										null,
										null,
										null,
										null,
										null,
										"gg help items",
										null,
										null,
										null,
										null,
										null,
										null,
										true,
										false);
								for(String m : itemsHelp) {
									p.sendMessage(m);
								}
							}
							
						} else {
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', LangUtil.noPermission));
							return true;
							
						}						
						break;					
					}
					
				} else {
					List<String> userHelp = LangUtil.createStringList("lang.Commands.Help",
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							"gg help ",
							null,
							null,
							null,
							null,
							null,
							null,
							true,
							false);
					List<String> adminHelp = LangUtil.createStringList("lang.Commands.HelpAdmin",
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							"gg help admin",
							null,
							null,
							null,
							null,
							null,
							null,
							true,
							false);
					if(!p.hasPermission("gungame.admin")) {
						for (String msg : userHelp) {
							p.sendMessage(msg);
						}
					} else {
						for (String msg : userHelp) {
							p.sendMessage(msg);
						}
						for (String msg : adminHelp) {
							p.sendMessage(msg);
						}
					}
					
				}
				
				
				
			}
			
		}
				
		return true;
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("gg") | cmd.getName().equalsIgnoreCase("gungame") | cmd.getName().equalsIgnoreCase("gga")) {
			List<String> commandList = new ArrayList<String>();
			if(sender instanceof Player) {
				if(args.length == 0 || args == null) {
					
					commandList.clear();
					
					if(sender.hasPermission("gungame.admin") || sender.hasPermission("gungame.user")) {
						if(!GunGamePlugin.instance.arenaManager.isIngame((Player)sender)) {
							commandList.add("join");
						}					
						commandList.add("leave");
					}
					
					if(sender.hasPermission("gungame.admin")) {
						commandList.add("help");
						commandList.add("setgloballobby");
						commandList.add("arena");
						commandList.add("world");
						commandList.add("items");
					}
					
					return commandList;
					
				} else if(args.length >= 1 ) {

					switch(args[0]) {
						default:
							commandList.clear();
							
							if(sender.hasPermission("gungame.admin") || sender.hasPermission("gungame.user")) {
								if(sender.hasPermission("gungame.admin") || sender.hasPermission("gungame.user")) {
									if(!GunGamePlugin.instance.arenaManager.isIngame((Player)sender)) {
										commandList.add("join");
									}					
									commandList.add("leave");
								}
								
							}
							
							if(sender.hasPermission("gungame.admin")) {
								commandList.add("help");
								commandList.add("setgloballobby");
								commandList.add("arena");
								commandList.add("world");
								commandList.add("items");
							}
							break;
						case("join"):
							
							commandList.clear();
						
							for(Arena a : GunGamePlugin.instance.arenaManager.arenas) {
								String Aname = null;
								if((a.getGameState().equals(EGameState.LOBBY) || a.getGameState().equals(EGameState.STARTING)) && a.isEnabled() == true) {
									Aname = a.getName();
								}
								if(Aname != null) {
									commandList.add(Aname);
								}							
							}
						
							break;
						case("world"):
							
							commandList.clear();
						
							commandList.add("help");
							commandList.add("list");
							commandList.add("add");
							commandList.add("addspawn");
							commandList.add("addtankspawn");
							commandList.add("addturretspawn");
							commandList.add("setspawn");
							commandList.add("settankspawn");
							commandList.add("setturretspawn");
							commandList.add("tpspawn");
							commandList.add("tptankspawn");
							commandList.add("tpturretspawn");
							commandList.add("delspawn");
							commandList.add("deltankspawn");
							commandList.add("delturretspawn");
							commandList.add("listspawns");
							commandList.add("listtankspawns");
							commandList.add("listturretspawns");
							commandList.add("resetspawns");
							commandList.add("resettankspawns");
							commandList.add("resetturretspawns");
							commandList.add("set");
							commandList.add("save");
							commandList.add("listBuilders");
							commandList.add("addBuilder");
							commandList.add("remBuilder");
							commandList.add("resetBuilders");
							commandList.add("setResourcepack");
							
							if(args.length >= 3) {
								switch(args[1]) {
								case "list": 
									commandList.clear();
									break;
								case "add":
									commandList.clear();
									break;
								default:
									commandList.clear();
									
									for(ArenaWorld aw : GunGamePlugin.instance.arenaManager.arenaWorlds) {
										commandList.add(aw.getName());
									}
									break;
								}
								if(args.length >= 4) {
									commandList.clear();
								}
								
							}
							
							break;
						case("arena"):
							
							commandList.clear();
							
							commandList.add("help");
							commandList.add("list");
							commandList.add("add");
							commandList.add("enable");
							commandList.add("setlobby");
							
							if(args.length >= 3) {
								switch(args[1]) {
								case "add":
									commandList.clear();
									break;
								case "list":
									commandList.clear();
									break;
								case "help":
									commandList.clear();
									break;
								default:
									commandList.clear();
									
									for(Arena a : GunGamePlugin.instance.arenaManager.arenas) {
										commandList.add(a.getName());
									}
									break;
								}
								if(args.length >= 4) {
									commandList.clear();
								}
							}
							
							break;
						case("items"):
							
							commandList.clear();
						
							commandList.add("list");
							commandList.add("give");
							commandList.add("help");
							
							if(args.length >= 2) {
								if(args[1].equals("give")) {
									
									commandList.clear();
									
									commandList.add("radar");
									//commandList.add("c4");
									commandList.add("crowbar");
									commandList.add("suicide_jacket");
									commandList.add("flaregun");
								} 
							}
							
							break;
					}
					return commandList;
				}
			}
			
		}
		
		return null;
	}

}
