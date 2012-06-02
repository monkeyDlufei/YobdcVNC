package yobdc.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConnectDlg extends Dialog {

	private Context context;
	private TestViewActivity mainView;
	
	public String ip = null;
	public String passwd = null;

	public ConnectDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.connect);

		mainView = (TestViewActivity) context;

		final EditText edit_ip = (EditText) findViewById(R.id.edit_ip);
		final EditText edit_passwd = (EditText) findViewById(R.id.edit_passwd);
		Button bt_connect = (Button) findViewById(R.id.bt_connect);

		if (bt_connect != null) {
			bt_connect.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ip = edit_ip.getText().toString();
					passwd = edit_passwd.getText().toString();
					
					mainView.Vnc().ip = ip;
					mainView.Vnc().passwd = passwd;
					mainView.Vnc().InitVnc();
					
					dismiss();
				}
			});
		}
	}

}
