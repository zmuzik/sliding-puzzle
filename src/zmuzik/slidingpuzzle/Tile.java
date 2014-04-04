package zmuzik.slidingpuzzle;

import android.graphics.Bitmap;

public class Tile {
	Bitmap bitmap;
	int origX;
	int origY;
	int tileNumber;
	
	public Tile(int x, int y) {
		origX = x;
		origY = y;
	}
	
	public Tile(int x, int y, Bitmap bitmap, int tileNumber) {
		origX = x;
		origY = y;
		this.bitmap = bitmap;
		this.tileNumber = tileNumber;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public int getOrigX() {
		return origX;
	}
	
	public int getOrigY() {
		return origY;
	}
	
	public int getTileNumber() {
		return tileNumber;
	}
}