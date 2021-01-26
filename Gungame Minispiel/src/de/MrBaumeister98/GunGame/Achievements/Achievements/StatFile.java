package de.MrBaumeister98.GunGame.Achievements.Achievements;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Util.Util;

public class StatFile {
	
	private FileConfiguration config;
	private File configFile;
	private StatPlayer statPlayer;
	
	public StatFile(Player p, StatPlayer statP) {
		this.config = Util.getPlayerGunGameFileConfiguration(p);
		this.configFile = Util.getPlayerGunGameFile(p);
		this.statPlayer = statP;
	}
	private void save() {
		try {
			this.config.save(this.configFile);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	public void updateKills() {
		int oldkills = this.config.getInt("Statistics.Kills.Total", 0);
		oldkills++;
		this.config.set("Statistics.Kills.Total", oldkills);
		save();
	}
	public void updateMaximumKillstreak() {
		this.config.set("Statistics.KillStreak.Highest", this.statPlayer.getCurrentKillStreak());
		save();
	}
	public void updateMaximumKillstreakNoDamage() {
		this.config.set("Statistics.KillStreak.WithoutTakingDamage", this.statPlayer.getCurrentKillStreakNoDamage());
		save();
	}
	public void updateDamageTaken() {
		Double temp = this.config.getDouble("Statistics.Damage.Taken");
		temp = temp + this.statPlayer.getTakenDamage();
		this.config.set("Statistics.Damage.Taken", temp);
		save();
	}
	public void updateDamageDealt() {
		Double temp = this.config.getDouble("Statistics.Damage.Dealt");
		temp = temp + this.statPlayer.getDealtDamage();
		this.config.set("Statistics.Damage.Dealt", temp);
		save();
	}
	public void updateDamageTakenNoDie() {
		Double temp = this.config.getDouble("Statistics.Damage.TakenNoDie");
		if(this.statPlayer.getTakenDamageNoDie() >= temp) {
			this.config.set("Statistics.Damage.TakenNoDie", this.statPlayer.getTakenDamageNoDie());
			save();
		}
	}
	public void updateDeathCount() {
		Integer temp = this.config.getInt("Statistics.Deaths.Count");
		temp++;
		this.config.set("Statistics.Deaths.Count", temp);
		save();
	}
	public void updateGamesPlayed() {
		Integer temp = this.config.getInt("Statistics.Games.Played");
		temp++;
		this.config.set("Statistics.Games.Played", temp);
		save();
	}
	public void updateWinsAndLosed(Boolean won, Boolean tookDamage) {
		if(won == true) {
			this.config.set("Statistics.Games.Lost.Series", 0);
			Integer temp = this.config.getInt("Statistics.Games.Won.Total");
			temp++;
			this.config.set("Statistics.Games.Won.Total", temp);
			
			Integer temp2 = this.config.getInt("Statistics.Games.Won.Series");
			temp2++;
			this.config.set("Statistics.Games.Won.Series", temp2);
			if(tookDamage == false) {
				Integer temp3 = this.config.getInt("Statistics.Games.Won.NoDamageTaken");
				temp3++;
				this.config.set("Statistics.Games.Won.NoDamageTaken", temp3);
			}
		} else if(won == false) {
			this.config.set("Statistics.Games.Won.Series", 0);
			Integer temp = this.config.getInt("Statistics.Games.Lost.Total");
			temp++;
			this.config.set("Statistics.Games.Lost.Total", temp);
			
			Integer temp2 = this.config.getInt("Statistics.Games.Lost.Series");
			temp2++;
			this.config.set("Statistics.Games.Lost.Series", temp2);
		}
		save();
	}
	public void updateJoinedGames() {
		Integer temp = this.config.getInt("Statistics.Games.Joined");
		temp++;
		this.config.set("Statistics.Games.Joined", temp);
		save();
	}
	public void updateHeadShotsDealt() {
		Integer temp = this.config.getInt("Statistics.Kills.Headshots");
		temp++;
		this.config.set("Statistics.Kills.Headshots", temp);
		save();
	}
	public void updateLocksPicked() {
		Integer temp = this.config.getInt("Statistics.LocksPicked");
		temp++;
		this.config.set("Statistics.LocksPicked", temp);
		save();
	}
	public void updateSuicideBombings() {
		Integer temp = this.config.getInt("Statistics.Deaths.SuicideBombings");
		temp++;
		this.config.set("Statistics.Deaths.SuicideBombings", temp);
		save();
	}
	public void updatePlantedC4() {
		Integer temp = this.config.getInt("Statistics.C4Planted");
		temp++;
		this.config.set("Statistics.C4Planted", temp);
		save();
	}
	public void updateKillsLastRound() {
		Integer temp = this.statPlayer.getKills();
		this.config.set("Statistics.Kills.LastRound", temp);
		save();
	}
	
	public void updateReloadCount() {
		Integer temp = this.config.getInt("Statistics.Weapons.ReloadCount");
		temp++;
		this.config.set("Statistics.Weapons.ReloadCount", temp);
		save();
	}
	public void updateShotCount() {
		Integer temp = this.config.getInt("Statistics.Weapons.ShotCount");
		temp++;
		this.config.set("Statistics.Weapons.ShotCount", temp);
		save();
	}
	public void updateGrenadesThrown() {
		Integer temp = this.config.getInt("Statistics.Weapons.ThrownGrenades");
		temp++;
		this.config.set("Statistics.Weapons.ThrownGrenades", temp);
		save();
	}
	
	
	
	
	
	
	
	public Integer getKillStreak() {
		return this.config.getInt("Statistics.KillStreak.Highest");
	}
	public Integer getKillStreakNoDamage() {
		return this.config.getInt("Statistics.KillStreak.WithoutTakingDamage");
	}
	
	public Double getProgress(CriteriaE criteria) {
		//String ret = null;
		Double temp = 0.0D;
		Integer tempI = 0;
		switch(criteria) {
		default:
			break;
		case DIE:
			tempI = this.config.getInt("Statistics.Deaths.Count");
			break;
		case DAMAGE_DEALT:
			temp = this.config.getDouble("Statistics.Damage.Dealt");
			break;
		case JOIN:
			tempI = this.config.getInt("Statistics.Games.Joined");
			break;
		case PLAYGAME:
			tempI = this.config.getInt("Statistics.Games.Played");
			break;
		case HEADSHOT:
			tempI = this.config.getInt("Statistics.Kills.Headshots");
			break;
		case KILL:
			tempI = this.config.getInt("Statistics.Kills.Total");
			break;
		case KILLSTREAK:
			tempI = this.config.getInt("Statistics.KillStreak.Highest");
			break;
		case KILLSTREAK_NO_DAM:
			tempI = this.config.getInt("Statistics.KillStreak.WithoutTakingDamage");
			break;
		case IMPOSSIBLE:
			break;
		case LOCKPICK:
			tempI = this.config.getInt("Statistics.LocksPicked");
			break;
		case LOSE:
			tempI = this.config.getInt("Statistics.Games.Lost.Total");
			break;
		case LOSE_SERIES:
			tempI = this.config.getInt("Statistics.Games.Lost.Series");
			break;
		case RELOAD:
			tempI = this.config.getInt("Statistics.Weapons.ReloadCount");
			break;
		case SHOOT:
			tempI = this.config.getInt("Statistics.Weapons.ShotCount");
			break;
		case SUICIDEBOMB:
			tempI = this.config.getInt("Statistics.Deaths.SuicideBombings");
			break;
		case TAKE_DAMAGE:
			temp = this.config.getDouble("Statistics.Damage.Taken");
			break;
		case TAKE_DAMAGE_NO_DIE:
			temp = this.config.getDouble("Statistics.Damage.TakenNoDie");
			break;
		case THROWGRENADE:
			tempI = this.config.getInt("Statistics.Weapons.ThrownGrenades");
			break;
		/*case USE_C4:
			tempI = this.config.getInt("Statistics.C4Planted");
			break;*/
		case WIN:
			tempI = this.config.getInt("Statistics.Games.Won.Total");
			break;
		case WIN_NO_DAM:
			tempI = this.config.getInt("Statistics.Games.Won.NoDamageTaken");
			break;
		case WIN_SERIES:
			tempI = this.config.getInt("Statistics.Games.Won.Series");
			break;
		case KILLS_ONE_ROUND:
			tempI = this.config.getInt("Statistics.Kills.LastRound");
			break;
		}
		if(tempI != 0) {
			temp = tempI.doubleValue();
		}
		return temp;
	}
	
}
