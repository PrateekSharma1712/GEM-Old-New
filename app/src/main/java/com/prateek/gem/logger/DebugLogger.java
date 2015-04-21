package com.prateek.gem.logger;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.prateek.gem.AppConstants;
import com.prateek.gem.R;
import com.prateek.gem.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * DebugLogger class; used to get the log from the customers for debugging
 */
public class DebugLogger {
	private static String METHODS	= "methods";
	private static String ERRORS	= "errors";
	private static String MESSAGES	= "messages";
	
	private static File logFile		= null;
	
	// instance for DebugLogger 
	private static DebugLogger instanceLogger = null;
	
	public static boolean enabled	= false;
	
	/*
	 * Method to get the instance for DebugLogger class
	 */
	public static DebugLogger sharedDebugLog() {
		if (instanceLogger == null) {
			instanceLogger = new DebugLogger();
			instanceLogger.initialize();
		}
		return instanceLogger;
	}
	
	/*
	 * Method to initialize debug logger
	 */
	public void initialize() {
		
	}
	
	/*
	 * Method to print the log into file for debugging
	 */
	private static void debugLog(String log) {
//		// TEMP: print the log in console
		if (enabled) {
			writeToLog(log);
		}
		System.out.println(log);
	}
	
	public static void method(Object log) {
		debugLog(METHODS, log);
	}
	
	public static void message(Object log) {
		debugLog(MESSAGES, log);
	}
	
	public static void data(Object log) {
//		debugLog(DATAS, log);
	}
	
	public static void error(Object log) {
		debugLog(ERRORS, log);
//		Log.e(ERRORS, log.toString());
	}
	
	public static void debugLog(String id, Object log) {
		if (id == null) {
			id = AppConstants.EMPTY_STRING;
		}
		debugLog("!@"+id + " : " + log);
	}
	
	public static void initLog(Context context) {
		String storagePath = "";
		if (Utils.isSDPresent()) {
			File sdCard = Environment.getExternalStorageDirectory();
			storagePath = sdCard.getPath();
		} else {
			storagePath = context.getFilesDir().getAbsolutePath();
		}
		File dataDir = new File(storagePath + "/"
				+ context.getResources().getString(R.string.app_name) + "/");
		dataDir.mkdir();
		System.out.println("Logger path : " + dataDir.getAbsolutePath());
		logFile = new File(dataDir, "log.txt");
	}
	
	public static void uninit() {
		if (logFile != null && logFile.exists()) {
			logFile.delete();
		}
	}
	
	public static void disableLog(Context context) {
		String storagePath = "";
		if (Utils.isSDPresent()) {
			File sdCard = Environment.getExternalStorageDirectory();
			storagePath = sdCard.getPath();
		} else {
			storagePath = context.getFilesDir().getAbsolutePath();
		}
		File dataDir = new File(storagePath + "/"
				+ context.getResources().getString(R.string.app_name) + "/");
		logFile = new File(dataDir, "log.txt");
		uninit();
	}
	
	public static void writeToLog(String log) {
		if (logFile == null) {
			Log.e("", "Can not write the logs");
			return;
		}
		try {
			FileWriter fw = new FileWriter(logFile, true); 

			Calendar cal = Calendar.getInstance();
			Date currentLocalTime = cal.getTime();
			DateFormat date = new SimpleDateFormat("dd-MMM-yyy HH:mm:ss z",
					Locale.US);
			String localTime = date.format(currentLocalTime);
			fw.write("\n");
			fw.write(localTime);	// appends current time to the file
			fw.write(": " + log);	// appends the string to the file
			fw.close();
		} catch (Exception e) {
			Log.e("", e.toString());
		}

	}
}
