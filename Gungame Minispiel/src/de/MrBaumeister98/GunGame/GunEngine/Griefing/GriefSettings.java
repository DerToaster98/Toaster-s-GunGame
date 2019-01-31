package de.MrBaumeister98.GunGame.GunEngine.Griefing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import net.md_5.bungee.api.ChatColor;

public class GriefSettings implements Listener {
	
	public World world;
	
	private Boolean explosionsGrief;
	private Boolean physicsGrief;
	private Boolean protectWaterBodies;
	private Boolean bulletGlassGrief;
	private Boolean bulletsIgniteTNT;
	private Inventory menu = null;
	
	private List<String> enabledLore;
	private List<String> disabledLore;

	public GriefSettings(World w) {
		this.enabledLore = new ArrayList<String>();
		enabledLore.add(ChatColor.GREEN + "Enabled");
		this.disabledLore = new ArrayList<String>();
		disabledLore.add(ChatColor.RED + "Disabled");
		this.world = w;
		
		File infoFileF = new File(this.world.getWorldFolder().getAbsolutePath() + "/data/gungame", "info.yml");
		FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
		
		if(!infoFileF.exists()) {
			try {
				infoFile.set("GunEngineGrief.Explosions", false);
				infoFile.set("GunEngineGrief.PhysicsEngine", false);
				infoFile.set("GunEngineGrief.ProtectWaterBodies", true);
				infoFile.set("GunEngineGrief.BulletsBreakGlass", false);
				infoFile.set("GunEngineGrief.BulletsIgniteTNT", false);
				
				infoFile.set("InstalledAdvancements", true);
				infoFile.set("Version", GunGamePlugin.instance.getDescription().getVersion());
				
				infoFile.save(infoFileF);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.explosionsGrief = infoFile.getBoolean("GunEngineGrief.Explosions", false);
		this.physicsGrief = infoFile.getBoolean("GunEngineGrief.PhysicsEngine", false);
		this.protectWaterBodies = infoFile.getBoolean("GunEngineGrief.ProtectWaterBodies", true);
		this.bulletGlassGrief = infoFile.getBoolean("GunEngineGrief.BulletsBreakGlass", false);
		this.bulletsIgniteTNT = infoFile.getBoolean("GunEngineGrief.BulletsIgniteTNT", false);
		
		printSettings();
		
		createGUI();
	}
	private void printSettings() {
		Debugger.logInfoWithColoredText(ChatColor.GREEN + "Grief Settings for world: " + ChatColor.YELLOW + this.world.getName() + ChatColor.GREEN + ":");
		if(this.explosionsGrief) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Explosions: " + ChatColor.GREEN + "ALLOWED");
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Explosions: " + ChatColor.RED + "DENIED");
		}
		if(this.physicsGrief) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Physics: " + ChatColor.GREEN + "ALLOWED");
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Physics: " + ChatColor.RED + "DENIED");
		}
		if(this.protectWaterBodies) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Protect bodies of water: " + ChatColor.GREEN + "ENABLED");
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Protect bodies of water: " + ChatColor.RED + "DISABLED");
		}
		if(this.bulletGlassGrief) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Bullets break glass: " + ChatColor.GREEN + "ALLOWED");
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Bullets break glass: " + ChatColor.RED + "DENIED");
		}
		if(this.bulletsIgniteTNT) {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Bullets ignite TNT: " + ChatColor.GREEN + "ALLOWED");
		} else {
			Debugger.logInfoWithColoredText(ChatColor.YELLOW + "    > Bullets ignite TNT: " + ChatColor.RED + "DENIED");
		}
	}
	private void createGUI() {
		
		
		String ttl0 = LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Explosions.Title");
		String ttl1 = LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Physics.Title");
		String ttl2 = LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.BreakGlass.Title");
		String ttl3 = LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.IgniteTNT.Title");
		String ttl4 = LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.ProtectWaterBodies.Title");
		
		Material mat0 = Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Explosions.Item")));
		Material mat1 = Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Physics.Item")));
		Material mat2 = Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.BreakGlass.Item")));
		Material mat3 = Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.IgniteTNT.Item")));
		Material mat4 = Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.ProtectWaterBodies.Item")));
		
		ItemStack is0 = new ItemStack(mat0, 1);
		ItemStack is1 = new ItemStack(mat1, 1);
		ItemStack is2 = new ItemStack(mat2, 1);
		ItemStack is3 = new ItemStack(mat3, 1);
		ItemStack is4 = new ItemStack(mat4, 1);
		
		ItemMeta m1 = is0.getItemMeta();
		ItemMeta m2 = is1.getItemMeta();
		ItemMeta m3 = is2.getItemMeta();
		ItemMeta m4 = is3.getItemMeta();
		ItemMeta m5 = is4.getItemMeta();
		
		m1.setDisplayName(ttl0);
		m2.setDisplayName(ttl1);
		m3.setDisplayName(ttl2);
		m4.setDisplayName(ttl3);
		m5.setDisplayName(ttl4);
		
		m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		m2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		m2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		m3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		m3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		m4.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		m4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		m5.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		m5.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		if(this.explosionsGrief) {
			m1.addEnchant(Enchantment.DURABILITY, 1, true);
			m1.setLore(this.enabledLore);
		} else {
			m1.setLore(this.disabledLore);
		}
		if(this.physicsGrief) {
			m2.addEnchant(Enchantment.DURABILITY, 1, true);
			m2.setLore(this.enabledLore);
		} else {
			m2.setLore(this.disabledLore);
		}
		if(this.protectWaterBodies) {
			m5.addEnchant(Enchantment.DURABILITY, 1, true);
			m5.setLore(this.enabledLore);
		} else {
			m5.setLore(this.disabledLore);
		}
		if(this.bulletGlassGrief) {
			m3.addEnchant(Enchantment.DURABILITY, 1, true);
			m3.setLore(this.enabledLore);
		} else {
			m3.setLore(this.disabledLore);
		}
		if(this.bulletsIgniteTNT) {
			m4.addEnchant(Enchantment.DURABILITY, 1, true);
			m4.setLore(this.enabledLore);
		} else {
			m4.setLore(this.disabledLore);
		}
		
		is0.setItemMeta(m1);
		is1.setItemMeta(m2);
		is2.setItemMeta(m3);
		is3.setItemMeta(m4);
		is4.setItemMeta(m5);
		
		is0 = ItemUtil.addTags(is0, "GunGame_Item", "Grief-Explosions");
		is1 = ItemUtil.addTags(is1, "GunGame_Item", "Grief-Physics");
		is2 = ItemUtil.addTags(is2, "GunGame_Item", "Grief-Glass");
		is3 = ItemUtil.addTags(is3, "GunGame_Item", "Grief-Ignite");
		is4 = ItemUtil.addTags(is4, "GunGame-Item", "Grief-Waterbodies");
		
		is0 = ItemUtil.setGunGameItem(is0);
		is1 = ItemUtil.setGunGameItem(is1);
		is4 = ItemUtil.setGunGameItem(is4);
		is2 = ItemUtil.setGunGameItem(is2);
		is3 = ItemUtil.setGunGameItem(is3);
		
		if(this.menu == null) {
			Inventory tmp = Bukkit.createInventory(null, InventoryType.HOPPER, LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Title"));
			this.menu = tmp;
		}
		
		this.menu.setItem(0, is0);
		this.menu.setItem(1, is1);
		this.menu.setItem(2, is4);
		this.menu.setItem(3, is2);
		this.menu.setItem(4, is3);
		
	}
	
	public Boolean getGriefAllowed(EGriefType type) {
		switch(type) {
		case BULLETS_IGNITE_TNT:
			return this.bulletsIgniteTNT;
		case EXPLOSIONS:
			return this.explosionsGrief;
		case PROTECT_WATER_BODIES:
			return this.protectWaterBodies;
		case PHYSIC_ENGINE:
			return this.physicsGrief;
		case SHOTS_BREAK_GLASS:
			return this.bulletGlassGrief;
		default:
			return false;
		}
	}
	public void openGUI(Player p) {
		p.openInventory(this.menu);
	}
	public void setGriefAllowed(EGriefType type, Boolean allowed) {
		File infoFileF = new File(this.world.getWorldFolder().getAbsolutePath() + "/data/gungame", "info.yml");
		FileConfiguration infoFile = YamlConfiguration.loadConfiguration(infoFileF);
		switch(type) {
		case BULLETS_IGNITE_TNT:
			this.bulletsIgniteTNT = allowed;
			infoFile.set("GunEngineGrief.BulletsIgniteTNT", allowed);
			break;
		case EXPLOSIONS:
			this.explosionsGrief = allowed;
			infoFile.set("GunEngineGrief.Explosions", allowed);
			if(!allowed) {
				setGriefAllowed(EGriefType.PHYSIC_ENGINE, false);
			}
			break;
		case PROTECT_WATER_BODIES:
			this.protectWaterBodies = allowed;
			infoFile.set("GunEngineGrief.ProtectWaterBodies", allowed);
			break;
		case PHYSIC_ENGINE:
			this.physicsGrief = allowed;
			infoFile.set("GunEngineGrief.PhysicsEngine", allowed);
			break;
		case SHOTS_BREAK_GLASS:
			this.bulletGlassGrief = allowed;
			infoFile.set("GunEngineGrief.BulletsBreakGlass", allowed);
			break;
		default:
			break;
		}
		try {
			infoFile.save(infoFileF);
		} catch (IOException e) {
			e.printStackTrace();
		}
		printSettings();
	}
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory() != null && event.getClickedInventory().equals(this.menu)) {
			event.setCancelled(true);
			switch(event.getSlot()) {
			case 0:	
				ItemStack item = new ItemStack(Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Explosions.Item"))), 1);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Explosions.Title"));
				
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				
				if(!this.explosionsGrief) {
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
					meta.setLore(this.enabledLore);
				} else {
					meta.setLore(this.disabledLore);
				}
				item.setItemMeta(meta);
				item = ItemUtil.addTags(item, "GunGame_Item", "Grief-Explosions");
				item = ItemUtil.setGunGameItem(item);
				
				this.menu.setItem(0, item);
				
				if(this.explosionsGrief) {
					ItemStack phItem = new ItemStack(Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Physics.Item"))), 1);
					ItemMeta phMeta = phItem.getItemMeta();
					phMeta.setDisplayName(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Physics.Title"));
					
					phMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					phMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					
					phMeta.setLore(this.disabledLore);
					
					phItem.setItemMeta(phMeta);
					phItem = ItemUtil.addTags(phItem, "GunGame_Item", "Grief-Physics");
					phItem = ItemUtil.setGunGameItem(phItem);
					
					this.menu.setItem(1, phItem);
					
					//setGriefAllowed(GriefType.PHYSIC_ENGINE, false);
				}
				
				setGriefAllowed(EGriefType.EXPLOSIONS, !this.explosionsGrief);
				break;
			case 1:
				ItemStack item1 = new ItemStack(Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Physics.Item"))), 1);
				ItemMeta meta1 = item1.getItemMeta();
				meta1.setDisplayName(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.Physics.Title"));
				
				meta1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				
				if(!this.physicsGrief) {
					meta1.addEnchant(Enchantment.DURABILITY, 1, true);
					meta1.setLore(this.enabledLore);
				} else {
					meta1.setLore(this.disabledLore);
				}
				item1.setItemMeta(meta1);
				item1 = ItemUtil.addTags(item1, "GunGame_Item", "Grief-Physics");
				item1 = ItemUtil.setGunGameItem(item1);
				
				this.menu.setItem(1, item1);
				
				setGriefAllowed(EGriefType.PHYSIC_ENGINE, !this.physicsGrief);
				break;
			case 2:
				ItemStack item4 = new ItemStack(Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.ProtectWaterBodies.Item"))), 1);
				ItemMeta meta4 = item4.getItemMeta();
				meta4.setDisplayName(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.ProtectWaterBodies.Title"));
				
				meta4.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta4.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				
				if(!this.protectWaterBodies) {
					meta4.addEnchant(Enchantment.DURABILITY, 1, true);
					meta4.setLore(this.enabledLore);
				} else {
					meta4.setLore(this.disabledLore);
				}
				item4.setItemMeta(meta4);
				item4 = ItemUtil.addTags(item4, "GunGame_Item", "Grief-Waterbodies");
				item4 = ItemUtil.setGunGameItem(item4);
				
				this.menu.setItem(2, item4);
				
				setGriefAllowed(EGriefType.PROTECT_WATER_BODIES, !this.protectWaterBodies);
				break;
			case 3:
				ItemStack item2 = new ItemStack(Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.BreakGlass.Item"))), 1);
				ItemMeta meta2 = item2.getItemMeta();
				meta2.setDisplayName(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.BreakGlass.Title"));
				
				meta2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				
				if(!this.bulletGlassGrief) {
					meta2.addEnchant(Enchantment.DURABILITY, 1, true);
					meta2.setLore(this.enabledLore);
				} else {
					meta2.setLore(this.disabledLore);
				}
				item2.setItemMeta(meta2);
				item2 = ItemUtil.addTags(item2, "GunGame_Item", "Grief-Glass");
				item2 = ItemUtil.setGunGameItem(item2);
				
				this.menu.setItem(3, item2);
				
				setGriefAllowed(EGriefType.SHOTS_BREAK_GLASS, !this.bulletGlassGrief);
				break;
			case 4:
				ItemStack item3 = new ItemStack(Material.valueOf(ChatColor.stripColor(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.IgniteTNT.Item"))), 1);
				ItemMeta meta3 = item3.getItemMeta();
				meta3.setDisplayName(LangUtil.buildGUIString("GunEngine.GriefSettingsGUI.Icons.IgniteTNT.Title"));
				
				meta3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				
				if(!this.bulletsIgniteTNT) {
					meta3.addEnchant(Enchantment.DURABILITY, 1, true);
					meta3.setLore(this.enabledLore);
				} else {
					meta3.setLore(this.disabledLore);
				}
				item3.setItemMeta(meta3);
				item3 = ItemUtil.addTags(item3, "GunGame_Item", "Grief-Ignite");
				item3 = ItemUtil.setGunGameItem(item3);
				
				this.menu.setItem(4, item3);
				
				setGriefAllowed(EGriefType.BULLETS_IGNITE_TNT, !this.bulletsIgniteTNT);
				break;
			default:
				break;
			}
		}
	}
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(event.getInventory() != null && event.getInventory().equals(this.menu)) {
			event.setCancelled(true);
		}
	}
}
