package de.MrBaumeister98.GunGame.GunEngine.Tanks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.Game.Core.Debugger.Debugger;
import de.MrBaumeister98.GunGame.Game.Util.LangUtil;
import de.MrBaumeister98.GunGame.Game.Util.Util;
import de.MrBaumeister98.GunGame.Game.Util.Math.VectorUtil;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;
import net.md_5.bungee.api.ChatColor;

public class TankMover /*extends Thread*/ {
	
	private Tank tank;
	private TankConfig config;
	
	private Boolean W_pressed;
	private Boolean S_pressed;
	
	//private ArmorStand entity;
	
	private Boolean running;
	
	private Double currentSpeed;

	private Boolean rightTrackPowered = false;
	private Boolean leftTrackPowered = false;
	
	private Boolean sittingOnSlab = false;
	
	private float turnAngle;
	private int taskID;
	
	public TankMover(Tank tank) {
		this.tank = tank;
		this.config = tank.getConfig();
		//this.getEntity() = this.tank.getBodyArmorStand();
		
		this.currentSpeed = 0.0;
		this.W_pressed = false;
		this.S_pressed = false;
		this.rightTrackPowered = false;
		this.leftTrackPowered = false;
		
		this.turnAngle = this.tank.getBodyArmorStand().getEyeLocation().getYaw();
		
		this.running = true;
	}
	public void stopRun() {
		this.running = false;
		Bukkit.getScheduler().cancelTask(this.taskID);
	}
	public double getCurrentSpeed() {
		return this.currentSpeed;
	}
	public float getTurnAngle() {
		return this.turnAngle;
	}
	private ArmorStand getEntity() {
		return this.tank.getBodyArmorStand();
	}
	
