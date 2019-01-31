package de.MrBaumeister98.GunGame.Items.TrackPadRenderer;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class OverviewImageRenderer {
	
	private BufferedImage renderedImage;
	private List<PixelPos> locsToBeRendered;
	
	public OverviewImageRenderer(List<PixelPos> locs) {
		
		this.locsToBeRendered = locs;

		Collections.sort(this.locsToBeRendered, new Comparator<PixelPos>() {
			@Override
			public int compare(PixelPos o1, PixelPos o2) {

				return o1.getY() - o2.getY();
			}
		});

		this.renderedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
	}
	
	public void renderImage() {
		for(PixelPos loc : this.locsToBeRendered) {
			RGBColor color = getColor(loc);
			
			int rgb = (255<<24) | (color.getR()<<16) | (color.getG()<<8) | color.getB();
			
			this.renderedImage.setRGB(loc.getX(), loc.getZ(), rgb);
		}
	}
	
	public RGBColor getColor(PixelPos pos) {
		return new RGBColor(0, pos.getY(), 0);
	}

	public BufferedImage getRenderedImage() {
		return renderedImage;
	}

}
