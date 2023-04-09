package com.benayoub.aghithni;


import static android.Manifest.permission.*;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final int CAMERA_REQ_CODE = 8857;
    private static final int REQUEST_PERMISSION = 789;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ImageView avatar;
    TextView fullName;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog progressDialog;
    private final int SELECT_PICTURE = 200;
    private static final int PERMISSION_REQUEST_CODE = 123;
    ActivityResultLauncher<String> mGetContent;


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
        swipeRefreshLayout = mContainer.findViewById(R.id.profile_frg_id);
        avatar = mContainer.findViewById(R.id.avatar_pic_id);
        fullName = mContainer.findViewById(R.id.full_name_txt_id);
        checkSignin();
        downloadPic();
        refresh();

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (shouldShowRequestPermissionRationale(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Explain to the user why we need to read the contacts
                    }

                    int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=8888;
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant that should be quite unique

                    return;
                }
                imageChooser();


            }
        });



        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent=new Intent(getContext(),UCropActivity.class);
                intent.putExtra("DATA",result.toString());
                startActivityForResult(intent,101);

            }
        });

        currentUser=mAuth.getCurrentUser();
        if (currentUser != null) {


            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://aghithni-38fd3.appspot.com/images/" + uid + ".jpeg");


            getDownloadUrl(reference);
            FirebaseUser mFirebaseUser = mAuth.getCurrentUser();
            if(mFirebaseUser != null) {
                String currentUserID = mFirebaseUser.getDisplayName(); //Do what you need to do with the id
            fullName.setText(currentUserID);
            }
        }
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
            text.setTextColor(text.getContext().getColor(R.color.black));
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

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {
        // create an instance of the
        // intent of the type image

      /*  Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);*/
        mGetContent.launch("image/*");


    }




    // this function is triggered when user
    // selects the image from the imageChooser
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==-1 && requestCode==101){
            String result=data.getStringExtra("RESULT");
            Uri resultUri=null;
            if (result!=null){
                resultUri=Uri.parse(result);
            }
            avatar.setImageURI(resultUri);
            upload();
        }
    }

    private void upload() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

        StorageReference storageRef = storage.getReference();
        String uid = mAuth.getUid();
        String path = "images/" + uid + ".jpeg";
// Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child(path);

        // Get the data from an ImageView as bytes
        avatar.setDrawingCacheEnabled(true);
        avatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) avatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                // mack progress bar dialog

                progressDialog.setTitle("Uploading....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("upload " + (int) progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload picture", Toast.LENGTH_SHORT).show();
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_LONG).show();
                Uri photoUrl = currentUser.getPhotoUrl();

                avatar.setImageResource(R.drawable.user_pic);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                progressDialog.dismiss();
                // Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void downloadPic() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //mAuth = FirebaseAuth.getInstance();
       // currentUser = mAuth.getCurrentUser();
        if (user != null) {

            if (user.getPhotoUrl() != null) {




                for (UserInfo userInfo : user.getProviderData()) {

                   if (FacebookAuthProvider.PROVIDER_ID.equals(userInfo.getProviderId())) {



                 Glide.with(this).load(R.drawable.user_pic)

                         .into(avatar);



                }








                    if (userInfo.getProviderId().equals("google.com")) {


                        Glide.with(this).load(user.getPhotoUrl()).into(avatar);
                    }else {

                        Glide.with(this).load(user.getPhotoUrl()).into(avatar);

                    }
                }

                }
                    }/*else{
                        mAuth = FirebaseAuth.getInstance();
                        currentUser = mAuth.getCurrentUser();
                        Glide.with(this).load(currentUser.getPhotoUrl()).into(avatar);

                    }*/




            }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "onSuccess: " + uri);
                setUserProfileUrl(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed TO download URI" + e.getCause());
            }
        });
    }

    private void setUserProfileUrl(Uri uri) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri).build();


        currentUser.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //   Toast.makeText(getContext(), "Profile image Updated.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile image failed.", Toast.LENGTH_LONG).show();

                    }
                });

    }

    private void refresh() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAuth.getCurrentUser()!=null){


               // FirebaseUser user = mAuth.getCurrentUser();
                /*String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://aghithni-38fd3.appspot.com/images/" + uid + ".jpeg");


                getDownloadUrl(reference);*/
                    currentUser.reload();
                swipeRefreshLayout.setRefreshing(false);

            }
                else {
                    Toast.makeText(getContext(),"You need to sign in first",Toast.LENGTH_SHORT).show();

                }
            }

        });
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
            } else {
                // permission denied
            }
        }
    }


}
