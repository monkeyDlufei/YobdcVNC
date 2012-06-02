package yobdc.rfb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import sun.awt.image.BytePackedRaster;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import yobdc.activity.R;
import yobdc.exception.GetReasonFailException;
import yobdc.exception.ProtocolNotSupportedException;
import yobdc.exception.UnKnownHostExceptioin;
import yobdc.util.*;

public class VncClient extends ImageView {
	private RfbProtocol rfb;
	private byte securityType;
	public Framebuffer framebuffer;
	private EncodeFactory factory;

	int dragStartX = 0;
	int dragStartY = 0;

	public int ScreenOption = 0;

	public final int SCREEN_DRAG_MODE = 0;
	public final int SCREEN_MOUSE_MDOE = 1;

	private VncRect vncRect = null;

	public String ip = null;
	public String passwd = null;

	private VncClient myVNC = this;

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (true) {
			if (framebuffer != null) {
				int w = framebuffer.getWidth();
				int h = framebuffer.getHeight();
				try {
					canvas.drawBitmap(framebuffer.pixels, 0, w, stop_x, stop_y,
							w, h, false, null);

				} catch (Exception e) {
					// TODO: handle exception
					e.toString();
				}
			}
		} else {
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.my);
			bmp = bmp.copy(Bitmap.Config.RGB_565, true);

