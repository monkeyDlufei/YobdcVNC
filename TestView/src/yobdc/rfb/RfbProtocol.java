package yobdc.rfb;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

import yobdc.exception.GetReasonFailException;
import yobdc.exception.ProtocolNotSupportedException;
import yobdc.exception.UnKnownHostExceptioin;

public class RfbProtocol {

	// Encoding Constants
	public static final int RAW_ENCODING = 0;
	public static final int COPYRECT_ENCODING = 1;
	public static final int RRE_ENCODING = 2;
	public static final int CORRE_ENCODING = 4;
	public static final int HEXTILE_ENCODING = 5;
	public static final int ZLIB_ENCODING = 6;
	public static final int ZRLE_ENCODING = 16;

	// Server to Client Message-Type constants
	public static final int FRAMEBUFFER_UPDATE = 0;
	public static final int SET_COLOUR_MAP_ENTRIES = 1;
	public static final int BELL = 2;
	public static final int SERVER_CUT_TEXT = 3;

	// Client to Server Message-Type constants
	protected final byte SET_PIXEL_FORMAT = 0;
	protected final byte SET_ENCODINGS = 2;
	protected final byte FRAMEBUFFER_UPDATE_REQUEST = 3;
	protected final byte KEY_EVENT = 4;
	protected final byte POINTER_EVENT = 5;
	protected final byte CLIENT_CUT_TEXT = 6;

	// public final int MOUSE_

	protected int verMajor; // Major version of Protocol--probably 3
	protected int verMinor; // Minor version of Protocol--probably 3, 7, or 8

	private Socket socket;
	private DataInputStream reader;
	private DataOutputStream writer;

	private Context context = null;

	public RfbProtocol() {

	}

	public RfbProtocol(Context context) {
		this.context = context;
	}

	/**
	 * @param host
	 *            Target ip
	 * @param port
	 *            Target port
	 * @return Success is true, failure is false.
	 * @throws UnKnownHostExceptioin
	 */
	public boolean Connect(String host, int port) throws UnKnownHostExceptioin {
		String ex_message = context.getResources().getString(
				yobdc.activity.R.string.ex_unknownhost);
		try {
			socket = new Socket(host, port);
			reader = new DataInputStream(socket.getInputStream());
			writer = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			throw new UnKnownHostExceptioin(ex_message);
			// return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			throw new UnKnownHostExceptioin(ex_message);
		}
		return true;
	}

	public boolean Close() {
		try {
			if (reader != null) {
				reader.close();
			}
			if (writer != null) {
				writer.close();
			}
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			return false;
		}
		return true;
	}

