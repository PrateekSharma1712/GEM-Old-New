package com.prateek.gem.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.prateek.gem.R;

public class MyProgressDialog extends AlertDialog {

	private TextView dialogTextView;
	private String dialogText;
	
	
	public MyProgressDialog(Context context) {
		super(context);
		System.out.println("showing");
	}
	
	public MyProgressDialog(Context context, String dialogText) {
		super(context);
		this.dialogText = dialogText;
	}


	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();	 	 
		setContentView(R.layout.dialog_loading);
		dialogTextView = (TextView) findViewById(R.id.dialogText);
		if(dialogText != null){
			dialogTextView.setVisibility(View.VISIBLE);
			dialogTextView.setText(dialogText);
		}
		else{
			dialogTextView.setVisibility(View.GONE);
		}
		setCancelable(true);
	}
}
