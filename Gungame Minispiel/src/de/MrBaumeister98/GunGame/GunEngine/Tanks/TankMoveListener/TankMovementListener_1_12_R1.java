package de.MrBaumeister98.GunGame.GunEngine.Tanks.TankMoveListener;

import java.util.UUID;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Tanks.Tank;
import net.minecraft.server.v1_12_R1.PacketPlayInSteerVehicle;

public class TankMovementListener_1_12_R1 extends PacketAdapter {
	
	public TankMovementListener_1_12_R1(Plugin plugin, ListenerPriority priority, PacketType[] types) {
		super(plugin, priority, types);
	}
	
	@Override
	public void onPacketReceiving(PacketEvent event) {
		if(event.getPacketType().equals(PacketType.Play.Client.STEER_VEHICLE) && event.getPlayer() != null && event.getPlayer().getVehicle() != null && event.getPlayer().getVehicle().hasMetadata("GG_Tank")) {
			
			/** 1.13 Values
			 * 
			 * a = 
			 * 
			 * b > 0 = left
			 * 
			 * b < 0 = right
			 * 
			 * c > 0 = forward
			 * 
			 * c < 0 = backwards
			 * 
			 * d = space
			 * 
			 * e = dismount
			 * 
			 */
			
			
			
			PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle)event.getPacket().getHandle();
			/*if(packet.a()) {
				//event.getPlayer().sendMessage("a");
			}*/
			
			//TANK ENTITY
			UUID tankID = UUID.fromString(event.getPlayer().getVehicle().getMetadata("GG_Tank").get(0).asString());
			Tank tank = GunGamePlugin.instance.tankManager.getTankByID(tankID);
			
			if(tank != null && tank.isAlive()) {
				Boolean forward = false;
				Boolean backwards = false;
				Boolean right = false;
				Boolean left = false;
				Boolean space = false;
				
				/*if(packet.a()) {
					//event.getPlayer().sendMessage("a");
				}*/
				if(packet.c()) {
					//event.getPlayer().sendMessage("d");
					space = true;
				}
				if(packet.d()) {
					//event.getPlayer().sendMessage("e");
					tank.disMount(event.getPlayer());
				}
				/*if(packet.b() == (float)0) {
					right = true;
					left = true;
				}
				else */if(packet.a() < 0) {
					//event.getPlayer().sendMessage("b < 0");
					right = true;
				}
				else if(packet.a() > 0) {
					//event.getPlayer().sendMessage("b > 0");
					left = true;
				}
				/*if(packet.c() == (float) 0) {
					backwards = true;
					forward = true;
				}
				else */if(packet.b() < 0) {
					//event.getPlayer().sendMessage("c < 0");
					backwards = true;
				}
				else if(packet.b() > 0) {
					//event.getPlayer().sendMessage("c > 0");
					forward = true;
				}
				tank.transmitMovementParameters(forward, backwards, left, right);
				if(space) {
					tank.shoot();
				}
				
				//tank.move(true, true, true, false, EMoveDirection.NONE);
			}
		}
	}

}
