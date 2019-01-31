package de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations;
//CODE BY: Arceus02
import org.bukkit.Bukkit;
import org.bukkit.World;

import de.MrBaumeister98.GunGame.ArenaWorldRestore.Exceptions.NoSuchWorldException;

public class WorldSaver implements WorldOperator {
	
	private final String worldName;
	private boolean jobDone = false;
	
	public WorldSaver(String worldName) {
		this.worldName = worldName;
	}
	
	public void execute() throws NoSuchWorldException {
		World world = Bukkit.getWorld(worldName);
		if(world == null) {
			throw new NoSuchWorldException(worldName);
		}
		world.save();
		Bukkit.getWorld(worldName).setAutoSave(false);
		jobDone = true;
	}
	
	public Boolean isJobDone() {
		return this.jobDone;
	}
	
	public String getResultMessage() {
		if(isJobDone()) {
			return "Successfully saved world " + worldName;
		} else {
			return "World " + worldName + " isn't saved yet.";
		}
	}
	
}
