package de.MrBaumeister98.GunGame.GunEngine.Turrets.AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.Math.ComplexBody;
import de.MrBaumeister98.GunGame.Game.Util.Math.MathLocation;
import de.MrBaumeister98.GunGame.Game.Util.Math.Plane;
import de.MrBaumeister98.GunGame.Game.Util.Math.VectorUtil;
import de.MrBaumeister98.GunGame.Game.Util.Math.VectorUtil.EAxis;

public class FOVObserver {
	
	private ComplexBody fovBox;
	private Sentry turret;
	private Location fovBoxCenter;
	
	private int taskID;
	private double radius;
	private boolean targetLocked;
	private UUID targetID;

	public FOVObserver(Sentry turret, double rad) {
		this.radius = rad;
		this.turret = turret;
		this.fovBox = new ComplexBody();
		Location eyeLoc = turret.hitbox.getEyeLocation();
		Vector direction = turret.hitbox.getLocation().getDirection().normalize().multiply(this.radius);
		Vector vLeft = VectorUtil.rotateVector(EAxis.AXIS_Y, direction.normalize(), 315.0).normalize().multiply(this.radius);
		Vector vRight = VectorUtil.rotateVector(EAxis.AXIS_Y, direction.normalize(), 45.0).normalize().multiply(this.radius);
		Vector vUp = new Vector(0.0, 10.0, 0.0);
		Vector vDown = new Vector(0.0, 10.0, 0.0);
		
		Location leftUp = eyeLoc.add(vLeft).add(vUp);
		Location leftDown = eyeLoc.add(vLeft).add(vDown);
		Location rightUp = eyeLoc.add(vRight).add(vUp);
		Location rightDown = eyeLoc.add(vRight).add(vDown);
		
		MathLocation eye = new MathLocation(eyeLoc);
		
		this.fovBoxCenter = eyeLoc.add(direction.normalize().multiply(this.radius /2));
		MathLocation insideBody = new MathLocation(this.fovBoxCenter);
		
		Vector vLU = new Vector(leftUp.getX() - eyeLoc.getX(), leftUp.getY() - eyeLoc.getY(), leftUp.getZ() - eyeLoc.getY());
		Vector vLD = new Vector(leftDown.getX() - eyeLoc.getX(), leftDown.getY() - eyeLoc.getY(), leftDown.getZ() - eyeLoc.getY());
		Vector vRU = new Vector(rightUp.getX() - eyeLoc.getX(), rightUp.getY() - eyeLoc.getY(), rightUp.getZ() - eyeLoc.getZ());
		Vector vRD = new Vector(rightDown.getX() - eyeLoc.getX(), rightDown.getY() - eyeLoc.getY(), rightDown.getZ() - eyeLoc.getZ());
		
		Vector vLuRu = new Vector(rightUp.getX() - leftUp.getX(), rightUp.getY() - leftUp.getY(), rightUp.getZ() - leftUp.getZ());
		Vector vLuLd = new Vector(0.0, leftDown.getY() - leftUp.getY(), 0.0);
		
		Plane front = new Plane(vLuRu, vLuLd, new MathLocation(leftUp), insideBody);
		Plane left = new Plane(vLU, vLD, eye, insideBody);
		Plane right = new Plane(vRU, vRD, eye, insideBody);
		Plane up = new Plane(vLU, vRU, eye, insideBody);
		Plane down = new Plane(vLD, vRD, eye, insideBody);
		
		this.fovBox.addPlane(front);
		this.fovBox.addPlane(up);
		this.fovBox.addPlane(down);
		this.fovBox.addPlane(left);
		this.fovBox.addPlane(right);
	}
	
