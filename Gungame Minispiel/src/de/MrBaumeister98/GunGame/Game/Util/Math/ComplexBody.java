package de.MrBaumeister98.GunGame.Game.Util.Math;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class ComplexBody {
	
	private List<Plane> planes;
	
	public ComplexBody() {
		this.planes = new ArrayList<Plane>();
	}
	
	public List<Plane> getPlanes() {
		return this.planes;
	}
	
	public void addPlane(Plane plane) {
		this.planes.add(plane);
	}
	
	public boolean isPointInsideBody(MathLocation loc) {
		if(this.planes != null && !this.planes.isEmpty()) {
			//boolean condition = this.planes.get(0).isPointBehindPlane(loc);
			for(Plane plane : this.planes) {
				//if(plane.isPointBehindPlane(loc) != condition) {
				if(!plane.isPointBehindPlane(loc)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean isPointInsideBody(Location loc) {
		return isPointInsideBody(new MathLocation(loc));
	}

}
