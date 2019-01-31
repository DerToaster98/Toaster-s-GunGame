package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import org.bukkit.Location;

import de.MrBaumeister98.GunGame.Game.Util.Util;

public class TankData {
	
	public TankConfig tc;
	public Location location;
	public Float turnAngle;
	public Double hp;
	public Integer mag;
	public Double turretAngleY;
	public Double barrelAngleX;
	
	public TankData(TankManager manager, String[] args) {
		this.tc = manager.getTankConfig(args[0]);
		this.location = Util.stringToLoc(args[1]);
		this.turnAngle = Float.valueOf(args[2]);
		this.hp = Double.valueOf(args[3]);
		this.mag = Integer.valueOf(args[4]);
		this.turretAngleY = Double.valueOf(args[5]);
		this.barrelAngleX = Double.valueOf(args[6]);
	}
	
	public String generateDataSaveString() {
		String data = this.tc.name;
		data = data + "," + Util.locToString(this.location);
		data = data + "," + ((Float)this.turnAngle).toString();
		data = data + "," + this.hp.toString();
		data = data + "," + this.mag.toString();
		data = data + "," + ((Double)this.turretAngleY).toString();
		data = data + "," + ((Double)this.barrelAngleX).toString();
		
		return data;
	}
}
