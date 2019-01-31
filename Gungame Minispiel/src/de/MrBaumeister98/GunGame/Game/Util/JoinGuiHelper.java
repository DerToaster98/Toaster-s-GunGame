package de.MrBaumeister98.GunGame.Game.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Arena.ArenaManager;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class JoinGuiHelper implements Listener {

	private ArenaManager manager = GunGamePlugin.instance.arenaManager;
	private Inventory selectionMenu;
	
	public JoinGuiHelper() {
		createSelMenu();
	}
	
	@SuppressWarnings("deprecation")
	private void createSelMenu() {
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, LangUtil.buildGUIString("JoinGUI.Menu.Name"));
		
		ItemStack blankItem = null;
		if(GunGamePlugin.instance.serverPre113) {
			blankItem = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1 , (short) 14);
		} else {
			blankItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1/* , (short) 14*/);
		}
			
		ItemMeta meta = blankItem.getItemMeta();
		meta.setDisplayName(" ");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		blankItem.setItemMeta(meta);
		
		inv.setItem(1, blankItem);
		inv.setItem(3, blankItem);
		
		
		ItemStack quickjoinitem = new ItemStack(Material.IRON_SWORD, 1);
		ItemMeta meta1 = quickjoinitem.getItemMeta();
		meta1.setDisplayName(LangUtil.getStringByPath("lang.GUI.JoinGUI.Menu.QuickJoin"));
		meta1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta1.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta1.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		quickjoinitem.setItemMeta(meta1);
		
		inv.setItem(0, quickjoinitem);
		
		
		ItemStack randomjoinitem = null;
		if(GunGamePlugin.instance.serverPre113) {
			randomjoinitem = new ItemStack(Material.valueOf("COMMAND"), 1);
		} else {
			randomjoinitem = new ItemStack(Material.valueOf("COMMAND_BLOCK"), 1);
		}
		ItemMeta meta2 = randomjoinitem.getItemMeta();
		meta2.setDisplayName(LangUtil.getStringByPath("lang.GUI.JoinGUI.Menu.RandomJoin"));
		meta2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta2.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		randomjoinitem.setItemMeta(meta2);
		
		inv.setItem(2, randomjoinitem);
		
		
		ItemStack searchjoinitem = null;
		if(GunGamePlugin.instance.serverPre113) {
			searchjoinitem = new ItemStack(Material.valueOf("WORKBENCH"), 1);
		} else {
			searchjoinitem = new ItemStack(Material.valueOf("CRAFTING_TABLE"), 1);
		}
		ItemMeta meta3 = searchjoinitem.getItemMeta();
		meta3.setDisplayName(LangUtil.getStringByPath("lang.GUI.JoinGUI.Menu.All"));
		meta3.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta3.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		searchjoinitem.setItemMeta(meta3);
		
		inv.setItem(4, searchjoinitem);
		
		this.selectionMenu = inv;
	}
	
	private ItemStack createArenaIcon(Arena arena) {
		ItemStack stack = null;
		if(GunGamePlugin.instance.serverPre113) {
			stack = new ItemStack(Material.valueOf("WORKBENCH"), 1);
		} else {
			stack = new ItemStack(Material.valueOf("CRAFTING_TABLE"), 1);
		}
		
		String name = LangUtil.getStringByPath("lang.GUI.JoinGUI.ArenaIcon.Name");
		name = name.replaceAll("%name%", arena.getName());
		
		String state = LangUtil.getStringByPath("lang.GUI.JoinGUI.ArenaIcon.Status");
		state = state.replaceAll("%state%", arena.getGameState().toString());
		
		String players = LangUtil.getStringByPath("lang.GUI.JoinGUI.ArenaIcon.Players");
		players = players.replaceAll("%currentPlayers%", String.valueOf(arena.getPlayers().size()));
		players = players.replaceAll("%maxPlayers%", arena.getMaxPlayers().toString());
		
		String map = LangUtil.getStringByPath("lang.GUI.JoinGUI.ArenaIcon.Map");
		if(arena.getArenaWorld() != null) {
			map = map.replaceAll("%map%", arena.getArenaWorld().getName());
		} else {
			map = map.replaceAll("%map%", "???");
		}
		
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		List<String> lore = new ArrayList<String>();
		lore.add(players);
		lore.add(map);
		lore.add(state);
		meta.setLore(lore);
		
		if(arena.getPlayers().size() >= arena.getMinPlayers()) {
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
		}
		
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		stack.setItemMeta(meta);

		stack = ItemUtil.addTags(stack, "GG_JoinGUI_Arena", arena.getName());
		
		return stack;
	}
	
	public void openMenu(Player p) {
		if(this.selectionMenu == null) {
			createSelMenu();
		}
		p.openInventory(this.selectionMenu);
		
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory() != null && event.getClickedInventory().getTitle().equals(this.selectionMenu.getTitle()) && ItemUtil.hasKey(event.getCurrentItem(), "GG_JoinGUI_Arena") && !this.manager.isIngame((Player)event.getWhoClicked())) {
			event.setCancelled(true);
			String aID = ItemUtil.getString(event.getCurrentItem(), "GG_JoinGUI_Arena");
			try {
				Arena tmp = this.manager.getArena(aID);
				this.manager.tryJoin((Player)event.getWhoClicked(), tmp);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		if(event.getClickedInventory() != null && event.getClickedInventory().equals(this.selectionMenu) && !this.manager.isIngame((Player)event.getWhoClicked())) {
			event.setCancelled(true);
			
			List<Arena> arenasWithPlayers = new ArrayList<Arena>();
			for(Arena a : this.manager.arenas) {
				if(a.getPlayers().size() > 0 && a.getPlayers().size() < a.getMaxPlayers()) {
					arenasWithPlayers.add(a);
				}
			}
			Player p = (Player)event.getWhoClicked();
			/**
			 * 1:= Quick
			 * 
			 * 2:= Random
			 * 
			 * 3:= Standard
			 */
			if(!arenasWithPlayers.isEmpty()) {
				
				Collections.sort(arenasWithPlayers, new Comparator<Arena>() {

					@Override
					public int compare(Arena a1, Arena a2) {
						return a1.getPlayers().size() - a2.getPlayers().size();
					}
				});
				Collections.reverse(arenasWithPlayers);
				
				switch(event.getSlot()) {
				default:
					break;
				case 0:
					this.manager.tryJoin(p, arenasWithPlayers.get(0));
					break;
				case 2:
					Integer i = new Random().nextInt(arenasWithPlayers.size());
					this.manager.tryJoin(p, arenasWithPlayers.get(i));
					break;
				case 4:
					Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, this.selectionMenu.getTitle());
					for(Arena a : arenasWithPlayers) {
						ItemStack tmp = createArenaIcon(a);
						inv.addItem(tmp);
					}
					p.openInventory(inv);
					break;
				}
			} else {
				switch(event.getSlot()) {
				default:
					break;
				case 0:
					randomJoin(p);
					break;
				case 2:
					randomJoin(p);
					break;
				case 4:
					Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, this.selectionMenu.getTitle());
					for(Arena a : this.manager.arenas) {
						ItemStack tmp = createArenaIcon(a);
						inv.addItem(tmp);
					}
					p.openInventory(inv);
					break;
				}
			}
		}
	}
	private void randomJoin(Player p) {
		Integer i = new Random().nextInt(this.manager.arenas.size());
		Arena a = this.manager.arenas.get(i);
		this.manager.tryJoin(p, a);
	}
}
