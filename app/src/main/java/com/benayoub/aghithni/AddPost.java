package com.benayoub.aghithni;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AddPost extends AppCompatActivity {
    Button selectPic,post,addNumBtn;
    TextView bloodTypeTxt;
    Chip search,donate,blood,medical;
    Spinner spinner;
    ArrayList<Uri> list;
    SliderAdapterExample adapter;
    SliderView sliderView;
    JSONArray firstArray;

    Dialog dialog;
    Button confirmNum,cancelDlg;


    Button deletePic;

    String name;
    List<String> wilayaNames = new ArrayList<>();
    ArrayList<String> communesList = new ArrayList<>();
    ArrayAdapter<String> communeAdapter;
    JSONArray jsonArray;
    JSONObject obj;
    String selectedState;

    ActivityResultLauncher<String> mGetContent;
   String colum[] = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE};

    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://gist.githubusercontent.com/ayoubbentameur/06e623397d2dd224c9add9cd92ad0ffd/raw/356a446dc6ef11a2c1bbfec7db57b1b509493b26/testJson.json";

        Resources res = getResources();
        String selectbloodtype = res.getString(R.string.select_bloodtype_txt);
        String[] bloodTypes = {selectbloodtype,"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        selectPic=findViewById(R.id.selectPic_id);
        spinner = findViewById(R.id.spinnerBloodType_id);
        search=findViewById(R.id.searchChip_id);
       donate=findViewById(R.id.donateChip_id);
        blood=findViewById(R.id.bloodChip_id);
        medical=findViewById(R.id.medecineChip_id);
        post=findViewById(R.id.post_btn_id);
        deletePic=findViewById(R.id.delete_pic_id);
        bloodTypeTxt=findViewById(R.id.txtBloodType_id);
        addNumBtn=findViewById(R.id.addPhoneNmbr_id);
        // Initialize the spinners and their adapters
        Spinner wilayaSpinner=findViewById(R.id.spinner);
        Spinner communeSpinner=findViewById(R.id.spinner2);

        spinner.setVisibility(View.INVISIBLE);
        bloodTypeTxt.setVisibility(View.INVISIBLE);




        addNumBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dialog = new Dialog(AddPost.this, android.R.style.Theme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_add_phonebmbr_addpost);
                dialog.setCanceledOnTouchOutside(true);
                cancelDlg= dialog.findViewById(R.id.cancel_dlg_btn);
                confirmNum=dialog.findViewById(R.id.confirm_dlg_btn);
             EditText numberDlg=dialog.findViewById(R.id.number_dlg_id);
                cancelDlg.setOnClickListener(v -> dialog.dismiss());

                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
                confirmNum.setOnClickListener(v -> {
                    if (numberDlg!=null){
                        number=numberDlg.getText().toString();
                    }
                    dialog.dismiss();
                    addNumBtn.setText(number);


                });



            }
        });



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





        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        // Parse the JSON response

                        //get the current app Language
                        String currentLang = getResources().getConfiguration().locale.getLanguage();
                        jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (currentLang.equals("fr") || currentLang.equals("en")){
                            firstArray = jsonObject.getJSONArray("fr");
                            for (int i = 0; i < firstArray.length(); i++) {
                                obj = firstArray.getJSONObject(i);
                                name = obj.getString("name");
                                Log.d("JSON", "Name:" + name);
                                wilayaNames.add(name);
                            }
                        }else if (currentLang.equals("ar")){
                            firstArray = jsonObject.getJSONArray("ar");

                            for (int i = 0; i < firstArray.length(); i++) {
                                obj = firstArray.getJSONObject(i);
                                name = obj.getString("name");
                                Log.d("JSON", "Name:" + name);
                                wilayaNames.add(name);
                            }
                        }






                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Error",e.getMessage());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, wilayaNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Finally, set the adapter to the spinner
                    wilayaSpinner.setAdapter(adapter);








                }, error -> {
            // Handle error

        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);


        wilayaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the user's selection here

                selectedState = wilayaSpinner.getSelectedItem().toString();
                JsonArrayRequest request;

                request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                    try {
                        // Parse the JSON data and extract the required information

                        if (communeAdapter!=null){
                            communeAdapter.clear();
                        }
                        JSONArray jsonArray = response.getJSONObject(0).getJSONArray("ar");
                        JSONObject jsonObject = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("name").equals(selectedState)) {
                                break;
                            }
                        }
                        JSONArray communesArray = jsonObject.getJSONArray("communes");
                        for (int j = 0; j < communesArray.length(); j++) {
                            String commune = communesArray.getString(j);
                            communesList.add(commune);

                            Log.d("Commune List", commune);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    communeAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, communesList);
                    communeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    communeSpinner.setAdapter(communeAdapter);
                    communeAdapter.notifyDataSetChanged();
                }, Throwable::printStackTrace);

                queue.add(request);





            }



            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
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