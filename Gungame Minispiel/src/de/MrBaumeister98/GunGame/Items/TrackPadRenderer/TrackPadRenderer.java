package de.MrBaumeister98.GunGame.Items.TrackPadRenderer;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapView;

class TrackPadRenderer extends org.bukkit.map.MapRenderer {
	
	private OverviewImageRenderer imageRender;
	private MapCursorCollection cursors;
	private Boolean rendered;
	
	public TrackPadRenderer(OverviewImageRenderer imageRenderer, MapCursorCollection cursorColl) {
		this.imageRender = imageRenderer;
		this.cursors = cursorColl;
		this.rendered = false;
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player requestingPlayer) {
		if(this.rendered) {
			return;
		}
		canvas.drawImage(0, 0, this.imageRender.getRenderedImage());
		if(this.cursors.size() > 0 && this.cursors != null) {
			canvas.setCursors(cursors);
		}
		view.setUnlimitedTracking(false);
		
		this.rendered = true;
	}

}
