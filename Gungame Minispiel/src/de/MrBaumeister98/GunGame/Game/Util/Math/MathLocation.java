package de.MrBaumeister98.GunGame.Game.Util.Math;

import org.bukkit.Location;

public class MathLocation {
	
	private Double x1;
	private Double x2;
	private Double x3;
	
	public MathLocation(Location loc) {
		this.x1 = loc.getX();
		this.x2 = loc.getY();
		this.x3 = loc.getZ();
	}
	
	public Double getX1() {
		return this.x1;
	}
	public Double getX2() {
		return this.x2;
	}
	public Double getX3() {
		return this.x3;
	}

}
