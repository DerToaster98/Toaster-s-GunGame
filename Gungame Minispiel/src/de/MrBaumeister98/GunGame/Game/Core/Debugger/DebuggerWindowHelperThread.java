package de.MrBaumeister98.GunGame.Game.Core.Debugger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class DebuggerWindowHelperThread extends Thread {
	
	private List<Arena> arenas;
	private GunGamePlugin plugin;
	private ArenaManager manager;
	private DebuggerWindow window;
	private JTable debugTable;
	private Boolean running;
	
	public DebuggerWindowHelperThread(GunGamePlugin plugin) {
		try {
			DebuggerWindow frame = new DebuggerWindow();
			frame.setVisible(true);
			this.window = frame;
			this.debugTable = this.window.overview;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.plugin = plugin;
		this.running = true;
		this.manager = this.plugin.arenaManager;
		
		this.arenas = new ArrayList<Arena>();
	}
	@Override
	public void run() {
		while(this.running) {
			refreshArenaList();
			
			for(Integer i = 0; i < this.arenas.size(); i++) {
				fillDebugTable(this.arenas.get(i), i);
			}
			
			try {
				sleep(100);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
	public void refreshArenaList() {
		this.arenas.clear();
		for(Arena a : this.manager.getArenaList()) {
			this.arenas.add(a);
		}
	}
	
	public void fillDebugTable(Arena a, Integer row) {
		this.debugTable.getModel().setValueAt(a.getName(), row +1, 0);
		this.debugTable.getModel().setValueAt(a.getGameState().toString(), row +1, 1);
		this.debugTable.getModel().setValueAt(a.getPlayers().size() + "/" + a.getMaxPlayers(), row +1, 2);
		if(a.getArenaWorld() != null) {
			this.debugTable.getModel().setValueAt(a.getArenaWorld().getName(), row +1, 3);
		} else {
			this.debugTable.getModel().setValueAt("???", row +1, 3);
		}
	}
	public void startstop(Boolean run) {
		this.running = run;
		if(run) {
			this.run();
		}
	}
	@SuppressWarnings({ })
	public void log(String log) {
		SimpleDateFormat SysTime = new SimpleDateFormat("HH:mm:ss");
		String sysTime = SysTime.format(new Date());
		
		log = sysTime + ": " + log; //+ "\n";
		//this.window.logwindow.insertText(log, this.window.logwindow.getText().length());
		//this.window.logwindow.setText(this.window.logwindow.getText() + "\n" + log);
		this.window.log(log);
	}
	/*public void addArena(Arena arena) {
		if(!this.arenas.contains(arena)) {
			this.arenas.add(arena);
		}
	}
	public void remArena(Arena arena) {
		if(this.arenas.contains(arena)) {
			this.arenas.remove(arena);
		}
	}*/

}
