package com.prateek.gem.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by prateek on 23/11/14.
 */
public class AppDialog {

    private static AlertDialog.Builder builder = null;
    private static DialogClickListener mDialogClickListener = null;

    public static void init(Context context, String title, String message, String[] items) {

        initializeBuilder(context);

        // setting listener for clicks
        mDialogClickListener = (DialogClickListener) context;

        if(title != null) {
            builder.setTitle(title);
        }

        if(message != null) {
            builder.setMessage(message);
        }

        if(items != null && items.length != 0) {
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDialogClickListener.onItemClick(which);
                }
            });
        }

        //Showing Dialog
        show();

    }

    private static void initializeBuilder(Context context) {
        if(builder == null) {
            builder = new AlertDialog.Builder(context);
        }
    }

    /**
     * method to show dialog, initiated in init
     *
     */
    private static void show() {
        builder.create();
        builder.show();
    }

    public interface DialogClickListener {
        public void onItemClick(int position);
    }
}
