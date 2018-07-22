package service.courier.app.dmcx.courierservice.LocalDatabase;

import android.content.Context;
import android.content.SharedPreferences;

import service.courier.app.dmcx.courierservice.Variables.Vars;

public class LocalDB {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public LocalDB(Context context) {
        sharedPreferences = context.getSharedPreferences(Vars.PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveBooleanValue(String key, boolean task) {
        editor.putBoolean(key, task);
        editor.commit();
    }

    public void saveStringValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public Boolean retriveBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public String retriveStringValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void clearDB() {
        editor.clear();
        editor.commit();
    }


}
