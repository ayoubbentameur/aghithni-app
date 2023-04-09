package com.benayoub.aghithni;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class IntroApp extends AppCompatActivity {
    Timer timer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences2=getSharedPreferences("lang",MODE_PRIVATE);
        String language=sharedPreferences2.getString("language","");

        switch (language){
            case "en":
                setLocale("en");
                break;

            case "fr":
                setLocale("fr");
                break;

            case "ar":
                setLocale("ar");
                break;

            default:
                Locale.getDefault().getLanguage();
                    break;

        }

        setContentView(R.layout.activity_intro);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);

            boolean theme = sharedPreferences.getBoolean("value", true);
            if (theme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent(IntroApp.this, Choose.class);
                startActivity(intent);
                finish();
            }
        },1000);



    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }

}