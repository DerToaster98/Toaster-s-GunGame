package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import org.bukkit.Location;

import de.MrBaumeister98.GunGame.Game.Util.Util;

public class TurretData {
	
	public TurretConfig tc;
	public Location location;
	public Float angle;
	public Double hp;
	public Integer mag;
	public Double heat;
	public Float startYaw;
	
	public TurretData(TurretManager manager, String[] args) {
		this.tc = manager.getTurretConfig(args[0]);
		this.location = Util.stringToLoc(args[1]);
		this.angle = Float.valueOf(args[2]);
		this.hp = Double.valueOf(args[3]);
		this.mag = Integer.valueOf(args[4]);
		this.heat = Double.valueOf(args[5]);
		this.startYaw = Float.valueOf(args[6]);
	}
	
	public String generateDataSaveString() {
		String data = this.tc.name;
		data = data + "," + Util.locToString(this.location);
		data = data + "," + this.angle.toString();
		data = data + "," + this.hp.toString();
		data = data + "," + this.mag.toString();
		data = data + "," + this.heat.toString();
		data = data + "," + this.startYaw.toString();
		
		return data;
	}

}
