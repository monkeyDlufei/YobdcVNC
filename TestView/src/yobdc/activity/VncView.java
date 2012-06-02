package yobdc.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class VncView extends ImageView {

	private Bitmap bmp;
	private int intArray[];
	
	public VncView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.my);
		bmp = bmp.copy(Bitmap.Config.RGB_565, true);
		
		intArray = new int[bmp.getWidth()*bmp.getHeight()];
		bmp.getPixels(intArray, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		this.setImageBitmap(bmp);
	}

}
