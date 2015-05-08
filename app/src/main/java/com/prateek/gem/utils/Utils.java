package com.prateek.gem.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.prateek.gem.App;
import com.prateek.gem.AppConstants;
import com.prateek.gem.OnModeConfirmListener;
import com.prateek.gem.R;
import com.prateek.gem.logger.DebugLogger;
import com.prateek.gem.model.ExpenseOject;
import com.prateek.gem.model.Item;
import com.prateek.gem.model.SectionHeaderObject;
import com.prateek.gem.widgets.ConfirmationDialog;
import com.prateek.gem.widgets.MyToast;

public class Utils {

    private static float xFactor = -1;

    public enum ColorFilter{
        PRIMARY, PRIMARYDARK, ACCENT
    }
	
	public static void setActionBar(ActionBar actionBar,int title){
		actionBar.setTitle(title);
	}
	
	public static String stringify(Object a){
		if(a!=null){
			return a.toString().trim();
		}
		return "";
	}

	/*
	 * usage: date formatting into Friday, Oct 18, 2013
	 */
	public static String formatDate(String dateString){
		String formattedDate = "";
		//String format = "cccc, MMM dd, yyyy";
		String format = "MMM dd, yyyy";
		Date date = new Date(Long.parseLong(dateString));
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
		formattedDate = sdf.format(date);			
		return formattedDate;
	}

    /*
	 * usage: date formatting into Friday, Oct 18, 2013
	 */
    public static String formatDateWithoutYear(String dateString){
        String formattedDate = "";
        String format = "MMM dd";
        Date date = new Date(Long.parseLong(dateString));
        SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
        formattedDate = sdf.format(date);
        return formattedDate;
    }

	
	/*
	 * usage: date formatting into Friday, Oct 18, 2013
	 */
	public static String formatShortDate1(String dateString){
		String formattedDate = "";
		String format = "MMdd";
		Date date = new Date(Long.parseLong(dateString));	
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
		formattedDate = sdf.format(date);			
		return formattedDate;
	}
	
	/*
	 * usage: date formatting into 20-02-15
	 */
	public static String formatShortDate(String dateString){
		String formattedDate = "";
		String format = "dd"+"-"+"MM"+"-"+"yy";
		Date date = new Date(Long.parseLong(dateString));	
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
		formattedDate = sdf.format(date);			
		return formattedDate;
	}
	
