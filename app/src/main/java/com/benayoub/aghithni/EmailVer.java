package com.benayoub.aghithni;

import androidx.annotation.InterpolatorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVer extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    TextView verifyTxt;
            Button verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_ver);
        verify=findViewById(R.id.verify_email_btn);
        verifyTxt=findViewById(R.id.verifyemail_txt_id);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 auth = FirebaseAuth.getInstance();

                 user = auth.getCurrentUser();


                user.sendEmailVerification()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("done", "Email sent.");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("failed", "Email not send.");

                            }
                        });
            }
        });


    }
}