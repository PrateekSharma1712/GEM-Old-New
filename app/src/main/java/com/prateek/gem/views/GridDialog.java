package com.prateek.gem.views;

import com.prateek.gem.AppConstants;
import com.prateek.gem.OnModeConfirmListener;
import com.prateek.gem.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class GridDialog extends DialogFragment{	
	
	OnModeConfirmListener modeConfirmListener;
	
	public GridDialog() {
		super();		
	}	
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
        	modeConfirmListener = (OnModeConfirmListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onModeConfirmListener");
        }
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(getActivity());
		Window window = dialog.getWindow();
		window.requestFeature(Window.FEATURE_NO_TITLE);
		window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.dialog_grid_detail);
		
		TextView dialogHeading = (TextView) dialog.findViewById(R.id.dialogHeading);
		dialogHeading.setText(getArguments().getString("Title"));		
		ImageView cameraButton = (ImageView) dialog.findViewById(R.id.cameraButton);
		ImageView existingButton = (ImageView) dialog.findViewById(R.id.existingButton);
		cameraButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				modeConfirmListener.openNewActivity(AppConstants.REQUEST_CODE_CLICK_IMAGE);				
				dismiss();
			}
		});
		
		existingButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				modeConfirmListener.openNewActivity(AppConstants.REQUEST_CODE_FROM_GALLERY);
				dismiss();
			}
		});
		return dialog;	
	}

}
