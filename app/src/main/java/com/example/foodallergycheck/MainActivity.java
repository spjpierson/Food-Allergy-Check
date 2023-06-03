package com.example.foodallergycheck;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText foodSearch;
    ImageButton searchFoodButton;

    Button updateAndAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        foodSearch = (EditText) findViewById(R.id.editTextSearchFood);
        searchFoodButton = (ImageButton) findViewById(R.id.imageButtonSearch);
        updateAndAddButton = (Button) findViewById(R.id.add_n_uodate_food_button);

        searchFoodButton.setOnClickListener(this);
        updateAndAddButton.setOnClickListener(this);

    }

    public void onClick(View view){


        if(searchFoodButton.getId() == view.getId()){
            String searchText = foodSearch.getText().toString();
            Toast.makeText(this.getApplicationContext(),searchText + "was press",Toast.LENGTH_SHORT).show();
        }

        if(updateAndAddButton.getId() == view.getId()){
            Toast.makeText(this.getApplicationContext(),"AddButton was press",Toast.LENGTH_LONG).show();
        }
    }
}