package com.michaelmagdy.ocrfirebasemlkit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ImageView imageView;
    Button selectImgBtn, detectTextBtn;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        selectImgBtn = findViewById(R.id.select_btn);
        detectTextBtn = findViewById(R.id.detect_btn);

        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });
        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                detectText();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null){

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectText(){

        if (bitmap == null){
            Toast.makeText(this, "Bitmap is null", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseVisionImage firebaseVisionImage =
                    FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionTextRecognizer firebaseVisionTextRecognizer =
                    FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            processText(firebaseVisionText);
                        }
                    });
        }
    }

    private void processText(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size() == 0){
            Toast.makeText(this, "No Text Detected", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder stringBuilder = new StringBuilder();;
            for (FirebaseVisionText.TextBlock block : blocks){
                stringBuilder.append(block.getText());
                stringBuilder.append("\n");

            }
            textView.setText(stringBuilder.toString());
            Log.d("DetText", stringBuilder.toString());
        }
    }

    private void detectText2(){

        List<String> optList = new ArrayList<>();
        optList.add("ar");
        optList.add("en");

        if (bitmap == null){
            Toast.makeText(this, "Bitmap is null", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseVisionImage firebaseVisionImage =
                    FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVisionDocumentTextRecognizer firebaseVisionTextRecognizer =
                    FirebaseVision.getInstance().getCloudDocumentTextRecognizer();
            firebaseVisionTextRecognizer.processImage(firebaseVisionImage)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                        @Override
                        public void onSuccess(FirebaseVisionDocumentText firebaseVisionText) {
                            processText2(firebaseVisionText);
                        }
                    });
        }
    }

    private void processText2(FirebaseVisionDocumentText firebaseVisionText) {

        List<FirebaseVisionDocumentText.Block> blocks = firebaseVisionText.getBlocks();
        if (blocks.size() == 0){
            Toast.makeText(this, "No Text Detected", Toast.LENGTH_SHORT).show();
        } else {
            StringBuilder stringBuilder = new StringBuilder();;
            for (FirebaseVisionDocumentText.Block block : blocks){
                stringBuilder.append(block.getText());
                stringBuilder.append("\n");

            }
            textView.setText(stringBuilder.toString());
            Log.d("DetText", stringBuilder.toString());
        }
    }
}