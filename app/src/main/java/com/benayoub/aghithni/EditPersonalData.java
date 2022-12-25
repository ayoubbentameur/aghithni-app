package com.benayoub.aghithni;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class EditPersonalData extends AppCompatActivity {
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    ImageView avatarEditData;
    EditText fullnameEditData,emailEditData;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_personal_data);
        avatarEditData=findViewById(R.id.avatar_pic_id);
        fullnameEditData=findViewById(R.id.fullname_edit_id);
        emailEditData=findViewById(R.id.email_edit_id);
        save=findViewById(R.id.save_btn_id);
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        if (currentUser!=null) {
            if (currentUser.getPhotoUrl()!=null){
                for (UserInfo userInfo : currentUser.getProviderData()) {
                    if (userInfo.getProviderId().equals("facebook.com")) {
                        int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
                        Uri profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(dimensionPixelSize , dimensionPixelSize);
                        Glide.with(this).load(profilePictureUri)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(avatarEditData);
                    }
                    else if (userInfo.getProviderId().equals("google.com")){
                        Glide.with(this).load(currentUser.getPhotoUrl()).into(avatarEditData);

                    }

                }
            }            fullnameEditData.setText(currentUser.getDisplayName());
            emailEditData.setText(currentUser.getEmail());


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
        }


    }
}