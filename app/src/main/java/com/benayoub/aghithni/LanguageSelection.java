package com.benayoub.aghithni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;

import android.util.DisplayMetrics;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class LanguageSelection extends AppCompatActivity {
    TextView arabic,english,frensh;
    Context context;
    Resources resources;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
                arabic=findViewById(R.id.arabic_id);
                english=findViewById(R.id.english_id);
                frensh=findViewById(R.id.frensh_id);
// Call this on the main thread as it may require Activity.restart()
    savestate.retriveData(LanguageSelection.this,"key_1");

        arabic.setOnClickListener(view -> {
            context = LocaleHelper.setLocale(LanguageSelection.this, "ar");
            resources = context.getResources();
        });


        english.setOnClickListener(view -> {

            Locale myLocale = new Locale("en");
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(LanguageSelection.this, DashboardActivity.class);
            finish();
            startActivity(refresh);
            finish();
            savestate.insertData(getApplicationContext(),"key_1","en");
            Toast.makeText(LanguageSelection.this, "Data Inserted", Toast.LENGTH_SHORT).show();
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        frensh.setOnClickListener(view -> {
            context = LocaleHelper.setLocale(LanguageSelection.this, "fr-rFR");
            resources = context.getResources();

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savestate.getPrefs(this);
    }
}