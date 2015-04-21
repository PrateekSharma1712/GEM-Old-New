package com.prateek.gem.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.prateek.gem.AppConstants;
import com.prateek.gem.logger.DebugLogger;

/**
 * Created by prateek on 18/11/14.
 */
public class AppSharedPreference {
    // shared reference
    private static AppSharedPreference preferences = null;

    public static AppSharedPreference sharedPreference() {
        if (preferences == null) {
            DebugLogger
                    .message("sharedPreference :: initialize BBSharedPreference");
            preferences = new AppSharedPreference();
        }

        return preferences;
    }

    public static SharedPreferences.Editor getEditor(String key, Activity activity) {
        SharedPreferences preference = null;
        if (activity != null) {
            preference = activity.getSharedPreferences(key, 0);
        } else if (AppDataManager.appContext != null) {
            preference = AppDataManager.appContext.getSharedPreferences(key, 0);
        }
        if (preference == null) {
            DebugLogger
                    .message("getEditor :: There is no preferences for given key");
        }
        return preference.edit();
    }

    /**
     * Method to retrieve the account preferences
     *
     * @param key
     * @return String
     */
    public static String getPreferenceString(String key) {
        DebugLogger.method("BBSharedPreference :: getPreferenceString");
        DebugLogger.method("AppDataManager.currentScreen :: " + AppDataManager.currentScreen);
        DebugLogger.method("AppDataManager.appContext :: " + AppDataManager.appContext);
        SharedPreferences preference = getPreferencesFor(
                AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen == null ? AppDataManager.appContext
                        : AppDataManager.currentScreen);
        if (preference == null) {
            DebugLogger.message("Account preference can not be loaded.");
            return AppConstants.EMPTY_STRING;
        }
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Key is null or empty. Account Preference can not be loaded.");
            return AppConstants.EMPTY_STRING;
        }
        if (preference.contains(key) == false) {
            DebugLogger.message("Key is not found in the Account preferences.");
            return AppConstants.EMPTY_STRING;
        }
        return preference.getString(key, AppConstants.EMPTY_STRING);
    }

    /**
     * Method to retrieve the account preferences
     *
     * @param key
     * @return String
     */
    public static boolean getPreferenceBoolean(String key) {
        DebugLogger.method("BBSharedPreference :: getPreferenceBoolean");
        DebugLogger.method("AppDataManager.currentScreen :: " + AppDataManager.currentScreen);
        DebugLogger.method("AppDataManager.appContext :: " + AppDataManager.appContext);
        SharedPreferences preference = getPreferencesFor(
                AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen == null ? AppDataManager.appContext
                        : AppDataManager.currentScreen);
        if (preference == null) {
            DebugLogger.message("Account preference can not be loaded.");
            return false;
        }
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Key is null or empty. Account Preference can not be loaded.");
            return false;
        }
        if (preference.contains(key) == false) {
            DebugLogger.message("Key is not found in the Account preferences.");
            return false;
        }
        return preference.getBoolean(key, false);
    }

    /*
	 * Method to return the SharedPreference based on the given key
	 *
	 * @param String: Key to get the shared preference activity
	 *
	 * @param Activity: activity to get the preferences
	 *
	 * @return SharedPreferences: returns the SharedPreferences
	 */
    public static SharedPreferences getPreferencesFor(String key,
                                                      Context context) {
        DebugLogger
                .method("SharedPreferences :: getPreferencesFor :: activity : "
                        + context);
        if (context == null || key == null) {
            DebugLogger
                    .message("SharedPreferences :: getPreferencesFor :: Can not get the preference with null");
            return null;
        }
        SharedPreferences preference = context.getSharedPreferences(key, 0);
        if (preference == null) {
            DebugLogger
                    .message("getPreferencesFor :: There is no preferences for given key");
        }
        return preference;
    }

    /**
     * Method to store the account preferences
     *
     * @param key
     * @param value
     * @return none
     */
    public static void storeAccPreference(String key, String value) {
        DebugLogger.method("BBSharedPreference :: storeAccPreference");
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            
            DebugLogger
                    .message("Key is null or empty. Account preferences can not be stored.");
            return;
        }
        if (value == null || value.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Value for given key is null or empty. Account preferences can not be stored.");
            return;
        }
        SharedPreferences.Editor editor = getEditor(AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen);
        editor.putString(key, value);
        editor.commit();
        DebugLogger.message("Account Preference is stored successfully.");
    }

    /**
     * Method to store the account preferences
     *
     * @param key
     * @param value
     * @return none
     */
    public static void storeAccPreference(String key, boolean value) {
        DebugLogger.method("BBSharedPreference :: storeAccPreference");
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {

            DebugLogger
                    .message("Key is null or empty. Account preferences can not be stored.");
            return;
        }
        SharedPreferences.Editor editor = getEditor(AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen);
        editor.putBoolean(key, value);
        editor.commit();
        DebugLogger.message("Account Preference is stored successfully.");
    }

    public static void storePreferences(String key, int value) {
        DebugLogger.method("BBSharedPreference :: storeAccPreference");
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Key is null or empty. Account preferences can not be stored.");
            return;
        }

        SharedPreferences.Editor editor = getEditor(AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen);
        editor.putInt(key, value);
        editor.commit();
        DebugLogger.message("Account Preference is stored successfully.");
    }

    public static void storePreferences(String key, boolean value) {
        DebugLogger.method("BBSharedPreference :: storeAccPreference");
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Key is null or empty. Account preferences can not be stored.");
            return;
        }

        SharedPreferences.Editor editor = getEditor(AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen);
        editor.putBoolean(key, value);
        editor.commit();
        DebugLogger.message("Account Preference is stored successfully.");
    }

    /**
     * Method to store the account preferences
     *
     * @param key
     * @param value
     * @return none
     */
    public static void storePreferences(String key, String value) {
        DebugLogger.method("BBSharedPreference :: storeAccPreference");
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Key is null or empty. Account preferences can not be stored.");
            return;
        }
        if (value == null || value.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Value for given key is null or empty. Account preferences can not be stored.");
            return;
        }
        SharedPreferences.Editor editor = getEditor(AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen);
        editor.putString(key, value);
        editor.commit();
        DebugLogger.message("Account Preference is stored successfully.");
    }

    public static void removeAccPreference(String key) {
        DebugLogger.method("BBSharedPreference :: storeAccPreference");
        if (key == null || key.equalsIgnoreCase(AppConstants.EMPTY_STRING)) {
            DebugLogger
                    .message("Key is null or empty. Account preferences can not be stored.");
            return;
        }

        SharedPreferences.Editor editor = getEditor(AppConstants.CUSTOM_PREFERENCE,
                AppDataManager.currentScreen);
        editor.remove(key);
        editor.commit();
        DebugLogger.message("Account Preference removed successfully.");
    }

}
