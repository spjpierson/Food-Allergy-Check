package com.example.foodallergycheck;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        FirebaseApp.initializeApp(this);


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
                    String infoText = "Please fill in all fields and take photos";

                    if(foodName.isEmpty()){
                        infoText = "Food Name is Empty. ";
                    }

                    if(label.isEmpty()){
                        infoText = infoText + " Label is Empty. ";
                    }

                    if(foodImageUri == null){
                        infoText = infoText + " Food Image is Null. ";
                    }

                    if(labelImageUri == null){
                        infoText = infoText + "  Label Image is Null. ";
                    }

                    Toast.makeText(AddFoodActivity.this,infoText, Toast.LENGTH_SHORT).show();
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

                    // Get the current date as the global key
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // Create a reference to the database node for the current date
                    DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child(currentDate);

                    if (foodName.isEmpty() || label.isEmpty() || foodImageUri == null || labelImageUri == null) {
                        // Create a new food object with the details
                        Food food = new Food(foodName, "", "", label);
                        foodRef.setValue(food);
                    } else {
                        // Create a new food object with the details
                        String foodImageUriString = foodImageUri != null ? foodImageUri.toString() : "";
                        String labelImageUriString = labelImageUri != null ? labelImageUri.toString() : "";
                        Food food = new Food(foodName, foodImageUriString, labelImageUriString, label);
                        foodRef.setValue(food);
                    }


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
            foodImageUri = getImageUri(AddFoodActivity.this,imageBitmap);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE_LABEL && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            labelImageView.setImageBitmap(imageBitmap);
            String recognizedText = processLabelImage(imageBitmap);
            labelEditText.setText(recognizedText);

            labelImageUri = getImageUri(AddFoodActivity.this,imageBitmap);
        }
    }

    // Method to get the URI from the Bitmap
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String processLabelImage(Bitmap imageBitmap) {
        // Create an ML Kit TextRecognizer with default options
        TextRecognizerOptions options = new TextRecognizerOptions.Builder().build();
        TextRecognizer recognizer = TextRecognition.getClient(options);

        // Convert the imageBitmap to an ML Kit InputImage
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);

        // Process the image to extract text
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text texts) {
                        // Process the extracted text
                        String extractedText = processExtractedText(texts);

                        // Set the extracted text to the labelEditText
                        labelEditText.setText(extractedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur during text recognition
                        String errorMessage = "ML Kit"+ "Text recognition failed: " + e.getMessage();
                        Toast.makeText(getApplicationContext(), errorMessage,Toast.LENGTH_LONG).show();
                    }
                });

        return null; // We'll set the extracted text in the success listener above
    }

    private String processExtractedText(Text texts) {
        StringBuilder stringBuilder = new StringBuilder();

        // Flag to indicate if we should include words after "contain"
        boolean includeWords = false;

        // Iterate through the recognized text blocks
        for (Text.TextBlock textBlock : texts.getTextBlocks()) {
            // Iterate through the recognized lines within the text block
            for (Text.Line line : textBlock.getLines()) {
                // Iterate through the recognized elements within the line
                for (Text.Element element : line.getElements()) {
                    // Convert the element's text to lowercase for case-insensitive comparison
                    String text = element.getText().toLowerCase();

                    if (includeWords) {
                        // Add the element's text to the stringBuilder
                        stringBuilder.append(element.getText()).append(" ");

                        // Check if a period (".") is encountered
                        if (text.contains(".")) {
                            includeWords = false; // Stop including words after encountering a period
                            break; // Exit the loop
                        }
                    } else {
                        // Check if the element's text contains "contain"
                        if (text.contains("contain")) {
                            includeWords = true; // Start including words after "contain"
                        }
                    }
                }
            }
        }

        String extractedText = stringBuilder.toString().trim();
        // Remove the period at the end of the string, if present
        if (extractedText.endsWith(".")) {
            extractedText = extractedText.substring(0, extractedText.length() - 1);
        }
        return extractedText;
    }

}