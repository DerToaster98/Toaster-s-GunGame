package de.MrBaumeister98.GunGame.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.ItemUtil;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;

public class FlareGun implements Listener {
	
	public static Integer visibleTime = GunGamePlugin.instance.getConfig().getInt("Config.Items.FlareGun.GlowingDuration", 200);
	
	@SuppressWarnings("deprecation")
	public static ItemStack getFlareGun() {
		Material m;
		String mat = GunGamePlugin.instance.getConfig().getString("Config.Items.FlareGun.Item.Material", "BOW");
		try {
			m = Material.getMaterial(mat);
		} catch(IllegalArgumentException iaex) {
			m = Material.getMaterial(GunGamePlugin.instance.serverPre113 ? "IRON_BARDING" : "IRON_HORSE_ARMOR");
			Debugger.logError("&4ERROR: &cMaterial " + mat + " is not valid! Path: Config.Items.FlareGun.Item.Material");
			Debugger.logWarning("&eUsing default value [" + m.toString() + "] instead...");
		}
		if(m == null) {
			m = Material.getMaterial(GunGamePlugin.instance.serverPre113 ? "IRON_BARDING" : "IRON_HORSE_ARMOR");
			Debugger.logError("&4ERROR: &cMissing value for path: Config.Items.FlareGun.Item.Material");
			Debugger.logWarning("&eUsing default value [" + m.toString() + "] instead...");
		}
		ItemStack item = new ItemStack(m, 1);
		short dmg = Short.valueOf(GunGamePlugin.instance.getConfig().getString("Config.Items.FlareGun.Item.Damage", "0"));
		item.setDurability(dmg);
		
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		String name = LangUtil.buildItemName("FlareGun");
		meta.setDisplayName(name);
		
		List<String> lore = LangUtil.buildItemLore("FlareGun");
		List<String> lore2 = new ArrayList<String>();
		if(lore != null && !lore.isEmpty()) {
			for(String s : lore) {
				s = s.replaceAll("%timer%", FlareGun.visibleTime.toString());
				lore2.add(s);
			}
		}
		meta.setLore(lore2);
		
		item.setItemMeta(meta);
		
		item = ItemUtil.addTags(item, "GunGame_Item", "FlareGun");
		item = ItemUtil.setGunGameItem(item);
		
		item.setAmount(1);
		
		return item.clone();
	}
	
	public static boolean isFlareGun(ItemStack item) {
		return ItemUtil.hasTag(item, "GunGame_Item", "FlareGun");
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
			if(event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR) || !Util.isInteractable(event.getClickedBlock())) {
				ItemStack item = event.getItem();
				if(isFlareGun(item)) {
					Snowball projectile = event.getPlayer().launchProjectile(Snowball.class, event.getPlayer().getEyeLocation().getDirection().multiply(1.5D));
					projectile.setBounce(false);
					projectile.setGlowing(true);
					projectile.setInvulnerable(true);
					projectile.setShooter(event.getPlayer());
					projectile.setMetadata("GG_FlareGun_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
					
					Player p = event.getPlayer();
					if(GunGamePlugin.instance.serverPre113) {
						p.playSound(p.getEyeLocation(), Sound.valueOf("ENTITY_FIREWORK_LAUNCH"), 1.5F, 0.8F);
					} else {
						p.playSound(p.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.5F, 0.8F);
					}
					
					if(!p.getGameMode().equals(GameMode.CREATIVE)) {
						int amount = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount();
						if(amount > 1) {
							p.getInventory().getItem(p.getInventory().getHeldItemSlot()).setAmount(amount -1);
						} 
						if(amount == 1) {
							p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
						}
					}
				}
			}
		}
	}
	/**@EventHandler
	public void onInteractEntity(PlayerInteractAtEntityEvent event) {
		//if(event.getClickedBlock() == null || event.getClickedBlock().getType().equals(Material.AIR) || !Util.isInteractable(event.getClickedBlock())) {
			ItemStack item = null;
			if(event.getHand().equals(EquipmentSlot.HAND)) {
				item = event.getPlayer().getInventory().getItemInMainHand();
			}
			if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
				item = event.getPlayer().getInventory().getItemInOffHand();
			}
			if(isFlareGun(item)) {
				Snowball projectile = event.getPlayer().launchProjectile(Snowball.class, event.getPlayer().getEyeLocation().getDirection().multiply(1.5D));
				projectile.setBounce(false);
				projectile.setGlowing(true);
				projectile.setInvulnerable(true);
				projectile.setShooter(event.getPlayer());
				projectile.setMetadata("GG_FlareGun_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
				
				Player p = event.getPlayer();
				p.playSound(p.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, 1.5F, 0.8F);
				
				if(!p.getGameMode().equals(GameMode.CREATIVE)) {
					int amount = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount();
					if(amount > 1) {
						p.getInventory().getItem(p.getInventory().getHeldItemSlot()).setAmount(amount -1);
					} 
					if(amount == 1) {
						p.getInventory().remove(p.getInventory().getItem(p.getInventory().getHeldItemSlot()));
					}
				}
			}
		//}
	}**/
	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		if(event.getEntity().hasMetadata("GG_FlareGun_Projectile")) {
			if(event.getHitBlock() != null) {
				Util.createExplosion(event.getEntity().getLocation(),
						true,
						false,
						true,
						false,
						0.5F,
						event.getEntity().getUniqueId(),
						1,
						false,
						0);	
			}
			if(event.getHitEntity() != null) {
				Entity hit = event.getHitEntity();
				if(hit instanceof LivingEntity) {
					hit.setGlowing(true);
					hit.setFireTicks(5);
					
					Firework fw = (Firework) hit.getWorld().spawnEntity(((LivingEntity) hit).getEyeLocation().add(0.0, 1.0, 0.0), EntityType.FIREWORK);
					FireworkMeta meta = fw.getFireworkMeta();
					meta.setPower(2);
					FireworkEffect fwe = FireworkEffect.builder()
							.flicker(false)
							.trail(true)
							.with(Type.BALL_LARGE)
							.withColor(Color.RED)
							.withColor(Color.ORANGE)
							.build();
					meta.addEffect(fwe);
					fw.setFireworkMeta(meta);
					
					UUID hitID = hit.getUniqueId();
					Bukkit.getScheduler().scheduleSyncDelayedTask(GunGamePlugin.instance, new Runnable() {
						
						@Override
						public void run() {
							Entity ent = Bukkit.getEntity(hitID);
							if(ent != null && !ent.isDead()) {
								ent.setGlowing(false);
							}
						}
					}, FlareGun.visibleTime.longValue());
				}
			}
		}
	}

}
