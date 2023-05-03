package com.benayoub.aghithni;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;

public class AddPost extends AppCompatActivity {
    Button selectPic,post;
    TextView bloodTypeTxt;
    Chip search,donate,blood,medical;
  //  ChipGroup chipGroup;
    Spinner spinner;
    ArrayList<Uri> list;
    SliderAdapterExample adapter;
    SliderView sliderView;
Button deletePic;

    ActivityResultLauncher<String> mGetContent;
   String colum[] = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        Resources res = getResources();
        String selectbloodtype = res.getString(R.string.select_bloodtype_txt);
        String[] bloodTypes = {selectbloodtype,"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        selectPic=findViewById(R.id.selectPic_id);
        spinner = findViewById(R.id.spinnerBloodType_id);
        search=findViewById(R.id.searchChip_id);
       donate=findViewById(R.id.donateChip_id);
        blood=findViewById(R.id.bloodChip_id);
        medical=findViewById(R.id.medecineChip_id);
     //   chipGroup=findViewById(R.id.chipGroup);
        post=findViewById(R.id.post_btn_id);
        deletePic=findViewById(R.id.delete_pic_id);
        bloodTypeTxt=findViewById(R.id.txtBloodType_id);
        spinner.setVisibility(View.INVISIBLE);
        bloodTypeTxt.setVisibility(View.INVISIBLE);


        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bloodTypes);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String bloodType = parent.getItemAtPosition(position).toString();
                // Do something with the selected blood type
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


    blood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (blood.isChecked()){
                bloodTypeTxt.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);

            } else {
                bloodTypeTxt.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);

            }
        }
    });

        list=new ArrayList<>();

         sliderView = findViewById(R.id.imageSlider);

        adapter = new SliderAdapterExample(list);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        //sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.BLACK);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);

        sliderView.setClickable(true);
        selectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        }) ;
        //SliderAdapterExample sliderAdapterExample = new SliderAdapterExample(list);
        deletePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               list.remove(sliderView.getCurrentPagePosition());
               adapter.notifyDataSetChanged();
           if (list.isEmpty()){
               deletePic.setVisibility(View.GONE);
               sliderView.setVisibility(View.GONE);
           }
            }

        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < list.size(); i++) {
                    Uri uri = list.get(i);
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();

                    StorageReference imageRef = storageRef.child("images/posts/" + uri.getLastPathSegment());
                    UploadTask uploadTask = imageRef.putFile(uri);

                    // Register observers to listen for when the upload is done or if it fails
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Handle successful uploads
                            Toast.makeText(AddPost.this, "Done", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle failed uploads

                        }
                    });
                }
            }
        });

        if ((ActivityCompat.checkSelfPermission(
                this, colum[0]) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(
                        this, colum[1]) != PackageManager.PERMISSION_GRANTED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(colum, 123);
            }
        }
if (list.isEmpty()){
    sliderView.setVisibility(View.GONE);
    deletePic.setVisibility(View.GONE);
}



        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent=new Intent(AddPost.this, UcropPost.class);
                intent.putExtra("DATA",result.toString());
                startActivityForResult(intent,101);

            }
        });

    }

    void imageChooser() {
        mGetContent.launch("image/*");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==-1 && requestCode==101){
            sliderView.setVisibility(View.VISIBLE);
            deletePic.setVisibility(View.VISIBLE);
            String result=data.getStringExtra("RESULT");
            Uri resultUri=null;
            if (result!=null){
                resultUri=Uri.parse(result);
            }
            list.add(resultUri);

        }
        adapter.notifyDataSetChanged();
    }

}