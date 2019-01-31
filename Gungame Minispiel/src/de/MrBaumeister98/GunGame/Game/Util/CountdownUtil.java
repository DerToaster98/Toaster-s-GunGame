package de.MrBaumeister98.GunGame.Game.Util;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class CountdownUtil {
	
	private int taskID;
	private Arena owner;
	
	public CountdownUtil(Arena owner) {
		this.owner = owner;
	}
	
	public void startCountDown(Integer countdown, Integer borderBeginAnnounce) {
		countDownWithAnnounce(this.owner, countdown, borderBeginAnnounce);
	}
	public void startCountDown(Integer countdown, Integer borderBeginAnnounce, Runnable task) {
		countDownWithAnnounce(this.owner, countdown, borderBeginAnnounce, task);
	}
	public void cancelTask() {
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
	
	private void countDownWithAnnounce(Arena arena, Integer countdown, Integer borderBeginAnnounce) {
		Integer nmbr = countdown;
		Integer border = borderBeginAnnounce;
		//int taskID;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			Integer nmbr2 = nmbr;
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(nmbr2 <= border) {
					announceToPlayers(arena, nmbr2);
					if(nmbr2 <= 0) {
						cancelTask(taskID);
					}
				} else if(nmbr2 % 10 == 0 ) {
					announceToPlayers(arena, nmbr2);
				} 
				nmbr2--;
				
			}
			
		}, 0, 20);
	}
	private void countDownWithAnnounce(Arena arena, Integer countdown, Integer borderBeginAnnounce, Runnable task) {
		Integer nmbr = countdown;
		Integer border = borderBeginAnnounce;
		//int taskID;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			Integer nmbr2 = nmbr;
			@Override
			public void run() {
				if(nmbr2 == 0) {
					task.run();
				}
				if(nmbr2 <= border) {
					announceToPlayers(arena, nmbr2);
					if(nmbr2 <= 0) {
						cancelTask(taskID);
					}
				} else if(nmbr2 % 10 == 0 ) {
					announceToPlayers(arena, nmbr2);
				} 
				nmbr2--;
				
			}
			
		}, 0, 20);
	}
	
	private void announceToPlayers(Arena arena, Integer remainingTime) {
		for(Player p : arena.getPlayers()) {
			if(GunGamePlugin.instance.serverPre113) {
				p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_PLING"), 1, 1);
			} else {
				p.playSound(p.getLocation(), Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"), 1, 1);
			}
			p.sendMessage(LangUtil.createString("lang.Info.countingDown",
					arena,
					(arena.getArenaWorld() == null ? null : arena.getArenaWorld().getName()),
					p,
					remainingTime,
					null,
					arena.getMinPlayers(),
					arena.getMaxPlayers(),
					null,
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
	private void cancelTask(int taskID2) {
		Bukkit.getScheduler().cancelTask(taskID2);
	}

	public Arena getOwner() {
		return owner;
	}

}