	//@Override
	public void /*run()*/execute() {
		/*while*/if (this.running && this.tank.isAlive() && this.getEntity() != null/*&& this.tank.getTankPos().getChunk().isLoaded()*/) {
			if(this.tank.getDriverUUID() == null || Bukkit.getEntity(this.tank.getDriverUUID()) == null) {
				this.S_pressed = false;
				this.W_pressed = false;
				this.leftTrackPowered = false;
				this.rightTrackPowered = false;
				
				if(this.tank.getSoundset() != null) {
					this.tank.getSoundset().driveSound.stop();
					this.tank.getSoundset().engineIdleSound.play(this.tank.getWorld(), this.tank.getTankPos());
					//this.tank.getSoundset().brakingSound.play(this.tank.getWorld(), this.tank.getTankPos());
				}
			}
			if(this.getEntity().getLocation().getBlock().getType().equals(Material.GRASS_PATH) || this.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.GRASS_PATH)) {
				this.tank.getBodyArmorStand().teleport(this.tank.getTankPos().add(0.0, 0.15, 0.0));
			}
			try {
				//MOVEMENT CHECKS				
				//Bukkit.broadcastMessage(this.canMove().toString());
				Double vY = 0.0;
				switch(this.canMove()) {
				case MOVE_DOWN_BLOCK:
					vY = -0.75D;
					break;
				case MOVE_DOWN_SLAB:
					vY = -0.5D;
					break;
				case MOVE_UP_BLOCK:
					vY = 0.75D;
					break;
				case MOVE_UP_SLAB:
					vY = 0.5D;
					break;
				case NOT_PASSABLE:
					this.S_pressed = false;
					this.W_pressed = false;
					this.leftTrackPowered = false;
					this.rightTrackPowered = false;
					this.currentSpeed = 0.0;
					vY = 0.0;
					break;
				case PASS:
					vY = 0.0;
					break;
				default:
					break;
				}
				//Roll out speed
				if((this.W_pressed && this.S_pressed) || (!this.W_pressed && !this.S_pressed) /*|| !this.rightTrackState.equals(this.leftTrackState)*/) {
					if(
							(
									(this.W_pressed || this.S_pressed) && 
									!(
											(this.W_pressed && this.S_pressed) || 
											(!this.W_pressed && !this.S_pressed)
									)
							) 
						) 
					{
						//Move in right direction and accelerate or brake
					} else {
						if(this.currentSpeed != 0.0D) {
							if(this.tank.getSoundset() != null) {
								this.tank.getSoundset().driveSound.play(this.tank.getWorld(), this.tank.getTankPos());
								this.tank.getSoundset().engineIdleSound.stop();
								//this.tank.getSoundset().brakingSound.play(this.tank.getWorld(), this.tank.getTankPos());
							}
						}
						if(this.currentSpeed == 0.0D) {
							if(this.tank.getSoundset() != null) {
								this.tank.getSoundset().driveSound.stop();
								this.tank.getSoundset().engineIdleSound.play(this.tank.getWorld(), this.tank.getTankPos());
							}
						}
						if(this.currentSpeed < 0) {
							if(this.currentSpeed + this.config.getSpeedUpPerTick() >= 0) {
								this.currentSpeed = 0.0;
								this.tank.setDriving(false);
							} else {
								this.currentSpeed = this.currentSpeed + this.config.getSpeedUpPerTick();
							}
						}
						if(this.currentSpeed > 0) {
							if(this.currentSpeed - this.config.getSpeedUpPerTick() <= 0) {
								this.currentSpeed = 0.0;
								this.tank.setDriving(false);
							} else {
								this.currentSpeed = this.currentSpeed - this.config.getSpeedUpPerTick();
							}
						}
					}
				} else {
					if(this.W_pressed && (this.currentSpeed + this.config.getSpeedUpPerTick()) <= this.config.getMaxSpeed()) {
						this.currentSpeed = this.currentSpeed + (this.config.getSpeedUpPerTick() * 2.0);
					}
					if(this.S_pressed && (this.currentSpeed - this.config.getSpeedUpPerTick()) >= this.config.getMaxSpeedReverse()) {
						this.currentSpeed = this.currentSpeed - (this.config.getSpeedUpPerTick() * 2.0);
					}
				}
				//INIT MOVEMENT
				if(this.currentSpeed > 0 || this.currentSpeed < 0 || this.turnOnPlace()) {
					this.tank.setDriving(true);
					if(this.tank.getSoundset() != null) {
						this.tank.getSoundset().engineIdleSound.stop();
						this.tank.getSoundset().driveSound.play(this.tank.getWorld(), this.tank.getTankPos());
					}
					float angle = (float) 0.0;
					if(this.currentSpeed > 0 || this.currentSpeed < 0) {
						switch(this.getTurnAction()) {
						case NONE:
							Vector v = this.getEntity().getLocation().getDirection();
							v = v.normalize();
							v = v.multiply(this.currentSpeed);
							v = VectorUtil.rotateVectorAroundY(v, this.turnAngle);
							v.setY(vY);
							
							this.getEntity().setVelocity(v);
							break;
						case ROTATE:
							switch(this.getRotateAction()) {
							case ROTATE_LEFT:
								Location tmpLoc1 = this.getEntity().getLocation().clone();
								tmpLoc1.setYaw(this.getEntity().getLocation().getYaw() - this.config.getTurnAnglePerTick());
								this.getEntity().teleport(tmpLoc1);
								Vector v1 = this.getEntity().getLocation().getDirection();
								this.turnAngle = this.turnAngle - this.config.getTurnAnglePerTick();
								angle = angle - this.config.getTurnAnglePerTick();
								v1 = VectorUtil.rotateVectorAroundY(v1, 360.0D + this.turnAngle);
								v1 = v1.normalize();
								v1 = v1.multiply(this.currentSpeed * 0.5D);
								v1.setY(vY);
								
								this.getEntity().setVelocity(v1);
								break;
							case ROTATE_RIGHT:
								Location tmpLoc2 = this.getEntity().getLocation().clone();
								tmpLoc2.setYaw(this.getEntity().getLocation().getYaw() + this.config.getTurnAnglePerTick());
								this.getEntity().teleport(tmpLoc2);
								Vector v2 = this.getEntity().getLocation().getDirection();
								this.turnAngle = this.turnAngle + this.config.getTurnAnglePerTick();
								angle = angle + this.config.getTurnAnglePerTick();
								v2 = VectorUtil.rotateVectorAroundY(v2, this.turnAngle);
								v2 = v2.normalize();
								v2 = v2.multiply(this.currentSpeed * 0.5D);
								v2.setY(vY);
								
								this.getEntity().setVelocity(v2);
								break;
							default:
								break;							
							}
							break;
						case TURN_LEFT:
							Location tmpLoc1 = this.getEntity().getLocation().clone();
							tmpLoc1.setYaw(this.getEntity().getLocation().getYaw() - this.config.getTurnAnglePerTick());
							this.getEntity().teleport(tmpLoc1);
							Vector v1 = this.getEntity().getLocation().getDirection();
							this.turnAngle = this.turnAngle - this.config.getTurnAnglePerTick();
							angle = angle - this.config.getTurnAnglePerTick();
							v1 = VectorUtil.rotateVectorAroundY(v1, 360.0D + this.turnAngle);
							v1 = v1.normalize();
							v1 = v1.multiply(this.currentSpeed);
							v1.setY(vY);
							
							this.getEntity().setVelocity(v1);
							break;
						case TURN_RIGHT:
							Location tmpLoc2 = this.getEntity().getLocation().clone();
							tmpLoc2.setYaw(this.getEntity().getLocation().getYaw() + this.config.getTurnAnglePerTick());
							this.getEntity().teleport(tmpLoc2);							Vector v2 = this.getEntity().getLocation().getDirection();
							this.turnAngle = this.turnAngle + this.config.getTurnAnglePerTick();
							angle = angle + this.config.getTurnAnglePerTick();
							v2 = VectorUtil.rotateVectorAroundY(v2, this.turnAngle);
							v2 = v2.normalize();
							v2 = v2.multiply(this.currentSpeed);
							v2.setY(vY);
							
							this.getEntity().setVelocity(v2);
							break;
						default:
							break;					
						}
					} else if(this.turnOnPlace()) {
						switch(this.getRotateAction()) {
						case ROTATE_LEFT:
							Location tmpLoc1 = this.getEntity().getLocation().clone();
							this.turnAngle = this.turnAngle - this.config.getTurnAnglePerTick();
							angle = angle - this.config.getTurnAnglePerTick();
							tmpLoc1.setYaw(this.turnAngle);
							
							this.getEntity().teleport(tmpLoc1);
							break;
						case ROTATE_RIGHT:
							Location tmpLoc2 = this.getEntity().getLocation().clone();
							this.turnAngle = this.turnAngle + this.config.getTurnAnglePerTick();
							angle = angle + this.config.getTurnAnglePerTick();
							tmpLoc2.setYaw(this.turnAngle);
							
							this.getEntity().teleport(tmpLoc2);
							break;
						default:
							break;
						
						}
					}
					this.tank.adjustHeads(this.turnAngle);
				}
				
				if(this.tank.getDriverUUID() != null) {
					Entity driver = Bukkit.getEntity(this.tank.getDriverUUID());
					if(driver != null) {
						this.tank.adjustTurret(driver.getLocation().getYaw(), driver.getLocation().getPitch());
					}
				}
				if(MoveUtil.isSlab(this.tank.getTankPos().getBlock())) {
					this.sittingOnSlab = true;
				} else {
					this.sittingOnSlab = false;
				}
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			
			if(this.tank.getDriverUUID() != null && Bukkit.getPlayer(this.tank.getDriverUUID()) != null && Bukkit.getPlayer(this.tank.getDriverUUID()).isOnline()) {
				GunGamePlugin.instance.weaponManager.visualHelper.sendTankStatus(Bukkit.getPlayer(this.tank.getDriverUUID()), this.tank);
			}
			
			/*try {
				sleep(50);
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}*/
		} else if(!this.tank.isAlive() || !this.running || this.getEntity() == null) {
			this.stopRun();
		}
	}
	public void start() {
		//TankMover ref = this;
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(GunGamePlugin.instance, new Runnable() {
			
			@Override
			public void run() {
				execute();
			}
		}, 0, 1);
	}
	
