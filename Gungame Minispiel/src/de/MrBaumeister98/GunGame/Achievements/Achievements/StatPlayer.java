package de.MrBaumeister98.GunGame.Achievements.Achievements;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class StatPlayer {
	
	private UUID uuid;
	private Player player;
	private StatFile file;
	private StatManager manager;
	
	private Boolean tookDamageDuringRound = false;
	//private Boolean won = false;
	
	private Double damageDealt = 0.0D;
	private Double damageTaken = 0.0D;
	private Double damageTakenNoDie = 0.0D;
	private Integer headShotsDealt = 0;
	private Integer currentKillStreak = 0;
	private Integer kills = 0;
	private Integer currentKillStreakNoDamage = 0;
	private Integer locksPicked = 0;
	private Integer c4Planted = 0;
	private Integer grenadesThrown = 0;
	private Integer weaponsShot = 0;
	private Integer suicideBombed = 0;
	private Integer deathCount = 0;
	private Integer weaponsReloaded = 0;
	
	private List<GunGameAchievement> reachedAchs = new ArrayList<GunGameAchievement>();
	
	public StatPlayer(Player p, StatManager manager) {
		this.player = p;
		this.uuid = p.getUniqueId();
		this.file = new StatFile(p, this);
		this.manager = manager;
	}
	private void check() {
		this.manager.calculateAchievements(this.player);
	}
	
	
	//DONE
	public void incrementWeaponsReloaded() {
		this.weaponsReloaded++;
		this.file.updateReloadCount();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementWeaponShots() {
		this.weaponsShot++;
		this.file.updateShotCount();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementGrenadesThrown() {
		this.grenadesThrown++;
		this.file.updateGrenadesThrown();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementC4Planted() {
		this.c4Planted++;
		this.file.updatePlantedC4();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementSuicideBombings() {
		this.suicideBombed++;
		this.file.updateSuicideBombings();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementLocksPicked() {
		this.locksPicked++;
		this.file.updateLocksPicked();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//MISSING BECAUSE OF MISSING HEADSHOT DETECTION
	public void incrementHeadShotsDealt() {
		this.headShotsDealt++;
		this.file.updateHeadShotsDealt();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementKillStreak() {
		this.kills++;
		this.file.updateKills();
		this.currentKillStreak++;
		if(this.currentKillStreak >= this.file.getKillStreak()) {
			this.file.updateMaximumKillstreak();
		}
		//if(this.tookDamageDuringRound == false) {
			this.currentKillStreakNoDamage++;
			if(this.currentKillStreakNoDamage >= this.file.getKillStreakNoDamage()) {
				this.file.updateMaximumKillstreakNoDamage();
			}
		//}
			
			if(Util.grantAchievementsAfterUnlocking) {
				check();
			} else {
				refreshList();
			}
	}
	//DONE
	public void incrementJoinedGames() {
		this.file.updateJoinedGames();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementTakenDamage(Double damage) {
		this.currentKillStreakNoDamage = 0;
		Double temp = this.damageTaken + damage;
		this.damageTaken = temp;
		this.file.updateDamageTaken();
		if(this.deathCount == 0 | this.deathCount == null | this.deathCount <= 0) {
			Double temp2 = this.damageTakenNoDie + damage;
			this.damageTakenNoDie = temp2;
			this.file.updateDamageTakenNoDie();
		}
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementDealtDamage(Double damage) {
		Double temp = this.damageDealt + damage;
		this.damageDealt = temp;
		this.file.updateDamageDealt();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementDeaths() {
		this.currentKillStreak = 0;
		this.currentKillStreakNoDamage = 0;
		this.damageTakenNoDie = 0.0D;
		this.tookDamageDuringRound = true;
		
		this.deathCount++;
		this.file.updateDeathCount();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	//DONE
	public void incrementGamesPlayed() {
		this.file.updateGamesPlayed();
		
		this.file.updateKillsLastRound();
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		}
	}
	//DONE
	public void winORlose(Boolean won) {
		this.file.updateWinsAndLosed(won, this.tookDamageDuringRound);
		
		if(Util.grantAchievementsAfterUnlocking) {
			check();
		} else {
			refreshList();
		}
	}
	
	
	private void refreshList() {
		Player statP = Bukkit.getPlayer(this.uuid);
		if(statP != null && statP.isOnline()) {
			for(GunGameAchievement ggach : GunGamePlugin.instance.achUtil.achievements) {
				Double minimumReach = ggach.getToReach().doubleValue();
				//CHECK IF PLAYER ALREADY HAS THE ADVANCEMENT
				if(!statP.getAdvancementProgress(ggach.getAdv().getAdvancement()).isDone()) {
					//CHECK IF THE CONDITION IS REACHED
					if(this.getFile().getProgress(ggach.getCriteria()) >= minimumReach) {
						//GRANT THE ADVANCEMENT
						this.reachedAchs.add(ggach);
						
					}
				}
			}
		}
	}
	
	public List<GunGameAchievement> getReachedList() {
		return this.reachedAchs;
	}
	
	
	
	
	
	
	
	
	
	
	public Integer getCurrentKillStreak() {
		return this.currentKillStreak;
	}
	public Integer getCurrentKillStreakNoDamage() {
		return this.currentKillStreakNoDamage;
	}
	public Double getTakenDamage() {
		return this.damageTaken;
	}
	public Double getDealtDamage() {
		return this.damageDealt;
	}
	public Double getTakenDamageNoDie() {
		return this.damageTakenNoDie;
	}
	public Integer getKills() {
		return this.kills;
	}
	public Player getPlayer() {
		return this.player;
	}
	public StatFile getFile() {
		return this.file;
	}



	public UUID getUuid() {
		return uuid;
	}
}