	@SuppressWarnings("unused")
	//Checks for new Target if no target is set or out of range
	private void execute() {
		//If target is set, check if it still exists and is not out of range
		if(targetLocked) {
			//Check if target is alive
			if(Bukkit.getEntity(this.targetID) != null) {
				Entity target = Bukkit.getEntity(this.targetID);
				//target is in range and it exists -> Shoot at it and adjust sentry direction
				if(target != null && !target.isDead() && this.fovBox.isPointInsideBody(target.getLocation())) {
					//SHOOT AT IT >:D
				} else {
					//Target is dead or out of range -> search new target -> DONE
					this.targetID = null;
					this.turret.setState(ESentryState.LOOK_FOR_TARGETS);
					this.targetLocked = false;
				}	
			} else {
				//Target doesn't exist anymore -> search new target
				this.targetID = null;
				this.turret.setState(ESentryState.LOOK_FOR_TARGETS);
				this.targetLocked = false;
			}
		}
		//Target is not set -> search for new target in range
		if(!targetLocked) {
			List<Entity> inFOVBox = new ArrayList<Entity>();
			if(this.turret.getWorld().getNearbyEntities(this.fovBoxCenter, this.radius, this.radius, this.radius) != null && !this.turret.getWorld().getNearbyEntities(this.fovBoxCenter, this.radius, this.radius, this.radius).isEmpty()) {
				//DONE: Parse all target in range and clear that out, that are not inside the FOV
				for(Entity ent : this.turret.getWorld().getNearbyEntities(this.fovBoxCenter, this.radius, this.radius, this.radius)) {
					if(this.fovBox.isPointInsideBody(ent.getLocation())) {
						inFOVBox.add(ent);
					}
				}
				List<Entity> targets = new ArrayList<Entity>();
				for(Entity ent : inFOVBox) {
					//Clear all enemies out of selection list, that are not hostile (Creates new list with all enemies in range
					if(isEnemy(ent)) {
						targets.add(ent);
					}
				}
				//Clear list with all entities in FOV
				inFOVBox.clear();
				inFOVBox = null;
				//If a target is still in the list -> search target by distance to sentry
				if(targets != null && !targets.isEmpty() && targets.size() > 0) {
					//Sort by distance to turret
					try {
						Collections.sort(targets, new Comparator<Entity>() {

							@Override
							public int compare(Entity o1, Entity o2) {
								Integer distanceE1 = ((Double)turret.hitbox.getLocation().distance(o1.getLocation())).intValue();
								Integer distanceE2 = ((Double)turret.hitbox.getLocation().distance(o2.getLocation())).intValue();
								return distanceE1 - distanceE2;
							}
						});
					} catch(Exception ex) {
						Debugger.logError("Error occurred whilst sorting target list by target distance! Error: \n");
						ex.printStackTrace();
					}
					
					Entity target = targets.get(0);
					//new target set and locked !
					targetID = target.getUniqueId();
					this.turret.setState(ESentryState.SHOOT_AT_TARGET);
					targetLocked = true;
				} else {
					//No target found -> search again and turn head
					//Look around and spin yourself right round.... like a record....
					targetID = null;
					this.turret.setState(ESentryState.LOOK_FOR_TARGETS);
					targetLocked = false;
				}
			}
		}
	}
	
	private boolean isEnemy(Entity ent) {
		boolean enemy = false;
		if(this.turret.getEnemyTypes().contains(ent.getType())) {
			enemy = true;
		}
		UUID entID = ent.getUniqueId();
		if(entID.equals(this.turret.getOwnerID()) || this.turret.getAllies().contains(entID) || isInSameTeamAsOwner(ent)) {
			enemy = false;
		}
		if(ent instanceof Tameable) {
			Tameable pet = (Tameable)ent;
			if(pet.getOwner().getUniqueId().equals(this.turret.getOwnerID()) || this.turret.getAllies().contains(pet.getOwner().getUniqueId())) {
				enemy = false;
			}
		}
		return enemy;
	}
	
	@SuppressWarnings("deprecation")
	private boolean isInSameTeamAsOwner(Entity toCheck) {
		boolean inSameTeam = false;
		
		if(toCheck != null && toCheck instanceof Player) {
			Entity turretOwner = null;
			try {
				turretOwner = Bukkit.getEntity(this.turret.getOwnerID());
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			if(turretOwner != null) {
				if(turretOwner instanceof Player) {
					Player owner = (Player)turretOwner;
					Team ownerTeam = owner.getScoreboard().getPlayerTeam(Bukkit.getOfflinePlayer(owner.getUniqueId()));
					if(ownerTeam.hasPlayer(Bukkit.getOfflinePlayer(toCheck.getUniqueId()))) {
						inSameTeam = true;
					}
				}
			}
		}
		
		return inSameTeam;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
}
