package de.MrBaumeister98.GunGame.Game.Arena;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.EGameState;
import de.MrBaumeister98.GunGame.Game.Util.TabListUtil;

public class SpectatorHidingThread {

	public ArenaManager manager;
	public boolean running;
	private int taskID;
	
	public SpectatorHidingThread(ArenaManager manager) {
		this.manager = manager;
		this.running = false;
	}
	
	public void start() {
		this.running = true;
		
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				execute();
			}
		}, 0, 1);
	}
	
	public void stop() {
		this.running = false;
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
	
	public void execute() {
		try {
			if(this.running) {
				
				for(Arena arena : this.manager.arenas) {
					if(arena.getPlayers() != null && !arena.getPlayers().isEmpty() && arena.getPlayers().size() > 0 && !arena.getGameState().equals(EGameState.ENDGAME) && !arena.getGameState().equals(EGameState.RESTORING)) {
						for(UUID specID : this.manager.spectatoriIDs) {
							Player spec = Bukkit.getPlayer(specID);
							spec.setGameMode(GameMode.SPECTATOR);
							spec.setGlowing(true);
							for(Player player : arena.getPlayers()) {
								if(player.isOnline() && spec.isOnline() && !player.getUniqueId().equals(specID)) {
									if(player.canSee(spec)) {
											player.hidePlayer(GunGamePlugin.instance, spec);
									}
								}
							}
						}
						//HIDES ALL PLAYERS THAT ARE NOT IN THE ARENA FOR THE PLAYERS IN THE ARENA
						for(Player p : arena.getPlayers()) {
							/*if(GunGamePlugin.instance.TabListAPIloaded) {
								String header = "&8<<<&l&cGun&r&l&7Game&r&8>>>";
								String playerKills = "&aKills: &e" + arena.getKills(p) + "&a/&c" + arena.getKillsToWin();
								de.Herbystar.TTA.TTA_Methods.sendTablist(p, header, playerKills);
							}*/
							try {
								String header = "&8<<<&l&cGun&r&l&7Game&r&8>>>";
								String playerKills = "&aKills: &e" + arena.getKills(p) + "&a/&c" + arena.getKillsToWin();
								TabListUtil.sendTabTitle(p, header, playerKills);
							} catch(Exception ex) {
								ex.printStackTrace();
							}
							
							for(Player pOnline : Bukkit.getOnlinePlayers()) {
								if(this.manager.isIngame(pOnline)) {
									if(!arena.equals(this.manager.getArena(pOnline))) {
										if(p.canSee(pOnline)) {
												p.hidePlayer(GunGamePlugin.instance, pOnline);
										}
									} else if(arena.equals(this.manager.getArena(pOnline))) {
										if(!p.canSee(pOnline)) {
												p.showPlayer(GunGamePlugin.instance, pOnline);
										}
									}
								} else {
									if(p.canSee(pOnline)) {
											p.hidePlayer(GunGamePlugin.instance, pOnline);
									}
								}
							}
						}
					}
				}
				
				/*try {
					sleep(25);
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				}*/
				
			} else {
				Bukkit.getScheduler().cancelTask(this.taskID);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
