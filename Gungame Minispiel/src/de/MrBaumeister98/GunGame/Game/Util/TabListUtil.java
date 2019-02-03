package de.MrBaumeister98.GunGame.Game.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class TabListUtil {

	public static void sendTabTitle(Player player, String header, String footer) {
		if (header == null)
			header = "";
		header = ChatColor.translateAlternateColorCodes('&', header);

		if (footer == null)
			footer = "";
		footer = ChatColor.translateAlternateColorCodes('&', footer);

		/*
		 * TabTitleSendEvent tabTitleSendEvent = new TabTitleSendEvent(player, header,
		 * footer); Bukkit.getPluginManager().callEvent(tabTitleSendEvent); if
		 * (tabTitleSendEvent.isCancelled()) { return; }
		 */
		header = header.replaceAll("%player%", player.getDisplayName());
		footer = footer.replaceAll("%player%", player.getDisplayName());
		try {
			Object tabHeader = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
					.getMethod("a", new Class[] { String.class })
					.invoke(null, new Object[] { "{\"text\":\"" + header + "\"}" });
			Object tabFooter = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
					.getMethod("a", new Class[] { String.class })
					.invoke(null, new Object[] { "{\"text\":\"" + footer + "\"}" });
			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutPlayerListHeaderFooter")
					.getConstructor(new Class[0]);
			Object packet = titleConstructor.newInstance(new Object[0]);
			try {
				Field aField = packet.getClass().getDeclaredField("a");
				aField.setAccessible(true);
				aField.set(packet, tabHeader);
				Field bField = packet.getClass().getDeclaredField("b");
				bField.setAccessible(true);
				bField.set(packet, tabFooter);
			} catch (Exception e) {
				Field aField = packet.getClass().getDeclaredField("header");
				aField.setAccessible(true);
				aField.set(packet, tabHeader);
				Field bField = packet.getClass().getDeclaredField("footer");
				bField.setAccessible(true);
				bField.set(packet, tabFooter);
			}
			sendPacket(player, packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", new Class[] { getNMSClass("Packet") })
					.invoke(playerConnection, new Object[] { packet });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
