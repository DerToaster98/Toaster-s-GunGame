package de.MrBaumeister98.GunGame.Items.TrackPad;

class PixelPos {
	
	private int x;
	private int y;
	private int z;
	
	public PixelPos(int lX, int lY, int lZ) {
		setX(lX);
		setY(lY);
		setZ(lZ);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}

}
