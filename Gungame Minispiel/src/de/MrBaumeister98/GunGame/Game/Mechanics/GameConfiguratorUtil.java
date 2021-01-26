package de.MrBaumeister98.GunGame.Game.Mechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;

public class GameConfiguratorUtil implements Listener {
	
	private Arena owner;
	private Inventory menu;
	
	private Boolean explosion;
	private Boolean fire;
	private Boolean health;
	private Boolean protectBodiesOfWater;
	private Boolean physics;
	
	public GameConfiguratorUtil(Arena arena) {
	
		this.owner = arena;
		
		this.explosion = false;
		this.fire = false;
		this.health = false;
		this.protectBodiesOfWater = true;
		this.physics = true;
		
		Inventory tmp = Bukkit.createInventory(null, InventoryType.HOPPER, LangUtil.buildGUIString("GameConfigurator.Title"));
		
		ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.ExplosionsBreakBlocks.Item")), 1);
		ItemStack itm2 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.FireSpread.Item")), 1);
		ItemStack itm5 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.ProtectWaterBodies.Item")), 1);
		ItemStack itm3 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.RegenerateHealth.Item")), 1);
		ItemStack itm4 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.PhysicsEnabled.Item")), 1);
		
		ItemMeta m1 = itm1.getItemMeta();
		m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.ExplosionsBreakBlocks.Name"));
		m1.addEnchant(Enchantment.DURABILITY, 1, true);
		
		ItemMeta m2 = itm2.getItemMeta();
		m2.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.FireSpread.Name"));
		
		ItemMeta m5 = itm5.getItemMeta();
		m5.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.ProtectWaterBodies.Name"));
		m5.addEnchant(Enchantment.DURABILITY, 1, true);
		
		ItemMeta m3 = itm3.getItemMeta();
		m3.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.RegenerateHealth.Name"));
		
		ItemMeta m4 = itm4.getItemMeta();
		m4.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.PhysicsEnabled.Name"));
		m4.addEnchant(Enchantment.DURABILITY, 1, true);
		
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
		
		itm1.setItemMeta(m1);
		itm2.setItemMeta(m2);
		itm3.setItemMeta(m3);
		itm4.setItemMeta(m4);
		itm5.setItemMeta(m5);
		
		itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Explosions");
		itm2 = ItemUtil.addTags(itm2, "GunGame_Item", "Configurator-Fire");
		itm3 = ItemUtil.addTags(itm3, "GunGame_Item", "Configurator-Regeneration");
		itm4 = ItemUtil.addTags(itm4, "GunGame_Item", "Configurator-Phyics");
		itm5 = ItemUtil.addTags(itm5, "GunGame-Item", "Configurator-ProtectWater");
		
		itm1 = ItemUtil.setGunGameItem(itm1);
		itm2 = ItemUtil.setGunGameItem(itm2);
		itm3 = ItemUtil.setGunGameItem(itm3);
		itm4 = ItemUtil.setGunGameItem(itm4);
		itm5 = ItemUtil.setGunGameItem(itm5);
		
		tmp.setItem(0, itm1);
		tmp.setItem(1, itm2);
		tmp.setItem(2, itm5);
		tmp.setItem(3, itm3);
		tmp.setItem(4, itm4);
		
		this.menu = tmp;
	}
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory() != null && event.getClickedInventory().equals(this.menu)) {
			event.setCancelled(true);
			//event.getWhoClicked().sendMessage("Slot:" + event.getSlot());
			switch(event.getSlot()) {
			default:
				break;
			case 0:
				if(event.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					this.explosion = false;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.ExplosionsBreakBlocks.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.ExplosionsBreakBlocks.Name"));
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Explosions");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(0, itm1);
				} else {
					this.explosion = true;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.ExplosionsBreakBlocks.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.ExplosionsBreakBlocks.Name"));
					m1.addEnchant(Enchantment.DURABILITY, 1, true);
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Explosions");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(0, itm1);
				}
				break;
			case 1:
				if(event.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					this.fire = false;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.FireSpread.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.FireSpread.Name"));
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Fire");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(1, itm1);
				} else {
					this.fire = true;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.FireSpread.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.FireSpread.Name"));
					m1.addEnchant(Enchantment.DURABILITY, 1, true);
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Fire");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(1, itm1);
				}
				break;
			case 2:
				if(event.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					this.protectBodiesOfWater = false;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.ProtectWaterBodies.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.ProtectWaterBodies.Name"));
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-ProtectWater");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(2, itm1);
				} else {
					this.protectBodiesOfWater = true;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.ProtectWaterBodies.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.ProtectWaterBodies.Name"));
					m1.addEnchant(Enchantment.DURABILITY, 1, true);
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-ProtectWater");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(2, itm1);
				}
				break;
			case 3:
				if(event.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					this.health = false;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.RegenerateHealth.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.RegenerateHealth.Name"));
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Regeneration");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(3, itm1);
				} else {
					this.health = true;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.RegenerateHealth.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.RegenerateHealth.Name"));
					m1.addEnchant(Enchantment.DURABILITY, 1, true);
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Regeneration");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(3, itm1);
				}
				break;
			case 4:
				if(event.getCurrentItem().containsEnchantment(Enchantment.DURABILITY)) {
					this.physics = false;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.PhysicsEnabled.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.PhysicsEnabled.Name"));
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Physics");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(4, itm1);
				} else {
					this.physics = true;
					
					ItemStack itm1 = new ItemStack(Material.valueOf(LangUtil.buildGUIString("GameConfigurator.Contents.PhysicsEnabled.Item")), 1);
					ItemMeta m1 = itm1.getItemMeta();
					m1.setDisplayName(LangUtil.buildGUIString("GameConfigurator.Contents.PhysicsEnabled.Name"));
					m1.addEnchant(Enchantment.DURABILITY, 1, true);
					
					m1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					m1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					itm1.setItemMeta(m1);
					itm1 = ItemUtil.addTags(itm1, "GunGame_Item", "Configurator-Physics");
					itm1 = ItemUtil.setGunGameItem(itm1);
					this.menu.setItem(4, itm1);
				}
				break;
			/*case 4:
				
				break;*/
			}
			event.getWhoClicked().openInventory(this.menu);
		}
	}
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(event.getInventory() != null && event.getInventory().equals(this.menu)) {
			event.setCancelled(true);
		}
	}
	public void reset() {
		this.explosion = true;
		this.fire = false;
		this.protectBodiesOfWater = true;
		this.health = false;
		this.physics = true;
	}
	public void openMenu(Player p) {
		if(GunGamePlugin.instance.arenaManager.getArena(p).equals(this.owner)) {
			p.openInventory(this.menu);
		}
	}
	public void transferValues() {
		this.owner.explosionsBreakBlocks = this.explosion;
		this.owner.fireSpred = this.fire;
		this.owner.protectBodiesOfWater = this.protectBodiesOfWater;
		this.owner.physics = this.physics;
		this.owner.regenerateHealth = this.health;
	}

}
