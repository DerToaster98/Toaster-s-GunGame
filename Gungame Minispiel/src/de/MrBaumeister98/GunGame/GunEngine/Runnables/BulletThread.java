package de.MrBaumeister98.GunGame.GunEngine.Runnables;

import java.util.UUID;

import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Gun;

public class BulletThread /*extends BukkitRunnable*/ {
	
	private UUID shooterID;
	private Gun gunObject;
	private Vector vector;
	//private Location startLoc;
	private Projectile bullet;
	//private Double shootingForce;
	//private Boolean running;
	
	public BulletThread(Gun weapon, Player shooter, Vector direction) {
		this.gunObject = weapon;
		this.shooterID = shooter.getUniqueId();

		this.vector = direction.normalize().multiply(this.gunObject.getShootingForce());

		this.bullet = null;
		
		switch(this.gunObject.getBulletType()) {
		case ARROW:
			
			this.bullet = shooter.launchProjectile(Arrow.class, this.vector);

			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			((Arrow)this.bullet).setKnockbackStrength(0);
			this.bullet.setShooter(shooter);

			this.bullet.setSilent(true);
			((Arrow)this.bullet).setVelocity(this.vector);

			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			((Arrow)this.bullet).setPickupStatus(PickupStatus.DISALLOWED);
			
			break;
		case ARROW_EXPLOSIVE:
			
			this.bullet = shooter.launchProjectile(Arrow.class, this.vector);

			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			((Arrow)this.bullet).setKnockbackStrength(0);
			this.bullet.setShooter(shooter);

			this.bullet.setSilent(true);
			((Arrow)this.bullet).setVelocity(this.vector);

			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Projectile_Explode_On_Contact", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			((Arrow)this.bullet).setPickupStatus(PickupStatus.DISALLOWED);
			
			break;
		case EGG:
			
			this.bullet = shooter.launchProjectile(Egg.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			((Egg)this.bullet).setVelocity(this.vector);

			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		case ENDERPEARL:
			
			this.bullet = shooter.launchProjectile(EnderPearl.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			((EnderPearl)this.bullet).setVelocity(this.vector);
			
			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		case FIREBALL:
			
			this.bullet = shooter.launchProjectile(SmallFireball.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			
			((SmallFireball)this.bullet).setIsIncendiary(false);
			((SmallFireball)this.bullet).setDirection(this.vector);
			((SmallFireball)this.bullet).setVelocity(this.vector);
			((SmallFireball)this.bullet).setYield(this.gunObject.getShotDamage().floatValue());
			
			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		case FIREBALL_LARGE:
			
			this.bullet = shooter.launchProjectile(Fireball.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			
			((Fireball)this.bullet).setIsIncendiary(false);
			((Fireball)this.bullet).setDirection(this.vector);
			((Fireball)this.bullet).setVelocity(this.vector);
			((Fireball)this.bullet).setYield(this.gunObject.getShotDamage().floatValue());
			
			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		case SKULL:
			
			this.bullet = shooter.launchProjectile(WitherSkull.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			
			((WitherSkull)this.bullet).setIsIncendiary(false);
			((WitherSkull)this.bullet).setDirection(this.vector);
			((WitherSkull)this.bullet).setVelocity(this.vector);
			((WitherSkull)this.bullet).setCharged(false);
			((WitherSkull)this.bullet).setYield(this.gunObject.getShotDamage().floatValue());
			
			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		case SNOWBALL:
			
			this.bullet = shooter.launchProjectile(Snowball.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			
			((Snowball)this.bullet).setVelocity(this.vector);
			
			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		case DRAGON_FIREBALL:
			
			this.bullet = shooter.launchProjectile(DragonFireball.class, this.vector);
			this.bullet.setShooter(shooter);
			
			this.bullet.setInvulnerable(true);
			this.bullet.setBounce(false);
			
			this.bullet.setSilent(true);
			
			((DragonFireball)this.bullet).setIsIncendiary(false);
			((DragonFireball)this.bullet).setDirection(this.vector);
			((DragonFireball)this.bullet).setVelocity(this.vector);
			((DragonFireball)this.bullet).setYield(this.gunObject.getShotDamage().floatValue());
			
			this.bullet.setMetadata("GG_Projectile", new FixedMetadataValue(GunGamePlugin.instance, true));
			this.bullet.setMetadata("GG_Shooter", new FixedMetadataValue(GunGamePlugin.instance, this.shooterID.toString()));
			this.bullet.setMetadata("GG_OwningWeapon", new FixedMetadataValue(GunGamePlugin.instance, weapon.getGunName()));
			this.bullet.setVelocity(this.vector);
			
			break;
		default:
			break;
		
		}
		
		//this.running = true;
	}

}
