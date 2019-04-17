package de.MrBaumeister98.GunGame.Game.Util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;
import de.MrBaumeister98.GunGame.GunEngine.Griefing.EGriefType;

public class WaterbodyProtectionUtil {
	
	public static boolean canBlockBeDestroyed(Block block, World world, Material willChangeTo) {
		if(
				(
					world.hasMetadata("GG_ProtectWater") &&
					world.getMetadata("GG_ProtectWater").get(0).asBoolean() == false
				) || 
				(
					!GunGamePlugin.instance.griefHelper.isGGWorld(world) && 
					GunGamePlugin.instance.griefHelper.getGriefAllowed(EGriefType.PROTECT_WATER_BODIES, world) == false
				)
			) {
			//Water bodies are not protected --> block can be broken
			return true;
		} else {
			//Water bodies are protected, block may not be broken
			// DONE: Support einfügen, wenn block oben drauf runter fallen kann und neben oder wasser ist
			List<Block> blocksToCheck = new ArrayList<Block>();
			blocksToCheck.add(block);
			while(block.getRelative(BlockFace.UP).getType().hasGravity()) {
				block = block.getRelative(BlockFace.UP);
				blocksToCheck.add(block);
			}
			for(Block b : blocksToCheck) {
				if(isBlockFluid(b) ||  (isBlockNextToWater(b) && !isFullBlock(willChangeTo))) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean isFullBlock(Material m) {
		if(!m.equals(Material.AIR) &&
				Util.isFullBlock(m) &&
				!isSlabOrStair(m)) {
			return true;
		}
		return false;
	}
	
	private static boolean isSlabOrStair(Material m) {
		if(GunGamePlugin.instance.serverPre113) {
			if(m.equals(Material.valueOf("PURPUR_SLAB")) ||
					m.equals(Material.valueOf("WOOD_STEP")) ||
					m.equals(Material.valueOf("STONE_SLAB2")) ||
					m.equals(Material.valueOf("STEP")) ||
					m.equals(Material.valueOf("ACACIA_STAIRS")) ||
					m.equals(Material.valueOf("BIRCH_WOOD_STAIRS")) ||
					m.equals(Material.valueOf("BRICK_STAIRS")) ||
					m.equals(Material.valueOf("COBBLESTONE_STAIRS")) ||
					m.equals(Material.valueOf("DARK_OAK_STAIRS")) ||
					m.equals(Material.valueOf("JUNGLE_WOOD_STAIRS")) ||
					m.equals(Material.valueOf("NETHER_BRICK_STAIRS")) ||
					m.equals(Material.valueOf("PURPUR_STAIRS")) ||
					m.equals(Material.valueOf("QUARTZ_STAIRS")) ||
					m.equals(Material.valueOf("RED_SANDSTONE_STAIRS")) ||
					m.equals(Material.valueOf("SANDSTONE_STAIRS")) ||
					m.equals(Material.valueOf("SMOOTH_STAIRS")) ||
					m.equals(Material.valueOf("SPRUCE_WOOD_STAIRS")) ||
					m.equals(Material.valueOf("WOOD_STAIRS"))
				) {
				return true;
			}
		} else {
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
					m.equals(Material.ACACIA_STAIRS) ||
					m.equals(Material.STONE_BRICK_STAIRS) ||
					m.equals(Material.BIRCH_STAIRS) ||
					m.equals(Material.BRICK_STAIRS) ||
					m.equals(Material.COBBLESTONE_STAIRS) ||
					m.equals(Material.DARK_OAK_STAIRS) ||
					m.equals(Material.DARK_PRISMARINE_STAIRS) ||
					m.equals(Material.JUNGLE_STAIRS) ||
					m.equals(Material.NETHER_BRICK_STAIRS) ||
					m.equals(Material.OAK_STAIRS) ||
					m.equals(Material.PRISMARINE_BRICK_STAIRS) ||
					m.equals(Material.PRISMARINE_STAIRS) ||
					m.equals(Material.PURPUR_STAIRS) ||
					m.equals(Material.QUARTZ_STAIRS) ||
					m.equals(Material.RED_SANDSTONE_STAIRS) ||
					m.equals(Material.SANDSTONE_STAIRS) ||
					m.equals(Material.SPRUCE_STAIRS) ||
					m.createBlockData() instanceof org.bukkit.block.data.type.Stairs ||
					m.createBlockData() instanceof org.bukkit.block.data.type.Slab
				) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isBlockNextToWater(Block b) {
		List<Block> neighbors = new ArrayList<Block>();
		neighbors.add(b.getRelative(BlockFace.UP));
		//neighbors.add(b.getRelative(BlockFace.DOWN));
     	neighbors.add(b.getRelative(BlockFace.NORTH));
		neighbors.add(b.getRelative(BlockFace.EAST));
		neighbors.add(b.getRelative(BlockFace.SOUTH));
		neighbors.add(b.getRelative(BlockFace.WEST));
		neighbors.add(b.getRelative(BlockFace.SELF));
		
		for(Block neighbor : neighbors) {
			if(isBlockFluid(neighbor)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isBlockFluid(Block b) {
		Material m = b.getType();
		if(GunGamePlugin.instance.serverPre113) {
			if(m.equals(Material.WATER) ||
					m.equals(Material.valueOf("STATIONARY_WATER")) ||
					m.equals(Material.LAVA) ||
					m.equals(Material.valueOf("STATIONARY_LAVA"))
				) {
				return true;
			}
		} else {
			if(b.isLiquid() || 
					(
						b.getBlockData() instanceof Waterlogged &&
						((Waterlogged)b.getBlockData()).isWaterlogged()
					)
			  ) {
				return true;
			}
		}
		return false;
	}

}
