package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class GunEngineLoopingSound {
	
	public String config;
	
	private ArrayList<SoundCategory> category = new ArrayList<SoundCategory>();
	private ArrayList<Sound> sound = new ArrayList<Sound>();
	private ArrayList<Float> pitch = new ArrayList<Float>();
	private ArrayList<Float> volume = new ArrayList<Float>();
	private ArrayList<Long> delay = new ArrayList<Long>();
	private long duration;
	private boolean valid;
	private boolean playing;
	private int taskID;
	private int taskID2;
	private Location location;
	
	public GunEngineLoopingSound(String inputSounds, long duration) {
		this.config = inputSounds;
		this.duration = duration;
		
		if(this.config != null && this.config.length() != 0 && !(this.config.equalsIgnoreCase("-")) && !(this.config.equalsIgnoreCase("none"))) {
			readString();
		} else {
			this.valid = false;
		}
	}
	
	private void readString() {
		String[] splitted = this.config.split(",");
		
		for(String s : splitted) {
			String[] snd = s.split("-");
			this.sound.add(Sound.valueOf(snd[0]));
			this.pitch.add(Float.valueOf(snd[1]));
			this.volume.add(Float.valueOf(snd[2]));
			this.delay.add(Long.valueOf(snd[3]));
			this.category.add(SoundCategory.valueOf(snd[4]));
		}
		//this.duration = this.getPlayDuration();
		
		this.valid = true;
	}
	
	public void play(World world, Location loc) {
		this.location = loc;
		if(this.valid && !this.playing) {
			this.playing = true;
			//while(this.playing) {
				/*GunEngineLoopingSound ref = this;
				this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
					
					@Override
					public void run() {
						for(int i = 0; i < ref.category.size(); i++) {
							Integer I = i;
								ref.taskID2 = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
									
									@Override
									public void run() {
										world.playSound(ref.location, sound.get(I),  category.get(I),  volume.get(I),  pitch.get(I));
									}
									
								}, ref.delay.get(i));
						}		
					}
				}, 0, this.duration);*/
			//}
			GunEngineLoopingSound ref = this;
			for(int i = 0; i < this.category.size(); i++) {
				Integer I = i;
					this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							world.playSound(ref.location, sound.get(I),  category.get(I),  volume.get(I),  pitch.get(I));
						}
						
					}, this.delay.get(i));
			}
			this.taskID2 = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					ref.playing = false;
				}
			}, this.duration);
		}	
	}
	public void stop() {
		Bukkit.getScheduler().cancelTask(this.taskID2);
		Bukkit.getScheduler().cancelTask(this.taskID);
		//this.valid = false;
		if(this.location != null) {
			for(Sound snd : this.sound) {
				try {
					for(Entity ent : this.location.getWorld().getNearbyEntities(this.location, 6.0, 6.0, 6.0)) {
						if(ent instanceof Player) {
							((Player)ent).stopSound(snd);
						}
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		this.playing = false;
	}
	public long getPlayDuration() {
		/*long dur = 0;
		for(long l : this.delay) {
			dur = dur + l;
		}
		return dur;*/
		return this.duration;
	}

}
