package de.MrBaumeister98.GunGame.Achievements.Achievements;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;

import de.MrBaumeister98.GunGame.Achievements.AdvencementAPI.AdvancementAPI;
import de.MrBaumeister98.GunGame.Achievements.AdvencementAPI.FrameType;
import de.MrBaumeister98.GunGame.Achievements.AdvencementAPI.Trigger;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class GunGameAchievement {
	
	public GunGameAchievementUtil manager;
	
	private boolean loaded;
	private boolean enumerated;
	private boolean hidden;
	private String key;
	private String name;
	private String icon;
	private List<String> desc;
	private CriteriaE criteria;
	private FrameType frameType;
	private Integer toReach;
	private AdvancementAPI adv;
	private String parentName;
	private GunGameAchievement parent;
	
	public GunGameAchievement(GunGameAchievementUtil manager, String key, String parentName, String name, List<String> desc, String icon, CriteriaE crit, Integer toreach, FrameType fType, boolean hidden) {
		
		this.manager = manager;
		setKey(key);
		setName(name);
		setHidden(hidden);
		setDesc(desc);
		setIcon(icon);
		setCriteria(crit);
		crit.addAchievement(this);
		setFrameType(fType);
		setToReach(toreach);
		if(parentName != null && parentName.equalsIgnoreCase("NULL")) {
			this.parent = null;
			this.parentName = null;
		} else if(parentName != null) {
			this.parentName = parentName;
		}
		setEnumerated(false);
		if(this.getFrameType().equals(FrameType.CHALLENGE)) {
			setEnumerated(true);
		}
		this.setLoaded(false);
	}
	
	public void initialize() {
		if(this.parentName != null) {
			this.parent = this.manager.achievementMap.get(this.parentName);
		}
		addAdvancement();
		
		this.setLoaded(true);
	}
	
	
	private void addAdvancement() {
		this.manager.getPlugin();
		    String parentString = null;
		    if(this.parentName != null) {
		    	parentString = this.parent.getAdv().getId().toString();
		    }
		    String desc2 = "";
		    for(String s : this.desc) {
		    	desc2 = desc2 + s + "\n";
		    }
			AdvancementAPI advncmnt = AdvancementAPI.builder(new NamespacedKey(GunGamePlugin.instance, "GunGame/" + this.key))
					.title(ChatColor.translateAlternateColorCodes('&', this.name))
					.description(desc2)
					.counter(this.toReach > 1 ? 1 : this.toReach )
					.icon("minecraft:" + this.icon.toLowerCase())
					.trigger(
							Trigger.builder(
									Trigger.TriggerType.IMPOSSIBLE, "gungame")
							)
					.hidden(this.isHidden())
					.toast(true)
					//.background("minecraft:textures/block/cyan_terracotta.png")
					.background("minecraft:textures/gui/advancements/backgrounds/adventure.png")
					.frame(this.frameType)
					.parent(parentString)
					.build();
		
		advncmnt.add();
		//Debugger.logInfo(advncmnt.getId().toString());
		//Debugger.logInfo(parentString);
		this.setAdv(advncmnt);
		this.setLoaded(true);
		this.manager.addAdvancement(this.getKey(), this.getAdv());
	}
	
	
	
	
	
	
	
	
	
	
	
	

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getDesc() {
		return this.desc;
	}

	private void setDesc(List<String> desc) {
		this.desc = desc;
	}

	public String getIcon() {
		return this.icon;
	}

	private void setIcon(String icon) {
		this.icon = icon;
	}

	public CriteriaE getCriteria() {
		return criteria;
	}

	private void setCriteria(CriteriaE criteria) {
		this.criteria = criteria;
	}

	public FrameType getFrameType() {
		return frameType;
	}

	private void setFrameType(FrameType frameType) {
		this.frameType = frameType;
	}

	public Integer getToReach() {
		return toReach;
	}

	private void setToReach(Integer toReach) {
		this.toReach = toReach;
	}


	public String getKey() {
		return key;
	}


	private void setKey(String key) {
		this.key = key;
	}


	public AdvancementAPI getAdv() {
		return adv;
	}


	private void setAdv(AdvancementAPI adv) {
		this.adv = adv;
	}


	public boolean isLoaded() {
		return loaded;
	}


	private void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	public String getParentName() {
		return this.parentName;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isEnumerated() {
		return enumerated;
	}

	public void setEnumerated(boolean enumerated) {
		this.enumerated = enumerated;
	}
	
	
	

}
