package com.example.foodallergycheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddFoodActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE_FOOD = 1;
    private static final int REQUEST_IMAGE_CAPTURE_LABEL = 2;

    EditText foodNameEditText, labelEditText;
    ImageView foodImageView, labelImageView;
    Button takeFoodPhotoButton, takeLabelPhotoButton, submitButton;

    Uri foodImageUri, labelImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        foodNameEditText = findViewById(R.id.editTextFoodName);
        labelEditText = findViewById(R.id.editTextLabel);

        foodImageView = findViewById(R.id.imageViewFood);
        labelImageView = findViewById(R.id.imageLabel);

        takeFoodPhotoButton = findViewById(R.id.takeFoodPhotoButton);
        takeLabelPhotoButton = findViewById(R.id.takeLabelPhotoButton);

        submitButton = findViewById(R.id.buttonSubmit);

        takeFoodPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_FOOD);
            }
        });

        takeLabelPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent(REQUEST_IMAGE_CAPTURE_LABEL);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String foodName = foodNameEditText.getText().toString();
                String label = labelEditText.getText().toString();

                // Validate the input fields
                if (foodName.isEmpty() || label.isEmpty() || foodImageUri == null || labelImageUri == null) {
                    Toast.makeText(AddFoodActivity.this, "Please fill in all fields and take photos", Toast.LENGTH_SHORT).show();
                } else {
                    // Perform any necessary processing or saving of the data
                    Toast.makeText(AddFoodActivity.this, "Food added successfully!", Toast.LENGTH_SHORT).show();

                    // Clear the input fields
                    foodNameEditText.setText("");
                    labelEditText.setText("");
                    foodImageView.setImageResource(android.R.color.transparent);
                    labelImageView.setImageResource(android.R.color.transparent);
                    foodImageUri = null;
                    labelImageUri = null;
                }
            }
        });
    }

    private void dispatchTakePictureIntent(int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_FOOD && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            foodImageView.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_LABEL && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            labelImageView.setImageBitmap(imageBitmap);
        }
    }
}