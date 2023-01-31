package com.benayoub.aghithni;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassword extends AppCompatActivity {

    TextInputEditText newpsswd,confirmpsswd;
    TextInputLayout psswd;
    Button updatePsswd;
    FirebaseAuth auth;
    FirebaseUser user;
    Dialog dialog;
    Button cancelDlg,signoutDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);


        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        
        newpsswd=findViewById(R.id.new_psswd_id);
        confirmpsswd=findViewById(R.id.confirm_psswd_id);
        psswd=findViewById(R.id.confirmPsswdid);
        updatePsswd=findViewById(R.id.update_psswd_btn_id);
        updatePsswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newpsswd.getText().toString().equals(confirmpsswd.getText().toString())){
                    user.updatePassword(newpsswd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("done", "User password updated.");
                                        Intent intent=new Intent(UpdatePassword.this,DashboardActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(e.getMessage(),"failed");
                                    launchSignoutDlg();
                                }
                            });
                }else if (!newpsswd.getText().toString().equals(confirmpsswd.getText().toString())){

                    psswd.setError(psswd.getContext().getText(R.string.psswd_not_match));
                }else if (newpsswd.getText().toString().isEmpty() && confirmpsswd.getText().toString().isEmpty() ){
                    psswd.setError("invalid input");
                    confirmpsswd.setError("Invalid input");

                }


            }

        });




    }
    void launchSignoutDlg () {

        dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signout_onfaillure);
        dialog.setCanceledOnTouchOutside(true);
        signoutDlg = dialog.findViewById(R.id.signout_dlg_btn);
        cancelDlg = dialog.findViewById(R.id.cancel_dlg_btn);
        signoutDlg.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


        cancelDlg.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

}