	/*
	 * Get the version VNC server supports.
	 */
	public void ReadProtocolVersion() throws ProtocolNotSupportedException {
		byte[] b = new byte[12];
		int iRet = 0;
		try {
			iRet = reader.read(b, 0, 12);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// As of the time of writing, the only supported versions are 3.3,
		// 3.7, and 3.8.
		if (b[0] == 0x52
				&& // R
				b[1] == 0x46
				&& // F
				b[2] == 0x42
				&& // B
				b[3] == 0x20
				&& // (space)
				b[4] == 0x30
				&& // 0
				b[5] == 0x30
				&& // 0
				b[6] == 0x33
				&& // 3
				b[7] == 0x2e
				&& // .
				(b[8] == 0x30 || // 0
				b[8] == 0x38)
				&& //
				(b[9] == 0x30 || // 0
				b[9] == 0x38)
				&& //
				(b[10] == 0x33 || //
						b[10] == 0x36 || b[10] == 0x37 || b[10] == 0x38 || b[10] == 0x39)
				&& b[11] == 0x0a) // \n
		{
			verMajor = 3;

			switch (b[10]) {
			case 0x33:
			case 0x36:
				verMinor = 3;
				break;
			case 0x37:
				verMinor = 7;
				break;
			case 0x38:
				verMinor = 8;
				break;
			case 0x39:
				verMinor = 8;
				break;
			}
		} else {
			throw new ProtocolNotSupportedException(
					"RFB Protocol version NOT supported.");
		}
	}

	/*
	 * Send to server the protocol version client supports.
	 */
	public void WriteProtocolVersion() {
		String verStr = String.format("RFB 003.00%d\n", verMinor);
		try {
			byte[] b = verStr.getBytes();
			writer.write(b);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] ReadSecurityTypes() {
		byte[] types = null;
		try {
			switch (verMinor) {

			case 3: {
				byte b = reader.readByte();
				types = new byte[] { b };
			}
				break;

			default: {
				byte num = reader.readByte();
				types = new byte[num];
				for (int i = 0; i < types.length; i++) {
					types[i] = reader.readByte();
				}
			}
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return types;
	}

	public byte GetSupportedSecurityType(byte[] types) {
		for (int i = 0; i < types.length; i++) {
			if (types[i] == 1 || types[i] == 2) {
				return types[i];
			}
		}
		return 0;
	}

	public void WriteSecurityType(byte type) {
		if (verMinor >= 7) {
			try {
				writer.write(type);
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public float ServerVersion() {
		return (float) verMajor + (verMinor * 0.1f);
	}

	public int ReadSecurityResult() {
		try {
			return reader.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public String ReadSecurityFailureReason() throws GetReasonFailException {
		try {
			int len = reader.readInt();
			byte[] b = new byte[len];
			reader.read(b, 0, len);
			String reason = new String(b, "UTF8");
			return reason;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new GetReasonFailException("Get Fail Message ERROR");
		}
	}

	public byte[] ReadSecurityChallenge() {
		byte[] b = new byte[16];
		try {
			reader.read(b, 0, 16);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return b;
	}

	public void WriteSecurityResponse(byte[] response) {
		try {
			writer.write(response);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void WriteClientInitialisation(boolean shared) throws IOException {
		writer.write((byte) (shared ? 1 : 0));
		writer.flush();
	}

	public Framebuffer ReadServerInitialization() throws IOException {
		int w = (int) reader.readShort();
		int h = (int) reader.readShort();

		int[] b = new int[16];
		for (int i = 0; i < b.length; i++) {
			b[i] = reader.readUnsignedByte();
		}
		Framebuffer buffer = Framebuffer.FromPixelFormat(b, w, h);

		int len = reader.readInt();
		byte[] bName = new byte[len];
		int num = reader.read(bName, 0, len);
		String desktopName = new String(bName, "UTF8");
		buffer.setDesktopName(desktopName);

		return buffer;
	}

	public void WritePadding(int len) throws IOException {
		byte[] padding = new byte[len];
		writer.write(padding, 0, padding.length);
	}

	public void WriteSetPixelFormat(Framebuffer buffer) throws IOException {
		byte[] b = new byte[20];

		b[0] = (byte) SET_PIXEL_FORMAT;
		b[4] = (byte) buffer.getBitsPerPixel();
		b[5] = (byte) buffer.getDepth();
		b[6] = (byte) (buffer.isBigEndian() ? 1 : 0);
		b[7] = (byte) (buffer.isTrueColour() ? 1 : 0);
		b[8] = (byte) ((buffer.getRedMax() >> 8) & 0xff);
		b[9] = (byte) (buffer.getRedMax() & 0xff);
		b[10] = (byte) ((buffer.getGreenMax() >> 8) & 0xff);
		b[11] = (byte) (buffer.getGreenMax() & 0xff);
		b[12] = (byte) ((buffer.getBlueMax() >> 8) & 0xff);
		b[13] = (byte) (buffer.getBlueMax() & 0xff);
		b[14] = (byte) buffer.getRedShift();
		b[15] = (byte) buffer.getGreenShift();
		b[16] = (byte) buffer.getBlueShift();

		writer.write(b);
		writer.flush();
	}

	public void WriteSetEncodings() throws IOException {
		int[] encodingArray = new int[] { RAW_ENCODING,
				 HEXTILE_ENCODING,
		 COPYRECT_ENCODING,
//		 CORRE_ENCODING,
//		 RRE_ENCODING,
//		 ZLIB_ENCODING
		};
		byte[] b = new byte[4 + 4 * encodingArray.length];

		b[0] = (byte) SET_ENCODINGS;
		b[2] = (byte) ((encodingArray.length >> 8) & 0xff);
		b[3] = (byte) (encodingArray.length & 0xff);

		for (int i = 0; i < encodingArray.length; i++) {
			b[4 + 4 * i] = (byte) ((encodingArray[i] >> 24) & 0xff);
			b[5 + 4 * i] = (byte) ((encodingArray[i] >> 16) & 0xff);
			b[6 + 4 * i] = (byte) ((encodingArray[i] >> 8) & 0xff);
			b[7 + 4 * i] = (byte) (encodingArray[i] & 0xff);
		}

		writer.write(b);
		writer.flush();
	}

	public void WriteFramebufferUpdateRequest(boolean incremental, int x,
			int y, int w, int h) throws IOException {
		byte[] buffer = new byte[10];

		buffer[0] = (byte) FRAMEBUFFER_UPDATE_REQUEST;
		buffer[1] = (byte) (incremental ? 1 : 0);
		buffer[2] = (byte) ((x >> 8) & 0xff);
		buffer[3] = (byte) (x & 0xff);
		buffer[4] = (byte) ((y >> 8) & 0xff);
		buffer[5] = (byte) (y & 0xff);
		buffer[6] = (byte) ((w >> 8) & 0xff);
		buffer[7] = (byte) (w & 0xff);
		buffer[8] = (byte) ((h >> 8) & 0xff);
		buffer[9] = (byte) (h & 0xff);

		writer.write(buffer);
		writer.flush();
	}

	public int ReadServerMessageType() throws IOException {
		if (socket.isClosed()) {
			return -1;
		}
		int num = (int) reader.readByte();
		return num;
	}

	public int ReadFramebufferUpdate() throws IOException {
		int padding = (int) reader.readByte();
		int numRect = reader.readUnsignedShort();
		return numRect;
	}

	public VncRect ReadVncRect() throws IOException {
		int x = reader.readUnsignedShort();
		int y = reader.readUnsignedShort();
		int w = reader.readUnsignedShort();
		int h = reader.readUnsignedShort();
		int encodingType = reader.readInt();

		VncRect vncRect = new VncRect(x, y, w, h);
		vncRect.setEncodingType(encodingType);

		return vncRect;
	}

	public void readFully(byte b[], int off, int len) throws IOException {
		reader.read(b, off, len);
	}

	public byte[] ReadBytes(int num) throws IOException {
		byte[] b = new byte[num];
		int count = reader.read(b);
		return b;
	}

	public void WriteKeyEvent(int keysym, boolean down) throws IOException {
		byte[] b = new byte[8];
		b[0] = KEY_EVENT;
		b[1] = (byte) (down ? 1 : 0);
		b[2] = 0;
		b[3] = 0;
		b[4] = (byte) ((keysym >> 24) & 0xff);
		b[5] = (byte) ((keysym >> 16) & 0xff);
		b[6] = (byte) ((keysym >> 8) & 0xff);
		b[7] = (byte) (keysym & 0xff);

		writer.write(b);
		writer.flush();
	}

	public void WritePointerEvent(int x, int y, byte buttonMask)
			throws IOException {
		byte[] b = new byte[6];
		b[0] = POINTER_EVENT;
		b[1] = buttonMask;
		b[2] = (byte) ((x >> 8) & 0xff);
		b[3] = (byte) (x & 0xff);
		b[4] = (byte) ((y >> 8) & 0xff);
		b[5] = (byte) (y & 0xff);

		writer.write(b);
		writer.flush();
	}

	public Point ReadCopyRectPosition() throws IOException {
		int x = reader.readUnsignedShort();
		int y = reader.readUnsignedShort();
		Point p = new Point(x, y);
		return p;
	}

	public int ReadInt() throws IOException {
		return reader.readInt();
	}

	public byte ReadByte() throws IOException {
		return reader.readByte();
	}

	public int ReadPixel(int bytePerPixel) throws IOException {
		byte[] b = ReadBytes(bytePerPixel);
		int color = Color.rgb(b[2] & 0xFF, b[1] & 0xFF, b[0] & 0xFF);
		return color;
	}

	public int ReadUnsignedByte() throws IOException {
		return reader.readUnsignedByte();
	}
	
	public void WriteCutText(String text) throws IOException{
		byte[] b = new byte[8 + text.length()];

	    b[0] = (byte) CLIENT_CUT_TEXT;
	    b[4] = (byte) ((text.length() >> 24) & 0xff);
	    b[5] = (byte) ((text.length() >> 16) & 0xff);
	    b[6] = (byte) ((text.length() >> 8) & 0xff);
	    b[7] = (byte) (text.length() & 0xff);
	    
	    System.arraycopy(text.getBytes(), 0, b, 8, text.length());
	    
	    writer.write(b);
	    writer.flush();
	}
	
	public String ReadCutText() throws IOException{
		reader.readByte();
		reader.readByte();
		reader.readByte();
		
		int len = reader.readInt();
		byte[] b = new byte[len];
		int num = reader.read(b, 0, len);
		String str = new String(b, "UTF8");
		return str;
	}
	
	public int ReadUnsignedShort() throws IOException{
		int n = reader.readUnsignedShort();
		return n;
	}
}
