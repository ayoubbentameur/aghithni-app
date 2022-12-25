package com.benayoub.aghithni;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    ImageView avatar;
    TextView fullName;
    ProgressBar progressBar;


    private final int PICK_IMAGE_REQUEST = 1;
    private final int PICK_CAMERA_REQUEST = 0;

    //making mContainer Public for reuse it in the checkSignin() method.
    View mContainer;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private android.app.Fragment ConstraintLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = inflater.inflate(R.layout.fragment_profile, null);
        checkSignin();
        updateUI();



        return mContainer;
    }


    //this method to check if the user is signin or not.
    void checkSignin() {

        // updateUI();
        // Glide.with(this).load(signInAccount.getPhotoUrl()).into(userImage);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        //if the user not signed in show a toast & button and a tex explain must to sign in to show the data.
        if (user == null) {
            Toast.makeText(getContext(), R.string.toast_login, Toast.LENGTH_SHORT).show();

            LinearLayout linearLayout = mContainer.findViewById(R.id.linearLayout1);


            TextView text = new TextView(getActivity());
            Button button = new Button(getActivity());

            final int i = 5;
            text.setId(i);
            text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(15, 500, 15, 15);
            params.gravity = 17;
            text.setLayoutParams(params);
            text.setGravity(Gravity.CENTER);
            text.setText(R.string.dialog_message);
            text.setTextColor(Color.BLACK);
            text.setTextSize(20);

            final int b = 2;
            button.setId(b);

            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params2.topMargin = 100;
            // Constant Value of Center gravity: 17 (0x00000011)
            params2.gravity = 17;
            button.setLayoutParams(params2);
            button.setTextColor(Color.WHITE);
            button.setText(R.string.signin);


            button.setBackgroundResource(R.drawable.button_neutral_background);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intentLogin);
                }
            });
            Log.d("View", "Start");
            try {

                linearLayout.addView(text);
                linearLayout.addView(button);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    public void selectImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, PICK_CAMERA_REQUEST);


                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE_REQUEST);


                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case PICK_CAMERA_REQUEST:

                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), selectedImage, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DESCRIPTION);
                        avatar.setImageBitmap(selectedImage);
                    }
                    break;
                case PICK_IMAGE_REQUEST:
                    if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

                        InputStream stream;
                        try {
                            Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();
                            stream = getActivity().getContentResolver().openInputStream(data.getData());
                            Bitmap realImage = BitmapFactory.decodeStream(stream);

                            avatar.setImageBitmap(realImage);
                            handleUpload(realImage);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);

        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data=baos.toByteArray();



        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("images")
                .child(uid + ".jpeg");


        reference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                        Toast.makeText(getContext(),"Profile Pic Uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure", e.getCause());
                    }
                });
    }





    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri).build();


        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Profile image Updated.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile image failed.", Toast.LENGTH_LONG).show();

                    }
                });


    }

    private void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            // Assign variable
            avatar = mContainer.findViewById(R.id.avatar_pic_id);
            fullName = mContainer.findViewById(R.id.full_name_txt_id);

            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage(getContext());

                }
            });



            setAvatar();
            fullName.setText(user.getDisplayName());

        }

    }
     void setAvatar(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectImage(getContext());
                    }
                });

                if (user.getPhotoUrl()!=null){
                    for (UserInfo userInfo : user.getProviderData()) {
                       if (userInfo.getProviderId().equals("facebook.com")) {
                            int dimensionPixelSize = getResources().getDimensionPixelSize(com.facebook.R.dimen.com_facebook_profilepictureview_preset_size_large);
                            Uri profilePictureUri= Profile.getCurrentProfile().getProfilePictureUri(dimensionPixelSize , dimensionPixelSize);
                            Glide.with(this).load(profilePictureUri)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(avatar);
                                                }
                       else if (userInfo.getProviderId().equals("google.com")){
                            Glide.with(this).load(user.getPhotoUrl()).into(avatar);

                        }

                    }
                }
            }

    }
}





