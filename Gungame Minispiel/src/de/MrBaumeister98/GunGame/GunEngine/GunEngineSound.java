package de.MrBaumeister98.GunGame.GunEngine;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class GunEngineSound {
	
	public String config;
	
	private ArrayList<SoundCategory> category = new ArrayList<SoundCategory>();
	private ArrayList<Sound> sound = new ArrayList<Sound>();
	private ArrayList<Float> pitch = new ArrayList<Float>();
	private ArrayList<Float> volume = new ArrayList<Float>();
	private ArrayList<Long> delay = new ArrayList<Long>();
	private boolean valid;
	@SuppressWarnings("unused")
	private int taskID;
	
	public GunEngineSound(String input) {
		this.config = input;
		
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
		
		this.valid = true;
	}
	
	public void play(World world, Location loc) {
		if(this.valid) {
			for(int i = 0; i < this.category.size(); i++) {
				Integer I = i;
					this.taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							world.playSound(loc, sound.get(I),  category.get(I),  volume.get(I),  pitch.get(I));
						}
						
					}, this.delay.get(i));
			}
		}	
	}
	public long getPlayDuration() {
		long dur = 0;
		for(long l : this.delay) {
			dur = dur + l;
		}
		return dur;
	}

}
