package yobdc.rfb;

import java.lang.IllegalArgumentException;

public class Framebuffer {
	private String name;

	private int	 bpp;
	private int	 depth;
	private boolean bigEndian;
	private boolean trueColour;
	private int	 redMax;
	private int	 greenMax;
	private int	 blueMax;
	private int	 redShift;
	private int	 greenShift;
	private int	 blueShift;

	private int width;
	private int height;
	public int[] pixels;
	private int pixelCount;
	
	private int copyRectX;
	private int copyRectY;
	
	public Framebuffer(int w, int h){
		this.width = w;
		this.height = h;
		
		this.pixelCount = w * h;
		this.pixels = new int[this.pixelCount];
	}
	
	public static Framebuffer FromPixelFormat(int[] b, int width, int height){
		if (b.length!=16) {
			throw new IllegalArgumentException("b.length != 16");
		}
		
		int temp = 0;
		Framebuffer buffer = new Framebuffer(width, height);
				
		temp = (int)b[0];
		buffer.setBitsPerPixel(temp);
		
		temp = (int)b[1];
		buffer.setDepth(temp);
		buffer.setBigEndian(b[2]!=0);
		buffer.setTrueColour(b[3]!=0);
		
		temp = (int) (b[5] | b[4] << 8);
		buffer.setRedMax(temp);
		
		temp = (int) (b[7] | b[6] << 8);
		buffer.setGreenMax(temp);
		
		temp = (int) (b[9] | b[8] << 8);
		buffer.setBlueMax(temp);
		
		temp = (int) b[10];
		buffer.setRedShift(temp);
		
		temp = (int) b[11];
		buffer.setGreenShift(temp);

		temp = (int) b[12];
		buffer.setBlueShift(temp);
		
		return buffer;
	}
	
	public byte[] ToPixelFormat()
	{
		byte[] b = new byte[16];
		
		b[0]  = (byte) bpp;
		b[1]  = (byte) depth;
		b[2]  = (byte) (bigEndian ? 1 : 0);
		b[3]  = (byte) (trueColour ? 1 : 0);
		b[4]  = (byte) ((redMax >> 8) & 0xff);
		b[5]  = (byte) (redMax & 0xff);
		b[6]  = (byte) ((greenMax >> 8) & 0xff);
		b[7]  = (byte) (greenMax & 0xff);
		b[8]  = (byte) ((blueMax >> 8) & 0xff);
		b[9]  = (byte) (blueMax & 0xff);
		b[10] = (byte) redShift;
		b[11] = (byte) greenShift;
		b[12] = (byte) blueShift;
		// plus 3 bytes padding = 16 bytes
		
		return b;
	}
	
	public String getDesktopName() {
		return name;
	}
	public void setDesktopName(String name) {
		this.name = name;
	}
	public int getBitsPerPixel() {
		return bpp;
	}
	public void setBitsPerPixel(int bpp) {
		this.bpp = bpp;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public boolean isBigEndian() {
		return bigEndian;
	}
	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}
	public boolean isTrueColour() {
		return trueColour;
	}
	public void setTrueColour(boolean trueColour) {
		this.trueColour = trueColour;
	}
	public int getRedMax() {
		return redMax;
	}
	public void setRedMax(int redMax) {
		this.redMax = redMax;
	}
	public int getGreenMax() {
		return greenMax;
	}
	public void setGreenMax(int greenMax) {
		this.greenMax = greenMax;
	}
	public int getBlueMax() {
		return blueMax;
	}
	public void setBlueMax(int blueMax) {
		this.blueMax = blueMax;
	}
	public int getRedShift() {
		return redShift;
	}
	public void setRedShift(int redShift) {
		this.redShift = redShift;
	}
	public int getGreenShift() {
		return greenShift;
	}
	public void setGreenShift(int greenShift) {
		this.greenShift = greenShift;
	}
	public int getBlueShift() {
		return blueShift;
	}
	public void setBlueShift(int blueShift) {
		this.blueShift = blueShift;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int[] getPixels() {
		return pixels;
	}
	public void setPixels(int index, int value) {
		this.pixels[index] = value;
	}
	public int getPixelCount() {
		return pixelCount;
	}
	public void setPixelCount(int pixelCount) {
		this.pixelCount = pixelCount;
	}
}
