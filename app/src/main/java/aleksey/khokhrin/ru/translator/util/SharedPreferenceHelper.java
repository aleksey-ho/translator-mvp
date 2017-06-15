package aleksey.khokhrin.ru.translator.util;

import android.content.Context;
import android.content.SharedPreferences;

import aleksey.khokhrin.ru.translator.TranslatorApplication;

public class SharedPreferenceHelper {

    private static SharedPreferenceHelper sharedPreference;
    private static final String PREFS_NAME = TranslatorApplication.class.getName() + ".preferences";
    public static final String FIRST_APP_LAUNCH_PREF = "FIRST_APP_LAUNCH_PREF";

    public static SharedPreferenceHelper getInstance() {
        if (sharedPreference == null) {
            sharedPreference = new SharedPreferenceHelper();
        }
        return sharedPreference;
    }

    public void saveBoolean(Context context, boolean val, String Key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Key, val);
        editor.apply();
    }

    public boolean getBooleanValue(Context context, String Key, boolean defaultValue) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(Key, defaultValue);
    }

}
