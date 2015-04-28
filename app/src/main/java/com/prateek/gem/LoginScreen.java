package com.prateek.gem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.Users;
import com.prateek.gem.persistence.DB;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.MyProgressDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginScreen extends BaseActivity {

    Button registerButton = null, loginButton = null;
    private EditText phoneNumberView,mCodeView,mPasswordView = null;
    private UserLoginTask mAuthTask;
    private String mPhoneNumber;
    private String mPassword;
    private Users user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DebugLogger.message("onCreate");
        super.onCreate(savedInstanceState);
        baseActivity = this;
        String regId = AppSharedPreference.getPreferenceString(AppConstants.ADMIN_ID);
        DebugLogger.message("regId :: "+regId);
        if(regId != null && !TextUtils.isEmpty(regId)) {
            finish();
            startActivity(mainLandingIntent);
        } else {
            setContentView(R.layout.activity_login_screen);


            phoneNumberView = (EditText) findViewById(R.id.phoneNumber);
            mCodeView = (EditText) findViewById(R.id.code);
            mPasswordView = (EditText) findViewById(R.id.password);


            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            registerButton = (Button) findViewById(R.id.registerButton);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeyboard(mCodeView);
                    Intent intent = new Intent(LoginScreen.this, RegisterActivity.class);
                    startActivity(intent);

                }
            });

            loginButton = (Button) findViewById(R.id.loginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utils.isConnected(baseActivity)) {
                        Utils.hideKeyboard(mCodeView);
                        attemptLogin();
                    } else {
                        Utils.showToast(baseActivity, getString(R.string.nonetwork));
                    }
                }
            });

            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        Utils.hideKeyboard(phoneNumberView);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugLogger.method("onResume");
        String regId = AppSharedPreference.getPreferenceString(AppConstants.ADMIN_ID);
        DebugLogger.message("regId :: "+regId);
        if(regId != null && !TextUtils.isEmpty(regId)) {
            String number = AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PHONE);
            String password = AppSharedPreference.getPreferenceString(AppConstants.ADMIN_PASSWORD);
            phoneNumberView.setText(number);
            mPasswordView.setText(password);
            finish();
            startActivity(mainLandingIntent);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        phoneNumberView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mPhoneNumber = mCodeView.getText().toString() + phoneNumberView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mPhoneNumber)) {
            phoneNumberView.setError(getString(R.string.error_field_required));
            focusView = phoneNumberView;
            cancel = true;
        } else if(mPhoneNumber.length() != 13){
            phoneNumberView.setError(getString(R.string.wrongnumber));
            focusView = phoneNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private int errorCode = 1;
        ServiceHandler handler = new ServiceHandler();
        String message;
        MyProgressDialog pd = new MyProgressDialog(baseActivity, true, "Signing in...");

        @Override
        protected Boolean doInBackground(Void... params) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.LOGIN));
            list.add(new BasicNameValuePair(DB.TMembers.PHONE_NUMBER, mPhoneNumber));
            list.add(new BasicNameValuePair("mPassword", mPassword));
            String json = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST,list);

            JSONObject object = null;
            boolean result = false;
            if(json != null){
                try{
                    System.out.println("try");
                    object = new JSONObject(json);
                    if(!object.getBoolean("error")){
                        user = new Users();
                        user.setGcmRegId(object.getString(Users.GCM_REG_ID));
                        user.setUserName(object.getString(Users.USERNAME));
                        user.setPhoneNumber(mPhoneNumber);
                        user.setPassword(mPassword);
                        user.setEmail(object.getString(Users.EMAIL));
                        result = true;
                    }else{
                        message = object.getString("message");
                        result = false;

                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }else{
                result = false;
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pd.dismiss();
            if(success){
                AppSharedPreference.storePreferences(AppConstants.ADMIN_ID, user.getGcmRegId());
                AppSharedPreference.storePreferences(AppConstants.ADMIN_NAME, user.getUserName());
                AppSharedPreference.storePreferences(AppConstants.ADMIN_EMAIL, user.getEmail());
                AppSharedPreference.storePreferences(AppConstants.ADMIN_PHONE, user.getPhoneNumber());
                AppSharedPreference.storePreferences(AppConstants.ADMIN_PASSWORD, user.getPassword());


                finish();
                startActivity(mainLandingIntent);
                App.getInstance().setAdmin(user);
            }
            else {
                System.out.println("Message " + message);
                if (message != null) {
                    if (message.equals("passwordwrong")) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                    } else if (message.equals("usernotfound")) {
                        phoneNumberView.setError(getString(R.string.notregistered));
                    }
                } else {
                    Utils.showToast(baseActivity, "Something went wrong, please try again");
                }
            }
            mAuthTask = null;
				/*if (success) {
					System.out.println("success 1");
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("registeringId", mPhoneNumber);
					System.out.println(mPhoneNumber);
					editor.commit();
					// Setting Admin
					GEMApp.getInstance().setAdmin(admin);
					System.out.println(GEMApp.getInstance().getAdmin());
					finish();
					intent = new Intent(SetupActivity.this,MainActivity.class);
					startActivity(intent);

				} else {
					if(errorCode == 2){
						System.out.println("2");
						phoneNumberView.setError(getString(R.string.notregistered));
						phoneNumberView.requestFocus();
					}else if(errorCode == 1){
						System.out.println("1");
						mPasswordView.setError(getString(R.string.error_incorrect_password));
						mPasswordView.requestFocus();
					}
				}*/
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            pd.dismiss();
        }
    }

}
