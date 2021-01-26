package de.MrBaumeister98.GunGame.GunEngine;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.MrBaumeister98.GunGame.Game.Core.FileManager;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;
import de.MrBaumeister98.GunGame.GunEngine.Turrets.Turret;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class VisualsHelper {
	
	public WeaponManager manager;
	@SuppressWarnings("unused")
	private HashMap<Location,Integer> breakMap;
	
	public VisualsHelper(WeaponManager manager) {
		this.manager = manager;
		this.breakMap = new HashMap<Location,Integer>();
	}
	
	public void sendOutOfAmmo(Player p) {
		String text = buildGEString("Visuals.OutOfAmmo", null, null, null, null, null, null);
		sendActionbar(p, text);
	}
	public void sendOverheated(Player p) {
		String text = buildGEString("Visuals.Overheat", null, null, null, null, null, null);
		sendActionbar(p, text);
	}
	public void sendReloadingWeapon(Player p) {
		String text = buildGEString("Visuals.Reloading", null, null, null, null, null, null);
		sendActionbar(p, text);
	}
	public void sendRemainingShots(Player p, Integer remaining, Gun gun) {
		String text = buildGEString("Visuals.RemainingShots", null, null, buildRemainingShots(remaining, gun), null, null, null);
		sendActionbar(p, text);
	}
	public void sendMissingAmmo(Player p, Gun weapon, Ammo missing) {
		String text = buildGEString("Visuals.MissingAmmo", missing.getAmmoName(), weapon, null, null, null, null);
		sendActionbar(p, text);
	}
	public void sendMissingAmmo(Player p, Gun weapon, Grenade missing) {
		String text = buildGEString("Visuals.MissingAmmo", missing.getGrenadeName(), weapon, null, null, null, null);
		sendActionbar(p, text);
	}
	public void sendMissingGrenades(Player p, Gun weapon) {
		String text = buildGEString("Visuals.MissingGrenades", null, weapon, null, null, null, null);
		sendActionbar(p, text);
	}
	public void sendTurretStatus(Player p, Turret turret) {
		Integer remaining = turret.getMagazine();
		Integer maxAmmo = turret.config.getMagazineSize();
		String ammo = "&8[-";
		if(remaining >= (maxAmmo / 2)) {
			ammo = ammo + "&a" + remaining;
		} else if(remaining < (maxAmmo / 2)) {
			if(remaining >= (maxAmmo /4)) {
				ammo = ammo + "&6" + remaining;
			} else if(remaining < (maxAmmo /4)){
				ammo = ammo + "&c" + remaining;
			}
		}
		ammo = ammo + "/&2" + maxAmmo + "&8-]";
		
		
		Double heat = turret.getTemperature();
		Double maxHeat = turret.config.getCriticalHeat();
		
		DecimalFormat df = new DecimalFormat("####.##");
		
		String heatS = "&8[-";
		if(heat <= maxHeat /2) {
			heatS = heatS + "&a" + df.format(heat);
		} else if(heat > maxHeat /2) {
			
			if(heat > (maxHeat *0.75)) {
				if(heat > maxHeat * 0.9) {
					heatS = heatS + "&4" + df.format(heat);
				} else if(heat <= maxHeat * 0.9) {
					heatS = heatS + "&c" + df.format(heat);
				}
			} else if(heat <= (maxHeat *0.75)) {
				heatS = heatS + "&6" + df.format(heat);
			}
			
		}
		heatS = heatS + "�C/&2" + maxHeat + "�C&8-]";
		
		String text = buildGEString("Visuals.TurretStatus", null, null, ammo, heatS, null, null);
		sendActionbar(p, text);
	}
	public void sendTankStatus(Player p, Tank tank) {
		Integer remaining = tank.getMagazine();
		Integer maxAmmo = tank.getConfig().getMagazineSize();
		String ammo = "&8[-";
		if(tank.isReloading()) {
			ammo = ammo + buildGEString("Visuals.Reloading", null, null, null, null, null, null) + "&8-]";
		} else {
			if(remaining >= (maxAmmo / 2)) {
				ammo = ammo + "&a" + remaining;
			} else if(remaining < (maxAmmo / 2)) {
				if(remaining >= (maxAmmo /4)) {
					ammo = ammo + "&6" + remaining;
				} else if(remaining < (maxAmmo /4)){
					ammo = ammo + "&c" + remaining;
				}
			}
			ammo = ammo + "/&2" + maxAmmo + "&8-]";
		}		
		
		String health = "&8[-";
			Double hpn = tank.getHealth();
			//Double hpm = tank.getConfig().getMaxHealth();
			DecimalFormat df = new DecimalFormat("####.##");
			health = health + "&c" + df.format(hpn);
		health = health + "&8-]";
		
		Double speed = new Double(tank.getTankMover().getCurrentSpeed());
		Double kmh = Math.abs(speed) * (double)50.0;
		DecimalFormat df2 = new DecimalFormat("####.##");
		String kmhs = "&8[-" + "&a" + df2.format(kmh) + " &cKM/h" + "&8-]";
		
		String text = buildGEString("Visuals.TankStatus", null, null, ammo, null, health, kmhs);
		text = text.replaceAll("%shots%", ammo);
		text = text.replaceAll("%hp%", health);
		text = text.replaceAll("%speed%", kmhs);
		sendActionbar(p, text);
	}
	/*public void sendRealisticHolding(Gun gun, Player player) {
		Double range = GunGamePlugin.instance.getServer().getViewDistance() * 16.0D;
		for(Entity ent : player.getNearbyEntities(range /2, range /2, range /2)) {
			if(ent instanceof Player) {
				if((Player)ent != player) {
					//SEND PACKETS
					Player p = (Player)ent;
					net.minecraft.server.v1_12_R1.ItemStack bow = new net.minecraft.server.v1_12_R1.ItemStack(Item.getById(261));
					
					((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)player).getHandle().getId(), EnumItemSlot.MAINHAND, bow));
					DataWatcher watcher = new DataWatcher(((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)player).getHandle());
					watcher.register(new DataWatcherObject<>(5, DataWatcherRegistry.a), (byte) 1);
					((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)player).getHandle().getId(), EnumItemSlot.MAINHAND, bow));
					((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer)player).getHandle().getId(), watcher, true));
					//p.sendMessage("SEND!");
				}
			}
		}
	}*/
	/*public void sendBlockDamage(Location block, Player p) {
		Double range = GunGamePlugin.instance.getServer().getViewDistance() * 16.0D;
		for(Entity ent : p.getNearbyEntities(range /2, range /2, range /2)) {
			if(ent instanceof Player) {
				PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(0,
						new BlockPosition(block.getBlockX(), block.getBlockY(), block.getBlockZ()),
						getBlockDamage(block));
				this.breakMap.put(block, getBlockDamage(block));
				((CraftPlayer)ent).getHandle().playerConnection.sendPacket(packet);
				//p.sendMessage("SEND!");
			}
		}		
	}*/
	/*private Integer getBlockDamage(Location loc) {
		if(this.breakMap.containsKey(loc)) {
			if(this.breakMap.get(loc) < 9) {
				return this.breakMap.get(loc) +1;
			} else {
				return this.breakMap.get(loc);
			}
		} 
		return 0;
	}*/
	
	private void sendActionbar(Player p, String text) {
		if(p != null) {
			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
		}
	}
	
	
	private String buildRemainingShots(Integer remaining, Gun gun) {
		Integer diff = gun.getMaxAmmo() - remaining;
		if(gun.getMaxAmmo() < 21) {
			String tmp = "&8[&a";
			for(int i = 0; i < remaining; i++) {
				tmp = tmp + "|";
			}
			tmp = tmp + "&c";
			for(int i = 0; i<diff; i++) {
				tmp = tmp + "|";
			}
			tmp = tmp + "&8]          ";
			
			String tmp2 = "&8[-";
			if(remaining >= (gun.getMaxAmmo() / 2)) {
				tmp2 = tmp2 + "&a" + remaining;
			} else if(remaining < (gun.getMaxAmmo() / 2)) {
				if(remaining >= (gun.getMaxAmmo() /4)) {
					tmp2 = tmp2 + "&6" + remaining;
				} else if(remaining < (gun.getMaxAmmo() /4)){
					tmp2 = tmp2 + "&c" + remaining;
				}
			}
			tmp2 = tmp2 + "/&2" + gun.getMaxAmmo() + "&8-]";
			
			return tmp + tmp2;
		} else {
			String tmp = "&8[-";
			if(remaining >= (gun.getMaxAmmo() / 2)) {
				tmp = tmp + "&a" + remaining;
			} else if(remaining < (gun.getMaxAmmo() / 2)) {
				if(remaining >= (gun.getMaxAmmo() /4)) {
					tmp = tmp + "&6" + remaining;
				} else if(remaining < (gun.getMaxAmmo() /4)){
					tmp = tmp + "&c" + remaining;
				}
			}
			tmp = tmp + "/&2" + gun.getMaxAmmo() + "&8-]";
			return tmp;
		}
	}
	private String buildGEString(String path, String ammo, Gun weapon, String shots, String heat, String hp, String speed) {
		String inFile = null;
		try {
			String tmp = FileManager.getLang().getString("lang.Commands.GunEngine." + path);
			if(tmp != null) {
				inFile = tmp;
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		if(ammo != null) {
			inFile = inFile.replace("%ammo%", ammo);
		}
		if(weapon != null) {
			inFile = inFile.replace("%weapon%", weapon.getGunName());
		}
		if(shots != null) {
			inFile = inFile.replace("%shots%", shots);
		}
		if(heat != null) {
			inFile = inFile.replace("%heat%", heat);
		}
		if(hp != null) {
			inFile = inFile.replace("%hp%", hp);
		}
		if(speed != null) {
			inFile = inFile.replace("%speed%", speed);
		}
		
		return ChatColor.translateAlternateColorCodes('&', inFile);
	}
}
