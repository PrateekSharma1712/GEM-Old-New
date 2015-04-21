package com.prateek.gem.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.prateek.gem.R;

public class CustomView extends LinearLayout {

	View view = null;
	LayoutInflater inflator = null;
	
	public CustomView(Context context) {
		super(context);
		inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.key_value_layout, this);
		view = inflator.inflate(R.layout.key_value_layout, this);
	}

	public CustomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CustomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public View getView() {
		return view;
	}

}
