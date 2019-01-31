package de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations;
//CODE BY: Arceus02
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;

import de.MrBaumeister98.GunGame.ArenaWorldRestore.Exceptions.NoSuchWorldException;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.Exceptions.SendableException;
import de.MrBaumeister98.GunGame.ArenaWorldRestore.Util.ZipFileUtil;
import de.MrBaumeister98.GunGame.Game.Core.FileManager;

public class WorldZipper implements WorldOperator {
	
	private final String worldName;
	private boolean jobDone = false;
	
	public WorldZipper(String worldName) {
		this.worldName = worldName;
	}
	
	public void execute() throws SendableException {
		if(exists()) {
			try {
				zipWorldFolder();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SendableException("Error while zipping : check server console for more info.");
			}
			jobDone = true;
		} else {
			throw new NoSuchWorldException(worldName);
		}
	}
	
	private void zipWorldFolder() throws IOException {
		File worldFolder = getWorldFolder();
		File backupFile = getBackupFile();
		try {
			ZipFileUtil.zipDirectory(worldFolder, backupFile);
		} catch (IOException e) {
			throw e;
		}
	}
	
	private File getWorldFolder() {
		File worldContainer = Bukkit.getWorldContainer();
		File worldFolder = new File(worldContainer, worldName);
		return worldFolder;
	}
	
	private File getBackupFile() {
		File backupFolder = FileManager.getBackupFolder();
		File backupFile = new File(backupFolder, worldName + ".zip");
		return backupFile;
	}
	
	private boolean exists() {
		File worldFolder = getWorldFolder();
		return worldFolder.isDirectory();
	}
	
	public Boolean isJobDone() {
		return this.jobDone;
	}
	
	public String getResultMessage() {
		if(isJobDone()) {
			return "Successfully zipped world " + worldName;
		} else {
			return "World " + worldName + " isn't zipped yet.";
		}
	}
	
}