			intArray = new int[bmp.getWidth() * bmp.getHeight()];
			bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
					bmp.getHeight());
		}
	}

	private Thread backgroundThread;

	private Bitmap bmp;

	private VncClient myclient = this;

	public Button bt1;
	public Button bt2;

	public VncClient(Context context, AttributeSet attrs) {
		super(context, attrs);

		rfb = new RfbProtocol(this.getContext());
		securityType = 0;

		// setAdjustViewBounds(true);
		// setMaxWidth(1600);
		// setMaxHeight(1200);
		// setScaleType(ImageView.ScaleType.);

		if (bmp != null) {
			myclient.setImageBitmap(bmp);
		}

		/*
		 * setOnLongClickListener(new View.OnLongClickListener() {
		 * 
		 * @Override public boolean onLongClick(View v) { // TODO Auto-generated
		 * method stub int i = 0; i++; return false; } });
		 */

		// bmp = BitmapFactory.decodeResource(getResources(), R.drawable.my);
		// bmp = bmp.copy(Bitmap.Config.RGB_565, true);
		//
		// intArray = new int[bmp.getWidth() * bmp.getHeight()];
		// bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0, bmp.getWidth(),
		// bmp.getHeight());
		// try {
		// myclient.setImageBitmap(bmp);
		//
		// } catch (Exception e) {
		// e.toString();
		// }

		// Thread t = new Thread() {
		// public void run() {
		// for (int i = 0; i < 8; i++) {
		// TestThread();
		// try {
		// this.sleep(1000*3);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// };
		// t.start();
	}

	public void InitVnc() {
		boolean passwdPending = false;
		try {
			passwdPending = Connect(ip, 5900);
		} catch (UnKnownHostExceptioin e) {
			// TODO Auto-generated catch block
			Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG)
					.show();
			return;
		} catch (GetReasonFailException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG)
			.show();
		}

		boolean authResult = false;
		if (passwdPending) {
			try {
				authResult = Authenticate(passwd);
			} catch (GetReasonFailException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG)
				.show();
			}
		}

		if (authResult) {
			Initialize();

			Thread t = new Thread() {
				public void run() {
					RequestScreenUpdate(false);

					while (true) {
						GetRfbUpdate();
					}
				}
			};
			t.start();
		}

	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	static final int BIGGER = 3;
	static final int SMALLER = 4;

	private int start_x = 0;
	private int start_y = 0;
	private int stop_x = 0;
	private int stop_y = 0;
	private int mode = 0;
	private float scale = 1.0f;

	private float beforeLenght;
	private float afterLenght;

	public int screenW = 800;
	public int screenH = 480;
	private TranslateAnimation trans;

	private void UpdateMove() {
		dragStartX += stop_x - start_x;
		dragStartY += stop_y - start_y;
	}

	float downX = 0;
	float downY = 0;
	float currentX = 0;
	float currentY = 0;
	float scrollByX = 0;
	float scrollByY = 0;
	float totalX = 0;
	float totalY = 0;

	long startTime = 0;
	long endTime = 0;
	long longPressedTime = 2000;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int i = 0;
		switch (ScreenOption) {
		case SCREEN_DRAG_MODE: {

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {

				start_x = (int) event.getX();
				start_y = (int) event.getY();
				// if (framebuffer != null) {
				// int real_x = (int) (start_x - stop_x);
				// int real_y = (int) (start_y - stop_y);
				// SendPointerEvent(real_x, real_y, MouseData.MOUSE_LEFT_CLICK);
				// }
				downX = start_x;
				downY = start_y;
			}
				break;

			case MotionEvent.ACTION_MOVE: {
				currentX = event.getX();
				currentY = event.getY();
				scrollByX = (int) (downX - currentX);
				scrollByY = (int) (downY - currentY);
				stop_x -= scrollByX;
				stop_y -= scrollByY;

				if (stop_x + deskWidth < screenW) {
					stop_x = screenW - deskWidth;
				}

				if (stop_x > 0) {
					stop_x = 0;
				}

				if (stop_y + deskHeight < screenH) {
					stop_y = screenH - deskHeight;
				}

				if (stop_y > 0) {
					stop_y = 0;
				}

				downX = currentX;
				downY = currentY;
			}
				break;

			case MotionEvent.ACTION_UP: {
				start_x = (int) event.getX();
				start_y = (int) event.getY();
				// if (framebuffer != null) {
				// int real_x = start_x - dragStartX;
				// int real_y = start_y - dragStartY;
				// SendPointerEvent(real_x, real_y, MouseData.MOUSE_RELEASE);
				// }
			}
				break;

			}
		}
			break;

		case SCREEN_MOUSE_MDOE: {

			switch (event.getAction() & MotionEvent.ACTION_MASK) {

			case MotionEvent.ACTION_DOWN: {
				startTime = System.currentTimeMillis();

				start_x = (int) event.getX();
				start_y = (int) event.getY();
				if (framebuffer != null) {
					int real_x = (int) (start_x - stop_x);
					int real_y = (int) (start_y - stop_y);
					SendPointerEvent(real_x, real_y, MouseData.MOUSE_LEFT_CLICK);
				}
				downX = start_x;
				downY = start_y;
			}
				break;

			case MotionEvent.ACTION_MOVE:
				break;

			case MotionEvent.ACTION_UP: {
				endTime = System.currentTimeMillis();

				start_x = (int) event.getX();
				start_y = (int) event.getY();
				if (framebuffer != null) {
					int real_x = start_x - stop_x;
					int real_y = start_y - stop_y;
					SendPointerEvent(real_x, real_y, MouseData.MOUSE_RELEASE);

					long temp = endTime - startTime;
					if (temp > longPressedTime) {
						SendPointerEvent(real_x, real_y,
								MouseData.MOUSE_RIGHT_CLICK);
						SendPointerEvent(real_x, real_y,
								MouseData.MOUSE_RELEASE);
					}
				}
			}
				break;
			}
		}
			break;

		default:
			break;
		}

		return true;
	}

	private void scrollBy(float scrollByX2, float scrollByY2) {
		// TODO Auto-generated method stub

	}

	private void setScale(float temp, int flag) {

		if (flag == BIGGER) {
			this.setFrame(this.getLeft() - (int) (temp * this.getWidth()),
					this.getTop() - (int) (temp * this.getHeight()),
					this.getRight() + (int) (temp * this.getWidth()),
					this.getBottom() + (int) (temp * this.getHeight()));
		} else if (flag == SMALLER) {
			this.setFrame(this.getLeft() + (int) (temp * this.getWidth()),
					this.getTop() + (int) (temp * this.getHeight()),
					this.getRight() - (int) (temp * this.getWidth()),
					this.getBottom() - (int) (temp * this.getHeight()));
		}
	}

	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

	private int intArray[];

	Handler handler = new Handler();

	public void TestThread() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < intArray.length; i++) {
					intArray[i] = intArray[i] >> 1;
				}
				bmp.setPixels(intArray, 0, bmp.getWidth(), 0, 0,
						bmp.getWidth(), bmp.getHeight());
				myclient.setImageBitmap(bmp);

			}
		});
	}

	public boolean Connect(String host, int port) throws UnKnownHostExceptioin, GetReasonFailException {
		boolean bRet = rfb.Connect(host, port);
		// boolean bRet = rfb.Connect("ivy-pc", 5900);
		if (bRet) {
			try {
				rfb.ReadProtocolVersion();
			} catch (ProtocolNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rfb.WriteProtocolVersion();

			byte[] types = rfb.ReadSecurityTypes();
			if (types.length > 0) {
				if (types[0] == 0) {
					System.out.println("Connection failed");
					return false;
				} else {
					securityType = rfb.GetSupportedSecurityType(types);
					rfb.WriteSecurityType(securityType);

					if (rfb.ServerVersion() == 3.8f && securityType == 1) {
						System.out.println("Connection failed"
								+ rfb.ReadSecurityFailureReason());
					}
					return (securityType > 1) ? true : false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean Authenticate(String passwd) throws GetReasonFailException {
		if (passwd == null) {
			return false;
		}

		if (securityType == 2) {
			byte[] challenge = rfb.ReadSecurityChallenge();
			rfb.WriteSecurityResponse(EncChallenge(passwd, challenge));
		} else {
			return false;
		}

		int result = rfb.ReadSecurityResult();
		if (result == 0) {
			return true;
		} else {
			if (rfb.ServerVersion() == 3.8f) {
				String reason = rfb.ReadSecurityFailureReason();
				throw new GetReasonFailException(reason);
			}
			return false;
		}
	}

	public void Disconnect() {
		if (rfb != null) {
			rfb.Close();
		}
	}

	/*
	 * Wait to be implemented
	 */
	public byte[] EncChallenge(String passwd, byte[] challenge) {
		DESUtil des = new DESUtil(passwd);
		return des.EncryptByte(challenge);
	}

	public void Initialize() {
		try {
			rfb.WriteClientInitialisation(false);
			framebuffer = rfb.ReadServerInitialization();

			rectWidth = framebuffer.getWidth();
			rectHeight = framebuffer.getHeight();
			// rectWidth = 800;
			// rectHeight = 480;
			bmp = Bitmap.createBitmap(rectWidth, rectHeight,
					Bitmap.Config.RGB_565);

			rfb.WriteSetPixelFormat(framebuffer);
			rfb.WriteSetEncodings();
			factory = new EncodeFactory(rfb, framebuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void RequestScreenUpdate(boolean incremental) {
		try {
			int w = framebuffer.getWidth();
			int h = framebuffer.getHeight();
			rfb.WriteFramebufferUpdateRequest(incremental, 0, 0, w, h);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SendKeyEvent(int keysym, boolean down) {
		try {
			rfb.WriteKeyEvent(keysym, down);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GetRfbUpdate() {
		try {
			int messageType = rfb.ReadServerMessageType();
			switch (messageType) {
			case RfbProtocol.BELL: {
			}
				break;

			case RfbProtocol.FRAMEBUFFER_UPDATE: {

				int numRect = rfb.ReadFramebufferUpdate();
				for (int i = 0; i < numRect; i++) {

					vncRect = rfb.ReadVncRect();

					switch (vncRect.getEncodingType()) {

					case RfbProtocol.RAW_ENCODING: {
						DecodeRaw(vncRect);
					}
						break;

					case RfbProtocol.COPYRECT_ENCODING: {
						DecodeCopyRect(vncRect);
					}
						break;

					case RfbProtocol.RRE_ENCODING: {
						DecodeRre(vncRect);
					}
						break;

					case RfbProtocol.CORRE_ENCODING: {
						DecodeCoREE(vncRect);
					}
						break;

					case RfbProtocol.HEXTILE_ENCODING: {
						DecodeHextile(vncRect);
					}
						break;

					case RfbProtocol.ZLIB_ENCODING: {
						DecodeZlib(vncRect);
					}
						break;

					case RfbProtocol.ZRLE_ENCODING: {

					}
						break;

					default:
						break;
					}

					RequestScreenUpdate(true);
				}
			}
				break;

			case RfbProtocol.SERVER_CUT_TEXT: {
				HandleBell();
			}
				break;

			case RfbProtocol.SET_COLOUR_MAP_ENTRIES: {
				HandleColorMap();
			}
				break;

			default:
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void HandleColorMap(){
		try {
			rfb.ReadByte();
			rfb.ReadByte();
			rfb.ReadByte();
			
			int num = rfb.ReadUnsignedShort();
			for (int i = 0; i < num; i++) {
				rfb.ReadBytes(6);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void HandleBell(){
		try {
			String str = rfb.ReadCutText();
			ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			cm.setText(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SendPointerEvent(int x, int y, byte buttonMask) {
		try {
			rfb.WritePointerEvent(x, y, buttonMask);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean repaintsEnabled = true;

	private void ReDraw() {
		if (repaintsEnabled) {
			handler.post(updateCanvas);
		}
	}

	int rectWidth = 0;
	int rectHeight = 0;
	int rectX = 0;
	int rectY = 0;
	int deskWidth;
	int deskHeight;

	byte[] rawBuffer = new byte[128];

	private void DecodeRaw(VncRect rect) {
		rectX = rect.getX();
		rectY = rect.getY();
		rectWidth = rect.getWidth();
		rectHeight = rect.getHeight();
		deskWidth = framebuffer.getWidth();
		deskHeight = framebuffer.getHeight();
		int bytePerPixel = framebuffer.getBitsPerPixel() / 8;

		try {

			if (bytePerPixel == 1) {

			} else {
				/*
				 * int l = rectWidth*rectHeight*bytePerPixel; int t =
				 * deskWidth-rectWidth;
				 * 
				 * if (l>rawBuffer.length) { rawBuffer = new byte[l]; }
				 * rfb.readFully(rawBuffer, 0, l);
				 * 
				 * for (int i = 0; i < framebuffer.pixels.length; i++) { int idx
				 * = i*4; framebuffer.pixels[i] = Color.rgb(rawBuffer[idx +2] &
				 * 0xFF, rawBuffer[idx +1] & 0xFF, rawBuffer[idx +0] & 0xFF); }
				 * int s = 0; s = 1;
				 */

				/*
				 * final int l = rectWidth*4; if (l>rawBuffer.length) {
				 * rawBuffer = new byte[l]; } int i,offset; for (int dy = rectY;
				 * dy <rectY+rectHeight; dy++) { rfb.readFully(rawBuffer, 0, l);
				 * offset = dy*deskWidth+rectX; for (i = 0; i<rectWidth;i++) {
				 * final int idx=i*4;
				 * //framebuffer.pixels[offset+i]=(rawBuffer[idx + 2] & 0xff) <<
				 * 16 | (rawBuffer[idx + 1] & 0xff) << 8 | (rawBuffer[idx] &
				 * 0xff); framebuffer.pixels[offset+i]=Color.rgb(rawBuffer[idx
				 * +2] & 0xFF, rawBuffer[idx +1] & 0xFF, rawBuffer[idx +0] &
				 * 0xFF); } handler.post(updateCanvas);
				 * 
				 * }
				 */

				for (int dy = rectY; dy < rectY + rectHeight; dy++) {
					int offset = dy * deskWidth + rectX;
					for (int dx = 0; dx < rectWidth; dx++) {
						byte[] bTemp = rfb.ReadBytes(bytePerPixel);

						int pixelData = Color.rgb(bTemp[2] & 0xFF,
								bTemp[1] & 0xFF, bTemp[0] & 0xFF);

						if (offset + dx < 0
								|| offset + dx >= framebuffer.pixels.length) {
							break;
						}
						framebuffer.pixels[offset + dx] = pixelData;
					}
					ReDraw();
				}
				/*
				 * for (int dy = rectY; dy < rectY + rectHeight; dy++) { int
				 * offset = dy * deskWidth + rectX;
				 * 
				 * byte[] bTemp = rfb.ReadBytes(bytePerPixel*rectWidth); for
				 * (int dx = 0; dx < rectWidth; dx++) { int i = dx*4;
				 * 
				 * int pixelData = Color.rgb(bTemp[i+2] & 0xFF, bTemp[i+1] &
				 * 0xFF, bTemp[i+0] & 0xFF);
				 * 
				 * if (offset + dx < 0 || offset + dx >=
				 * framebuffer.pixels.length) { break; }
				 * framebuffer.pixels[offset + dx] = pixelData; }
				 * handler.post(updateCanvas); }
				 */

			}

			// save();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Runnable updateCanvas = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			myclient.invalidate();
		}
	};

	private void DecodeCopyRect(VncRect rect) {
		rectX = rect.getX();
		rectY = rect.getY();
		rectWidth = rect.getWidth();
		rectHeight = rect.getHeight();
		deskWidth = framebuffer.getWidth();
		deskHeight = framebuffer.getHeight();
		int nonCopyStride = deskWidth - rectWidth;

		try {
			Point srcPoint = rfb.ReadCopyRectPosition();

			int iSrc = srcPoint.y * deskWidth + srcPoint.x;
			int iDest = rectY * deskWidth + rectX;

			if (iDest < iSrc) {
				for (int y = 0; y < rectHeight; y++) {
					for (int x = 0; x < rectWidth; x++) {
						if (iDest >= framebuffer.pixels.length
								|| iSrc >= framebuffer.pixels.length) {
							continue;
						}
						framebuffer.pixels[iDest] = framebuffer.pixels[iSrc];
						iDest++;
						iSrc++;
					}
					iDest += nonCopyStride;
					iSrc += nonCopyStride;
				}
			} else {
				iDest += rectHeight * deskWidth + rectWidth;
				iSrc += rectHeight * deskWidth + rectWidth;

				for (int y = 0; y < rectHeight; y++) {
					for (int x = 0; x < rectWidth; x++) {
						if (iDest >= framebuffer.pixels.length
								|| iSrc >= framebuffer.pixels.length) {
							continue;
						}
						framebuffer.pixels[iDest] = framebuffer.pixels[iSrc];
						iDest--;
						iSrc--;
					}
					iDest -= nonCopyStride;
					iSrc -= nonCopyStride;
				}
			}

			ReDraw();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void DecodeRre(VncRect rect) {
		try {

			int bytePerPixel = framebuffer.getBitsPerPixel() / 8;
			int num = rfb.ReadInt();
			rfb.readFully(backColorBuffer, 0, bytePerPixel);

			int color = 0;
			if (bytePerPixel != 1) {
				color = Color.rgb(backColorBuffer[2] & 0xFF,
						backColorBuffer[1] & 0xFF, backColorBuffer[0] & 0xFF);
			}
			FillBackColor(rect, color);

			int len = num * (bytePerPixel + 8);
			if (len > rre_buf.length)
				rre_buf = new byte[len];

			rfb.readFully(rre_buf, 0, len);

			int sx, sy, sw, sh;
			int i = 0;

			for (int j = 0; j < num; j++) {
				if (bytePerPixel == 1) {
					// color = colorPalette[0xFF & rre_buf[i++]];
				} else {
					color = Color.rgb(rre_buf[i + 2] & 0xFF,
							rre_buf[i + 1] & 0xFF, rre_buf[i] & 0xFF);
					i += 4;
				}

				sx = rect.x + ((rre_buf[i] & 0xff) << 8)
						+ (rre_buf[i + 1] & 0xff);
				i += 2;
				sy = rect.y + ((rre_buf[i] & 0xff) << 8)
						+ (rre_buf[i + 1] & 0xff);
				i += 2;
				sw = ((rre_buf[i] & 0xff) << 8) + (rre_buf[i + 1] & 0xff);
				i += 2;
				sh = ((rre_buf[i] & 0xff) << 8) + (rre_buf[i + 1] & 0xff);
				i += 2;

				FillRect(new VncRect(sx, sy, sw, sh), color);

				ReDraw();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void DecodeCoREE(VncRect rect) {
		try {

			int bytePerPixel = framebuffer.getBitsPerPixel() / 8;
			int num = rfb.ReadInt();
			rfb.readFully(backColorBuffer, 0, bytePerPixel);

			int color = 0;
			if (bytePerPixel != 1) {
				color = Color.rgb(backColorBuffer[2] & 0xFF,
						backColorBuffer[1] & 0xFF, backColorBuffer[0] & 0xFF);
			}
			FillBackColor(rect, color);

			int len = num * (bytePerPixel + 8);
			if (len > rre_buf.length)
				rre_buf = new byte[len];

			rfb.readFully(rre_buf, 0, len);

			int sx, sy, sw, sh;
			int i = 0;

			for (int j = 0; j < num; j++) {
				if (bytePerPixel == 1) {
					// color = colorPalette[0xFF & rre_buf[i++]];
				} else {
					color = Color.rgb(rre_buf[i + 2] & 0xFF,
							rre_buf[i + 1] & 0xFF, rre_buf[i] & 0xFF);
					i += 4;
				}
				sx = rect.x + (rre_buf[i++] & 0xFF);
				sy = rect.y + (rre_buf[i++] & 0xFF);
				sw = rre_buf[i++] & 0xFF;
				sh = rre_buf[i++] & 0xFF;

				FillRect(new VncRect(sx, sy, sw, sh), color);

				ReDraw();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private final int RAW = 0x01;
	private final int BACKGROUND_SPECIFIED = 0x02;
	private final int FOREGROUND_SPECIFIED = 0x04;
	private final int ANY_SUBRECTS = 0x08;
	private final int SUBRECTS_COLOURED = 0x10;

	private void DecodeHextile(VncRect rect) {
		deskWidth = framebuffer.getWidth();
		deskHeight = framebuffer.getHeight();

		backColor = Color.BLACK;
		foreColor = Color.BLACK;

		for (int ty = rect.y; ty < rect.y + rect.height; ty += 16) {
			int th = (rect.y + rect.height - ty < 16) ? (rect.y + rect.height - ty)
					: 16;

			for (int tx = 0; tx < rect.x + rect.width; tx += 16) {
				int tw = (rect.x + rect.width - tx < 16) ? (rect.x + rect.width - tx)
						: 16;
				try {
					DecodeSubHextile(tx, ty, tw, th);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ReDraw();
		}
	}

	private byte[] backColorBuffer = new byte[4];
	private int backColor = 0;
	private int foreColor = 0;
	byte[] rre_buf = new byte[128];

	private void DecodeSubHextile(int tx, int ty, int tw, int th)
			throws IOException {
		int bytePerPixel = framebuffer.getBitsPerPixel() / 8;
		int subencoding = rfb.ReadUnsignedByte();

		VncRect tRect = new VncRect(tx, ty, tw, th);

		if ((subencoding & RAW) != 0) {
			DecodeRaw(tRect);
			return;
		}

		if ((subencoding & BACKGROUND_SPECIFIED) != 0) {
			rfb.readFully(backColorBuffer, 0, bytePerPixel);
			backColor = Color.rgb(backColorBuffer[2] & 0xFF,
					backColorBuffer[1] & 0xFF, backColorBuffer[0] & 0xFF);
		}

		FillRect(tRect, backColor);

		if ((subencoding & FOREGROUND_SPECIFIED) != 0) {
			rfb.readFully(backColorBuffer, 0, bytePerPixel);
			foreColor = Color.rgb(backColorBuffer[2] & 0xFF,
					backColorBuffer[1] & 0xFF, backColorBuffer[0] & 0xFF);
		}

		if ((subencoding & ANY_SUBRECTS) == 0) {
			return;
		}

		int num = rfb.ReadUnsignedByte();
		int bufSize = num * 2;
		if ((subencoding & SUBRECTS_COLOURED) != 0) {
			bufSize += num * bytePerPixel;
		}
		if (rre_buf.length < bufSize)
			rre_buf = new byte[bufSize];
		rfb.readFully(rre_buf, 0, bufSize);

		int b1, b2, sx, sy, sw, sh;
		int i = 0;

		if ((subencoding & SUBRECTS_COLOURED) == 0) {
			for (int j = 0; j < num; j++) {
				b1 = rre_buf[i++] & 0xFF;
				b2 = rre_buf[i++] & 0xFF;
				sx = tx + (b1 >> 4);
				sy = ty + (b1 & 0xf);
				sw = (b2 >> 4) + 1;
				sh = (b2 & 0xf) + 1;
				FillRect(new VncRect(sx, sy, sw, sh), foreColor);
			}
		} else if (bytePerPixel == 1) {
			// for (int j = 0; j < num; j++) {
			// hextile_fg = colorPalette[0xFF & rre_buf[i++]];
			// b1 = rre_buf[i++] & 0xFF;
			// b2 = rre_buf[i++] & 0xFF;
			// sx = tx + (b1 >> 4);
			// sy = ty + (b1 & 0xf);
			// sw = (b2 >> 4) + 1;
			// sh = (b2 & 0xf) + 1;
			// handleHextileSubrectPaint.setColor(hextile_fg);
			// if ( valid)
			// bitmapData.drawRect(sx, sy, sw, sh, handleHextileSubrectPaint);
			// }
		} else {
			for (int j = 0; j < num; j++) {
				foreColor = Color.rgb(rre_buf[i + 2] & 0xFF,
						rre_buf[i + 1] & 0xFF, rre_buf[i] & 0xFF);
				i += 4;
				b1 = rre_buf[i++] & 0xFF;
				b2 = rre_buf[i++] & 0xFF;
				sx = tx + (b1 >> 4);
				sy = ty + (b1 & 0xf);
				sw = (b2 >> 4) + 1;
				sh = (b2 & 0xf) + 1;
				FillRect(new VncRect(sx, sy, sw, sh), foreColor);
			}
		}

	}

	private void FillBackColor(VncRect rect, int color) {
		rectX = rect.getX();
		rectY = rect.getY();
		rectWidth = rect.getWidth();
		rectHeight = rect.getHeight();
		deskWidth = framebuffer.getWidth();
		deskHeight = framebuffer.getHeight();
	}

	private void FillRect(VncRect rect) {
		int bytePerPixel = framebuffer.getBitsPerPixel() / 8;
		int index = 0;
		int offset = 0;
		deskWidth = framebuffer.getWidth();
		deskHeight = framebuffer.getHeight();

		index = rect.getY() * deskWidth + rect.getX();
		offset = deskWidth - rect.getWidth();

		for (int y = 0; y < rect.getHeight(); y++) {
			for (int x = 0; x < rect.getWidth(); x++) {
				try {
					if (index >= framebuffer.pixels.length) {
						break;
					}
					framebuffer.pixels[index] = rfb.ReadPixel(bytePerPixel);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				index++;
			}
			index += offset;
		}
	}

	private void FillRect(VncRect rect, int color) {
		deskWidth = framebuffer.getWidth();
		deskHeight = framebuffer.getHeight();

		int offset = rect.x + rect.y * deskWidth;
		if (rect.width > 10) {
			for (int j = 0; j < rect.height; j++, offset += deskWidth) {
				Arrays.fill(framebuffer.pixels, offset, offset + rect.width,
						color);
			}
		} else {
			for (int j = 0; j < rect.height; j++, offset += deskWidth
					- rect.width) {
				for (int k = 0; k < rect.width; k++, offset++) {
					framebuffer.pixels[offset] = color;
				}
			}
		}
	}

	private byte[] zlibBuffer = new byte[128];
	private Inflater zlibInflater;

	private void DecodeZlib(VncRect rect) {
		try {

			int bytePerPixel = framebuffer.getBitsPerPixel() / 8;
			deskWidth = framebuffer.getWidth();
			deskHeight = framebuffer.getHeight();

			int num = rfb.ReadInt();
			if (zlibBuffer == null || zlibBuffer.length < num) {
				zlibBuffer = new byte[num * 2];
			}

			rfb.readFully(zlibBuffer, 0, num);

			if (zlibBuffer == null) {
				zlibInflater = new Inflater();
			}
			zlibInflater.setInput(zlibBuffer, 0, num);

			if (bytePerPixel == 1) {

			} else {
				int len = rect.width * 4;
				if (len > zlibBuffer.length) {
					zlibBuffer = new byte[len];
				}

				int offset = 0;

				for (int dy = rect.y; dy < rect.y + rect.height; dy++) {
					zlibInflater.inflate(zlibBuffer, 0, len);
					offset = rect.y * deskHeight + rect.x;
					for (int i = 0; i < rect.width; i++) {
						int index = i * 4;
						framebuffer.pixels[index + i] = Color.rgb(
								zlibBuffer[index + 2] & 0xFF,
								zlibBuffer[index + 1] & 0xFF,
								zlibBuffer[index] & 0xFF);
					}
					ReDraw();
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void save() {
		String filePath = "/sdcard/foo2.txt";
		if (!filePath.endsWith(".txt") && !filePath.endsWith(".log"))
			filePath += ".txt";
		File file = new File(filePath);
		try {
			OutputStream outstream = new FileOutputStream(file);
			OutputStreamWriter out = new OutputStreamWriter(outstream);
			out.write(framebuffer.pixels.toString());
			out.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	private int ReadPixel(byte[] b, int index) {
		return (b[index + 2] & 0xff) << 16 | (b[index + 1] & 0xff) << 8
				| (b[index] & 0xff);
	}

	private int ReadPixel(byte[] b) {
		int iPixel = (int) (b[0] & 0xFF | b[1] << 8 | b[2] << 16 | b[3] << 24);
		byte red = (byte) ((iPixel >> framebuffer.getRedShift()) & framebuffer
				.getRedMax());
		byte green = (byte) ((iPixel >> framebuffer.getGreenShift()) & framebuffer
				.getGreenMax());
		byte blue = (byte) ((iPixel >> framebuffer.getBlueShift()) & framebuffer
				.getBlueMax());

		return Rgb2ColorInt(red, green, blue);
	}

	private int Rgb2ColorInt(byte red, byte green, byte blue) {
		return (int) (blue & 0xFF | green << 8 | red << 16 | 0xFF << 24);
	}

	public void SendCutText(String text) {
		if (rfb == null) {
			return;
		}
		try {
			rfb.WriteCutText(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
