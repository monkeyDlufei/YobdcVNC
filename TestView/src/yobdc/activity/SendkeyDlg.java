package yobdc.activity;

import yobdc.rfb.VncClient;
import yobdc.util.KeyData;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class SendkeyDlg extends Dialog {

	private Context context;
	private TestViewActivity mainView;

	private CheckBox checkCtrl;
	private CheckBox checkShift;
	private CheckBox checkAlt;
	private Spinner spinnerKey;
	private Button btSendkey;

	public SendkeyDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	int keysym = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sendkey);
		mainView = (TestViewActivity) context;

		checkAlt = (CheckBox) findViewById(R.id.checkboxAlt);
		checkShift = (CheckBox) findViewById(R.id.checkbox_Shift);
		checkCtrl = (CheckBox) findViewById(R.id.checkbox_Ctrl);
		spinnerKey = (Spinner) findViewById(R.id.spinnerKeySelect);
		btSendkey = (Button) findViewById(R.id.buttonSend);

		btSendkey.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (keysym != -1) {
					if (checkCtrl.isChecked()) {
						mainView.Vnc().SendKeyEvent(KeyData.Control_L, true);
					}
					if (checkAlt.isChecked()) {
						mainView.Vnc().SendKeyEvent(KeyData.Alt_L, true);
					}
					if (checkShift.isChecked()) {
						mainView.Vnc().SendKeyEvent(KeyData.Shift_L, true);
					}

					mainView.Vnc().SendKeyEvent(keysym, true);
					mainView.Vnc().SendKeyEvent(keysym, false);

					if (checkCtrl.isChecked()) {
						mainView.Vnc().SendKeyEvent(KeyData.Control_L, false);
					}
					if (checkAlt.isChecked()) {
						mainView.Vnc().SendKeyEvent(KeyData.Alt_L, false);
					}
					if (checkShift.isChecked()) {
						mainView.Vnc().SendKeyEvent(KeyData.Shift_L, false);
					}
				}
				dismiss();
			}
		});

		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(context, R.array.key_list,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerKey.setAdapter(adapter);

		spinnerKey.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				keysym = -1;

				switch (position) {

				case KeyData.Backspace_Index:
					keysym = KeyData.Backspace;
					break;

				case KeyData.Tab_Index:
					keysym = KeyData.Tab;
					break;

				case KeyData.Enter_Index:
					keysym = KeyData.Enter;
					break;

				case KeyData.Delete_index:
					keysym = KeyData.Delete;
					break;

				case KeyData.Escape_Index:
					keysym = KeyData.Escape;
					break;

				case KeyData.Home_Index:
					keysym = KeyData.Home;
					break;

				case KeyData.Left_Index:
					keysym = KeyData.Left;
					break;

				case KeyData.Up_Index:
					keysym = KeyData.Up;
					break;

				case KeyData.Right_Index:
					keysym = KeyData.Right;
					break;

				case KeyData.Down_Index:
					keysym = KeyData.Down;
					break;

				case KeyData.Page_Up_Index:
					keysym = KeyData.Page_Up;
					break;

				case KeyData.Page_Down_Index:
					keysym = KeyData.Page_Down;
					break;

				case KeyData.End_Index:
					keysym = KeyData.End;
					break;

				case KeyData.Print_Index:
					keysym = KeyData.Print;
					break;

				case KeyData.Insert_Index:
					keysym = KeyData.Insert;
					break;

				case KeyData.Break_Index:
					keysym = KeyData.Break;
					break;

				case KeyData.F1_Index:
					keysym = KeyData.F1;
					break;

				case KeyData.F2_Index:
					keysym = KeyData.F2;
					break;

				case KeyData.F3_Index:
					keysym = KeyData.F3;
					break;

				case KeyData.F4_Index:
					keysym = KeyData.F4;
					break;

				case KeyData.F5_Index:
					keysym = KeyData.F5;
					break;

				case KeyData.F6_Index:
					keysym = KeyData.F6;
					break;

				case KeyData.F7_Index:
					keysym = KeyData.F7;
					break;

				case KeyData.F8_Index:
					keysym = KeyData.F8;
					break;

				case KeyData.F9_Index:
					keysym = KeyData.F9;
					break;

				case KeyData.F10_Index:
					keysym = KeyData.F10;
					break;

				case KeyData.F11_Index:
					keysym = KeyData.F11;
					break;

				case KeyData.F12_Index:
					keysym = KeyData.F12;
					break;

				case KeyData.Shift_L_Index:
					keysym = KeyData.Shift_L;
					break;

				case KeyData.Shift_R_Index:
					keysym = KeyData.Shift_R;
					break;

				case KeyData.Control_L_Index:
					keysym = KeyData.Control_L;
					break;

				case KeyData.Control_R_Index:
					keysym = KeyData.Control_R;
					break;

				case KeyData.Caps_Lock_Index:
					keysym = KeyData.Caps_Lock;
					break;

				case KeyData.Shift_Lock_Index:
					keysym = KeyData.Shift_Lock;
					break;

				case KeyData.Alt_L_Index:
					keysym = KeyData.Alt_L;
					break;

				case KeyData.Alt_R_Index:
					keysym = KeyData.Alt_R;
					break;

				case KeyData.Space_Index:
					keysym = KeyData.Space;
					break;

				case KeyData.Exclam_Index:
					keysym = KeyData.Exclam;
					break;

				case KeyData.Quotedbl_Index:
					keysym = KeyData.Quotedbl;
					break;

				case KeyData.Numbersign_Index:
					keysym = KeyData.Numbersign;
					break;

				case KeyData.Dollar_Index:
					keysym = KeyData.Dollar;
					break;

				case KeyData.Ampersand_Index:
					keysym = KeyData.Ampersand;
					break;

				case KeyData.Apostrophe_Index:
					keysym = KeyData.Apostrophe;
					break;

				case KeyData.Parenleft_Index:
					keysym = KeyData.Parenleft;
					break;

				case KeyData.Parenright_Index:
					keysym = KeyData.Parenright;
					break;

				case KeyData.Asterisk_Index:
					keysym = KeyData.Asterisk;
					break;

				case KeyData.Plus_Index:
					keysym = KeyData.Plus;
					break;

				case KeyData.Comma_Index:
					keysym = KeyData.Comma;
					break;

				case KeyData.Minus_Index:
					keysym = KeyData.Minus;
					break;

				case KeyData.Period_Index:
					keysym = KeyData.Period;
					break;

				case KeyData.Slash_Index:
					keysym = KeyData.Slash;
					break;

				case KeyData.Colon_Index:
					keysym = KeyData.Colon;
					break;

				case KeyData.Semicolon_Index:
					keysym = KeyData.Semicolon;
					break;

				case KeyData.Less_Index:
					keysym = KeyData.Less;
					break;

				case KeyData.Equal_Index:
					keysym = KeyData.Equal;
					break;

				case KeyData.Greater_Index:
					keysym = KeyData.Greater;
					break;

				case KeyData.Question_Index:
					keysym = KeyData.Question;
					break;

				case KeyData.At_Index:
					keysym = KeyData.At;
					break;

				case KeyData.a_Index:
					keysym = KeyData.a;
					break;

				case KeyData.b_Index:
					keysym = KeyData.b;
					break;

				case KeyData.c_Index:
					keysym = KeyData.c;
					break;

				case KeyData.d_Index:
					keysym = KeyData.d;
					break;

				case KeyData.e_Index:
					keysym = KeyData.e;
					break;

				case KeyData.f_Index:
					keysym = KeyData.f;
					break;

				case KeyData.g_Index:
					keysym = KeyData.g;
					break;

				case KeyData.h_Index:
					keysym = KeyData.h;
					break;

				case KeyData.i_Index:
					keysym = KeyData.i;
					break;

				case KeyData.j_Index:
					keysym = KeyData.j;
					break;

				case KeyData.k_Index:
					keysym = KeyData.k;
					break;

				case KeyData.l_Index:
					keysym = KeyData.l;
					break;

				case KeyData.m_Index:
					keysym = KeyData.m;
					break;

				case KeyData.n_Index:
					keysym = KeyData.n;
					break;

				case KeyData.o_Index:
					keysym = KeyData.o;
					break;

				case KeyData.p_Index:
					keysym = KeyData.p;
					break;

				case KeyData.q_Index:
					keysym = KeyData.q;
					break;

				case KeyData.r_Index:
					keysym = KeyData.r;
					break;

				case KeyData.s_Index:
					keysym = KeyData.s;
					break;

				case KeyData.t_Index:
					keysym = KeyData.t;
					break;

				case KeyData.u_Index:
					keysym = KeyData.u;
					break;

				case KeyData.v_Index:
					keysym = KeyData.v;
					break;

				case KeyData.w_Index:
					keysym = KeyData.w;
					break;

				case KeyData.x_Index:
					keysym = KeyData.x;
					break;

				case KeyData.y_Index:
					keysym = KeyData.y;
					break;

				case KeyData.z_Index:
					keysym = KeyData.z;
					break;

				case KeyData.Braceleft_Index:
					keysym = KeyData.Braceleft;
					break;

				case KeyData.Bar_Index:
					keysym = KeyData.Bar;
					break;

				case KeyData.Braceright_Index:
					keysym = KeyData.Braceright;
					break;

				case KeyData.Asciitilde_Index:
					keysym = KeyData.Asciitilde;
					break;

				case KeyData.Bracketleft_Index:
					keysym = KeyData.Bracketleft;
					break;

				case KeyData.Backslash_Index:
					keysym = KeyData.Backslash;
					break;

				case KeyData.Bracketright_Index:
					keysym = KeyData.Bracketright;
					break;

				case KeyData.Asciicircum_Index:
					keysym = KeyData.Asciicircum;
					break;

				case KeyData.Underscore_Index:
					keysym = KeyData.Underscore;
					break;

				case KeyData.Grave_Index:
					keysym = KeyData.Grave;
					break;

				default:
					break;
				}

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

}
