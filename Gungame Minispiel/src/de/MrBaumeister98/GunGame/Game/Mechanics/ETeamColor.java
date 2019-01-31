package de.MrBaumeister98.GunGame.Game.Mechanics;

import net.md_5.bungee.api.ChatColor;

public enum ETeamColor {

	WHITE, BLACK, YELLOW, GREEN, RED, BLUE, CYAN, GRAY, LIGHT_BLUE, LIGHT_GRAY, LIME, MAGENTA, ORANGE, PINK, PURPLE;

	public short shortValue(ETeamColor teamcolor) {
		short s = 0;
		switch (teamcolor) {
		case BLACK:
			s = 15;
			break;
		case BLUE:
			s = 11;
			break;
		case CYAN:
			s = 9;
			break;
		case GRAY:
			s = 7;
			break;
		case GREEN:
			s = 13;
			break;
		case LIGHT_BLUE:
			s = 3;
			break;
		case LIGHT_GRAY:
			s = 8;
			break;
		case LIME:
			s = 5;
			break;
		case MAGENTA:
			s = 2;
			break;
		case ORANGE:
			s = 1;
			break;
		case PINK:
			s = 6;
			break;
		case PURPLE:
			s = 10;
			break;
		case RED:
			s = 14;
			break;
		case WHITE:
			s = 0;
			break;
		case YELLOW:
			s = 4;
			break;
		default:
			break;
		}
		return s;
	}

	public ChatColor chatColorValue(ETeamColor teamcolor) {
		switch (teamcolor) {
		case BLACK:
			return ChatColor.BLACK;
		case BLUE:
			return ChatColor.BLUE;
		case CYAN:
			return ChatColor.DARK_AQUA;
		case GRAY:
			return ChatColor.DARK_GRAY;
		case GREEN:
			return ChatColor.DARK_GREEN;
		case LIGHT_BLUE:
			return ChatColor.BLUE;
		case LIGHT_GRAY:
			return ChatColor.GRAY;
		case LIME:
			return ChatColor.GREEN;
		case MAGENTA:
			return ChatColor.RED;
		case ORANGE:
			return ChatColor.GOLD;
		case PINK:
			return ChatColor.LIGHT_PURPLE;
		case PURPLE:
			return ChatColor.DARK_PURPLE;
		case RED:
			return ChatColor.DARK_RED;
		case WHITE:
			return ChatColor.WHITE;
		case YELLOW:
			return ChatColor.YELLOW;
		default:
			break;

		}
		return ChatColor.WHITE;
	}

}