	private Boolean turnOnPlace() {
		if(this.rightTrackPowered && !this.leftTrackPowered && !(this.S_pressed || this.W_pressed)) {
			return true;
		}if(!this.rightTrackPowered && this.leftTrackPowered && !(this.S_pressed || this.W_pressed)) {
			return true;
		}
		return false;
	}
	
	private TurnAction getTurnAction() {
		if(this.rightTrackPowered && !this.leftTrackPowered && (this.S_pressed || this.W_pressed && !(this.S_pressed && this.W_pressed))) {
			if(this.W_pressed && !this.S_pressed) {
				return TurnAction.TURN_RIGHT;
			} else if(this.S_pressed && !this.W_pressed) {
				return TurnAction.TURN_LEFT;
			}
		}
		if(!this.rightTrackPowered && this.leftTrackPowered && (this.S_pressed || this.W_pressed && !(this.S_pressed && this.W_pressed))) {
			if(this.W_pressed && !this.S_pressed) {
				return TurnAction.TURN_LEFT;
			} else if(this.S_pressed && !this.W_pressed) {
				return TurnAction.TURN_RIGHT;
			}
		}
		if(this.turnOnPlace()) {
			return TurnAction.ROTATE;
		}
		return TurnAction.NONE;
	}
	
	private RotateAction getRotateAction() {
		if(this.rightTrackPowered && !this.leftTrackPowered) {
			return RotateAction.ROTATE_RIGHT;
		}
		if(this.leftTrackPowered && !this.rightTrackPowered) {
			return RotateAction.ROTATE_LEFT;
		}
		return RotateAction.NONE;
	}
	
