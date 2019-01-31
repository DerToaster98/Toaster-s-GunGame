package de.MrBaumeister98.GunGame.GunEngine.Griefing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import net.md_5.bungee.api.ChatColor;

public class GriefHelper implements Listener {
	
	private List<World> knownWorlds;
	private HashMap<World, GriefSettings> worldSettings;
	private GunGamePlugin plugin;
	
	public GriefHelper(GunGamePlugin plugin) {
		this.plugin = plugin;
		this.worldSettings = new HashMap<World, GriefSettings>();
		this.knownWorlds = new ArrayList<World>();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onWorldLoad(WorldInitEvent event) {
		World world = event.getWorld();
		processWorld(world);
	}
	public void processWorld(World world) {
		if(this.knownWorlds.isEmpty() || !this.knownWorlds.contains(world)) {
			Debugger.logInfoWithColoredText(ChatColor.LIGHT_PURPLE + "Creating Griefsettings for world: " + ChatColor.YELLOW + world.getName() + ChatColor.LIGHT_PURPLE + "...");
			
			if(world.getName().contains("GunGame-")) {
				world.setMetadata("GunGameWorld", new FixedMetadataValue(GunGamePlugin.instance, true));
			}
			this.knownWorlds.add(world);
			
			GriefSettings gs = new GriefSettings(world);
			
			this.worldSettings.put(world, gs);
			
			this.plugin.getServer().getPluginManager().registerEvents(gs, this.plugin);
		}
	}
	public Boolean isGGWorld(World w) {
		if(w.hasMetadata("GunGameWorld") && w.getMetadata("GunGameWorld").get(0).asBoolean()) {
			return true;
		}
		return false;
	}
	public void openGUI(Player p) {
		this.worldSettings.get(p.getWorld()).openGUI(p);
	}
	public Boolean getGriefAllowed(GriefType type, World world) {
			GriefSettings gs = this.worldSettings.get(world);
			return gs.getGriefAllowed(type);
	}
	public void setGriefAllowed(GriefType type, Boolean allowed, World world) {
		if(this.knownWorlds.contains(world)) {
			GriefSettings gs = this.worldSettings.get(world);
			gs.setGriefAllowed(type, allowed);
		} else {
			Debugger.logInfoWithColoredText(ChatColor.RED +"[ERROR]: " + ChatColor.YELLOW + "Unknown World!");
		}
	}
}
