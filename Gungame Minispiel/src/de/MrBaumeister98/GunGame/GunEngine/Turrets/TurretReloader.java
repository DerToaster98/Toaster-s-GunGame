package de.MrBaumeister98.GunGame.GunEngine.Turrets;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Ammo;
import de.MrBaumeister98.GunGame.GunEngine.GunItemUtil;

public class TurretReloader {
	
	private Turret turret;
	private TurretConfig config;
	
	private Boolean running;
	private Ammo neededAmmo;
	
	public TurretReloader(Turret turret) {
		this.turret = turret;
		this.config = this.turret.config;
		this.neededAmmo = this.config.getNeededAmmo();
		
		this.running = false;
	}

	private void reload() {
		if(this.running && this.turret.getGunner() != null) {
			this.running = false;
			Integer gunnerAmmo = getPlayersAmmo(this.turret.getGunner(), this.neededAmmo);
			if(gunnerAmmo > 0) {
				
				Integer shotsPerAmmoItem = this.neededAmmo.getShotCount();
				Integer toLoad = this.config.getMagazineSize() - this.turret.getMagazine();			
				Integer ammoItemsToRemove;
				Integer modulo = toLoad % shotsPerAmmoItem;
				if(modulo != 0) {
					 ammoItemsToRemove = toLoad / shotsPerAmmoItem +1;
				} else {
					ammoItemsToRemove = toLoad / shotsPerAmmoItem;
				}
				
				if(toLoad <= gunnerAmmo) {
					//UPDATE REMAINING SHOTS
					this.turret.setMagazine(this.config.getMagazineSize());
					removeAmmoOfPlayer(this.turret.getGunner(), this.neededAmmo, ammoItemsToRemove);
				} else {
					Integer shots = 0;
		
					shots = this.turret.getMagazine() + gunnerAmmo;
					this.turret.setMagazine(shots);
		
					this.turret.getGunner().getInventory().remove(this.neededAmmo.getItem());
					removeAmmoOfPlayer(this.turret.getGunner(), this.neededAmmo);
				}
				GunGamePlugin.instance.weaponManager.visualHelper.sendTurretStatus(this.turret.getGunner(), this.turret);
				
			}
		}
	}
	private Integer getPlayersAmmo(Player p, Ammo ammo) {
		Integer c = 0;
		for(ItemStack s : p.getInventory().getContents()) {
			if(GunItemUtil.isMatchingAmmo(ammo, s)) {
				c = c + (ammo.getShotCount() * s.getAmount());
			}
		}
		return c;
	}
	private void removeAmmoOfPlayer(Player p, Ammo ammo) {
		for(int i = 0; i < p.getInventory().getContents().length; i++) {
			if(GunItemUtil.isMatchingAmmo(ammo, p.getInventory().getItem(i))) {
				p.getInventory().setItem(i, null);
			}
		}
	}
	private void removeAmmoOfPlayer(Player p, Ammo ammo, Integer count) {
		Integer slot = 0;
		while(count >0) {
			ItemStack stack = p.getInventory().getItem(slot);
			if(GunItemUtil.isMatchingAmmo(ammo, stack)) {
				if(stack.getAmount() > 1) {
					stack.setAmount(stack.getAmount() -1);
				} else {
					stack.setAmount(1);
					p.getInventory().remove(stack);
					slot = slot +1;
				}
				count = count -1;
			} else {
				slot = slot +1;
			}
		}
	}
	public void cancelReload() {
		this.running = false;
	}
	public void startReload() {
		if(GunItemUtil.hasPlayerAmmo(this.turret.getGunner(), this.neededAmmo)) {
			this.running = true;
			GunGamePlugin.instance.weaponManager.visualHelper.sendReloadingWeapon(this.turret.getGunner());
			this.config.getSoundSet().reloadSound.play(this.turret.position.getWorld(), this.turret.position);
			TurretReloader ref = this;
			Bukkit.getScheduler().runTaskLater(GunGamePlugin.instance, new Runnable() {
				
				@Override
				public void run() {
					ref.reload();
				}
			}, this.config.getReloadDuration());
		} else {
			this.running = false;
			GunGamePlugin.instance.weaponManager.visualHelper.sendMissingAmmo(this.turret.getGunner(), null, this.neededAmmo);
			this.config.getSoundSet().outOfAmmoSound.play(this.turret.position.getWorld(), this.turret.position);
		}
	}

}
