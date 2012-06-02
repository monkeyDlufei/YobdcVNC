package yobdc.rfb;

public class VncRect {
	public int x;
	public int y;
	public int width;
	public int height;
	public int encodingType;
	
	public VncRect(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		
		this.encodingType = 0;
	}
	
	public int getX() {
		return x;
	}
	public void setX(short x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(short y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(short width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(short height) {
		this.height = height;
	}
	public int getEncodingType() {
		return encodingType;
	}
	public void setEncodingType(int encodingType) {
		this.encodingType = encodingType;
	}
}
