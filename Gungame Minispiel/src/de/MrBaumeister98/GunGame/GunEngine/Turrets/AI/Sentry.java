package de.MrBaumeister98.GunGame.GunEngine.Turrets.AI;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.TurretConfig;

public class Sentry extends Turret {
	
	private List<UUID> allyIDList;
	private List<EntityType> enemyTypes;
	private UUID ownerID;
	private ESentryState currentState;

	public Sentry(Location position, float rotation, TurretConfig config) {
		super(position, rotation, config);
		// TODO Auto-generated constructor stub
		this.currentState = ESentryState.LOOK_FOR_TARGETS;
	}
	
	public UUID getOwnerID() {
		return this.ownerID;
	}
	public List<UUID> getAllies() {
		return this.allyIDList;
	}
	public List<EntityType> getEnemyTypes() {
		return this.enemyTypes;
	}
	public ESentryState getState() {
		return this.currentState;
	}
	public void setState(ESentryState newState) {
		this.currentState = newState;
	}

}
