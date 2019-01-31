package de.MrBaumeister98.GunGame.GunEngine;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class PlasmaParticleUtil {
	
	private Gun owner;
	
	private Particle particle;
	public Boolean meltsBlocks;
	public Double R;
	public Double G;
	public Double B;
	private float size;
	
	public PlasmaParticleUtil(Gun gun) {
		this.owner = gun;
		
		this.meltsBlocks = this.owner.canMeltBlocks();
		this.particle = Particle.REDSTONE;
		this.R = this.owner.getWeaponFile().getDouble("Effects.PlasmaTrail.Color.Red");
		if((this.R == 0 || this.R == 0.0) && GunGamePlugin.instance.serverPre113) {
			this.R = Double.MIN_VALUE;
		}
		this.G = this.owner.getWeaponFile().getDouble("Effects.PlasmaTrail.Color.Green");
		this.B = this.owner.getWeaponFile().getDouble("Effects.PlasmaTrail.Color.Blue");
		this.size = (float)this.owner.getWeaponFile().getDouble("Effects.PlasmaTrail.Size");
	}
	
	public void play(Location loc, Double x, Double y, Double z) {
		//loc.getWorld().spawnParticle(this.particle, loc, this.count, this.R, this.G, this.B, this.brightness);
		//loc.getWorld().spawnParticle(this.particle, x, y, z, 0, this.R, this.G, this.B, this.brightness);
		if(GunGamePlugin.instance.serverPre113) {
			loc.getWorld().spawnParticle(this.particle, x, y, z, 0, this.R, this.G, this.B, 1.0);
		} else {
			loc.getWorld().spawnParticle(this.particle, x, y, z, 1, new org.bukkit.Particle.DustOptions(Color.fromRGB(this.R.intValue(), this.G.intValue(), this.B.intValue()), this.size));
		}
	}

}
