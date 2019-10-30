package de.MrBaumeister98.GunGame.Game.Util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;

public class ScoreboardUtil {
	
	private Arena arena;
	private Scoreboard board;
	
	public ScoreboardUtil(Arena arena) {
		this.arena = arena;
		
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		
		Objective obj = null;
			obj = sb.registerNewObjective("GunGame-" + arena.getName(), "dummy", LangUtil.buildGUIString("Scoreboard.Title"));
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(LangUtil.buildGUIString("Scoreboard.Title"));
		
		String arenaString = LangUtil.buildGUIString("Scoreboard.Arena").replaceAll("%arena%", this.arena.getName());
		Score arenaName = obj.getScore(arenaString);
		arenaName.setScore(15);
		
		String modeString = LangUtil.buildGUIString("Scoreboard.Mode").replaceAll("%mode%", this.arena.getArenaMode().toString().toUpperCase());
		Score modeName = obj.getScore(modeString);
		modeName.setScore(14);
		
		String map = "???";
		if(this.arena.getArenaWorld() != null) {
			map = this.arena.getArenaWorld().getName();
			String mn = map;
			map = LangUtil.buildGUIString("Scoreboard.Map").replaceAll("%map%", mn);
		} else {
			map = LangUtil.buildGUIString("Scoreboard.Map").replaceAll("%map%", "???");
		}
		Score mapName = obj.getScore(map);
		mapName.setScore(13);
		
		String pc = LangUtil.buildGUIString("Scoreboard.Players")
				.replaceAll("%currentPlayers%", String.valueOf(this.arena.getPlayers().size()))
				.replaceAll("%minPlayers%", String.valueOf(this.arena.getMinPlayers()))
				.replaceAll("%maxPlayers%", String.valueOf(this.arena.getMaxPlayers()));
		Score playerCounter = obj.getScore(pc);
		playerCounter.setScore(12);
		
		this.board = sb;
	}
	
	public void updateScoreBoard() {
		for(Player p : this.arena.getPlayers()) {
			updateScoreBoard(p);
		}
	}
	public void updateScoreBoard(Player p) {
		Scoreboard sb = this.board;

		Objective obj = sb.getObjective(DisplaySlot.SIDEBAR);
		if(obj != null) {
			obj.unregister();
		}
		
			obj = sb.registerNewObjective("GunGame-" + arena.getName(), "dummy", LangUtil.buildGUIString("Scoreboard.Title"));
		
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(LangUtil.buildGUIString("Scoreboard.Title"));
		
		String arenaString = LangUtil.buildGUIString("Scoreboard.Arena").replaceAll("%arena%", this.arena.getName());
		Score arenaName = obj.getScore(arenaString);
		arenaName.setScore(15);
		
		String modeString = LangUtil.buildGUIString("Scoreboard.Mode").replaceAll("%mode%", this.arena.getArenaMode().toString().toUpperCase());
		Score modeName = obj.getScore(modeString);
		modeName.setScore(14);
		
		String map = "???";
		if(this.arena.getArenaWorld() != null) {
			map = this.arena.getArenaWorld().getName();
			String mn = map;
			map = LangUtil.buildGUIString("Scoreboard.Map").replaceAll("%map%", mn);
		} else {
			map = LangUtil.buildGUIString("Scoreboard.Map").replaceAll("%map%", "???");
		}
		Score mapName = obj.getScore(map);
		mapName.setScore(13);
		
		String pc = LangUtil.buildGUIString("Scoreboard.Players")
				.replaceAll("%currentPlayers%", String.valueOf(this.arena.getPlayers().size()))
				.replaceAll("%minPlayers%", String.valueOf(this.arena.getMinPlayers()))
				.replaceAll("%maxPlayers%", String.valueOf(this.arena.getMaxPlayers()));
		Score playerCounter = obj.getScore(pc);
		playerCounter.setScore(12);

		this.board = sb;

		p.setScoreboard(this.board);
	}
	public void destroy() {
		for(Player p : this.arena.getPlayers()) {
			/*try {
				
			} catch(Exception ex) {
				
			}*/
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
		
		if(this.board != null) {
			try {
				this.board.getObjective(DisplaySlot.SIDEBAR).unregister();
				this.board.clearSlot(DisplaySlot.SIDEBAR);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	public Arena getOwner() {
		return this.arena;
	}
	public Scoreboard getBoard() {
		return this.board;
	}

}
