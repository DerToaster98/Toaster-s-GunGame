package de.MrBaumeister98.GunGame.Game.Util.Math;

import org.bukkit.util.Vector;

public class VectorUtil {
	
	public static enum EAxis {
		AXIS_Y,
		AXIS_X,
		AXIS_Z;
	}
	public static Vector rotateVectorAroundY(Vector vector, double degrees) {
	    double rad = Math.toRadians(degrees);

	    double currentX = vector.getX();
	    double currentZ = vector.getZ();

	    double cosine = Math.cos(rad);
	    double sine = Math.sin(rad);

	    return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
	}
	public static Vector rotateVector(EAxis axis, Vector vector, Double degrees) {
		double rad = Math.toRadians(degrees);

	    double currentX = vector.getX();
	    double currentZ = vector.getZ();
	    double currentY = vector.getY();
	    
	    double cosine = Math.cos(rad);
		double sine = Math.sin(rad);
		
		switch(axis) {
		case AXIS_X:
			return new Vector(vector.getX(), (currentY * cosine - currentZ * sine), (currentY * sine - currentZ * cosine));
		case AXIS_Y:
			return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
		case AXIS_Z:
			return new Vector((currentX * cosine - currentY * sine), (currentX * sine - currentY * cosine), vector.getZ());
		default:
			break;
		
		}
		return null;
	}

}
