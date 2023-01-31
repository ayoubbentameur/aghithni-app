package com.benayoub.aghithni;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;

import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.facebook.AccessToken;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthCredential;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditPersonalData extends AppCompatActivity {
    FirebaseUser currentUser;
    ImageView avatarEditData;
    TextInputLayout emailLayout;
    FirebaseAuth mAuth;
    EditText fullnameEditData,emailEditData,emaildlg;
    Dialog dialog;
    Button save,delete,deleteDlg,cancelDlg,signoutDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_data);
        avatarEditData=findViewById(R.id.avatar_pic_id);
        fullnameEditData=findViewById(R.id.fullname_edit_id);
        emailEditData=findViewById(R.id.email_edit_id);
        emailLayout=findViewById(R.id.email_layout);
        save=findViewById(R.id.save_btn_id);
        delete=findViewById(R.id.delete_btn);

        mAuth = FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();



        if (currentUser!=null) {
            if (currentUser.getPhotoUrl()!=null){
                for (UserInfo userInfo : currentUser.getProviderData()) {
                    if (userInfo.getProviderId().equals("facebook.com")) {

                        /*int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
                        Uri profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(dimensionPixelSize , dimensionPixelSize);
                        Glide.with(this).load(profilePictureUri)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(avatarEditData);*/
                       // mAuth = FirebaseAuth.getInstance();
                        //currentUser = mAuth.getCurrentUser();
                        Glide.with(this).load(currentUser.getPhotoUrl()).into(avatarEditData);

                    }
                    else if (userInfo.getProviderId().equals("google.com")){
                        Glide.with(this).load(currentUser.getPhotoUrl()).into(avatarEditData);

                    }else {
                       // mAuth = FirebaseAuth.getInstance();
                       // currentUser = mAuth.getCurrentUser();
                        Glide.with(this).load(currentUser.getPhotoUrl()).into(avatarEditData);

                    }

                }
            }

            fullnameEditData.setText(currentUser.getDisplayName());
            emailEditData.setText(currentUser.getEmail());

            boolean ver=currentUser.isEmailVerified();

            if (ver){
        emailLayout.setHelperText(getString(R.string.email_verified));


            }
            else {
                emailLayout.setHelperText(getString(R.string.email_not_verified));

            }

            save.setOnClickListener(view -> {

                reauth();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullnameEditData.getText().toString().trim())

                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Done", "User profile updated.");

                            }
                        });


                user.updateEmail(emailEditData.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("Done", "User email address updated.");
                                Intent intent=new Intent(EditPersonalData.this,DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            Log.e(e.getMessage(), "User email address not updated.");
                              /*Some security-sensitive actions—such as deleting an account,
                               setting a primary email address, and changing a password—require that the user has recently signed in.
                                So we need to Re-authenticate the user.
               */
                        });

            });

            delete.setOnClickListener(view -> launchDeleteDlg());


        }


    }



    private void deleteAccount() {
      //  reauth();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Done", "User account deleted.");
                        Intent intent = new Intent(EditPersonalData.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(EditPersonalData.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(EditPersonalData.this, "failed", Toast.LENGTH_SHORT).show();
                    Log.w(e.getMessage(), "failed");

                    launchSignoutDlg();




                });
    }



    private void reauth(){


    for (UserInfo userInfo:currentUser.getProviderData()){
        if (userInfo.getProviderId().equals("google.com")){
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(EditPersonalData.this);
            if (acct != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Reauthenticated.");
                            Toast.makeText(getApplicationContext(), "reauthenticated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"failed", Toast.LENGTH_SHORT).show();

                        Log.w(e.getLocalizedMessage(), "non Reauthenticated.");

                    }
                });

            } if (userInfo.getProviderId().equals("facebook.com")){
                String tokenfb=AccessToken.getCurrentAccessToken().getToken();
                AuthCredential credential = FacebookAuthProvider.getCredential(tokenfb);
                currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Reauthenticated.");
                            Toast.makeText(getApplicationContext(), "reauthenticated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"failed", Toast.LENGTH_SHORT).show();

                        Log.w(e.getLocalizedMessage(), "non Reauthenticated.");

                    }
                });

            }else {
                launchSignoutDlg();
            }
        }

    }

}



    private void launchDeleteDlg() {

        dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.setCanceledOnTouchOutside(true);

        deleteDlg= dialog.findViewById(R.id.delete_dlg_btn);
        cancelDlg= dialog.findViewById(R.id.cancel_dlg_btn);
        emaildlg=dialog.findViewById(R.id.email_dlg_id);

        deleteDlg.setOnClickListener(v -> {
            if (emaildlg.getText().toString().trim().equals(currentUser.getEmail())){
                deleteAccount();
            }else {
                Toast.makeText(EditPersonalData.this,"Wrong Email",Toast.LENGTH_SHORT).show();

            }


        });


        cancelDlg.setOnClickListener(v -> dialog.dismiss());
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

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