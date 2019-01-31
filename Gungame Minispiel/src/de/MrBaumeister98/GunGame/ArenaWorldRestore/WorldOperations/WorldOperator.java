package de.MrBaumeister98.GunGame.ArenaWorldRestore.WorldOperations;
//CODE BY: Arceus02
import de.MrBaumeister98.GunGame.ArenaWorldRestore.Exceptions.SendableException;

public interface WorldOperator {
	
	public void execute() throws SendableException;
	
	public Boolean isJobDone();

	public String getResultMessage();
	
}