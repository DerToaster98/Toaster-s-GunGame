package de.MrBaumeister98.GunGame.Game.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaManager;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class SignListener implements Listener {
	
	public ArenaManager manager = GunGamePlugin.instance.arenaManager;
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onSignCreate(SignChangeEvent event) {
		if((event.getLine(0).equalsIgnoreCase("[GunGame]") || event.getLine(0).equalsIgnoreCase("[GG]")) && event.getPlayer().hasPermission("gungame.admin")) {
			String arenaName = "";
			try {
				arenaName = event.getLine(1);
			} catch (Exception ex) {
				
			}
			if(arenaName.equalsIgnoreCase("join")) {
				List<String> lines = new ArrayList<String>();
				lines.add(LangUtil.prefix);
				lines.add("----------------");
				lines.add(ChatColor.GREEN + "Join Game");
				lines.add("");
				for(int i = 0; i < lines.size(); i++) {
					event.setLine(i, lines.get(i));
					event.getBlock().getState().update();
					event.getBlock().getState().update(true);
				}
			}
			if(arenaName.equalsIgnoreCase("quickjoin")) {
				List<String> lines = new ArrayList<String>();
				lines.add(LangUtil.prefix);
				lines.add("----------------");
				lines.add(ChatColor.GREEN + "Quick Join");
				lines.add("");
				for(int i = 0; i < lines.size(); i++) {
					event.setLine(i, lines.get(i));
					event.getBlock().getState().update();
					event.getBlock().getState().update(true);
				}
			}
			if(arenaName.equalsIgnoreCase("rdmjoin")) {
				List<String> lines = new ArrayList<String>();
				lines.add(LangUtil.prefix);
				lines.add("----------------");
				lines.add(ChatColor.GREEN + "Random Game");
				lines.add("");
				for(int i = 0; i < lines.size(); i++) {
					event.setLine(i, lines.get(i));
					event.getBlock().getState().update();
					event.getBlock().getState().update(true);
				}
			}
			if(arenaName.equalsIgnoreCase("shop")) {
				List<String> lines = new ArrayList<String>();
				lines.add(LangUtil.prefix);
				lines.add("----------------");
				lines.add(ChatColor.GREEN + "Gun Shop");
				lines.add("");
				for(int i = 0; i < lines.size(); i++) {
					event.setLine(i, lines.get(i));
					event.getBlock().getState().update();
					event.getBlock().getState().update(true);
				}
			}
			else if(arenaName != null) {
				if(this.manager.isNameValid(arenaName)) {
					Arena arena = this.manager.getArena(arenaName);
					//arena.addSign(event.getBlock().getLocation());
					List<String> lines = arena.updateSignText();
					for(int i = 0; i<4; i++) {
						event.setLine(i, lines.get(i));
					}
					arena.addSign(event.getBlock().getLocation());
					FileManager.saveArenaConfig();
					event.getBlock().getState().update();
					event.getBlock().getState().update(true);
					Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Player " + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.YELLOW + " created a Join Sign for the Arena " + ChatColor.RED + arenaName + ChatColor.YELLOW + " at " + ChatColor.LIGHT_PURPLE + Util.locToString(event.getBlock().getLocation()));
				} else {
					event.getPlayer().sendMessage("Ungueltiger Name!");
				}
			} else {
				event.getPlayer().sendMessage("Ungueltiger Name!");
			}
		}
	}
	@EventHandler
	public void clickJoin(PlayerInteractEvent event) {
		if(event.getPlayer().hasPermission("gungame.admin")) {
			if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.getPlayer().isSneaking()) {
				Material blockType = event.getClickedBlock().getType();
				if(Util.isSignOrWallSign(blockType)) {
					Sign sign = (Sign) event.getClickedBlock().getState();
					String[] lines = sign.getLines();
					if(lines[0].equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LangUtil.prefix))) {
						String arenaName = ChatColor.stripColor(lines[1]);
						if(this.manager.isNameValid(arenaName) || arenaName.equalsIgnoreCase("join game") || arenaName.equalsIgnoreCase("quick join") || arenaName.equalsIgnoreCase("random game")) {
							event.setCancelled(true);
							this.manager.getArena(arenaName).remSign(event.getClickedBlock().getLocation());
							event.getClickedBlock().setType(Material.AIR);
							Debugger.logInfoWithColoredText(ChatColor.YELLOW + "Player " + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.YELLOW + " removed a Join Sign for the Arena " + ChatColor.RED + arenaName + ChatColor.YELLOW + " at " + ChatColor.LIGHT_PURPLE + Util.locToString(event.getClickedBlock().getLocation()));
						}
					}
				}
			}
		}
			/*} else */if(event.getPlayer().hasPermission("gungame.user") || event.getPlayer().hasPermission("gungame.admin")) {
				if((event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && event.getPlayer().isSneaking() == false) {
					Material blockType = event.getClickedBlock().getType();
					if(Util.isSignOrWallSign(blockType)) {
						Sign sign = (Sign) event.getClickedBlock().getState();
						String[] lines = sign.getLines();
						if(lines[0].equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', LangUtil.prefix))) {
							String arenaName = ChatColor.stripColor(lines[1]);
							if(ChatColor.stripColor(lines[2]).equalsIgnoreCase("join game")) {
								event.setCancelled(true);
								//OPEN JOIN GUI
								GunGamePlugin.instance.joinGuiManager.openMenu(event.getPlayer());
							}
							else if(ChatColor.stripColor(lines[2]).equalsIgnoreCase("quick join")) {
								event.setCancelled(true);
								List<Arena> arenasWithPlayers = new ArrayList<Arena>();
								for(Arena a : this.manager.arenas) {
									if(a.getPlayers().size() > 0 && a.getPlayers().size() < a.getMaxPlayers()) {
										arenasWithPlayers.add(a);
									}
								}
								if(!arenasWithPlayers.isEmpty()) {
									
									Collections.sort(arenasWithPlayers, new Comparator<Arena>() {

										@Override
										public int compare(Arena a1, Arena a2) {
											return a1.getPlayers().size() - a2.getPlayers().size();
										}
									});
									Collections.reverse(arenasWithPlayers);
									this.manager.tryJoin(event.getPlayer(), arenasWithPlayers.get(0));
								} else {
									Integer i = Util.getRandomNumber(this.manager.arenas.size());
									Arena a = this.manager.arenas.get(i);
									this.manager.tryJoin(event.getPlayer(), a);
								}
							}
							else if(ChatColor.stripColor(lines[2]).equalsIgnoreCase("random game")) {
								event.setCancelled(true);
								Integer i = Util.getRandomNumber(this.manager.arenas.size());
								Arena a = this.manager.arenas.get(i);
								this.manager.tryJoin(event.getPlayer(), a);
							}
							else if(ChatColor.stripColor(lines[2]).equalsIgnoreCase("gun shop")) {
								event.setCancelled(true);
								GunGamePlugin.instance.weaponShop.openShop(event.getPlayer());
							}
							else if(this.manager.isNameValid(arenaName)) {
								event.setCancelled(true);
								this.manager.tryJoin(event.getPlayer(), this.manager.getArena(arenaName));
							}
						}
					}
				}
			}
		//}		
	}

}
