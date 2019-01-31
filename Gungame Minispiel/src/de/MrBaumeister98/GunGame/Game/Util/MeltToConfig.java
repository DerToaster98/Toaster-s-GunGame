package de.MrBaumeister98.GunGame.Game.Util;

import org.bukkit.Material;

public class MeltToConfig {
	private Material changeTo;
	private Boolean coolsBack;
	
	public MeltToConfig(Material matTo, Boolean coolBack) {
		this.changeTo = matTo;
		this.coolsBack = coolBack;
	}
	
	public Material getChangeTo() {
		return this.changeTo;
	}
	public Boolean coolsBack() {
		return this.coolsBack;
	}
}
