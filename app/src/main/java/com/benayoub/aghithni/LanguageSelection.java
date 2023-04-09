package com.benayoub.aghithni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;

import android.util.DisplayMetrics;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class LanguageSelection extends AppCompatActivity {
    TextView arabic,english,frensh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
                arabic=findViewById(R.id.arabic_id);
                english=findViewById(R.id.english_id);
                frensh=findViewById(R.id.frensh_id);

            arabic.setOnClickListener(view -> {
                SharedPreferences.Editor editor=getSharedPreferences("lang",MODE_PRIVATE).edit();
                editor.putString("language","ar");
                editor.apply();
                setLocale("ar");
                restartActivity();
            });

            english.setOnClickListener(view -> {
                SharedPreferences.Editor editor=getSharedPreferences("lang",MODE_PRIVATE).edit();
                editor.putString("language","en");
                editor.apply();
                setLocale("en");
                restartActivity();
            });

            frensh.setOnClickListener(view -> {
                SharedPreferences.Editor editor=getSharedPreferences("lang",MODE_PRIVATE).edit();
                editor.putString("language","fr");
                editor.apply();
                setLocale("fr");
                restartActivity();
            });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        /*Intent refresh = new Intent(this, DashboardActivity.class);
        finish();
        startActivity(refresh);*/
    }
public void restartActivity(){
    Intent intent = new Intent(this, Choose.class);
    finish();
    startActivity(intent);

}
}