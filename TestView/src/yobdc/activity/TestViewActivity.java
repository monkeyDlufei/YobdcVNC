package yobdc.activity;

import yobdc.rfb.VncClient;
import yobdc.util.KeyData;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestViewActivity extends Activity {
	/** Called when the activity is first created. */

	private final int MENU_CONNECT = Menu.FIRST;

	private Menu menu;

	private Bitmap bmp;

	private int intArray[];

	private VncClient iv;

	
	public VncClient Vnc() {
		return iv;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// iv = new VncClient(getApplication(), null);
		setContentView(R.layout.main);
		iv = (VncClient) findViewById(R.id.vnc_canvas);
		
		WindowManager manage=getWindowManager();
	     Display display=manage.getDefaultDisplay();

	     iv.screenH=display.getHeight();

	     iv.screenW=display.getWidth();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	int singlechoice = 0;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.connect: {
			ConnectDlg connDlg = new ConnectDlg(this);
			connDlg.show();
			// iv.InitVnc();
			/*
			 * new AlertDialog.Builder(this) .setTitle("Please Enter")
			 * .setIcon(android.R.drawable.ic_dialog_info) .setView(new
			 * EditText(this)) .setPositiveButton("OK", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { // TODO Auto-generated method stub
			 * 
			 * Toast toast = Toast.makeText( getApplicationContext(), "aaa",
			 * Toast.LENGTH_SHORT); toast.setGravity(Gravity.CENTER, 0, 0);
			 * toast.show(); } }).setNegativeButton("Cancel", null).show();
			 */
		}
			break;

		case R.id.screen_option: {
			final CharSequence[] items = { "Drag Mode", "Mouse Mode" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Select Screen Mode");
			builder.setSingleChoiceItems(items, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							singlechoice = item;
							// Toast.makeText(getApplicationContext(),
							// items[item], Toast.LENGTH_SHORT).show();
						}
					});
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (iv != null) {
								iv.ScreenOption = singlechoice;
							}
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
			break;
			
		case R.id.special_key:{
			 SendkeyDlg sendkeyDlg = new SendkeyDlg(this);
			 sendkeyDlg.show();
		}
		break;

		case R.id.send_key: {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
			break;
			
		case R.id.cut_text:{
			CutTextDlg cutDlg = new CutTextDlg(this);
			cutDlg.show();
		}
		break;

		case R.id.exit: {
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (iv.framebuffer != null) {
			int keysym = KeyData.GetXKey(keyCode, event);
			this.Vnc().SendKeyEvent(keysym, true);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (iv.framebuffer != null) {
			int keysym = KeyData.GetXKey(keyCode, event);
			this.Vnc().SendKeyEvent(keysym, false);
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
/*		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);

		super.onBackPressed();*/
	}
}