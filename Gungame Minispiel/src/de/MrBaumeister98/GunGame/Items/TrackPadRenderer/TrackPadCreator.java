package de.MrBaumeister98.GunGame.Items.TrackPadRenderer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import de.MrBaumeister98.GunGame.Game.Core.GunGamePlugin;

public class TrackPadCreator {
	
	private Location startLoc;
	private UUID callerID;
	private List<PixelPos> acquiredLocs;
	private OverviewImageRenderer imageRender;
	
	protected short getMapID;
	private MapView mapView;
	private MapCanvas mapCanvas;
	private TrackPadRenderer mapRenderer;
	
	public static short renderMapView(Location center, UUID requester) {
		TrackPadCreator tpc = new TrackPadCreator(center, requester);
		tpc.acquireLocs();
		tpc.render();
		tpc.acquireTargets();
		return tpc.getMapID;
	}
	
	public TrackPadCreator(Location start, UUID callerID) {
		this.startLoc = start;
		this.callerID = callerID;
		this.acquiredLocs = new ArrayList<PixelPos>();
		
		this.mapView = Bukkit.createMap(start.getWorld());
		for(MapRenderer mr : this.mapView.getRenderers()) {
			this.mapView.removeRenderer(mr);
		}
	}
	
	public void acquireLocs() {
		for(int iX = 0; iX < 128; iX++) {
			for(int iZ = 0; iZ < 128; iZ++) {
				int y = 255;
				for(int iY = 255; iY > 0; iY--) {
					Block block = startLoc.getWorld().getBlockAt(this.startLoc.getBlockX() - 64 + iX, iY, this.startLoc.getBlockZ() - 64 + iZ);
					if(block != null) {
						Material matTmp = block.getType();
						if(GunGamePlugin.instance.serverPre113) {
							if(!(matTmp.equals(Material.valueOf("AIR")) ||
									matTmp.equals(Material.valueOf("STRING")) ||
									matTmp.equals(Material.valueOf("TRIPWIRE")) ||
									matTmp.equals(Material.valueOf("BARRIER"))
									)) {
								y = iY;
								iY = 0;
							}
						} else {
							if(!(matTmp.equals(Material.AIR) ||
									matTmp.equals(Material.STRING) ||
									matTmp.equals(Material.TRIPWIRE) ||
									matTmp.equals(Material.BARRIER))) {
								y = iY;
								iY = 0;
							}
						}
					} else {
					}
				}
				PixelPos tmpPos = new PixelPos(iX, y, iZ);
				this.acquiredLocs.add(tmpPos);
			}
		}
	}
	
	public void render() {
		this.imageRender = new OverviewImageRenderer(this.acquiredLocs);
		this.imageRender.renderImage();
		File folder = new File(GunGamePlugin.instance.getDataFolder() + "/trackmaprenderoutput/");
		if(!folder.exists()) {
			folder.mkdirs();
		}
		File file = new File(folder.getAbsolutePath() + "/render_X" + startLoc.getBlockX() + "-Z" + startLoc.getBlockZ() + ".png");
		try {
			ImageIO.write(this.imageRender.getRenderedImage(), "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void acquireTargets() {
		List<Location> playerMarks = new ArrayList<Location>();
		for(Entity lEnt : this.startLoc.getWorld().getNearbyEntities(this.startLoc, 64, 255, 64)) {
			if(lEnt instanceof LivingEntity) {
				if(lEnt instanceof Player) {
					if(!(lEnt.getUniqueId().equals(this.callerID))) {
						if(insideRange(lEnt.getLocation())) {
							playerMarks.add(lEnt.getLocation());
						}
					}
				}
			}
		}
		MapCursorCollection cursors = new MapCursorCollection();
		if(!playerMarks.isEmpty() && this.mapCanvas != null) {
			for(Location target : playerMarks) {
				if(GunGamePlugin.instance.serverPre113) {
					MapCursor cursorTmp = new MapCursor((byte)(target.getBlockX() - this.startLoc.getBlockX()), (byte)(target.getBlockZ() - this.startLoc.getBlockZ()), (byte)0, MapCursor.Type.RED_MARKER.getValue(), true);
					cursors.addCursor(cursorTmp);
				} else {
					MapCursor cursorTmp = new MapCursor((byte)(target.getBlockX() - this.startLoc.getBlockX()), (byte)(target.getBlockZ() - this.startLoc.getBlockZ()), (byte)0, MapCursor.Type.RED_MARKER, true);
					cursors.addCursor(cursorTmp);
				}
			}
		}
		this.mapRenderer = new TrackPadRenderer(this.imageRender, cursors);
		this.mapView.addRenderer(this.mapRenderer);
		Bukkit.getPlayer(this.callerID).sendMap(this.mapView);
		/**ItemStack map;
		if(GunGamePlugin.instance.serverPre113) {
			map = new ItemStack(Material.valueOf("MAP"), 1, (short) (this.mapView.getId() -1));
			map.setDurability(this.mapView.getId());
		} else {
			map = new ItemStack(Material.FILLED_MAP, 1);
			MapMeta meta = (MapMeta) map.getItemMeta();
			meta.setMapId(this.mapView.getId());
			map.setItemMeta(meta);
		}
		Bukkit.getPlayer(this.callerID).getInventory().addItem(map);
		**/
	}
	
	private boolean insideRange(Location loc) {
		if(loc.getBlockX() <= (this.startLoc.getBlockX() + 64)
				&& loc.getBlockX() >= (this.startLoc.getBlockX() - 64)
				&& loc.getBlockZ() >= (this.startLoc.getBlockZ() - 64)
				&& loc.getBlockZ() <= (this.startLoc.getBlockZ() + 64)
			) {
			return true;
		}
		return false;
	}
	
	protected int getMapID() {
		return this.mapView.getId();
	}

}
