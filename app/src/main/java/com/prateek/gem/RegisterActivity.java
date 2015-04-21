package com.prateek.gem;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.services.ServiceHandler;
import com.prateek.gem.utils.AppDataManager;
import com.prateek.gem.utils.AppSharedPreference;
import com.prateek.gem.utils.Utils;
import com.prateek.gem.views.MyProgressDialog;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends BaseActivity {

    private Spinner emailSpinner;
    private Button registerButton;
    private EditText adminName,adminNumber,numbercode,password,confirmPassword;
    private AccountManager mAccountManager;
    public String name,email,number,passwordValue;
    private AsyncTask<String,Void,Boolean> checkRegistrationTask;
    private AsyncTask<Void, Void, Boolean> registrationTask;
    /*private AsyncTask<Void, Void, Void> mRegisterTask;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        DebugLogger.message("AppDataManager.appContext :: "+ AppDataManager.appContext);

        emailSpinner = (Spinner) findViewById(R.id.email);
        adminName = (EditText) findViewById(R.id.adminName);
        adminNumber = (EditText) findViewById(R.id.adminNumber);
        numbercode = (EditText) findViewById(R.id.code);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        registerButton = (Button) findViewById(R.id.addregistrationButton);

        ArrayAdapter<String> emailAdapter = new ArrayAdapter<String>(baseActivity, android.R.layout.simple_spinner_dropdown_item, getAccountNames());
        emailAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        emailSpinner.setAdapter(emailAdapter);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(adminName);
                name = Utils.stringify(adminName.getText());
                email = Utils.stringify(emailSpinner.getSelectedItem().toString());
                String code = Utils.stringify(numbercode.getText());
                number = code + Utils.stringify(adminNumber.getText());
                passwordValue = Utils.stringify(password.getText());
                final String confirmPasswordValue = Utils.stringify(confirmPassword.getText());

                if(name.isEmpty()){
                    adminName.setError(getString(R.string.cannotempty));
                    adminName.requestFocus();
                }else if(number.isEmpty()){
                    adminNumber.setError(getString(R.string.cannotempty));
                    adminNumber.requestFocus();
                }else if(number.length() != 13){
                    adminNumber.setError(getString(R.string.wrongnumber));
                    adminNumber.requestFocus();
                }else if(code.isEmpty()){
                    numbercode.setError(getString(R.string.cannotempty));
                    numbercode.requestFocus();
                }else if(passwordValue.isEmpty()){
                    password.setError(getString(R.string.cannotempty));
                    password.requestFocus();
                }else if(passwordValue.length() < 4){
                    password.setError(getString(R.string.password));
                    password.requestFocus();
                }else if(confirmPasswordValue.isEmpty()){
                    confirmPassword.setError(getString(R.string.cannotempty));
                    confirmPassword.requestFocus();
                }else if(!passwordValue.equals(confirmPasswordValue)){
                    confirmPassword.setError(getString(R.string.notmatch));
                    confirmPassword.requestFocus();
                }else{
                    DebugLogger.message(name);
                    DebugLogger.message(email);
                    DebugLogger.message(number);
                    DebugLogger.message(passwordValue);
                    DebugLogger.message(confirmPasswordValue);

                    checkRegistrationTask = new AsyncTask<String, Void, Boolean>(){

                        MyProgressDialog pd = new MyProgressDialog(baseActivity, true, getString(R.string.load_checking_number));

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            pd.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... params) {
                            ServiceHandler handler = new ServiceHandler();
                            List<NameValuePair> list = new ArrayList<NameValuePair>();
                            list.add(new BasicNameValuePair("number", params[0]));
                            list.add(new BasicNameValuePair(AppConstants.SERVICE_ID, ""+ AppConstants.ServiceIDs.CHECK_USER_REGISTERED));
                            String jsonResponse = handler.makeServiceCall(AppConstants.URL_API, AppConstants.REQUEST_METHOD_POST, list);
                            DebugLogger.message(jsonResponse);
                            if(jsonResponse != null){
                                try {
                                    JSONObject jsonObject = new JSONObject(jsonResponse);
                                    if(!jsonObject.getBoolean("error")){
                                        return jsonObject.getBoolean("isregistered");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            return false;
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            super.onPostExecute(result);
                            DebugLogger.message("Resgistered "+result);

                            pd.dismiss();
                            if(!result){
                                DebugLogger.message("enter");
                                registrationTask.execute();
                            }else{
                                adminNumber.setError(getString(R.string.alreadyregistered));
                                adminNumber.requestFocus();
                            }
                            pd = null;

                        }

                    };

                    registrationTask = new AsyncTask<Void, Void, Boolean>(){

                        MyProgressDialog pd = new MyProgressDialog(baseActivity, true, getString(R.string.registering));

                        @Override
                        protected Boolean doInBackground(Void... params) {
                            List<NameValuePair> list = new ArrayList<NameValuePair>();
                            list.add(new BasicNameValuePair("regId", regid));
                            list.add(new BasicNameValuePair("name", name));
                            list.add(new BasicNameValuePair("email", email));
                            list.add(new BasicNameValuePair("number", number));
                            list.add(new BasicNameValuePair("password", passwordValue));

                            String json = new ServiceHandler().makeServiceCall(AppConstants.SERVER_URL, AppConstants.REQUEST_METHOD_POST, list);
                            JSONObject object = null;
                            DebugLogger.message("json"+json);
                            boolean result = false;
                            if(json != null) {
                                try {
                                    object = new JSONObject(json);
                                    if (!object.getBoolean("error")) {
                                        result = true;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return result;
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            pd.show();
                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            super.onPostExecute(result);
                            pd.dismiss();
                            DebugLogger.message("result"+result);
                            if(result) {
                                AppSharedPreference.storePreferences(AppConstants.ADMIN_ID, regid);
                                AppSharedPreference.storePreferences(AppConstants.ADMIN_NAME, name);
                                AppSharedPreference.storePreferences(AppConstants.ADMIN_EMAIL, email);
                                AppSharedPreference.storePreferences(AppConstants.ADMIN_PHONE, number);
                                AppSharedPreference.storePreferences(AppConstants.ADMIN_PASSWORD, passwordValue);
                                finish();
                            }

                        }
                    };

                    if(Utils.isConnected(baseActivity)){
                        checkRegistrationTask.execute(number,null,null);
                    }
                    //new AddAdmintask().execute(new String[]{name,number,code,passwordValue});
                }
            }
        });
    }

    private String[] getAccountNames() {
        mAccountManager = AccountManager.get(baseActivity);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
            DebugLogger.message("Account "+i+" \nName"+names[i]);
            DebugLogger.message("Type"+accounts[i].type);
        }
        return names;
    }
}
