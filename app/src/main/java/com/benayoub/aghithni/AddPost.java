package com.benayoub.aghithni;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;

public class AddPost extends AppCompatActivity {
    Button selectPic,post;
    RadioButton search,donate,blood,medical;
    RadioGroup radioGroup;
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
        search=findViewById(R.id.searchRadio_id);
        donate=findViewById(R.id.donateRadio_id);
        blood=findViewById(R.id.bloodRadio_id);
        medical=findViewById(R.id.medecineRadio_id);
        radioGroup=findViewById(R.id.radio);
        post=findViewById(R.id.post_btn_id);
        deletePic=findViewById(R.id.delete_pic_id);
        spinner.setVisibility(View.INVISIBLE);

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

                spinner.setVisibility(View.VISIBLE);

            } else {
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