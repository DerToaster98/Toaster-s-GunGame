package de.MrBaumeister98.GunGame.GunEngine.Tanks;


import org.bukkit.Material;
import org.bukkit.block.Block;

public abstract class MoveUtil {
	
	public static Boolean isFence(Block block) {
		Material m = block.getType();
			org.bukkit.block.data.BlockData data = block.getBlockData();
			if(m.equals(Material.ACACIA_FENCE) ||
					m.equals(Material.ACACIA_FENCE_GATE) ||
					m.equals(Material.BIRCH_FENCE) ||
					m.equals(Material.BIRCH_FENCE_GATE) ||
					m.equals(Material.DARK_OAK_FENCE) ||
					m.equals(Material.DARK_OAK_FENCE_GATE) ||
					m.equals(Material.JUNGLE_FENCE) ||
					m.equals(Material.JUNGLE_FENCE_GATE) ||
					m.equals(Material.NETHER_BRICK_FENCE) ||
					m.equals(Material.OAK_FENCE) ||
					m.equals(Material.OAK_FENCE_GATE) ||
					m.equals(Material.SPRUCE_FENCE) ||
					m.equals(Material.SPRUCE_FENCE_GATE) ||
					m.equals(Material.ACACIA_DOOR) ||
					m.equals(Material.BIRCH_DOOR) ||
					m.equals(Material.SPRUCE_DOOR) ||
					m.equals(Material.OAK_DOOR) ||
					m.equals(Material.DARK_OAK_DOOR) ||
					m.equals(Material.IRON_DOOR) ||
					m.equals(Material.JUNGLE_DOOR) ||
					m.equals(Material.IRON_BARS) ||
					data instanceof org.bukkit.block.data.type.Fence || 
					data instanceof org.bukkit.block.data.type.Gate || 
					data instanceof org.bukkit.block.data.type.Door || 
					m.equals(Material.IRON_BARS)) {
				return true;
			}
		return false;
	}
	private static Boolean isTrapDoorOnFloor(Block block) {
		Material m = block.getType();
			org.bukkit.block.data.BlockData blockdata = block.getBlockData();
			if(m.equals(Material.ACACIA_TRAPDOOR)
					|| m.equals(Material.BIRCH_TRAPDOOR)
					|| m.equals(Material.DARK_OAK_TRAPDOOR)
					|| m.equals(Material.IRON_TRAPDOOR)
					|| m.equals(Material.JUNGLE_TRAPDOOR)
					|| m.equals(Material.OAK_TRAPDOOR)
					|| m.equals(Material.SPRUCE_TRAPDOOR)
			) {
				org.bukkit.block.data.type.TrapDoor trapDoorData = (org.bukkit.block.data.type.TrapDoor) blockdata;
				if(!trapDoorData.isOpen()) {
					if(trapDoorData.getHalf().equals(org.bukkit.block.data.Bisected.Half.BOTTOM)) {
						return true;
					}
				}
			}
		return false;
	}
	/*public static Boolean isStair(Block block) {
		Material m = block.getType();
		if(GunGamePlugin.instance.serverPre113) {
			
		} else {
			org.bukkit.block.data.BlockData blockdata = block.getBlockData();
			if(blockdata instanceof org.bukkit.block.data.type.Stairs) {
				
			}
		}
	}*/
	public static Boolean isSlab(Block block) {
		Material m = block.getType();
			org.bukkit.block.data.BlockData blockdata = block.getBlockData();
			if(m.equals(Material.ACACIA_SLAB) ||
					m.equals(Material.BIRCH_SLAB) ||
					m.equals(Material.BRICK_SLAB) ||
					m.equals(Material.COBBLESTONE_SLAB) ||
					m.equals(Material.DARK_OAK_SLAB) ||
					m.equals(Material.DARK_PRISMARINE_SLAB) ||
					m.equals(Material.JUNGLE_SLAB) ||
					m.equals(Material.NETHER_BRICK_SLAB) ||
					m.equals(Material.OAK_SLAB) ||
					m.equals(Material.PETRIFIED_OAK_SLAB) ||
					m.equals(Material.PRISMARINE_BRICK_SLAB) ||
					m.equals(Material.PRISMARINE_SLAB) ||
					m.equals(Material.PURPUR_SLAB) ||
					m.equals(Material.QUARTZ_SLAB) ||
					m.equals(Material.RED_SANDSTONE_SLAB) ||
					m.equals(Material.SANDSTONE_SLAB) ||
					m.equals(Material.SPRUCE_SLAB) ||
					m.equals(Material.STONE_BRICK_SLAB) ||
					m.equals(Material.STONE_SLAB) ||
					isTrapDoorOnFloor(block)
					|| blockdata instanceof org.bukkit.block.data.type.Slab) {
				if(isTrapDoorOnFloor(block)) {
					org.bukkit.block.data.type.TrapDoor slabdata = (org.bukkit.block.data.type.TrapDoor)blockdata;
					if(!slabdata.isOpen() && slabdata.getHalf().equals(org.bukkit.block.data.Bisected.Half.BOTTOM)) {
						return true;
					}
				} else {
					org.bukkit.block.data.type.Slab slabdata = (org.bukkit.block.data.type.Slab)blockdata;
					if(slabdata.getType().equals(org.bukkit.block.data.type.Slab.Type.BOTTOM)) {
						return true;
					}
				}
			}
		return false;
	}
	public static Boolean isPassable(Block block) {
		Material m = block.getType();
			if((!m.isSolid() &&
					!m.toString().equals("END_ROD") && 
					!m.toString().equals("CHORUS_PLANT") && 
					!m.toString().equals("CHORUS_FLOWER") &&
					!m.toString().equals("SKULL")) //&&
					//!m.toString().equals("LADDER"))
					|| m.equals(Material.WATER)
					|| m.equals(Material.ACACIA_PRESSURE_PLATE)
					|| m.equals(Material.BIRCH_PRESSURE_PLATE)
					|| m.equals(Material.DARK_OAK_PRESSURE_PLATE)
					|| m.equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE)
					|| m.equals(Material.JUNGLE_PRESSURE_PLATE)
					|| m.equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE)
					|| m.equals(Material.OAK_PRESSURE_PLATE)
					|| m.equals(Material.SPRUCE_PRESSURE_PLATE)
					|| m.equals(Material.STONE_PRESSURE_PLATE)
				) {
				return true;
			}
		return false;
	}

}
