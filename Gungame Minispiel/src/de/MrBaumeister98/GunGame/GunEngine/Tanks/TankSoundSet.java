package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import org.bukkit.configuration.file.FileConfiguration;

import de.MrBaumeister98.GunGame.GunEngine.GunEngineLoopingSound;
import de.MrBaumeister98.GunGame.GunEngine.GunEngineSound;

public class TankSoundSet {
	
	public String name;
	private FileConfiguration config;
	
	public GunEngineLoopingSound engineIdleSound;
	public GunEngineLoopingSound driveSound;
	public GunEngineSound brakingSound;
	public GunEngineSound takeDamageSound;
	public GunEngineSound shootSound;
	public GunEngineSound reloadSound;
	public GunEngineSound bullethitSound;
	
	public TankSoundSet(FileConfiguration file) {
		this.config = file;

		this.name = this.config.getString("Name");
		
		if(this.config.getString("Engine.Idle") != null && !this.config.getString("Engine.Idle").equalsIgnoreCase("")) {
			Long dur = config.getLong("Engine.Idle.Duration", 90);
			this.engineIdleSound = new GunEngineLoopingSound(this.config.getString("Engine.Idle.Sounds"), dur);
		}
		
		if(this.config.getString("Engine.Driving") != null && !this.config.getString("Engine.Driving").equalsIgnoreCase("")) {
			Long dur = config.getLong("Engine.Driving.Duration", 130);
			this.driveSound = new GunEngineLoopingSound(this.config.getString("Engine.Driving.Sounds"), dur);
		}
		
		if(this.config.getString("Engine.Brake") != null && !this.config.getString("Engine.Brake").equalsIgnoreCase("")) {
			this.brakingSound = new GunEngineSound(this.config.getString("Engine.Brake"));
		}
		
		if(this.config.getString("TakeDamage") != null && !this.config.getString("TakeDamage").equalsIgnoreCase("")) {
			this.takeDamageSound = new GunEngineSound(this.config.getString("TakeDamage"));
		}
		
		if(this.config.getString("Shoot") != null && !this.config.getString("Shoot").equalsIgnoreCase("")) {
			this.shootSound = new GunEngineSound(this.config.getString("Shoot"));
		}
		
		if(this.config.getString("Reload") != null && !this.config.getString("Reload").equalsIgnoreCase("")) {
			this.reloadSound = new GunEngineSound(this.config.getString("Reload"));
		}
		
		if(this.config.getString("BulletHit") != null && !this.config.getString("BulletHit").equalsIgnoreCase("")) {
			this.bullethitSound = new GunEngineSound(this.config.getString("BulletHit"));
		}
	}
	public TankSoundSet(TankSoundSet tss) {
		this.name = tss.name;
		this.config = tss.config;
		this.brakingSound = new GunEngineSound(tss.brakingSound.config);
		this.bullethitSound = new GunEngineSound(tss.bullethitSound.config);
		this.driveSound = new GunEngineLoopingSound(tss.driveSound.config, tss.driveSound.getPlayDuration());
		this.engineIdleSound = new GunEngineLoopingSound(tss.engineIdleSound.config, tss.engineIdleSound.getPlayDuration());
		this.reloadSound = new GunEngineSound(tss.reloadSound.config);
		this.shootSound = new GunEngineSound(tss.shootSound.config);
		this.takeDamageSound = new GunEngineSound(tss.takeDamageSound.config);
	}
	
	public TankSoundSet clone() {
		return new TankSoundSet(this);
	}

}
