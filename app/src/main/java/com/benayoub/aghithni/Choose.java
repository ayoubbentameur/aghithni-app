package com.benayoub.aghithni;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Choose extends AppCompatActivity {
    CardView blood,medical;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        blood=findViewById(R.id.CardView_blood_id);
 medical=findViewById(R.id.CardView_medical_id);

    blood.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intentBlood=new Intent(Choose.this,DashboardActivity.class);
            startActivity(intentBlood);
        }
    });

    medical.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intnetMedical=new Intent(Choose.this,DashboardActivity.class);
            startActivity(intnetMedical);
        }
    });

    }
}


