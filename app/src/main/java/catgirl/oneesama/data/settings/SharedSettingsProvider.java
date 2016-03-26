package catgirl.oneesama.data.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class SharedSettingsProvider<T> implements SettingsProvider<T> {

    private Class<T> type;

    private String KEY;
    private Context context;

    public SharedSettingsProvider(Context context, Class<T> type) {
        this.context = context;
        KEY = type.getName().toLowerCase();
        this.type = type;
    }

    @Override
    public void commit(T model) {
        Context applicationContext = context;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        SharedPreferences.Editor ed = mPrefs.edit();
        if (model == null) {
            ed.putString(KEY, "");
        } else {
            Gson gson = new Gson();
            ed.putString(KEY, gson.toJson(model));
        }
        ed.apply();

    }

    @Override
    public T retrieve() {
        Context applicationContext = context;
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        String gsonData = mPrefs.getString(KEY, "");
        if (!gsonData.isEmpty()) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(gsonData, type);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
