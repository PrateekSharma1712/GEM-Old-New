package com.prateek.gem.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.prateek.gem.R;

public class MyToast extends Toast {
	
	View view;
	Toast toast;
	String message;	
	
	public MyToast(Context context) {
		super(context);
		
	}

	public MyToast(Context context, String message, boolean isLong) {
		super(context);
		this.message = message;
		//Typeface font1 = Typeface.createFromAsset(context.getAssets(), "Gruppo-Regular.ttf");
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = li.inflate(R.layout.toast_layout, null);
		TextView text = (TextView) view.findViewById(R.id.toastTextView);
		text.setText(message);
		//text.setTypeface(font1,Typeface.BOLD);
		toast = new Toast(context);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		if(isLong)
			toast.setDuration(Toast.LENGTH_LONG);
		else
			toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
		showToast();
	}

	public void showToast() {
		toast.show();
	}
	
}
