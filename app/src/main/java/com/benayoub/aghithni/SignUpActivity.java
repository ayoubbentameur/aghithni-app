package com.benayoub.aghithni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    /*
    Secure Password requirements
Password must contain at least one digit [0-9].
Password must contain at least one lowercase Latin character [a-z].
Password must contain at least one uppercase Latin character [A-Z].
Password must contain a length of at least 8 characters and a maximum of 20 characters.
     */

    private static final String PASSWORD_PATTERN="^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,20}$";

    private EditText edit_txt_Fullname, edit_txt_Username, edit_txt_Email, edit_txt_Pass, edit_txt_CoPass;
    private RadioButton radioMale, radioFemale;
    private Button button_register;
    private TextView text_view_login;
    ProgressBar signUp_progress;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    String fullname, username, email, password, co_password;
    String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUp_progress = findViewById(R.id.signUp_progress);
        edit_txt_Fullname = findViewById(R.id.fullnameField1);
        edit_txt_Username = findViewById(R.id.usernameField1);
        edit_txt_Email = findViewById(R.id.emailField1);
        edit_txt_Pass = findViewById(R.id.passwordField1);
        edit_txt_CoPass = findViewById(R.id.con_passwordField1);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        text_view_login = findViewById(R.id.text_view_login);
        button_register = findViewById(R.id.register_btn_id);


        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");

        text_view_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //        handle user SignUp button
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateFullname() | !validateUsername() | !validateEmail() | !validatePassword() | checkUserGender()) {
                    return;
                }

                if (password.equals(co_password)) {

                    //    progressbar VISIBLE
                    signUp_progress.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        UserData data = new UserData(fullname, username, email, gender,password);

                                        FirebaseDatabase.getInstance().getReference("UserData")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        //    progressbar GONE
                                                        signUp_progress.setVisibility(View.GONE);

                                                        Toast.makeText(SignUpActivity.this, "Successful Registered", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(SignUpActivity.this, Choose.class);
                                                        startActivity(intent);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        finish();
                                                    }
                                                });


                               } else {
                                        //    progressbar GONE
                                        signUp_progress.setVisibility(View.GONE);
                                        Toast.makeText(SignUpActivity.this, "Check Email id or Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignUpActivity.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateFullname() {
        fullname = edit_txt_Fullname.getText().toString().trim();
        if (TextUtils.isEmpty(fullname)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Full Name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateUsername() {
        username = edit_txt_Username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(SignUpActivity.this, "Enter Your User Name", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEmail() {
        email = edit_txt_Email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        //The trim() method removes whitespace from both ends of a string.
        password = edit_txt_Pass.getText().toString().trim();
        co_password = edit_txt_CoPass.getText().toString();
            Pattern pattern_passwd=Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher_passwd=pattern_passwd.matcher(password);
            boolean matchFound_psswd=matcher_passwd.find();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(co_password)) {
            Toast.makeText(SignUpActivity.this, "Enter Your Co-Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() <= 8) {
            Toast.makeText(SignUpActivity.this, "Password is Very Short", Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() >20){
            Toast.makeText(SignUpActivity.this, "Password is Very Long you can't use more than 20 char", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!matchFound_psswd){
            Toast.makeText(SignUpActivity.this, "Password must contain:Numbers,UPPERCASE letter 'A-Z' and lowercase letter 'a-z'", Toast.LENGTH_SHORT).show();

            return false;
        }
        else {
            return true;
        }
    }

    private boolean checkUserGender() {

        if (radioMale.isChecked()) {
            gender = "Male";
            return false;

        }
        if (radioFemale.isChecked()) {
            gender = "Female";
            return false;

        } else {
            Toast.makeText(SignUpActivity.this, "Select Your Gender", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    //    if the user already logged in then it will automatically send on Dashboard/MainActivity activity.
    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(SignUpActivity.this, SignUpActivity.class);
            startActivity(intent);

        }
    }
}



