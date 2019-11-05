package com.example.camerademo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    private String pictureFilePath;
    private int REQUEST_PICTURE_CAPTURE = 59;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
    }


    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap)extras.get("data");
            }
        }
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if request code is same as we requested
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK){

                // Get imageView dimensions
                int imageViewWidth = imageView.getWidth();
                int imageViewHeight = imageView.getHeight();
                String log = "Imageview Width: " + imageViewWidth + " ImageView height: " + imageViewHeight;
                Log.d("willy", log);

                // Create Bitmap options to only allow bitmap to be as big as the imageview
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(pictureFilePath, options);

                // Calculate proper scale for the image.
                int scaleFactor = Math.min(options.outWidth / imageViewWidth, options.outHeight / imageViewHeight);
                String log3 = "options out. Width:: " + options.outWidth + "  height: " + options.outHeight;
                Log.d("willy", log3);
                // Reset options to a new object and apply the scale
                options = new BitmapFactory.Options();
                options.inSampleSize = scaleFactor;

                // Decode the image
                Bitmap image = BitmapFactory.decodeFile(pictureFilePath, options);
                String log2 = "Image Width: " + image.getWidth() + " Image height: " + image.getHeight();
                Log.d("willy", log2);
                // Set image to imageView
                imageView.setImageBitmap(image);
            }
            /*
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                imageView.setImageURI(Uri.fromFile(imgFile));
            }
            */
        }
    }

    private void startCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        String pictureFile = "pic_" + timeStamp;

        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(dir, "iths");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                // Kunde inte skapa mapp
                Log.d("willy", "Kunde inte skapa mapp heh");
            }
        }

        File image = File.createTempFile( pictureFile, ".jpg", storageDir);
        //File image = new File(dir, pictureFile + ".jpg");
        //= File.createTempFile(pictureFile,  ".jpg", storageDir);

        pictureFilePath = image.getAbsolutePath();
        Log.d("willy", "Abs file path: " + pictureFilePath);
        return image;
    }

    public void startCameraBtnClicked(View view) {
        startCamera();
    }
}
