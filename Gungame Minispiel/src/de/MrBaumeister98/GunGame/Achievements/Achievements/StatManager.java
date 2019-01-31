package de.MrBaumeister98.GunGame.Achievements.Achievements;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Achievements.Achievements.GunGameAchievementUtil.CriteriaE;
import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class StatManager {
	
	public HashMap<UUID, StatPlayer> getStatPlayer = new HashMap<UUID, StatPlayer>();
	public Arena owner;
	
	public StatManager(Arena owner) {
		this.owner = owner;
		
		for(Player p : this.owner.getPlayers()) {
			UUID uuid = p.getUniqueId();
			StatPlayer statP = new StatPlayer(p, this);
			this.getStatPlayer.put(uuid, statP);
		}
	}
	
	public void endGame(Player Winner, Player Loser) {
		for(Player p : this.owner.getPlayers()) {
			this.getStatPlayer.get(p.getUniqueId()).incrementGamesPlayed();
		}
		StatPlayer statPW = this.getStatPlayer.get(Winner.getUniqueId());
		statPW.winORlose(true);
		StatPlayer statPL = this.getStatPlayer.get(Loser.getUniqueId());
		statPL.winORlose(false);
	}
	public void calculateAchievements() {
		for(StatPlayer statP : this.getStatPlayer.values()) {
			
			calculateAchievements(statP.getPlayer());
		}
	}
	public void calculateAchievements(Player p) {
		if(p != null && p.isOnline()) {
			StatPlayer statP = this.getStatPlayer.get(p.getUniqueId());
			
			if(Util.grantAchievementsAfterUnlocking) {
				for(GunGameAchievement ggach : GunGamePlugin.instance.achUtil.achievements) {
					Double minimumReach = ggach.getToReach().doubleValue();
					//CHECK IF PLAYER ALREADY HAS THE ADVANCEMENT
					if(!statP.getPlayer().getAdvancementProgress(ggach.getAdv().getAdvancement()).isDone()) {
						//CHECK IF THE CONDITION IS REACHED
						if(ggach.getCriteria().equals(CriteriaE.KILLS_ONE_ROUND)) {
							if(statP.getFile().getProgress(ggach.getCriteria()) == minimumReach) {
								//GRANT THE ADVANCEMENT
								ggach.getAdv().grant(statP.getPlayer());
								
							}
						} else {
							if(statP.getFile().getProgress(ggach.getCriteria()) >= minimumReach) {
								//GRANT THE ADVANCEMENT
								ggach.getAdv().grant(statP.getPlayer());
								
							}
						}
					}
				}
			} else {
				if(statP.getReachedList() != null && !statP.getReachedList().isEmpty() && statP.getReachedList().size() > 0) {
					for(GunGameAchievement ggach: statP.getReachedList()) {
						Double minimumReach = ggach.getToReach().doubleValue();
						//CHECK IF PLAYER ALREADY HAS THE ADVANCEMENT
						if(!statP.getPlayer().getAdvancementProgress(ggach.getAdv().getAdvancement()).isDone()) {
							//CHECK IF THE CONDITION IS REACHED
							if(statP.getFile().getProgress(ggach.getCriteria()) >= minimumReach) {
								//GRANT THE ADVANCEMENT
								ggach.getAdv().grant(statP.getPlayer());
								
							}
						}
					}
				}
			}
		}
	}
}