	public void setMoveMode(Boolean w, Boolean s) {
		this.W_pressed = w;
		this.S_pressed = s;
	}
	//NOTE: All sides are calculating fine!
	private EMoveAction canMove() {
		EMoveAction action = EMoveAction.NOT_PASSABLE;
		Vector v = this.getEntity().getLocation().getDirection();	
		switch(this.getTurnAction()) {
		case NONE:
			v = VectorUtil.rotateVectorAroundY(v, this.turnAngle);
			v = v.normalize().multiply(this.config.getLengthRadius() + 0.0625);
			break;
		case ROTATE:
			switch(this.getRotateAction()) {
			case NONE:
				break;
			case ROTATE_LEFT:
				v = VectorUtil.rotateVectorAroundY(v, this.turnAngle - this.config.getTurnAnglePerTick());
				v = v.normalize().multiply(this.config.getLengthRadius());
				break;
			case ROTATE_RIGHT:
				v = VectorUtil.rotateVectorAroundY(v, this.turnAngle + this.config.getTurnAnglePerTick());
				v = v.normalize().multiply(this.config.getLengthRadius());
				break;
			default:
				break;
			
			}
			break;
		case TURN_LEFT:
			v = VectorUtil.rotateVectorAroundY(v, this.turnAngle - this.config.getTurnAnglePerTick());
			v = v.normalize().multiply(this.config.getLengthRadius() + 0.0625);
			break;
		case TURN_RIGHT:
			v = VectorUtil.rotateVectorAroundY(v, this.turnAngle + this.config.getTurnAnglePerTick());
			v = v.normalize().multiply(this.config.getLengthRadius() + 0.0625);
			break;
		default:
			break;
		}
		if(!this.getTurnAction().equals(TurnAction.ROTATE) && this.S_pressed) {
			v = v.multiply(-1.0);
		}	
		Location midFrontEnd = this.tank.getTankPos().clone().add(v);

		Vector vRight = VectorUtil.rotateVectorAroundY(v, 90.0);
		Vector vLeft = VectorUtil.rotateVectorAroundY(v, 270.0);
		
		/*Bukkit.broadcastMessage(Util.vecToString(v));
		Bukkit.broadcastMessage(Util.vecToString(vRight));
		Bukkit.broadcastMessage(Util.vecToString(vLeft));*/
		
		if(this.getTurnAction().equals(TurnAction.NONE)) {
			Block middle = midFrontEnd.getBlock();
			
			vRight = vRight.normalize().multiply(this.config.getWidthRadius() + 0.0625);
			vLeft = vLeft.normalize().multiply(this.config.getWidthRadius() + 0.0625);
			
			Block right = midFrontEnd.clone()/*.add(v)*/.add(vRight).getBlock();
			Block left = midFrontEnd.clone()/*.subtract(v)*/.add(vLeft).getBlock();

			action = getMoveAction(new Block[] {middle, right, left});
		} 
		else if(this.getTurnAction().equals(TurnAction.ROTATE)) {
			switch(this.getRotateAction()) {
			case ROTATE_LEFT:
				vRight = vRight.normalize().multiply(this.config.getWidthRadius());
				vLeft = vLeft.normalize().multiply(this.config.getWidthRadius());
				Block FrontRight = midFrontEnd.clone()/*.add(v)*/.add(vRight).getBlock();
				Block FrontLeft = midFrontEnd.clone()/*.add(v)*/.add(vLeft).getBlock();
				
				Location midAftEnd = midFrontEnd.clone().subtract(v);

				Block AftRight = midAftEnd.clone()/*.add(v)*/.add(vRight).getBlock();
				Block AftLeft = midAftEnd.clone()/*.subtract(v)*/.add(vLeft).getBlock();
				
				action = getMoveAction(new Block[] {FrontRight, FrontLeft, AftRight, AftLeft});
				if(action.equals(EMoveAction.PASS) || action.equals(EMoveAction.MOVE_DOWN_BLOCK) || action.equals(EMoveAction.MOVE_DOWN_SLAB)) {
					action = EMoveAction.PASS;
				} else {
					action = EMoveAction.NOT_PASSABLE;
				}
				break;
			case ROTATE_RIGHT:
				vRight = vRight.normalize().multiply(this.config.getWidthRadius());
				vLeft = vLeft.normalize().multiply(this.config.getWidthRadius());
				Block FrontRight1 = midFrontEnd.clone().add(vRight).getBlock();
				Block FrontLeft1 = midFrontEnd.clone().add(vLeft).getBlock();
				
				Location midAftEnd1 = this.tank.getTankPos().clone().subtract(v);

				Block AftRight1 = midAftEnd1.clone().add(vRight).getBlock();
				Block AftLeft1 = midAftEnd1.clone().add(vLeft).getBlock();
				
				action = getMoveAction(new Block[] {FrontRight1, FrontLeft1, AftRight1, AftLeft1});
				if(action.equals(EMoveAction.PASS) || action.equals(EMoveAction.MOVE_DOWN_BLOCK) || action.equals(EMoveAction.MOVE_DOWN_SLAB)) {
					action = EMoveAction.PASS;
				} else {
					action = EMoveAction.NOT_PASSABLE;
				}
				break;
			default:
				break;			
			}
		}
		else {
			switch(this.getTurnAction()) {
			case TURN_LEFT:
				Block middle = midFrontEnd.getBlock();
				vRight = vRight.normalize().multiply(this.config.getWidthRadius() + 0.0625);
				vLeft = vLeft.normalize().multiply(this.config.getWidthRadius() + 0.0625);
				Block right = midFrontEnd.clone().add(vRight).getBlock();
				Block left = midFrontEnd.clone().add(vLeft).getBlock();
				
				action = getMoveAction(new Block[] {middle, right, left});
				break;
			case TURN_RIGHT:
				Block middle1 = midFrontEnd.getBlock();
				vRight = vRight.normalize().multiply(this.config.getWidthRadius() + 0.0625);
				vLeft = vLeft.normalize().multiply(this.config.getWidthRadius() + 0.0625);
				Block right1 = midFrontEnd.clone().add(vRight).getBlock();
				Block left1 = midFrontEnd.clone().add(vLeft).getBlock();
				
				action = getMoveAction(new Block[] {middle1, right1, left1});
				break;
			default:
				break;			
			}
		}
		return action;
	}
	private void dealWithFences(Block[] points) {
		try {
			if(GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.PHYSIC_ENGINE, (points[0].getWorld()))) {
				List<Block> blocks = new ArrayList<Block>();
				for(Block b : points) {
					blocks.add(b);
					blocks.add(b.getRelative(BlockFace.UP));
					blocks.add(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP));
				}
				for(Block b : blocks) {
					if(MoveUtil.isFence(b)) {
						b.getWorld().playSound(b.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0F, 1.0F);
						b.breakNaturally();
						b.setType(Material.AIR);
					}
					if(Util.isGlassPane(b.getType())) {
						b.getWorld().playSound(b.getLocation(), Sound.BLOCK_GLASS_BREAK, 8.0F, 0.8F);
						b.breakNaturally();
						b.setType(Material.AIR);
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			Debugger.logInfoWithColoredText(LangUtil.prefixErr + ChatColor.YELLOW + "Unknown world! World has no GunEngine-GriefManager!");
		}
	}
	private EMoveAction getMoveAction(Block[] checkpoints) {
		
		dealWithFences(checkpoints);
		
		Boolean passable = true;
		Boolean isSlab = true;
		Boolean isBlock = false;
		Boolean isFalling = false;
		//String ttmp = "";
		for(Block b : checkpoints) {
			if((MoveUtil.isPassable(b) || MoveUtil.isSlab(b) || (!MoveUtil.isPassable(b) && this.sittingOnSlab) ||(!MoveUtil.isPassable(b) && this.tank.getTankPos().getBlock().getType().equals(Material.GRASS_PATH))) && MoveUtil.isPassable(b.getRelative(BlockFace.UP)) && MoveUtil.isPassable(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP))) {
				if(!MoveUtil.isPassable(b) && this.sittingOnSlab && !MoveUtil.isSlab(b)) {
					return EMoveAction.MOVE_UP_SLAB;
				}
				if(!this.sittingOnSlab && !MoveUtil.isPassable(b) && !MoveUtil.isSlab(b) && this.tank.getTankPos().getBlock().getType().equals(Material.GRASS_PATH)) {
					return EMoveAction.MOVE_UP_SLAB;
				}
			} else {
				passable = false;
			}
			if(!MoveUtil.isPassable(b) && MoveUtil.isPassable(b.getRelative(BlockFace.UP)) && MoveUtil.isPassable(b.getRelative(BlockFace.UP).getRelative(BlockFace.UP))) {
				passable = true;
				isBlock = true;
				isSlab = false;
			}
			//ttmp = ttmp + b.getType().toString() + ", ";
		}
		
		if(passable) {
			
			if(this.tank.getTankPos().getBlock().getType().equals(Material.AIR) || this.tank.getTankPos().getBlock().getType().equals(Material.WATER)) {
				if(this.tank.getTankPos().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR) || this.tank.getTankPos().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.WATER)) {
					isFalling = true;
				}
			}
			//String tmp = "";
			for(Block b : checkpoints) {
				if(!MoveUtil.isSlab(b)) {
					isSlab = false;
				} else if (!MoveUtil.isPassable(b) && !MoveUtil.isSlab(b)) {
					isBlock = true;
				}
				//tmp = tmp + b.getType().toString() + ", ";
			}
			if(isSlab) {
				//Bukkit.broadcastMessage(passable.toString() + "   slab: " + isSlab.toString() + "    block: " + isBlock.toString() + "    mat: " + this.getEntity().getLocation().getBlock().getType().toString() + "    mats: " + tmp);
				if(this.sittingOnSlab) {
					if(this.tank.getTankPos().getBlock().getType().equals(Material.AIR) || this.tank.getTankPos().getBlock().getType().equals(Material.WATER)) {
						return EMoveAction.MOVE_DOWN_SLAB;
					} else {
						return EMoveAction.PASS;
					}
				}
				return EMoveAction.MOVE_UP_SLAB;
			} else if(isBlock) {
				//Bukkit.broadcastMessage(passable.toString() + "   slab: " + isSlab.toString() + "    block: " + isBlock.toString() + "    mat: " + this.getEntity().getLocation().getBlock().getType().toString() + "    mats: " + tmp);
				if(this.sittingOnSlab) {
					return EMoveAction.MOVE_UP_SLAB;
				}
				if(this.tank.getTankPos().getBlock().getType().equals(Material.GRASS_PATH)) {
					return EMoveAction.MOVE_UP_SLAB;
				}
				//if(!this.tank.getTankPos().getBlock().getType().equals(Material.WATER)) {
					return EMoveAction.MOVE_UP_BLOCK;
				//}
			} else if(isFalling) {
				return EMoveAction.MOVE_DOWN_BLOCK;
			}
			else {
				Integer isSlabTrue = 0;
				Integer isAirTrue = 0;
				//String tmp1 = "";
				for(Block b : checkpoints) {
					if(MoveUtil.isSlab(b.getRelative(BlockFace.DOWN))) {
						//isSlab = false;
						isSlabTrue++;
					} else if (MoveUtil.isPassable(b.getRelative(BlockFace.DOWN))) {
						//isBlock = true;
						isAirTrue++;
					}
					//tmp1 = tmp1 + b.getType().toString() + ", ";
				}
				//Bukkit.broadcastMessage(passable.toString() + "   slab: " + isSlab.toString() + "    block: " + isBlock.toString() + "    mat: " + this.getEntity().getLocation().getBlock().getType().toString() + "    mats: " + tmp1);
				if(this.sittingOnSlab && isAirTrue > 0 && isAirTrue > (checkpoints.length / 2)) {
					return EMoveAction.MOVE_DOWN_SLAB;
				}
				if(isSlabTrue > 0 && isSlabTrue >= (checkpoints.length / 2)) {
					return EMoveAction.MOVE_DOWN_SLAB;
				}
				if(isAirTrue > 0 && isAirTrue > (checkpoints.length / 2)) {
					if(this.sittingOnSlab) {
						return EMoveAction.MOVE_DOWN_SLAB;
					}
					return EMoveAction.MOVE_DOWN_BLOCK;
				}
				Boolean sittingOSlab = false;
				Double tmpD = this.tank.getTankPos().getY();
				if(tmpD != tmpD.intValue()) {
					sittingOSlab = true;
				}
				if(this.tank.getTankPos().getBlock().getType().equals(Material.AIR) && sittingOSlab) {
					return EMoveAction.MOVE_DOWN_SLAB;
				}
				return EMoveAction.PASS;
				
			}
			
		}
		//Bukkit.broadcastMessage(passable.toString() + "   slab: " + false + "    block: " + false + "    mat: " + this.getEntity().getLocation().getBlock().getType().toString() + "    mats: " + ttmp);
		
		return EMoveAction.NOT_PASSABLE;
	}

	public void setTrackStates(Boolean left, Boolean right) {
		this.leftTrackPowered = left;
		this.rightTrackPowered = right;
	}

	public enum TurnAction {
		NONE,
		TURN_LEFT,
		TURN_RIGHT,
		ROTATE;
	}
	public enum RotateAction {
		ROTATE_RIGHT,
		ROTATE_LEFT,
		NONE;
	}
	public void setTurnAngle(float angle) {
		this.turnAngle = angle;
	}
	
}
