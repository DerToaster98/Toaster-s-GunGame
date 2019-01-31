package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.configuration.file.FileConfiguration;

public class WeaponSoundSet {
	
	public String name;
	private FileConfiguration config;
	
	public GunEngineSound throwSound;
	public GunEngineSound equipSound;
	public GunEngineSound toggleZoom;
	public GunEngineSound explodeSound;
	
	public GunEngineSound outOfAmmoSound;
	public GunEngineSound reloadSound;
	public GunEngineSound shootSound;
	
	public GunEngineSound bullethitSound;
	
	public GunEngineSound turningBarrel;
	
	public WeaponSoundSet(FileConfiguration file) {
		this.config = file;

		this.name = this.config.getString("Name");
		
		if(this.config.getString("Equip") != null && !this.config.getString("Equip").equalsIgnoreCase("")) {
			this.equipSound = new GunEngineSound(this.config.getString("Equip"));
		}
		
		if(this.config.getString("Throw") != null && !this.config.getString("Throw").equalsIgnoreCase("")) {
			this.throwSound = new GunEngineSound(this.config.getString("Throw"));
		}
		
		if(this.config.getString("ToggleZoom") != null && !this.config.getString("ToggleZoom").equalsIgnoreCase("")) {
			this.toggleZoom = new GunEngineSound(this.config.getString("ToggleZoom"));
		}
		
		if(this.config.getString("Explode") != null && !this.config.getString("Explode").equalsIgnoreCase("")) {
			this.explodeSound = new GunEngineSound(this.config.getString("Explode"));
		}
		
		if(this.config.getString("OutOfAmmo") != null && !this.config.getString("OutOfAmmo").equalsIgnoreCase("")) {
			this.outOfAmmoSound = new GunEngineSound(this.config.getString("OutOfAmmo"));
		}
		
		if(this.config.getString("Reloading") != null && !this.config.getString("Reloading").equalsIgnoreCase("")) {
			this.reloadSound = new GunEngineSound(this.config.getString("Reloading"));
		}
		
		if(this.config.getString("Shooting") != null && !this.config.getString("Shooting").equalsIgnoreCase("")) {
			this.shootSound = new GunEngineSound(this.config.getString("Shooting"));
		}
		
		if(this.config.getString("TurnBarrel") != null && !this.config.getString("TurnBarrel").equalsIgnoreCase("")) {
			this.turningBarrel = new GunEngineSound(this.config.getString("TurnBarrel"));
		}
		
		if(this.config.getString("BulletHit") != null && !this.config.getString("BulletHit").equalsIgnoreCase("")) {
			this.bullethitSound = new GunEngineSound(this.config.getString("BulletHit"));
		}
	}
}
