package de.MrBaumeister98.GunGame.Items;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Items.TrackPadRenderer.TrackPadCreator;

public class TrackPadItem implements Listener {
	
	private static ItemStack baseItem;
	
	public static ItemStack getTrackPadBaseItem() {
		return TrackPadItem.baseItem.clone();
	}
	
	@SuppressWarnings("deprecation")
	public static void initTrackPadItem() {
		ItemStack trackpad;
			trackpad = new ItemStack(Material.FILLED_MAP, 1, (short)0);
		ItemMeta meta = trackpad.getItemMeta();
		meta.setDisplayName(LangUtil.buildItemName("Trackpad"));
		meta.setLore(LangUtil.buildItemLore("Trackpad"));
		List<String> lore = meta.getLore();
		lore.add("");
		String charges = LangUtil.getStringByPath("lang.Items.Trackpad.Charges");
		charges = ChatColor.translateAlternateColorCodes('&', charges);
		charges = charges.replaceAll("%charges%", String.valueOf(Math.abs(GunGamePlugin.instance.getConfig().getInt("Config.Items.TrackPad.Uses"))));
		lore.add(charges);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(lore);
		trackpad.setItemMeta(meta);
		
		trackpad = ItemUtil.addTags(trackpad, "GunGame_Item", "Trackpad");
		trackpad = ItemUtil.addTags(trackpad, "GG_TrackPad_ChargesLeft", Math.abs(GunGamePlugin.instance.getConfig().getInt("Config.Items.TrackPad.Uses")));
		trackpad = ItemUtil.setGunGameItem(trackpad);
		
		baseItem = trackpad;
	}
	
	@SuppressWarnings("deprecation")
	public static void giveTrackPad(Player p, int count) {
		ItemStack map = getTrackPadBaseItem();
		short mapID = TrackPadCreator.renderMapView(p.getLocation(), p.getUniqueId());
			MapMeta meta = (MapMeta) map.getItemMeta();
			meta.setMapId(mapID);
			map.setItemMeta(meta);
		map.setAmount(count);
		p.getInventory().addItem(map);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRequestMapUpdate(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(ItemUtil.hasTag(event.getItem(), "GunGame_Item", "Trackpad")) {
				if(GunGamePlugin.instance.arenaManager.isIngame(event.getPlayer().getUniqueId())) {
					int chargesLeft = ItemUtil.getInteger(event.getItem(), "GG_TrackPad_ChargesLeft");
					if(chargesLeft > 0) {
						//Item
						ItemStack map = event.getItem();
						//Updates Lore
						ItemMeta meta = map.getItemMeta();
						List<String> lore = meta.getLore();
						String charges = LangUtil.getStringByPath("lang.Items.Trackpad.Charges");
						charges = ChatColor.translateAlternateColorCodes('&', charges);
						charges = charges.replaceAll("%charges%", String.valueOf(Math.abs(chargesLeft -1)));
						lore.set(lore.size() -2, charges);
						meta.setLore(lore);
						map.setItemMeta(meta);
						//Updates Image
						short mapID = TrackPadCreator.renderMapView(event.getPlayer().getLocation(), event.getPlayer().getUniqueId());
							MapMeta mMeta = (MapMeta) map.getItemMeta();
							mMeta.setMapId(mapID);
							map.setItemMeta(mMeta);
						//Updates Uses Meta
						map = ItemUtil.addTags(map, "GG_TrackPad_ChargesLeft", chargesLeft -1);
					}
				}
			}
		}
	}

}
