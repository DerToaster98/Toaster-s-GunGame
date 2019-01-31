package de.MrBaumeister98.GunGame.Game.Mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import de.MrBaumeister98.GunGame.Game.Arena.Arena;
import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import net.md_5.bungee.api.ChatColor;

public class TDMMode implements Listener {
	
	private Arena owner;
	public Inventory menu;
	private HashMap<Integer, TDMTeam> teamSlotMap = new HashMap<Integer, TDMTeam>();
	private HashMap<TDMTeam, Integer> slotTeamMap = new HashMap<TDMTeam, Integer>();
	private HashMap<Team, TDMTeam> scoreboardteamTDMTeamMap = new HashMap<Team, TDMTeam>();
	private HashMap<ETeamColor, TDMTeam> teamByColor = new HashMap<ETeamColor, TDMTeam>();
	private HashMap<UUID, TDMTeam> teamByPlayer = new HashMap<UUID, TDMTeam>();
	private List<TDMTeam> allTeams = new ArrayList<TDMTeam>();
	private List<TDMTeam> participatingTeams = new ArrayList<TDMTeam>();
	
	//SLOT: 13 = random team
	//--> if slot greater than 13: slot = slot +1
	
	public TDMMode(Arena arena) {
		this.setOwner(arena);
		GunGamePlugin.instance.getServer().getPluginManager().registerEvents(this, GunGamePlugin.instance);
		generateMenu();
	}
	public List<TDMTeam> getTeams() {
		return this.participatingTeams;
	}
	public void openMenu(Player p) {
		p.openInventory(this.menu);
	}
	@EventHandler
	public void onDrag(InventoryDragEvent event) {
		if(event.getInventory() != null && event.getInventory().equals(this.menu)) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getInventory() != null && event.getClickedInventory() != null && event.getClickedInventory().equals(this.menu)) {
			event.setCancelled(true);
			TDMTeam team = null;
			if(event.getSlot() == 13) {
				team = this.getRandomTeam();
			} else {
				team = this.getTeamBySlot(event.getSlot());
			}
			if(this.teamByPlayer.containsKey(event.getWhoClicked().getUniqueId())) {
				this.teamByPlayer.get(event.getWhoClicked().getUniqueId()).remPlayer((Player)event.getWhoClicked());
			}
			team.addPlayer((Player)event.getWhoClicked());
			event.getWhoClicked().closeInventory();
		}
	}
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if(event.getInventory() != null && event.getInventory().equals(this.menu)) {
			if(!this.teamByPlayer.containsKey(event.getPlayer().getUniqueId())) {
				this.getRandomTeam().addPlayer((Player)event.getPlayer());
			}
		}
	}
	@SuppressWarnings("deprecation")
	public void generateMenu() {
		Inventory inv = Bukkit.createInventory(null, 18, LangUtil.buildGUIString("TeamSelector.Name"));
		//List<TDMTeam> tmpTeamList = new ArrayList<TDMTeam>();
		for(ETeamColor color : ETeamColor.values()) {
			TDMTeam tmpTeam = new TDMTeam(this.owner, color, this);
			this.allTeams.add(tmpTeam);
		}
		int listSlot = 0;
		for(int slot = 0; slot < 18; slot++) {
			if(slot == 13) {
				ItemStack rdmTeam = new ItemStack(Material.valueOf(LangUtil.buildGUIString("TeamSelector.TeamIcon.RandomTeam.Item")), 1);
				rdmTeam.setDurability(Short.valueOf(LangUtil.buildGUIString("TeamSelector.TeamIcon.RandomTeam.Damage")));
				ItemMeta meta = rdmTeam.getItemMeta();
				meta.setDisplayName(LangUtil.buildGUIString("TeamSelector.TeamIcon.RandomTeam.Name"));
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
				rdmTeam.setItemMeta(meta);
				inv.setItem(slot, rdmTeam);
			} else if(slot < 9) {
				inv.setItem(slot, this.allTeams.get(listSlot).getIcon());
				this.teamSlotMap.put(slot, this.allTeams.get(listSlot));
				this.slotTeamMap.put(this.allTeams.get(listSlot), slot);
				listSlot++;
			} else if(slot > 9 && slot < 13) {
				inv.setItem(slot, this.allTeams.get(listSlot).getIcon());
				this.teamSlotMap.put(slot, this.allTeams.get(listSlot));
				this.slotTeamMap.put(this.allTeams.get(listSlot), slot);
				listSlot++;
			} else if(slot > 13 && slot < 17) {
				inv.setItem(slot, this.allTeams.get(listSlot).getIcon());
				this.teamSlotMap.put(slot, this.allTeams.get(listSlot));
				this.slotTeamMap.put(this.allTeams.get(listSlot), slot);
				listSlot++;
			}
		}
		this.menu = inv;
	}
	public void assignTeam(Team scTeam, TDMTeam tdmTeam) {
		this.scoreboardteamTDMTeamMap.put(scTeam, tdmTeam);
	}
	public void assignTeam(ETeamColor color, TDMTeam tdmTeam) {
		this.teamByColor.put(color, tdmTeam);
	}
	public TDMTeam getRandomTeam() {
		//Random rdm = new Random();
		Integer rdmSlot = Util.getRandomNumber(this.allTeams.size());
		return this.allTeams.get(rdmSlot);
	}
	@SuppressWarnings("deprecation")
	public void onStartGame() {
		for(TDMTeam tdTeam : this.scoreboardteamTDMTeamMap.values()) {
			if(tdTeam.membercount > 0) {
				this.participatingTeams.add(tdTeam);
			}
		}
		if(this.participatingTeams.size() < 2) {
			if(this.participatingTeams.isEmpty()) {
				boolean b = true;
				for(Player p : this.owner.getPlayers()) {
					if(b) {
						this.teamByColor.get(ETeamColor.BLUE).addPlayer(p);
						b = false;
					} else {
						this.teamByColor.get(ETeamColor.RED).addPlayer(p);
						b = true;
					}
				}
			} else {
				boolean b = true;
				List<OfflinePlayer> ops = new ArrayList<OfflinePlayer>(this.participatingTeams.get(0).team.getPlayers()); 
				for(OfflinePlayer op : ops) {
					Player p = Bukkit.getPlayer(op.getUniqueId());
					this.participatingTeams.get(0).remPlayer(p);
				}
				for(OfflinePlayer op : ops) {
					Player p = Bukkit.getPlayer(op.getUniqueId());
					if(b) {
						this.teamByColor.get(ETeamColor.BLUE).addPlayer(p);
						b = false;
					} else {
						this.teamByColor.get(ETeamColor.RED).addPlayer(p);
						b = true;
					}
				}
			}
			this.participatingTeams.clear();
			this.participatingTeams.add(this.teamByColor.get(ETeamColor.BLUE));
			this.participatingTeams.add(this.teamByColor.get(ETeamColor.RED));
		}
		for(TDMTeam tdTeam : this.participatingTeams) {
			tdTeam.setupForGame();
		}
	}
	
	public Arena getOwner() {
		return owner;
	}
	public void setOwner(Arena owner) {
		this.owner = owner;
	}


	public class TDMTeam {
		
		private Arena arena;
		private TDMMode manager;
		private Team team;
		private ItemStack icon;
		private Integer membercount;
		private String name;
		private Integer points;
		private ETeamColor color;
		private ChatColor nameColor;
		private Integer killsToWin;
		
		public TDMTeam(Arena arena, ETeamColor color, TDMMode manager) {
			this.arena = arena;
			this.manager = manager;
			this.color = color;
			this.nameColor = color.chatColorValue(this.color);
			
			this.points = 0;
			this.membercount = 0;
			
			this.name = this.arena.getName() + "-TDT" + this.color.shortValue(this.color);
			
			Team t = this.arena.getScoreBoardUtil().getBoard().registerNewTeam(this.name);
			t.setDisplayName(this.nameColor + this.name);
			
			t.setSuffix("" + ChatColor.RESET);
			t.setPrefix(this.nameColor + "\u2B1B" + " " + this.nameColor);
			
			t.setCanSeeFriendlyInvisibles(true);
			t.setAllowFriendlyFire(false);
			
			t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
			t.setOption(Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.FOR_OWN_TEAM);
			
			this.team = t;
			this.manager.assignTeam(this.team, this);
			this.manager.assignTeam(this.color, this);
			generateIcon();
		}
		public String getDisplayName() {
			return this.getIcon().getItemMeta().getDisplayName();
		}
		public Integer getDistanceToVictory() {
			return (this.killsToWin - this.points);
		}
		public void setupForGame() {
			if(this.membercount > 2) {
				Double tmp = (double)this.arena.getKillsToWin() * ((double)this.membercount / (double)2.0);
				this.killsToWin = tmp.intValue();
			} else if(this.membercount > 1) {
				Double tmp = (double)this.arena.getKillsToWin();
				this.killsToWin = tmp.intValue();
			} else {
				Double tmp = (double)this.arena.getKillsToWin() * 0.75D;
				this.killsToWin = tmp.intValue();
			}
		}
		@SuppressWarnings("deprecation")
		public void onEndOfGame() {
			try {
				for(OfflinePlayer op : this.team.getPlayers()) {
					if(this.manager.teamByPlayer.containsKey(op.getUniqueId())) {
						this.manager.teamByPlayer.remove(op.getUniqueId(), this);
					}
					this.team.removePlayer(op);
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		@SuppressWarnings("deprecation")
		public void addKill() {
			this.points = this.points +1;
			for(OfflinePlayer op : this.team.getPlayers()) {
				Player p = Bukkit.getPlayer(op.getUniqueId());
				float percentage = (float)this.points / (float)this.killsToWin;
				p.setExp(percentage);
			}
			if(this.points >= this.killsToWin) {
				this.arena.preEndArena();
			}
		}
		@SuppressWarnings("deprecation")
		public void addPlayer(Player p) {
			this.membercount = this.membercount +1;
			this.team.addPlayer(p);
			this.manager.teamByPlayer.put(p.getUniqueId(), this);
			generateIcon();
			this.manager.menu.setItem(this.manager.slotTeamMap.get(this), this.icon);
		}
		@SuppressWarnings("deprecation")
		public void remPlayer(Player p) {
			this.membercount = this.membercount -1;
			this.team.removePlayer(p);
			this.manager.teamByPlayer.remove(p.getUniqueId());
			generateIcon();
			this.manager.menu.setItem(this.manager.slotTeamMap.get(this), this.icon);
		}
		@SuppressWarnings("deprecation")
		private void generateIcon() {
			ItemStack item = new ItemStack(Material.STONE);
			
			if(GunGamePlugin.instance.serverPre113) {
				//item = new ItemStack(Material.valueOf("WOOL"), 1, this.color.shortValue(this.color));
				item.setType(Material.valueOf("WOOL"));
				item.setDurability(this.color.shortValue(this.color));
			} else {
				//item = new ItemStack(Material.valueOf(this.color.toString() + "_WOOL"), 1);
				item.setType(Material.valueOf(this.color.toString() + "_WOOL"));
			}
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(this.nameColor + "Team " + LangUtil.buildGUIString("TeamSelector.Colors." + this.color.toString().toUpperCase()));
			List<String> lore = new ArrayList<String>();
			String lstmp = LangUtil.buildGUIString("TeamSelector.TeamIcon.Membercount");
			lstmp = lstmp.replaceAll("%count%", this.membercount.toString());
			lore.add(lstmp);
			meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			
			item.setItemMeta(meta);
			
			this.icon = item;
		}
		public ItemStack getIcon() {
			return this.icon;
		}
	}
	
	@Nullable
	public TDMTeam getTeamBySlot(Integer slot) {
		if(this.teamSlotMap.containsKey(slot)) {
			return this.teamSlotMap.get(slot);
		}
		return null;
	}
	public TDMTeam getTeam(Player p) {
		//Team scTeam = p.getScoreboard().getPlayerTeam((OfflinePlayer)p);
		if(this.teamByPlayer.containsKey(p.getUniqueId())) {
			return this.teamByPlayer.get(p.getUniqueId());
		}
		return null;
	}
	public void remPlayer(Player p) {
		this.getTeam(p).remPlayer(p);
	}

}
