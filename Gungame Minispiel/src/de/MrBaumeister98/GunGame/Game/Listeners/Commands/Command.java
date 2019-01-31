package de.MrBaumeister98.GunGame.Game.Listeners.Commands;

import java.util.List;

import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public interface Command {
		
	void executeForPlayer(Player sender, List<String> args);
	void executeConsole(ConsoleCommandSender sender, List<String> args);
	void executeCommandBlock(BlockCommandSender sender, List<String> args);
	
	String getName();
	boolean executedSuccessful();
	
	Command getParent();
	List<Command> getSubCommands();
	
	public default void execute(CommandSender sender, List<String> args) {
		if(sender instanceof Player) {
			try {
				executeForPlayer((Player) sender, args);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if (sender instanceof BlockCommandSender) {
			try {
				executeCommandBlock((BlockCommandSender)sender, args);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if(sender instanceof ConsoleCommandSender) {
			try {
				executeConsole((ConsoleCommandSender)sender, args);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
