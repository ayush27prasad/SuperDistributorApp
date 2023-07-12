package com.app.superdistributor.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.superdistributor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddOfferActivity extends AppCompatActivity {

    TextInputEditText OfferName;
    ShapeableImageView OfferImage;

    boolean isImgAdded = false;
    Button SubmitOfferDetailsBtn;

    DatabaseReference database;
    private ProgressDialog LoadingBar;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int PICK_IMAGE_REQUEST = 143;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_offer);

        LoadingBar=new ProgressDialog(this);
        database = FirebaseDatabase.getInstance().getReference();

        OfferName = findViewById(R.id.offerNameET);
        OfferImage = findViewById(R.id.offerImage);
        SubmitOfferDetailsBtn = findViewById(R.id.submitOfferDetailsBtn);

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        isImgAdded = true;
                        Intent data = result.getData();
                        // Get the selected image URI
                        OfferImage.setImageURI(data.getData());
                    }
                });

        OfferImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            }
        });

        SubmitOfferDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OfferName.getText().toString().equals("") ){
                    Toast.makeText(AddOfferActivity.this, "Please enter offer name", Toast.LENGTH_SHORT).show();
                }
                else if (!isImgAdded){
                    Toast.makeText(AddOfferActivity.this, "Please enter offer image", Toast.LENGTH_SHORT).show();
                }
                else{
                    LoadingBar.setTitle("Please Wait..");
                    LoadingBar.setMessage("Please Wait while we are checking our database...");
                    LoadingBar.show();

                    createNewOffer(OfferName.getText().toString(),OfferImage);
                }
            }
        });

    }

    private void createNewOffer(String offerName, ShapeableImageView offerImage) {

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Offers").child(offerName).exists()){
                    LoadingBar.dismiss();
                    Toast.makeText(AddOfferActivity.this, "Offer with this name already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    HashMap<String,Object> offers = new HashMap<>();
                    offers.put("Name", offerName);
                    offers.put("Image", offerImage);

                    database.child("Offers").child(offerName).updateChildren(offers)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    LoadingBar.dismiss();
                                    Toast.makeText(AddOfferActivity.this,"Offer Added",Toast.LENGTH_SHORT).show();
                                    OfferName.setText("");
                                    OfferImage.setImageResource(R.drawable.baseline_add_image);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddOfferActivity.this,"Couldn't save", Toast.LENGTH_SHORT).show();

            }
        });
    }
}