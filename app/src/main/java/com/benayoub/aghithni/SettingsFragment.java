package com.benayoub.aghithni;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.benayoub.aghithni.BuildConfig;
import com.google.firebase.auth.UserInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    View mContainerSettings;
    TextView signOut;
        TextView version,fullname,email,changeLanguage,editData;
        ImageView avatarSetting;
        RelativeLayout userSetting;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String versionName = BuildConfig.VERSION_NAME;

        mContainerSettings= inflater.inflate(R.layout.fragment_settings, container, false);


        changeLanguage=mContainerSettings.findViewById(R.id.change_lang_id);
        changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent languages=new Intent(getContext(),LanguageSelection.class);
              startActivity(languages);
            }
        });


            signOut();
            ifDisconnected();

            userSetting=mContainerSettings.findViewById(R.id.rltv_layout_settingUsr_id);
        currentUser=mAuth.getCurrentUser();

        userSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (currentUser == null) {
                        Intent intentLogin = new Intent(getContext(), LoginActivity.class);
                        startActivity(intentLogin);
                        getActivity().finish();
                        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    }

                }
            });

            avatarSetting=mContainerSettings.findViewById(R.id.profileCircleImageView);
            fullname=mContainerSettings.findViewById(R.id.usernameTextView);
            email=mContainerSettings.findViewById(R.id.email_id);

            editData=mContainerSettings.findViewById(R.id.editdata_id);
            editData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentUser!=null){
                        Intent intentedit=new Intent(getContext(),EditPersonalData.class);
                        startActivity(intentedit);

                    }else {
                        Intent intentedit=new Intent(getContext(),LoginActivity.class);
                        startActivity(intentedit);
                        getActivity().finish();
                    }
                }
            });



            if (currentUser!=null){
                if (currentUser.getPhotoUrl()!=null){
                    for (UserInfo userInfo : currentUser.getProviderData()) {
                        if (userInfo.getProviderId().equals("facebook.com")) {
                            int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
                            Uri profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(dimensionPixelSize , dimensionPixelSize);
                            Glide.with(this).load(profilePictureUri)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(avatarSetting);
                        }
                        else if (userInfo.getProviderId().equals("google.com")){
                            Glide.with(this).load(currentUser.getPhotoUrl()).into(avatarSetting);

                        }

                    }
                }



            fullname.setText(currentUser.getDisplayName());
            email.setText(currentUser.getEmail());
            }
            version=mContainerSettings.findViewById(R.id.version_id);
            version.setText("version:"+versionName);
            version.setTextColor(Color.GRAY);
            version.setTextSize(15);
        return mContainerSettings;
    }
            void signOut(){
                    signOut=mContainerSettings.findViewById(R.id.signOut_Id);
                    signOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(getContext(),DashboardActivity.class);
                            startActivity(intent);
                            getActivity().finish();


                        }
                    });
            }

            void ifDisconnected(){
        mAuth=FirebaseAuth.getInstance();

        FirebaseUser user=mAuth.getCurrentUser();
        if (user==null){
            signOut.setVisibility(View.INVISIBLE);

        }

            }



}


