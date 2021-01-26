package de.MrBaumeister98.GunGame.Achievements.Achievements;

import java.util.ArrayList;
import java.util.List;

public enum CriteriaE {
	JOIN,
	KILL,
	KILLS_ONE_ROUND,
	KILLSTREAK,
	KILLSTREAK_NO_DAM,
	TAKE_DAMAGE,
	TAKE_DAMAGE_NO_DIE,
	DAMAGE_DEALT,
	HEADSHOT,
	PLAYGAME,
	WIN,
	WIN_SERIES,
	WIN_NO_DAM,
	LOSE,
	LOSE_SERIES,
	DIE,
	LOCKPICK,
	/*USE_C4,*/
	RELOAD,
	SHOOT,
	SUICIDEBOMB,
	THROWGRENADE,
	IMPOSSIBLE;
	
	private List<GunGameAchievement> achievements;
	
	CriteriaE() {
		this.achievements = new ArrayList<GunGameAchievement>();
	}
	
	public void addAchievement(GunGameAchievement ach) {
		if(this.achievements != null && !this.achievements.contains(ach)) {
			this.achievements.add(ach);
		}
	}
	
	public List<GunGameAchievement> getAchievements() {
		return this.achievements;
	}
}