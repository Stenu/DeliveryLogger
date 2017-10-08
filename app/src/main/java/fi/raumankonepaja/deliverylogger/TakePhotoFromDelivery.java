package fi.raumankonepaja.deliverylogger;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class TakePhotoFromDelivery extends AppCompatActivity {

    Uri photoURI;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_from_delivery);

        EditText deliveryNumber = (EditText) findViewById(R.id.editTextDeliveryNumber);
        EditText deliveryPosition = (EditText) findViewById(R.id.editTextDeliveryPosition);

        TextChangeListener textChangeListener = new TextChangeListener();

        ImageView takenPictureImageView = (ImageView) findViewById(R.id.takenPictureImageView);

        // piilotetaan heti ekaksi  nappula
        showHideButton();

        deliveryNumber.addTextChangedListener(textChangeListener);
        deliveryPosition.addTextChangedListener(textChangeListener);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // täällä tarvii ratkaista pari asiaa
        // TODO otettu kuva pitää kääntää oikeaan asentoon
        // TODO kuva katoaa puhelinta käännettäessä, korjaa tämä

        // vaihdetaan kuva samalla imgavieviin
        Log.d("hoi", "täällä ollaan");

        if (mCurrentPhotoPath != null) {
            ImageView takenPictureImageView = (ImageView) findViewById(R.id.takenPictureImageView);
            takenPictureImageView.setVisibility(View.VISIBLE);
            takenPictureImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
        }

    }

    // tekstin muuttuja kuuntelija
    private class TextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            showHideButton();
        }
    }


    // metodi joka piilottaa kuvaa napin jos tietoja ei ole tarpeeksi
    private void showHideButton() {

        Button shootButton = (Button) findViewById(R.id.shootButton);
        EditText deliveryNumber = (EditText) findViewById(R.id.editTextDeliveryNumber);
        EditText deliveryPosition = (EditText) findViewById(R.id.editTextDeliveryPosition);

        if (deliveryNumber.getText().length() > 0 && deliveryPosition.getText().length() > 0) {
            shootButton.setVisibility(View.VISIBLE);
            Log.i("showHideButton metodi:", " näytä nappi");
        } else {
            shootButton.setVisibility(View.INVISIBLE);
        }


    }


    // tämä metodi laukaisee intentin, jossa kamera-apilla otetaan kuva
    static final int REQUEST_TAKE_PHOTO = 1;

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("ennen kuvan ottoa ", "ongelmia " + ex.getMessage().toString());

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //    Uri photoURI = FileProvider.getUriForFile(this,
                photoURI = FileProvider.getUriForFile(this,
                        "fi.raumankonepaja.deliverylogger.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


            }
        }
    }



// tässä luodaan uusi tiedosto, johon kuva sitten tallennetaan...
    private File createImageFile() throws IOException {
        // Create an image file name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // String imageFileName = "JPEG_" + timeStamp + "_";
        String imageFileName = "JPEG_" + "kuva1";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }





}
