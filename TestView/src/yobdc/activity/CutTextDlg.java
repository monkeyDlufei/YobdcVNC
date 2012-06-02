package yobdc.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CutTextDlg extends Dialog {

	private Context context;
	private TestViewActivity mainView;
	
	private String text = null;
	
	public CutTextDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.cut);
		
		mainView = (TestViewActivity) context;
		
		final EditText et_text = (EditText)findViewById(R.id.edit_passwd);
		
		Button bt_sendcut = (Button) findViewById(R.id.bt_sendcut);
		if (bt_sendcut!=null) {
			bt_sendcut.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					text = et_text.getText().toString();
					mainView.Vnc().SendCutText(text);
					
					dismiss();
				}
			});
		}
		
		Button bt_paste = (Button)findViewById(R.id.bt_paste);
		if (bt_paste!=null) {
			bt_paste.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
					text = et_text.getText().toString();
					text += cm.getText();
					et_text.setText(text);
				}
			});
		}
	}

}
