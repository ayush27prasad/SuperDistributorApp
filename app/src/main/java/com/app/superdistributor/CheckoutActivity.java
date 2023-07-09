package com.app.superdistributor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CheckoutActivity extends AppCompatActivity {

    Button BackToHomeBtn;
    String DealerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        DealerName = getIntent().getStringExtra("DealerName");

        BackToHomeBtn = findViewById(R.id.backtohomebtn);

        Intent intent = getIntent();

        BackToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CheckoutActivity.this, DealerHomeActivity.class);
                i.putExtra("DealerName",DealerName);
                startActivity(i);
            }
        });

    }
}