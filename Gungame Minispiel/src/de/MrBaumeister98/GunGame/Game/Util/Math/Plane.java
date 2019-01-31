package de.MrBaumeister98.GunGame.Game.Util.Math;

import org.bukkit.util.Vector;

public class Plane {
	
	private Double a;
	private Double b;
	private Double c;
	private Double d;
	private Vector normal;
	
	private boolean lesserThanZeroIsBehindPlane;
	
	public Plane(Vector v1, Vector v2, MathLocation pointOnPlane, MathLocation pointBehindPlane) {
		this.normal = v1.crossProduct(v2);
		this.a = this.normal.getX();
		this.b = this.normal.getY();
		this.c = this.normal.getZ();
		
		this.d = -1.0 * (this.a * pointOnPlane.getX1() + this.b * pointOnPlane.getX2() + this.c * pointOnPlane.getX3());
		this.lesserThanZeroIsBehindPlane = false;
		if(getResult(pointBehindPlane) <= 0.0) {
			this.lesserThanZeroIsBehindPlane = true;
		}
	}
	
	public boolean isPointBehindPlane(MathLocation loc) {
		if(this.lesserThanZeroIsBehindPlane) {
			if(getResult(loc) <= 0.0) {
				return true;
			} else {
				return false;
			}
		} else {
			if(getResult(loc) >= 0.0) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public Double getResult(MathLocation loc) {
		Double ret = 0.0;
		ret = (this.a * loc.getX1()) + (this.b * loc.getX2()) + (this.c * loc.getX3()) + this.d;
		return ret;
	}

}
