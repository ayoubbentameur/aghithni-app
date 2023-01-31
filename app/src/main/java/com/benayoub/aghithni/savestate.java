package com.benayoub.aghithni;

import android.content.Context;
import android.content.SharedPreferences;

public class savestate {
    public static SharedPreferences getPrefs(Context context){
        return  context.getSharedPreferences("LANGUAGE_APP",Context.MODE_PRIVATE);
    }

    public static void insertData(Context context,String key,String value){
        SharedPreferences.Editor editor=getPrefs(context).edit();
        editor.putString(key,value);
        editor.apply();
    }

    public static void retriveData(Context context, String key){
        getPrefs(context).getString(key, "no_data_found");
    }

}