	/**
	 * 
	 * @param dateString
	 * @return month name
	 */
	public static String getMonthName(String dateString){
		String formattedDate = "";
		String format = "MMMM yyyy";
		Date date = new Date(Long.parseLong(dateString));	
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.US);
		formattedDate = sdf.format(date);			
		return formattedDate;
	}

    public static int dpToPixels(int dpValue) {
        return (int) (dpValue * xFactor);
    }
	
	/*
	 * usage: loads same fragment to refresh the UI
	 */
	
	/*public static void reloadFragment(Fragment fragment,FragmentManager manager,Bundle bundle){	
		System.out.println("reload");
		FragmentTransaction transaction = manager.beginTransaction();
		fragment.setArguments(bundle);		
		transaction.replace(R.id.content_frame, fragment);
		transaction.commit();
	}*/
	
	
	/*
	 * Round to certain number of decimals
     * 
     * @param d
     * @param decimalPlace
     * @return
	 */
	
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
    
    /*
     * show normal correction text in Toast
     */
    public static void showToast(Context context, String message){
    	//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    	new MyToast(context,message,false);
    }
    
    /*
     * show normal correction text in Toast
     */
    public static void showToastLong(Context context, String message){
    	//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    	new MyToast(context,message,true);    	
    }
    
    /*
     * Convert wrong phone number formats to correct format 98 87 234323 - 9887234323 and add +91 everywhere     * 
     * 
     * @param String phone number
     * @return String
     * 
     */
    public static String correctNumber(String phoneNumber){
    	String result = phoneNumber;
    	result = phoneNumber.replaceAll(" ", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\-", "");
    	
    	if(!result.subSequence(0, 3).equals("+91")){
    		if(result.charAt(0) == '0'){
    			result = result.substring(1, result.length());
    		}
    		result = "+91"+result; 
    	}
		return result; 
    	
    }
    
    /*
     *Create or modify directory in media to store group photo
     *@return file path 
     */
    public static Uri getFolderPath(String folderName,boolean createIfNotExists){
    	if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && Environment.getExternalStorageDirectory().canWrite()){
			String filepath = Environment.getExternalStorageDirectory() + "/" + AppConstants.APP_NAME;
			File file = new File(filepath);
			/*if(Utils.checkFileCreation(file)){
				Log.e("FILE", "1Cannot create directory");
				return null;
			}*/
			filepath = filepath + "/" + folderName;
			file = new File(filepath);	
			/*if(Utils.checkFileCreation(file)){
				Log.e("FILE", "2Cannot create directory");
				return null;
			}*/
			if(file.isDirectory() && file.exists()){
				return Uri.fromFile(file);
			}
			else{
				if(createIfNotExists){
					if(file.mkdirs()){
						return Uri.fromFile(file);
					}
				}
			}
    	}    
		return null;    	
    }
    
    public static boolean deleteFile(String path)
	{
		if(path != null)
		{
			File file  = new File(path);
			if(file.exists())
				return file.delete();
			else
				Log.e("FILE", "file not exixt @ " +path);
		}
		return false;
	}

	private static boolean checkFileCreation(File file) {
		if(!file.exists())
		{
			Log.e("FILE",".file exists");
			if(!file.mkdirs())
			{		
				Log.e("FILE",".file mkdirs");
				return true;
			}
			return true;
		}
		else{
			Log.e("FILE","else");
			return false;
		}		
	}
	
	/**
	 * @return time of device in millisecs with timezone set to "GMT+5:30"
	 */
	public static long getCurrentTimeInMilliSecs()
	{
		//I Luv India :)
		return Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTimeInMillis();
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    final float totalPixels = width * height;
	    final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

	    return inSampleSize;
	}
	
	public static List<Item> gatherExpenses(int orderBy,List<ExpenseOject> expenses) {		
		Map<SectionHeaderObject, List<ExpenseOject>> groupByMap = new TreeMap<SectionHeaderObject, List<ExpenseOject>>();
		List<ExpenseOject> existingList;
		if(orderBy == AppConstants.DATE_WISE){						
			for(ExpenseOject expense : expenses){
				SectionHeaderObject sectionObject = new SectionHeaderObject();
				sectionObject.setHeaderTitle(expense.getDate());				
				if(groupByMap.containsKey(sectionObject)){
					existingList = groupByMap.get(sectionObject);
					existingList.add(expense);
				}
				else{
					existingList = new ArrayList<ExpenseOject>();
					existingList.add(expense);
				}
				groupByMap.put(sectionObject, existingList);
			}		
		}
		
		List<Item> items = new ArrayList<Item>();
		Set<Entry<SectionHeaderObject,List<ExpenseOject>>> entries = groupByMap.entrySet();
		for(Entry<SectionHeaderObject,List<ExpenseOject>> entry:entries){
			//Add Total Amount in SectionHeaderObject, since cannot add at he time of map evaluation because of map property
			//(key cannot be changed)
			SectionHeaderObject sectionHeaderObject = new SectionHeaderObject();
			sectionHeaderObject.setHeaderTitle(entry.getKey().getHeaderTitle());
			float amount = 0f;
			for(ExpenseOject expense:entry.getValue()){
				amount += expense.getAmount();
			}
			sectionHeaderObject.setAmount(amount);
			items.add(sectionHeaderObject);
			for(ExpenseOject expense:entry.getValue()){
				items.add(expense);
			}
		}
		return items;
	}
	
	public static List<SectionHeaderObject> getExpenseDateHeaders(List<ExpenseOject> list){
		List<Item> items = gatherExpenses(AppConstants.DATE_WISE, list);
		List<SectionHeaderObject> sectionHeaderList = new ArrayList<SectionHeaderObject>();
		for (Item item : items) {
			if(!item.isSection()){
				sectionHeaderList.add((SectionHeaderObject)item);
			}
		}
		return sectionHeaderList;
		
	}

	public static void showErrorOnButton(View view,FragmentActivity activity,String message) {
		Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake); 
		view.startAnimation(shake);
		((Button) view).setHint(message);
		((Button) view).setBackgroundResource(R.drawable.error_drawable);
		view.requestFocus();
	}

	public static void showError(View view,FragmentActivity activity) {
		Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake); 
		view.startAnimation(shake);
		((EditText) view).setError(activity.getResources().getString(R.string.warning_enter_group_message));
		((EditText) view).setBackgroundResource(R.drawable.error_drawable);
		view.requestFocus();
	}
	
	public static void showError(View view,FragmentActivity activity,String message) {
		Animation shake = AnimationUtils.loadAnimation(activity, R.anim.shake); 
		view.startAnimation(shake);
		((EditText) view).setError(message);
		((EditText) view).setBackgroundResource(R.drawable.error_drawable);
		view.requestFocus();
	}

	public static int uploadFile(String filepath) {
		int serverResponseCode = 0;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        File sourceFile = new File(filepath); 
         
        if (!sourceFile.isFile()) {
              
             Log.e("uploadFile", "Source File not exist :"+filepath); 
             return 0;
          
        }
        else
        {
             try { 
                  
                   // open a URL connection to the Servlet
                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
                 URL url = new URL(AppConstants.URL_UPLOADTOSERVER);
                  
                 // Open a HTTP  connection to  the URL
                 conn = (HttpURLConnection) url.openConnection(); 
                 conn.setDoInput(true); // Allow Inputs
                 conn.setDoOutput(true); // Allow Outputs
                 conn.setUseCaches(false); // Don't use a Cached Copy
                 conn.setRequestMethod("POST");
                 conn.setRequestProperty("Connection", "Keep-Alive");
                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                 conn.setRequestProperty("uploaded_file", filepath); 
                  
                 dos = new DataOutputStream(conn.getOutputStream());
        
                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
	                     + filepath + "\"" + lineEnd);
                  
                 dos.writeBytes(lineEnd);
        
                 // create a buffer of  maximum size
                 bytesAvailable = fileInputStream.available(); 
        
                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
                 buffer = new byte[bufferSize];
        
                 // read file and write it into form...
                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                    
                 while (bytesRead > 0) {
                      
                   dos.write(buffer, 0, bufferSize);
                   bytesAvailable = fileInputStream.available();
                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                    
                  }
        
                 // send multipart form data necesssary after file data...
                 dos.writeBytes(lineEnd);
                 dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        
                 // Responses from the server (code and message)
                 serverResponseCode = conn.getResponseCode();
                 String serverResponseMessage = conn.getResponseMessage();
                   
                 Log.i("uploadFile", "HTTP Response is : "
                         + serverResponseMessage + ": " + serverResponseCode);
                  
                 if(serverResponseCode == 200){
                	 String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                            +" http://www.preetimodi.com/gem_api/images"
                                            +filepath;
                              
                     Log.i("UPLOAD", msg);        
                           
                 }    
                  
                 //close the streams //
                 fileInputStream.close();
                 dos.flush();
                 dos.close();
                   
            } catch (MalformedURLException ex) {
               
                ex.printStackTrace();
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
            } catch (Exception e) {  
                e.printStackTrace();
                Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
            }
            return serverResponseCode;
         } // End else block 
       }
	
	public static boolean hasConnection(Context context){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
		
	}

	public static CharSequence addRupeeIcon(float amount) {		
		return AppConstants.RUPEE_ICON+amount;
	}
	
	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	public static File createBackupFile(String backupgem) {
	    File file = new File(Environment.getExternalStorageDirectory(), backupgem);
	    if(!file.exists()){
		    if (!file.mkdirs()) {
		        Log.e("FILE", "Directory not created");
		    }
	    }
	    return file;
	}
	
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

	public static String[] getMonths() {
		List<String> monthsList = new ArrayList<String>();
		for(ExpenseOject e : App.getInstance().getExpensesList()) {
			String monthName = getMonthName(String.valueOf(e.getDate()));
			if(!monthsList.contains(monthName)) {
				monthsList.add(monthName);
			}
		}
		
		String months[] = new String[monthsList.size()];
		
		for(int i =0;i<monthsList.size();i++) {
			months[i] = monthsList.get(i);
		}
		
		return months;
	}

    public static boolean isSDPresent() {
        Boolean status = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        return status;
    }

    /**
     * Method to hide keyboard
     *
     * @param eText
     *
     * @return none
     */
    public static void hideKeyboard(EditText eText) {
        DebugLogger.method("BBDataManager :: hideKeyboard");
        DebugLogger.message("Hide Keyboard");
        InputMethodManager manager = (InputMethodManager) AppDataManager.appContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null && eText != null) {
            manager.hideSoftInputFromWindow(eText.getWindowToken(), 0);
        }
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = Utils.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Utils.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Utils.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = Utils.getNetworkInfo(context);
        return (info != null && info.isConnected() && Utils.isConnectionFast(
                info.getType(), info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
				/*
				 * Above API level 7, make sure to set android:targetSdkVersion
				 * to appropriate level to use these
				 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static void toggleVisibility(View view, boolean withAnimation) {
        DebugLogger.message("view.getVisibility()"+view.getVisibility());
        switch(view.getVisibility()) {
            case View.GONE:
                view.setVisibility(View.VISIBLE);
                if(withAnimation)
                    view.startAnimation(inFromBottomAnimation(1000));
                break;
            case View.VISIBLE:
                view.setVisibility(View.GONE);
                if(withAnimation)
                    view.startAnimation(outFromBottomAnimation(1000));
                break;
        }
    }

    public static Animation inFromBottomAnimation(long duration) {

        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(duration);

        inFromBottom.setInterpolator(new AccelerateInterpolator());
        return inFromBottom;
    }

    public static Animation outFromBottomAnimation(long duration) {

        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f);
        inFromBottom.setDuration(duration);

        inFromBottom.setInterpolator(new AccelerateInterpolator());
        return inFromBottom;
    }

    public static void openConfirmationDialog(ActionBarActivity context, String itemNamesString, boolean withButtons, OnModeConfirmListener onModeConfirmListener) {
        ConfirmationDialog mcd = new ConfirmationDialog();
        mcd.setOnModeConfirmListener(onModeConfirmListener);
        Bundle bundle = new Bundle();
        bundle.putString(ConfirmationDialog.TITLE, context.getString(R.string.selected_item));
        bundle.putInt(AppConstants.ConfirmConstants.CONFIRM_KEY, AppConstants.ConfirmConstants.CONFIRM_SELECTED_ITEMS_LIST);
        bundle.putStringArrayList(ConfirmationDialog.LIST, new ArrayList(Arrays.asList(itemNamesString.split(", "))));
        if(withButtons) {
            bundle.putString(ConfirmationDialog.BUTTON1, context.getString(R.string.empty_string));
            bundle.putString(ConfirmationDialog.BUTTON2, context.getString(R.string.button_confirm_mode));
        }
        mcd.setArguments(bundle);
        mcd.show(context.getSupportFragmentManager(), "ComfirmationDialog");
    }

    public static int getColorFilter(ColorFilter filter) {
        switch (filter) {
            case PRIMARY:
                return AppDataManager.appContext.getResources().getColor(R.color.theme_default_primary);

            case PRIMARYDARK:
                return AppDataManager.appContext.getResources().getColor(R.color.theme_default_primary_dark);

            case ACCENT:
                return AppDataManager.appContext.getResources().getColor(R.color.theme_default_accent);
        }

        return AppDataManager.appContext.getResources().getColor(R.color.theme_default_primary);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
