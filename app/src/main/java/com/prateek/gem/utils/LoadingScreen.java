package com.prateek.gem.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.prateek.gem.AppConstants;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.widgets.MyProgressDialog;

/**
 * Created by prateek on 12/12/14.
 */
public class LoadingScreen {

    private static ProgressDialog progressDialog = null;

    public static void showLoading(Context context, String message) {
        if (context == null) {
            DebugLogger.message("Can not show the progress dialog");
            return;
        }

        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                DebugLogger.message("Can not show the progress dialog");
                return;
            }
        }

        dismissProgressDialog();

        progressDialog = new ProgressDialog(context);

        if (message == null || message.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            progressDialog.setMessage("Loading ...");
        } else {
            progressDialog.setMessage(message);
        }

        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        // show the progress dialog
        progressDialog.show();
    }

    /**
     * Method is to update the indicator message
     *
     * @param message
     */
    public static void updateIndicatorMessage(String message) {
        if (progressDialog != null) {
            progressDialog.setMessage(message);
        }
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            try {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
                DebugLogger.error("LoadingScreen :: dismissProgressDialog :: e : " + e);
            }
            progressDialog = null;
        }
    }
}
